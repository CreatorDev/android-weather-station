package com.imgtec.creator.lumpy.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
    active = true,
    nameInDb = "Group"
)
public class DBGroup {

  @Id(autoincrement = true)
  Long id;

  @Property(nameInDb = "name")
  String name;

  @Property(nameInDb = "order")
  Integer order;

  @ToMany(referencedJoinProperty = "groupID")
  private List<DBSensor> sensors;

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 1942392019)
public void refresh() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.refresh(this);
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 713229351)
public void update() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.update(this);
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 128553479)
public void delete() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.delete(this);
}

/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 1982921383)
public synchronized void resetSensors() {
    sensors = null;
}

/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 1026537238)
public List<DBSensor> getSensors() {
    if (sensors == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        DBSensorDao targetDao = daoSession.getDBSensorDao();
        List<DBSensor> sensorsNew = targetDao._queryDBGroup_Sensors(id);
        synchronized (this) {
            if(sensors == null) {
                sensors = sensorsNew;
            }
        }
    }
    return sensors;
}

/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 232174381)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getDBGroupDao() : null;
}

/** Used for active entity operations. */
@Generated(hash = 623530677)
private transient DBGroupDao myDao;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

public String getName() {
    return this.name;
}

public void setName(String name) {
    this.name = name;
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public Integer getOrder() {
    return this.order;
}

public void setOrder(Integer order) {
    this.order = order;
}

@Generated(hash = 833272544)
public DBGroup(Long id, String name, Integer order) {
    this.id = id;
    this.name = name;
    this.order = order;
}

@Generated(hash = 2038973064)
public DBGroup() {
}

}
