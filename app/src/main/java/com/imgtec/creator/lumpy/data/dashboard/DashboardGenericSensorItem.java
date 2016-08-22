package com.imgtec.creator.lumpy.data.dashboard;


import com.imgtec.creator.lumpy.data.api.Sensor;

public class DashboardGenericSensorItem extends DashboardItem {

  private final Sensor sensor;
  private int order = 0;

  public DashboardGenericSensorItem(Sensor sensor) {
    this.sensor = sensor;
  }

  public Sensor getSensor() {
    return this.sensor;
  }

  @Override
  public int getItemViewType() {
    return VIEW_TYPE_GENERIC_SENSOR;
  }

  @Override
  public long getItemID() {
    return sensor.getID();
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }
}
