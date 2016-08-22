package com.imgtec.creator.lumpy.data.api.pojo.IPSO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imgtec.creator.lumpy.data.api.pojo.Pojo;

public class IPSOPressure extends Pojo {

  @SerializedName("SensorValue")
  @Expose
  float value;

  @SerializedName("Units")
  @Expose
  String unit;

  @SerializedName("MinMeasuredValue")
  @Expose
  float minMeasuredValue;

  @SerializedName("MaxMeasuredValue")
  @Expose
  float maxMeasuredValue;

  @SerializedName("HOSTUniqueID")
  @Expose
  String hostUniqueID;

  @SerializedName("Last12Measurements")
  @Expose
  String lastMeasurements;

  @SerializedName("AverageValueIn12h")
  @Expose
  float averageValue12h;

  @SerializedName("AverageValuein24h")
  @Expose
  float averageValue24h;
}
