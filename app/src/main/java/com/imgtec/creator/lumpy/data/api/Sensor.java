package com.imgtec.creator.lumpy.data.api;


import com.imgtec.creator.lumpy.data.api.pojo.IPSO.IPSOSensor;

public abstract class Sensor {



  private final IPSOSensor ipsoSensor;

  /**
   * Unique NAME of client on which sensor is installed. Used to group sensors attached to one board.
   */
  private final String clientID;
  private final int objectID;


  public Sensor(IPSOSensor ipsoSensor, String deviceID, int objectID) {
    this.ipsoSensor = ipsoSensor;
    this.clientID = deviceID;
    this.objectID = objectID;
  }

  public float getCurrentValue() {
    return ipsoSensor.getValue();
  }

  public float getMinMeasuredValue() {
    return ipsoSensor.getMinMeasuredValue();
  }

  public float getMaxMeasuredValue() {
    return ipsoSensor.getMaxMeasuredValue();
  }

  public String getUnit() {
    return ipsoSensor.getUnit();
  }

  public String getClientID() {
    return clientID;
  }

  public String getInstanceID() {
    return ipsoSensor.getInstanceID();
  }

  public int getObjectID() {
    return objectID;
  }

  public long getID() {
    long id =  (clientID + objectID + ipsoSensor.getInstanceID()).hashCode();
    if (id < 0) {
      return id * -1;
    }
    return id;
  }
}
