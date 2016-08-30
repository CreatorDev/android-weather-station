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

package com.imgtec.creator.lumpy.dao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(
    active = true,
    nameInDb = "Sensor"
)
public class DBSensor {

    @Id
    private Long id;

    @Property(nameInDb = "clientID")
    private Long clientID;

    @Property(nameInDb = "groupID")
    private Long groupID;

    @Property(nameInDb = "order")
    @NotNull
    private Integer order;

    @ToOne(joinProperty = "clientID")
    private DBClient client;

    @ToOne(joinProperty = "groupID")
    private DBGroup group;

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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 184171101)
    public void setGroup(DBGroup group) {
        synchronized (this) {
            this.group = group;
            groupID = group == null ? null : group.getId();
            group__resolvedKey = groupID;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 882580339)
    public DBGroup getGroup() {
        Long __key = this.groupID;
        if (group__resolvedKey == null || !group__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBGroupDao targetDao = daoSession.getDBGroupDao();
            DBGroup groupNew = targetDao.load(__key);
            synchronized (this) {
                group = groupNew;
                group__resolvedKey = __key;
            }
        }
        return group;
    }

    @Generated(hash = 201187923)
    private transient Long group__resolvedKey;

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1794238139)
    public void setClient(DBClient client) {
        synchronized (this) {
            this.client = client;
            clientID = client == null ? null : client.getId();
            client__resolvedKey = clientID;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 623334526)
    public DBClient getClient() {
        Long __key = this.clientID;
        if (client__resolvedKey == null || !client__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBClientDao targetDao = daoSession.getDBClientDao();
            DBClient clientNew = targetDao.load(__key);
            synchronized (this) {
                client = clientNew;
                client__resolvedKey = __key;
            }
        }
        return client;
    }

    @Generated(hash = 1636229693)
    private transient Long client__resolvedKey;

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1133466108)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDBSensorDao() : null;
    }

    /** Used for active entity operations. */
    @Generated(hash = 186232798)
    private transient DBSensorDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public Long getGroupID() {
        return this.groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public Long getClientID() {
        return this.clientID;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
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

    @Generated(hash = 1067215974)
    public DBSensor(Long id, Long clientID, Long groupID, @NotNull Integer order) {
        this.id = id;
        this.clientID = clientID;
        this.groupID = groupID;
        this.order = order;
    }

    @Generated(hash = 1495009189)
    public DBSensor() {
    }

}