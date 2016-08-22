package com.imgtec.creator.lumpy.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.imgtec.creator.lumpy.dao.DBClient;
import com.imgtec.creator.lumpy.dao.DBClientDao;
import com.imgtec.creator.lumpy.dao.DBGroup;
import com.imgtec.creator.lumpy.dao.DBGroupDao;
import com.imgtec.creator.lumpy.dao.DBSensor;
import com.imgtec.creator.lumpy.dao.DBSensorDao;
import com.imgtec.creator.lumpy.dao.DaoMaster;
import com.imgtec.creator.lumpy.dao.DaoSession;
import com.imgtec.creator.lumpy.data.api.Sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DBManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(DBManager.class);

  private SQLiteDatabase db;
  private DaoSession session;

  public DBManager(Context appContext) {
    super();
    this.db = new DaoMaster.DevOpenHelper(appContext, "db", null).getWritableDatabase();
    this.session = new DaoMaster(db).newSession();
  }

  /**
   * Insert new client to database if not already exist. Serial number is subject to check whether
   * client exist or not, therefore serial number must be unique per device.
   * @param clientName client name
   * @param serialNumber client serial number / unique id
   */
  public void addClientIfNotExist(String clientName, String serialNumber) {
    DBClient client = session.getDBClientDao().queryBuilder().where(DBClientDao.Properties.Serial.eq(serialNumber)).build().unique();
    if (client == null) {
      client = new DBClient();
      client.setName(clientName);
      client.setSerial(serialNumber);
      session.getDBClientDao().insert(client);
    }
  }

  public void addSensorIfNotExist(Long sensorID, String clientSerialNumber) {
    DBSensor sensor = session.getDBSensorDao().queryBuilder().where(DBSensorDao.Properties.Id.eq(sensorID)).unique();
    DBClient client = session.getDBClientDao().queryBuilder().where(DBClientDao.Properties.Serial.eq(clientSerialNumber)).unique();
    if (sensor != null) {
      return;
    }
    if (client == null) {
      LOGGER.error("Couldn't add sensor [{}] to database as there is no client with serial number [{}]");
      return;
    }

    sensor = new DBSensor();
    sensor.setId(sensorID);
    sensor.setClientID(client.getId());

    DBSensor lastSensorInGroup = session.getDBSensorDao().queryBuilder().where(DBSensorDao.Properties.GroupID.isNull()).limit(1).orderDesc(DBSensorDao.Properties.Order).build().unique();
    if (lastSensorInGroup == null) {
      sensor.setOrder(1);
    } else {
      sensor.setOrder(lastSensorInGroup.getOrder()+1);
    }
    session.getDBSensorDao().insert(sensor);
  }

  public List<DBSensor> getAllSensors() {
    return session.getDBSensorDao().queryBuilder().build().list();
  }

  public DBSensor getSensorById(Long id) {
    return session.getDBSensorDao().queryBuilder().where(DBSensorDao.Properties.Id.eq(id)).unique();
  }

  public void setOrderOfSensors(List<Sensor> sensors) {

  }

  public void addGroup(String groupName) {
    DBGroup dbGroup = new DBGroup();
    DBGroup lastGroup = session.getDBGroupDao().queryBuilder().orderDesc(DBGroupDao.Properties.Order).limit(1).unique();
    if (lastGroup == null) {
      dbGroup.setOrder(1);
    } else {
      dbGroup.setOrder(lastGroup.getOrder()+1);
    }
    dbGroup.setName(groupName);
    session.getDBGroupDao().insert(dbGroup);
  }

  public List<DBGroup> getAllGroups() {
    return session.getDBGroupDao().queryBuilder().orderAsc(DBGroupDao.Properties.Order).list();
  }

  public DBGroup getGroupById(long groupID) {
    return session.getDBGroupDao().load(groupID);
  }

  /**
   * Updates group with specified ID with new name. All sensors attached to this group are detached
   * from it.
   * @param groupID
   * @param newName
   */
  public void updateGroup(Long groupID, String newName) {
    DBGroup dbGroup = session.getDBGroupDao().load(groupID);
    if (dbGroup != null) {
      dbGroup.setName(newName);
      dbGroup.update();
    }
  }

  /**
   * Removes group with specified ID from database
   * @param groupID
   */
  public void deleteGroup(Long groupID) {
    DBGroup dbGroup = session.getDBGroupDao().load(groupID);
    if (dbGroup != null) {
      List<DBSensor> sensors = dbGroup.getSensors();
      for (DBSensor sensor : sensors) {
        sensor.setGroupID(null);
        sensor.update();
      }

      dbGroup.delete();
    }
  }
}
