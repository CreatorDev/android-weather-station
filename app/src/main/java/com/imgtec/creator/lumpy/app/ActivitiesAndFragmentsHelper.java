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
