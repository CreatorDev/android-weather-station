/*
 * <b>Copyright (c) 2016, Imagination Technologies Limited and/or its affiliated group companies
 *  and/or licensors. </b>
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are permitted
 *  provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *      and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *      endorse or promote products derived from this software without specific prior written
 *      permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

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

  /**
   * Inserts new sensor in database with specified sensorID if none already exists.
   * @param sensorID unique ID of sensor
   * @param clientSerialNumber serial number / unique ID of hardware on which the sensor is running
   */
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

  /**
   * Return sensor with specified ID.
   * @param id ID of the sensor
   * @return Found sensor object or {@code null}
   */
  public DBSensor getSensorById(Long id) {
    return session.getDBSensorDao().queryBuilder().where(DBSensorDao.Properties.Id.eq(id)).unique();
  }

  /**
   * Saves new group in database. Newly created group will have 'order' set to maximum value of 'order'
   * + 1.
   * @param groupName name of the created group
   */
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

  /**
   * Returns all groups saved in database
   * @return list of groups saved in database, may be empty.
   */
  public List<DBGroup> getAllGroups() {
    return session.getDBGroupDao().queryBuilder().orderAsc(DBGroupDao.Properties.Order).list();
  }

  /**
   * Returns group with specified ID.
   * @param groupID ID of the group to retrieve
   * @return Found group or {@null}
   */
  public DBGroup getGroupById(long groupID) {
    return session.getDBGroupDao().load(groupID);
  }

  /**
   * Updates group with specified ID with new name.
   * @param groupID ID of the group to update
   * @param newName new name of the group
   */
  public void updateGroup(Long groupID, String newName) {
    DBGroup dbGroup = session.getDBGroupDao().load(groupID);
    if (dbGroup != null) {
      dbGroup.setName(newName);
      dbGroup.update();
    }
  }

  /**
   * Removes group with specified ID from database. All sensors attached to this group are detached
   * from it.
   * @param groupID ID of group to remove
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
