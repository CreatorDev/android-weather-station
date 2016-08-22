package com.imgtec.creator.lumpy.data.dashboard;

/**
 * Base class for item displayed on Dashboard. Just a marker.
 */
public abstract class DashboardItem {

  public static final int VIEW_TYPE_GENERIC_SENSOR = 1;
  public static final int VIEW_TYPE_HEADER = 2;

  public abstract int getItemViewType();

  /**
   * Return unique id of this item. This ID cannot duplicate across adapter items.
   * @return unique ID of this item
   */
  public abstract long getItemID();

}
