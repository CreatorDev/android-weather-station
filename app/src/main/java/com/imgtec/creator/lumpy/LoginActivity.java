package com.imgtec.creator.lumpy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.imgtec.creator.lumpy.app.ActivitiesAndFragmentsHelper;
import com.imgtec.creator.lumpy.app.ApplicationComponent;
import com.imgtec.creator.lumpy.fragments.LoginFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity implements LoginFragment.LoginFragmentListener {

  private static final String TAG = LoginActivity.class.getSimpleName();
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginActivity.class);

  @Inject ActivitiesAndFragmentsHelper activitiesAndFragmentsHelper;

  private LoginFragment loginFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loginFragment = LoginFragment.newInstance();
    activitiesAndFragmentsHelper.replaceFragment(getSupportFragmentManager(), loginFragment, R.id.container, false);
  }

  @Override
  protected void setComponent(ApplicationComponent appComponent) {
    appComponent.inject(this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Uri uri = intent.getData();
    if (uri!=null){
      handleResponseIntent(uri);
    }
  }

  private void handleResponseIntent(final Uri uri) {
    // propagate returned uri to LoginFragment
    loginFragment.handleResponseIntent(uri);
  }

  @Override
  public void onLoginSucceeded() {
    activitiesAndFragmentsHelper.startActivityAndFinishPreviousOne(this, MainActivity.class);
  }
}
