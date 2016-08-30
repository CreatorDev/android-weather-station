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

package com.imgtec.creator.lumpy.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.imgtec.creator.lumpy.R;
import com.imgtec.creator.lumpy.adapters.DashboardAdapter;
import com.imgtec.creator.lumpy.adapters.SimpleItemTouchHelperCallback;
import com.imgtec.creator.lumpy.app.ApplicationComponent;
import com.imgtec.creator.lumpy.data.api.ApiService;
import com.imgtec.creator.lumpy.data.api.ResourceManager;
import com.imgtec.creator.lumpy.data.api.Sensor;
import com.imgtec.creator.lumpy.data.dashboard.DashboardHeaderItem;
import com.imgtec.creator.lumpy.data.dashboard.DashboardItem;
import com.imgtec.creator.lumpy.db.DBManager;
import com.imgtec.creator.lumpy.views.DashboardGridLayoutManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;

public class DashboardFragment extends BaseFragment implements ResourceManager.ResourceManagerListener, DashboardAdapter.DashboardAdapterListener {

  public static final String TAG = "DashboardFragment";
  private static final Logger LOGGER = LoggerFactory.getLogger(DashboardFragment.class);


  @Inject ApiService apiService;
  @Inject ResourceManager resourceManager;
  @Inject DBManager dbManager;

  @BindView(R.id.dashboard_rv) RecyclerView recyclerView;

  private DashboardAdapter adapter;

  /**
   * Flag determining whether this fragment is currently in edit mode. Different toolbar is being
   * displayed depending on that flag.
   */
  private boolean editMode = false;


  public static DashboardFragment newInstance() {

    Bundle args = new Bundle();

    DashboardFragment fragment = new DashboardFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.frag_dashboard, container, false);
    setHasOptionsMenu(true);
    return v;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initUI();
  }

  @Override
  public void onResume() {
    super.onResume();
    resourceManager.addListener(this);
    adapter.setEventListener(this);
  }

  @Override
  public void onPause() {
    adapter.setEventListener(null);
    resourceManager.removeListener(this);
    super.onPause();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    int menuToInflate = editMode ? R.menu.menu_dashboard_edit_mode : R.menu.menu_dashboard;
    inflater.inflate(menuToInflate, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add_group:
        showAddGroupDialog();
        return true;
      case R.id.edit_mode:
        editMode = true;
        adapter.setEditMode(true);
        getActivity().invalidateOptionsMenu();
        return true;
      case R.id.action_done:
        editMode = false;
        adapter.setEditMode(false);
        getActivity().invalidateOptionsMenu();
        return true;
    }
    return false;
  }

  @Override
  protected void setComponent(ApplicationComponent appComponent) {
    appComponent.inject(this);
  }

  private void initUI() {

    adapter = new DashboardAdapter(getContext(), dbManager);

    final GridLayoutManager layoutManager = new DashboardGridLayoutManager(getContext(), getResources().getInteger(R.integer.dashboard_span_count), GridLayoutManager.VERTICAL, false);

    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {

        switch (adapter.getItem(position).getItemViewType()) {
          case DashboardItem.VIEW_TYPE_GENERIC_SENSOR:
            return 1;
          case DashboardItem.VIEW_TYPE_HEADER:
            return layoutManager.getSpanCount();
          default:
            return -1;
        }
      }
    });
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);


    ItemTouchHelper.Callback callback =
        new SimpleItemTouchHelperCallback(adapter);
    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
    touchHelper.attachToRecyclerView(recyclerView);

  }

  @Override
  public void onNewData(Map<String, List<Sensor>> newData) {
    adapter.setData(newData);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onEdit(int position) {
    DashboardItem item = adapter.getItem(position);
    showEditGroupDialog((DashboardHeaderItem) item);
  }

  @Override
  public void onRemove(int position) {
    DashboardItem item = adapter.getItem(position);
    showGorupDeleteConfirmationDialog((DashboardHeaderItem) item);
  }



  private void showAddGroupDialog() {
    final EditText editText = new EditText(getContext());

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.fab_margin), 0, getResources().getDimensionPixelSize(R.dimen.fab_margin), 0);
    editText.setLayoutParams(layoutParams);
    LinearLayout linearLayout = new LinearLayout(getContext());
    linearLayout.addView(editText);
    new AlertDialog.Builder(getContext())
        .setMessage(R.string.set_group_name)
        .setView(linearLayout)
        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            dbManager.addGroup(editText.getText().toString());
            adapter.refresh();
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create()
        .show();
  }

  private void showEditGroupDialog(final DashboardHeaderItem item) {
    final EditText editText = new EditText(getContext());
    editText.setText(item.getTitle());
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.fab_margin), 0, getResources().getDimensionPixelSize(R.dimen.fab_margin), 0);
    editText.setLayoutParams(layoutParams);
    LinearLayout linearLayout = new LinearLayout(getContext());
    linearLayout.addView(editText);
    new AlertDialog.Builder(getContext())
        .setMessage(R.string.set_group_name)
        .setView(linearLayout)
        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            item.setTitle(editText.getText().toString());
            dbManager.updateGroup(item.getUserTag(), editText.getText().toString());
            adapter.notifyGroupNameChanged(item);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create()
        .show();
  }

  private void showGorupDeleteConfirmationDialog(final DashboardHeaderItem item) {
    new AlertDialog.Builder(getContext())
        .setMessage("Do you really want to delete " + item.getTitle() + " group?")
        .setTitle(R.string.confirm_group_removal)
        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dbManager.deleteGroup(item.getUserTag());
            adapter.notifyGroupRemoved(item);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        })
        .create()
        .show();
  }


}
