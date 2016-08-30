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

package com.imgtec.creator.lumpy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
