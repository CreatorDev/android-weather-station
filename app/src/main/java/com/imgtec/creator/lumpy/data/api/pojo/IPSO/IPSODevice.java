package com.imgtec.creator.lumpy.data.api.pojo.IPSO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imgtec.creator.lumpy.data.api.pojo.Pojo;

public class IPSODevice extends Pojo {

  @SerializedName("SerialNumber")
  @Expose
  String serialNumber;

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }
}
