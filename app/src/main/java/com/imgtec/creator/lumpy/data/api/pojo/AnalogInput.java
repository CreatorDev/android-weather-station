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
package com.imgtec.creator.lumpy.data.api.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnalogInput extends Pojo {

  @SerializedName("AnalogInputCurrentValue")
  @Expose
  private float currentValue;

  @SerializedName("MinMeasuredValue")
  @Expose
  private float minMeasuredValue;

  @SerializedName("MaxMeasuredValue")
  @Expose
  private float maxMeasuredValue;

  @SerializedName("SensorType")
  @Expose
  private String sensorType;

  public float getCurrentValue() {
    return currentValue;
  }

  public void setCurrentValue(float currentValue) {
    this.currentValue = currentValue;
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

  public String getSensorType() {
    return sensorType;
  }

  public void setSensorType(String sensorType) {
    this.sensorType = sensorType;
  }
}
