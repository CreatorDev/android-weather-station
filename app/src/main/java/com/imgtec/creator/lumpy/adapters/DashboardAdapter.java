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

package com.imgtec.creator.lumpy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.imgtec.creator.lumpy.R;
import com.imgtec.creator.lumpy.dao.DBGroup;
import com.imgtec.creator.lumpy.dao.DBSensor;
import com.imgtec.creator.lumpy.data.api.HumiditySensor;
import com.imgtec.creator.lumpy.data.api.PressureSensor;
import com.imgtec.creator.lumpy.data.api.Sensor;
import com.imgtec.creator.lumpy.data.api.TemperatureSensor;
import com.imgtec.creator.lumpy.data.dashboard.DashboardGenericSensorItem;
import com.imgtec.creator.lumpy.data.dashboard.DashboardHeaderItem;
import com.imgtec.creator.lumpy.data.dashboard.DashboardItem;
import com.imgtec.creator.lumpy.data.dashboard.DashboardSensorItemFactory;
import com.imgtec.creator.lumpy.db.DBManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter that handles displaying {@link Sensor} items returned by {@link com.imgtec.creator.lumpy.data.api.ResourceManager}.
 * Sensors are displayed in group and in sorted order. Sort order is taken from database.
 * *IMPORTANT*
 * Groups are not passed to adapter directly but adapter uses database to fetch sensor groups.
 * Anytime database is modified user should call {@link #refresh()} to tell the adapter to update.
 * *IMPORTANT 2*
 * Adapter uses stableID feature to improve performance and support nice animations. Every sensor
 * passed to this adapter should have an unique ID.
 */
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardItemVH> implements ItemTouchHelperAdapter {

  public interface DashboardAdapterListener {

    /**
     * Called when edit btn is clicked on header (group name) item.
     * @param position position of header item in adapter
     * */
    void onEdit(int position);

    /**
     * Called when delete button is clicked on header (group name) item.
     * @param position position of header item in adapter
     */
    void onRemove(int position);
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(DashboardAdapter.class);

  private DBManager dbManager;
  private Context context;

  public DashboardAdapter(Context context, DBManager dbManager) {
    setHasStableIds(true);
    this.dbManager = dbManager;
    this.context = context;
  }

  /**
   * Keeps last received sensors in {@link #setData(Map)}. This collection is used in case
   * when we need to do reorder of items according to current DB state.
   */
  private List<Sensor> backupData = new ArrayList<>();

  /**
   * Exact data displayed in RecyclerView
   */
  private List<DashboardItem> data = new ArrayList<>();

  /**
   * Flag determining whether data displayd in this adapter are in edit mode
   */
  private boolean isEditMode = false;

  private DashboardAdapterListener listener;


  /**
   * Set new data to display in RecyclerView. Before data are displayed they are sorted and
   * attached to appropriate groups accordingly to current DB state. This method calls
   * {@link #notifyDataSetChanged()} by herself.
   * @param newData new set of data to display in RecyclerView
   */
  public void setData(Map<String,List<Sensor>> newData) {
    data.clear();
    backupData.clear();
    for (List<Sensor> sensorList : newData.values()) {
      backupData.addAll(sensorList);
    }
    data.addAll(convertIntoDashboardItems(backupData));
    notifyDataSetChanged();
  }

  /**
   * Refreshes adapter state eg. when drag and drop has been performed and therefore sensor may
   * now belong to another group or whole group has moved.
   */
  public void refresh() {
    data.clear();
    data.addAll(convertIntoDashboardItems(backupData));
    notifyDataSetChanged();
  }

  /**
   * <p>Informs this adapter that group name of specified dashboard item has been changed.</p>
   * <p>Does {@link #notifyItemChanged(int)} call internally so no need to cally any other notifications</p>
   * @param item dashboard item which has been updated.
   */
  public void notifyGroupNameChanged(DashboardHeaderItem item) {
    int position = data.indexOf(item);
    notifyItemChanged(position);
  }


  public void notifyGroupRemoved(DashboardHeaderItem item) {
    int position = data.indexOf(item);
    if (position != RecyclerView.NO_POSITION) {
      data.remove(position);
      notifyItemRemoved(position);
      refresh();
    }
  }

  public DashboardItem getItem(int position) {
    return data.get(position);
  }

  @Override
  public DashboardItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
    return createVH(parent, viewType);
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  @Override
  public void onBindViewHolder(DashboardItemVH holder, int position) {
    bindVH(holder, position);
  }

  @Override
  public int getItemViewType(int position) {
    return data.get(position).getItemViewType();
  }

  @Override
  public long getItemId(int position) {
    return getItem(position).getItemID();
  }

  private DashboardItemVH createVH(ViewGroup parent, int viewType) {
    switch (viewType) {
      case DashboardItem.VIEW_TYPE_GENERIC_SENSOR:
        return createGenericSensorItemVH(parent);
      case DashboardItem.VIEW_TYPE_HEADER:
        return createHeaderItemVH(parent);
    }
    return null;
  }

  private void bindVH(DashboardItemVH holder, int position) {
    switch (holder.getItemViewType()) {
      case DashboardItem.VIEW_TYPE_GENERIC_SENSOR:
        bindGenericSensorItemVH(holder, position);
        break;
      case DashboardItem.VIEW_TYPE_HEADER:
        bindHeaderItemVH(holder, position);
        break;
    }
  }

  private DashboardGenericSensorItemVH createGenericSensorItemVH(ViewGroup parent){
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_generic_sensor, parent, false);
    return new DashboardGenericSensorItemVH(v);
  }

  private DashboardHeaderItemVH createHeaderItemVH(ViewGroup parent){
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_header, parent, false);
    return new DashboardHeaderItemVH(v);
  }

  private void bindGenericSensorItemVH(DashboardItemVH holder, int position) {
    DashboardGenericSensorItemVH vh = (DashboardGenericSensorItemVH)holder;
    DashboardGenericSensorItem item = (DashboardGenericSensorItem) data.get(position);
    vh.icon.setImageResource(getIconBySensor(item.getSensor()));

    vh.value.setText(item.getDecimalFormat().format(item.getSensor().getCurrentValue()));
    vh.value.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.getCurrentValueFontSize(context.getResources()));
    vh.unit.setText(item.getSensor().getUnit());
    vh.unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.getUnitFontSize(context.getResources()));
    vh.minValue.setText(item.getDecimalFormat().format(item.getSensor().getMinMeasuredValue()));
    vh.maxValue.setText(item.getDecimalFormat().format(item.getSensor().getMaxMeasuredValue()));
    vh.minValueUnit.setText(item.getSensor().getUnit());
    vh.maxValueUnit.setText(item.getSensor().getUnit());
  }



  private void bindHeaderItemVH(final DashboardItemVH holder, int position) {
    DashboardHeaderItemVH vh = (DashboardHeaderItemVH) holder;
    DashboardHeaderItem item = (DashboardHeaderItem) data.get(position);
    vh.title.setText(item.getTitle());
    vh.editBtn.setVisibility(isEditMode && item.isEditable() ? View.VISIBLE : View.GONE);
    vh.deleteBtn.setVisibility(isEditMode && item.isEditable() ? View.VISIBLE : View.GONE);
    vh.editBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        handleEditBtnClick(holder.getAdapterPosition());
      }
    });
    vh.deleteBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (listener != null) {
          listener.onRemove(holder.getAdapterPosition());
        }
      }
    });
  }

  @Override
  public void onItemMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(data, i, i + 1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(data, i, i - 1);
      }
    }
    notifyItemMoved(fromPosition, toPosition);

  }

  @Override
  public void onItemDropped(int fromPosition, int toPosition) {
    if (getItem(toPosition) instanceof DashboardHeaderItem) {
      updateDBWithNewOrder(true);
    } else {
      updateDBWithNewOrder(false);
    }
    refresh();
  }

  public void setEditMode(boolean isEditMode) {
    this.isEditMode = isEditMode;
    notifyDataSetChanged();
  }

  public void setEventListener(DashboardAdapterListener listener) {
    this.listener = listener;
  }



  /***************
   * VIEW HOLDERS
   ***************/
  static abstract class DashboardItemVH extends RecyclerView.ViewHolder {

    DashboardItemVH(View itemView) {
      super(itemView);
    }
  }

  static class DashboardGenericSensorItemVH extends DashboardItemVH {

    @BindView(R.id.icon) ImageView icon;
    @BindView(R.id.value) TextView value;
    @BindView(R.id.unit) TextView unit;
    @BindView(R.id.min_value) TextView minValue;
    @BindView(R.id.min_value_unit) TextView minValueUnit;
    @BindView(R.id.max_value) TextView maxValue;
    @BindView(R.id.max_value_unit) TextView maxValueUnit;

    DashboardGenericSensorItemVH(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  static class DashboardHeaderItemVH extends DashboardItemVH {

    @BindView(R.id.title) TextView title;
    @BindView(R.id.edit_button) ImageButton editBtn;
    @BindView(R.id.delete_button) ImageButton deleteBtn;

    DashboardHeaderItemVH(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }


  /**
   * Converts list of sensors into an ordered list of {@link DashboardItem} objects ready to display.
   * Sensors are assigned to their groups and are sorted accordingly to {@link DBSensor#order}
   * property in ASC order.
   * @param sensors list of sensors
   * @return list of dashboard items
   */
  private List<DashboardItem> convertIntoDashboardItems(List<Sensor> sensors) {
    Map<DBGroup, List<DashboardGenericSensorItem>> result = new HashMap<>();
    List<DBGroup> groups = dbManager.getAllGroups();
    result.put(null, new ArrayList<DashboardGenericSensorItem>());
    for (DBGroup group : groups) {
      result.put(group, new ArrayList<DashboardGenericSensorItem>());
    }

    for (Sensor sensor : sensors) {
      DBSensor dbSensor = dbManager.getSensorById(sensor.getID());
      DBGroup group = dbSensor.getGroup();
      if (group == null) {
        List<DashboardGenericSensorItem> list = result.get(null);
        DashboardGenericSensorItem item = DashboardSensorItemFactory.createItem(sensor);
        item.setOrder(dbSensor.getOrder());
        list.add(item);
      } else {
        List<DashboardGenericSensorItem> list = result.get(group);
        DashboardGenericSensorItem item = DashboardSensorItemFactory.createItem(sensor);
        item.setOrder(dbSensor.getOrder());
        list.add(item);
      }
    }

    for (List<DashboardGenericSensorItem> l : result.values()) {
      Collections.sort(l, COMPARATOR);
    }
    List<DashboardItem> resultList = new ArrayList<>();
    if (result.get(null).size() > 0) {
      DashboardHeaderItem dashboardHeaderItem = new DashboardHeaderItem(context.getString(R.string.unattached), false);
      dashboardHeaderItem.setUserTag(-1L);
      resultList.add(dashboardHeaderItem);
      for (DashboardGenericSensorItem item : result.get(null)) {
        resultList.add(item);
      }
    }
    for (DBGroup dbGroup : groups) {
      DashboardHeaderItem dashboardHeaderItem;
      dashboardHeaderItem = new DashboardHeaderItem(dbGroup.getName(), true);
      dashboardHeaderItem.setUserTag(dbGroup.getId());


      resultList.add(dashboardHeaderItem);
      for (DashboardGenericSensorItem item : result.get(dbGroup)) {
        resultList.add(item);
      }
    }
    return resultList;
  }

  /**
   * Traverse through whole {@link #data} list and update database according to current item order.
   * may sort only groups or both sensors and groups depending on {@code onlyHeader} param.
   * When {@code onlyHeader} is set to {@code true} groups order is updated however sensors are left
   * untouched. This allows to move a whole group of sensors.
   * When {@code onlyHeader} is set to {@code false} order in database is mapped exactly from current
   * {@link #data} state.
   * Eg. when group label has been moved and this function has been called with {@code onluHeader} set
   * to false, all sensors that were previousle belonging to moved group will now be attached to
   * group they are now after the drag.
   *
   */
  private void updateDBWithNewOrder(boolean onlyHeader) {
    int sensorOrder = 1;
    int groupOrder = 1;
    long groupID = 0;
    for (DashboardItem item : data) {
      if (item instanceof DashboardHeaderItem) {
        sensorOrder = 1;
        groupID = ((DashboardHeaderItem) item).getUserTag();
        DBGroup dbGroup = dbManager.getGroupById(groupID);
        if (dbGroup != null) {
          dbGroup.setOrder(groupOrder);
          ++groupOrder;
          dbGroup.update();
        }
      }
      else if (item instanceof DashboardGenericSensorItem && !onlyHeader) {
        DBSensor dbSensor = dbManager.getSensorById(((DashboardGenericSensorItem)item).getItemID());
        dbSensor.setOrder(sensorOrder);
        if (groupID > 0) {
          dbSensor.setGroupID(groupID);
        } else {
          dbSensor.setGroupID(null);
        }
        dbSensor.update();
        ++sensorOrder;
      }
    }
  }

  private static final Comparator<DashboardGenericSensorItem> COMPARATOR = new Comparator<DashboardGenericSensorItem>() {
    @Override
    public int compare(DashboardGenericSensorItem t1, DashboardGenericSensorItem t2) {
      if (t1.getOrder() > t2.getOrder()) {
        return 1;
      } else if (t1.getOrder() < t2.getOrder()){
        return -1;
      } else {
        return 0;
      }
    }
  };

  private void handleEditBtnClick(int position) {
    if (listener == null) {
      return;
    }
    listener.onEdit(position);
  }

  private int getIconBySensor(Sensor sensor) {
    if (sensor instanceof TemperatureSensor) {
      return R.drawable.ic_temperature_64dp;
    } else if (sensor instanceof HumiditySensor) {
      return R.drawable.ic_humidity_64dp;
    } else if (sensor instanceof PressureSensor) {
      return R.drawable.ic_pressure_sensor_64dp;
    } else {
      return R.drawable.ic_temperature_64dp;
    }
  }


}
