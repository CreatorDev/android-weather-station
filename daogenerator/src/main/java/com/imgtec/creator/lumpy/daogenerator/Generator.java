package com.imgtec.creator.lumpy.daogenerator;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;
import org.greenrobot.greendao.generator.ToMany;

public class Generator {

  public static void main(String[] args) throws Exception {

    Schema schema = new Schema(1, "com.imgtec.creator.lumpy.dao");

    Entity sensor = schema.addEntity("Sensor");
    Property sensorID = sensor.addIdProperty().autoincrement().getProperty();
    sensor.addStringProperty("stringID").notNull().getProperty();
    sensor.addIntProperty("order");
    Property clientID = sensor.addLongProperty("clientID").notNull().getProperty();

    Entity client = schema.addEntity("Client");
    client.addIdProperty().autoincrement().getProperty();
    client.addStringProperty("name").notNull();
    client.addStringProperty("serial").index();

    ToMany toMany = client.addToMany(sensor, sensorID);
    toMany.setName("sensors");

    new DaoGenerator().generateAll(schema, "../app/src/main/java");
  }

}
