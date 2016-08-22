package com.imgtec.creator.lumpy.data.api;


import com.imgtec.creator.lumpy.data.api.pojo.IPSO.IPSOHumidity;

public class HumiditySensor extends Sensor {

  public HumiditySensor(IPSOHumidity ipsoSensor, String clientID, int objectID, int instanceId) {
    super(ipsoSensor, clientID, objectID);
  }
}
