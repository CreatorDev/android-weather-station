package com.imgtec.creator.lumpy.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(
    // Flag to make an entity "active": Active entities have update,
    // delete, and refresh methods.
    active = true,

    // Specifies the name of the table in the database.
    // By default, the name is based on the entities class name.
    nameInDb = "Client",

    // Define indexes spanning multiple columns here.
    indexes = {
        @Index(value = "serial DESC", unique = true)
    },

    // Flag if the DAO should create the database table (default is true).
    // Set this to false, if you have multiple entities mapping to one table,
    // or the table creation is done outside of greenDAO.
    createInDb = true
)
public class DBClient {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "serial")
    private String serial;

    @Property(nameInDb = "name")
    private String name;

    @ToMany(referencedJoinProperty = "clientID")
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
    @Generated(hash = 1580352284)
    public List<DBSensor> getSensors() {
        if (sensors == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBSensorDao targetDao = daoSession.getDBSensorDao();
            List<DBSensor> sensorsNew = targetDao._queryDBClient_Sensors(id);
            synchronized (this) {
                if(sensors == null) {
                    sensors = sensorsNew;
                }
            }
        }
        return sensors;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 53598485)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDBClientDao() : null;
    }

    /** Used for active entity operations. */
    @Generated(hash = 1709728317)
    private transient DBClientDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerial() {
        return this.serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 2030086321)
    public DBClient(Long id, String serial, String name) {
        this.id = id;
        this.serial = serial;
        this.name = name;
    }

    @Generated(hash = 293425734)
    public DBClient() {
    }

}

