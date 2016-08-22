package com.imgtec.creator.lumpy.data.api;


import android.os.Handler;

import com.google.gson.reflect.TypeToken;
import com.imgtec.creator.lumpy.data.api.pojo.Client;
import com.imgtec.creator.lumpy.data.api.pojo.Clients;
import com.imgtec.creator.lumpy.data.api.pojo.IPSO.IPSODevice;
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

        Instances<IPSOTemperature> temperatures = apiService.getInstances(client, TEMPERATURE_OBJECT_ID, new TypeToken<Instances<IPSOTemperature>>() {
        }).get();

//        Instances<IPSOPressure> pressures = apiService.getInstances(client, PRESSURE_OBJECT_ID, new TypeToken<Instances<IPSOPressure>>() {
//        }).get();
//        Instances<IPSOAirQuality> airQualities = apiService.getInstances(client, AIR_QUALITY_OBJECT_ID, new TypeToken<Instances<IPSOAirQuality>>() {
//        }).get();
//        Instances<IPSOHumidity> humidities = apiService.getInstances(client, HUMIDITY_OBJECT_ID, new TypeToken<Instances<IPSOHumidity>>() {
//        }).get();
//        Instances<IPSOThunderDistance> thunderDistances = apiService.getInstances(client, THUNDER_DISTANCE_OBJECT_ID, new TypeToken<Instances<IPSOThunderDistance>>() {
//        }).get();
//        Instances<IPSOThunderPower> thunderPowers = apiService.getInstances(client, THUNDER_POWER_OBJECT_ID, new TypeToken<Instances<IPSOThunderPower>>() {
//        }).get();


        for (IPSOTemperature temperature : temperatures.getItems()) {
          TemperatureSensor sensor = new TemperatureSensor(temperature, client.getName(), TEMPERATURE_OBJECT_ID);
          sensorList.add(sensor);
          dbManager.addSensorIfNotExist(sensor.getID(), ipsoDevice.getSerialNumber());
        }
//        for (IPSOHumidity humidity : humidities.getItems()) {
//          sensorList.add(new HumiditySensor(humidity));
//        }
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
