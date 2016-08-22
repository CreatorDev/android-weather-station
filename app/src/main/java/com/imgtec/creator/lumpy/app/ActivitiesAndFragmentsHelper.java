package com.imgtec.creator.lumpy.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Provides utility methods for managing activities and fragments.
 */
public class ActivitiesAndFragmentsHelper {

  private Context appContext;
  private Handler handler;

  ActivitiesAndFragmentsHelper(Context appContext, Handler handler) {
    this.handler = handler;
    this.appContext = appContext;
  }

  /**
   * Replaces fragment in specified container (if any) with the provided one.
   * @param mgr FragmentManager in context of which this operation will be performed
   * @param fragment new fragment that will replace the old one
   * @param container in to which new fragment will be added
   * @param addToBackStack whether this operation should be saved in backstack
   */
  public void replaceFragment(FragmentManager mgr, Fragment fragment, @IdRes int container, boolean addToBackStack) {
    FragmentTransaction transaction = mgr.beginTransaction();
    transaction.replace(container, fragment);
    if (addToBackStack) {
      transaction.addToBackStack(fragment.getClass().getSimpleName());
    }
    transaction.commit();
  }


  /**
   * Starts new activity and finishes previous activity that started it.
   * @param activity to finish
   * @param targetActivity activity to start
   */
  public void startActivityAndFinishPreviousOne(Activity activity, Class<? extends AppCompatActivity> targetActivity) {
    activity.startActivity(new Intent(activity, targetActivity));
    activity.finish();
  }

  /**
   * Shows Toast message. Can be executed from any thread as operation is dispatched to main thread.
   * @param message string resource id of message to display
   */
  public void showToast(@StringRes final int message) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
      }
    });
  }
}
