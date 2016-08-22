package com.imgtec.creator.lumpy.data.api.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IDPResult extends Pojo {

  @SerializedName("Name")
  @Expose
  private String name;

  @SerializedName("Key")
  @Expose
  private String key;

  @SerializedName("Secret")
  @Expose
  private String secret;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
