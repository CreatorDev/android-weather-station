package com.imgtec.creator.lumpy;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.imgtec.creator.lumpy.app.ApplicationComponent;
import com.imgtec.creator.lumpy.data.api.ApiService;
import com.imgtec.creator.lumpy.fragments.DashboardFragment;
import com.imgtec.creator.lumpy.app.ActivitiesAndFragmentsHelper;

import javax.inject.Inject;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

  @Inject ActivitiesAndFragmentsHelper activitiesAndFragmentsHelper;
  @Inject ApiService apiService;

  @BindView(R.id.toolbar) Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activitiesAndFragmentsHelper.replaceFragment(getSupportFragmentManager(), DashboardFragment.newInstance(), R.id.container, false);
  }

  @Override
  protected void setComponent(ApplicationComponent appComponent) {
    appComponent.inject(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_logout:
        apiService.logout();
        activitiesAndFragmentsHelper.startActivityAndFinishPreviousOne(this, LoginActivity.class);
        return true;
    }
    return false;
  }
}
