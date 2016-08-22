package com.imgtec.creator.lumpy.data.api.pojo.IPSO;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imgtec.creator.lumpy.data.api.pojo.Pojo;

public class IPSOSensor extends Pojo {

  @SerializedName("InstanceID")
  @Expose
  String instanceID;

  @SerializedName("SensorValue")
  @Expose
  float value;

  @SerializedName("SensorUnits")
  @Expose
  String unit;

  @SerializedName("MinMeasuredValue")
  @Expose
  float minMeasuredValue;

  @SerializedName("MaxMeasuredValue")
  @Expose
  float maxMeasuredValue;

  public String getInstanceID() {
    return instanceID;
  }

  public void setInstanceID(String instanceID) {
    this.instanceID = instanceID;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public float getMinMeasuredValue() {
    return minMeasuredValue;
  }

  public void setMinMeasuredValue(float minMeasuredValue) {
    this.minMeasuredValue = minMeasuredValue;
  }

  public float getMaxMeasuredValue() {
    return maxMeasuredValue;
  }

  public void setMaxMeasuredValue(float maxMeasuredValue) {
    this.maxMeasuredValue = maxMeasuredValue;
  }

}
