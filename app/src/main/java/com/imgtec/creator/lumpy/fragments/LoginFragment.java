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


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.imgtec.creator.lumpy.MainActivity;
import com.imgtec.creator.lumpy.R;
import com.imgtec.creator.lumpy.app.ActivitiesAndFragmentsHelper;
import com.imgtec.creator.lumpy.app.ApplicationComponent;
import com.imgtec.creator.lumpy.data.api.ApiService;
import com.imgtec.creator.lumpy.data.api.exceptions.NetworkException;
import com.imgtec.creator.lumpy.data.api.exceptions.NotFoundException;
import com.imgtec.creator.lumpy.data.api.exceptions.UnauthorizedException;
import com.imgtec.creator.lumpy.data.api.pojo.CreatorVoid;
import com.imgtec.creator.lumpy.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;



public class LoginFragment extends BaseFragment {

  public static final String TAG = LoginFragment.class.getSimpleName();
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginFragment.class);


  public interface LoginFragmentListener {
    void onLoginSucceeded();
  }

  @BindView(R.id.logIn) Button logIn;
  @BindView(R.id.keepLoggedIn) CheckBox keepLoggedIn;

  @Inject ApiService apiService;
  @Inject @Named("Main") Handler handler;
  @Inject ActivitiesAndFragmentsHelper activitiesAndFragmentsHelper;

  boolean allowAutologin = true;
  private Runnable loginTimeoutRunnable;
  private LoginFragmentListener loginFragmentListener;


  public static LoginFragment newInstance() {
    return new LoginFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View view = inflater.inflate(R.layout.frag_log_in, container, false);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    keepLoggedIn.setChecked(apiService.isAutoLoginEnabled());
  }

  @Override
  public void onResume() {
    super.onResume();
    initListeners();
    if (apiService.isAutoLoginEnabled() && allowAutologin) {
      allowAutologin = false;
      loginUsingRefreshToken();
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (!(context instanceof LoginFragmentListener)) {
      throw new IllegalArgumentException(context.getClass().getSimpleName() + " does not implement " + LoginFragmentListener.class.getSimpleName());
    }
    loginFragmentListener = (LoginFragmentListener) context;
  }

  @Override
  protected void setComponent(ApplicationComponent appComponent) {
    appComponent.inject(this);
  }

  @Override
  public boolean hasToolbar() {
    return false;
  }

  @Override
  public void onDetach() {
    loginFragmentListener = null;
    super.onDetach();
  }



  private void login() {

    final String client_id ="41e2bb5a-8e66-43dc-a4bb-31b7a9041b8f";
    final Uri redirectUri = Uri.parse("io.creatordev.iup:/callback");
    final String nonce = UUID.randomUUID().toString();
    final String state = "dummy_state";

    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
        Uri.parse("https://id.creatordev.io/oauth2/auth?"+
            "client_id=" + client_id + "&" +
            "scope=core+openid+offline&" +
            "redirect_uri=" + redirectUri + "&" +
            "state=" + state + "&" +
            "nonce=" + nonce + "&" +
            "response_type=id_token"));
    browserIntent.setFlags(browserIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
    startActivity(browserIntent);

  }

  private void loginUsingRefreshToken() {
    showProgressBar("Please wait", "Logging in...");
    loginTimeoutRunnable = new Runnable() {
      @Override
      public void run() {
        //hideProgress();
        activitiesAndFragmentsHelper.showToast(R.string.error_network);
        handler.removeCallbacks(this);
        hideProgressDialog();
      }
    };
    handler.postDelayed(loginTimeoutRunnable, Constants.SIXTY_SECONDS_MILLIS / 2);


    ListenableFuture<CreatorVoid> future = apiService.login();
    Futures.addCallback(future, new FutureCallback<CreatorVoid>() {
      @Override
      public void onSuccess(final CreatorVoid result) {
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            //hideProgress();
            handler.removeCallbacks(loginTimeoutRunnable);
            afterLogin();
            hideProgressDialog();
          }
        });
      }

      @Override
      public void onFailure(Throwable t) {
        handleLoginFailure(t);
      }
    });
  }

  public void handleLoginFailure(final Throwable t) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        //hideProgress();
        handler.removeCallbacks(loginTimeoutRunnable);
        int errorStringID;
        if (t instanceof UnauthorizedException) {
          errorStringID = R.string.error_login_unauthorized;
        } else if (t instanceof NotFoundException) {
          errorStringID = R.string.error_login_not_found;
        } else if (t instanceof NetworkException) {
          errorStringID = R.string.error_network;
        } else {
          errorStringID = R.string.error_unknown;
        }

        Toast.makeText(getContext(), errorStringID, Toast.LENGTH_SHORT).show();
      }
    });
  }


  private void afterLogin() {
    handler.post(new Runnable() {
      @Override
      public void run() {

        final Activity activity = getActivity();
        if (activity != null) {
          activitiesAndFragmentsHelper.startActivityAndFinishPreviousOne(activity, MainActivity.class);
        }

      }
    });
  }


  private void initListeners() {
    logIn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //showProgress(appContext.getString(R.string.logging_in));
        login();
      }
    });

  }

  public void handleResponseIntent(Uri uri) {
    showProgressBar("Please wait", "Logging in...");
    String token = uri.toString().split("#")[1].split("=")[1];
    ListenableFuture<CreatorVoid> future = apiService.login(token, keepLoggedIn.isChecked());
    Futures.addCallback(future, new FutureCallback<CreatorVoid>() {
      @Override
      public void onSuccess(CreatorVoid result) {
        if (LoginFragment.this.isDetached()) {
          LOGGER.warn("Fragment detached. Could not finalize login operation.");
        }
        loginFragmentListener.onLoginSucceeded();
        hideProgressDialog();
      }

      @Override
      public void onFailure(Throwable t) {
        if (LoginFragment.this.isAdded()) {
          handleLoginFailure(t);
          hideProgressDialog();
        }
      }
    });
  }
}

