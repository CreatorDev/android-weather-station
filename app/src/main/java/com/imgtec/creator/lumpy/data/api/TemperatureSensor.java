package com.imgtec.creator.lumpy.data.api;


import com.imgtec.creator.lumpy.data.api.pojo.IPSO.IPSOTemperature;

public class TemperatureSensor extends Sensor {

  public TemperatureSensor(IPSOTemperature ipsoSensor, String clientID, int objectID) {
    super(ipsoSensor, clientID, objectID);
  }
}
