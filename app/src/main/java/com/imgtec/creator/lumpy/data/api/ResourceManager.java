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


import android.os.Handler;

import com.google.gson.reflect.TypeToken;
import com.imgtec.creator.lumpy.data.api.exceptions.NotFoundException;
import com.imgtec.creator.lumpy.data.api.pojo.Client;
import com.imgtec.creator.lumpy.data.api.pojo.Clients;
import com.imgtec.creator.lumpy.data.api.pojo.IPSO.IPSODevice;
import com.imgtec.creator.lumpy.data.api.pojo.IPSO.IPSOHumidity;
import com.imgtec.creator.lumpy.data.api.pojo.IPSO.IPSOPressure;
import com.imgtec.creator.lumpy.data.api.pojo.IPSO.IPSOTemperature;
import com.imgtec.creator.lumpy.data.api.pojo.Instances;
import com.imgtec.creator.lumpy.db.DBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Encapsulates whole logic of managing resources. Takes care of having always fresh data that
 * can be displayed on any screen.
 * Use {@link #addListener(ResourceManagerListener)} and {@link #removeListener(ResourceManagerListener)}
 * to register/unregister for notifications form this object. All notifications are scheduled on main
 * thread so it's safe to do UI operations in {@link ResourceManagerListener} callbacks.
 */
public class ResourceManager {

  private static final int DEVICE_INFO_OBJECT_ID = 3;
  private static final int TEMPERATURE_OBJECT_ID = 3303;
  private static final int PRESSURE_OBJECT_ID = 3315;
  private static final int HUMIDITY_OBJECT_ID = 3304;
  private static final int AIR_QUALITY_OBJECT_ID = 3325;
  private static final int THUNDER_DISTANCE_OBJECT_ID = 3330;
  private static final int THUNDER_POWER_OBJECT_ID = 3328;

  private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


  public interface ResourceManagerListener {

    /**
     * Called when new data are available.
     * @param newData a map containing client name associated with list of sensors.
     */
    void onNewData(Map<String, List<Sensor>> newData);
  }

  /**
   * Used to dispatch operations to main thread.
   */
  private final Handler handler;
  private final ApiService apiService;
  private Map<String, List<Sensor>> sensorsMap = new HashMap<>();
  private DBManager dbManager;
  private CopyOnWriteArraySet<ResourceManagerListener> listeners = new CopyOnWriteArraySet<>();

  ResourceManager(ApiService apiService, Handler handler, DBManager dbManager) {
    this.handler = handler;
    this.apiService = apiService;
    this.dbManager = dbManager;
    executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        loadAllResources();
      }
    }, 0, 30, TimeUnit.SECONDS);
  }

  /**
   * Register listener that will handle notifications form this object.
   *
   * @param listener
   */
  public void addListener(ResourceManagerListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregister listener to no longer receive notifications form this object.
   *
   * @param listener
   */
  public void removeListener(ResourceManagerListener listener) {
    listeners.remove(listener);
  }

  private void loadAllResources() {

    try {
      Clients clients = apiService.getClients(0, 100).get();
      sensorsMap.clear();
      for (Client client : clients.getItems()) {
        List<Sensor> sensorList = new ArrayList<>();
        sensorsMap.put(client.getName(), sensorList);


        Instances<IPSODevice> deviceInfos = apiService.getInstances(client, DEVICE_INFO_OBJECT_ID, new TypeToken<Instances<IPSODevice>>() {
        }).get();
        IPSODevice ipsoDevice = deviceInfos.getItems().get(0);
        dbManager.addClientIfNotExist(client.getName(), deviceInfos.getItems().get(0).getSerialNumber());

        try {
          Instances<IPSOTemperature> temperatures = apiService.getInstances(client, TEMPERATURE_OBJECT_ID, new TypeToken<Instances<IPSOTemperature>>() {
          }).get();

          for (IPSOTemperature temperature : temperatures.getItems()) {
            Sensor sensor = new TemperatureSensor(temperature, client.getName(), TEMPERATURE_OBJECT_ID);
            sensorList.add(sensor);
            dbManager.addSensorIfNotExist(sensor.getID(), ipsoDevice.getSerialNumber());
          }

        } catch (ExecutionException e) {
          if (!(e.getCause() instanceof NotFoundException)) {
            throw e;
          }
        }

        try {
          Instances<IPSOPressure> pressures = apiService.getInstances(client, PRESSURE_OBJECT_ID, new TypeToken<Instances<IPSOPressure>>() {
          }).get();

          for (IPSOPressure pressure : pressures.getItems()) {
            Sensor sensor = new PressureSensor(pressure, client.getName(), PRESSURE_OBJECT_ID);
            sensorList.add(sensor);
            dbManager.addSensorIfNotExist(sensor.getID(), ipsoDevice.getSerialNumber());
          }

        } catch (ExecutionException e) {
          if (!(e.getCause() instanceof NotFoundException)) {
            throw e;
          }
        }
//        Instances<IPSOAirQuality> airQualities = apiService.getInstances(client, AIR_QUALITY_OBJECT_ID, new TypeToken<Instances<IPSOAirQuality>>() {
//        }).get();

        try {
          Instances<IPSOHumidity> humidities = apiService.getInstances(client, HUMIDITY_OBJECT_ID, new TypeToken<Instances<IPSOHumidity>>() {
          }).get();

          for (IPSOHumidity humidity : humidities.getItems()) {
            Sensor sensor = new HumiditySensor(humidity, client.getName(), HUMIDITY_OBJECT_ID);
            sensorList.add(sensor);
            dbManager.addSensorIfNotExist(sensor.getID(), ipsoDevice.getSerialNumber());
          }

        } catch (ExecutionException e) {
          if (!(e.getCause() instanceof NotFoundException)) {
            throw e;
          }
        }
//        Instances<IPSOThunderDistance> thunderDistances = apiService.getInstances(client, THUNDER_DISTANCE_OBJECT_ID, new TypeToken<Instances<IPSOThunderDistance>>() {
//        }).get();
//        Instances<IPSOThunderPower> thunderPowers = apiService.getInstances(client, THUNDER_POWER_OBJECT_ID, new TypeToken<Instances<IPSOThunderPower>>() {
//        }).get();





      }
      notifyNewDataAvailable();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }


  }

  private void notifyNewDataAvailable() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        for (ResourceManagerListener listener : listeners) {
          listener.onNewData(new HashMap<>(sensorsMap));
        }
      }
    });
  }
}
