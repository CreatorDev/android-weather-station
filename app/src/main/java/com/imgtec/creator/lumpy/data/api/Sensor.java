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
