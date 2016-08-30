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


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.imgtec.creator.lumpy.BaseActivity;
import com.imgtec.creator.lumpy.R;
import com.imgtec.creator.lumpy.app.App;
import com.imgtec.creator.lumpy.app.ApplicationComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Base fragment that enables Dagger DI and Butterknife View injection in it.
 * Abstract method {@link #setComponent(ApplicationComponent)} should be used to call
 *
 * *IMPORTANT*
 * Injected objects cannot be used before {@link #onActivityCreated(Bundle)} has been called
 *
 * *IMPORTANT 2*
 * This fragment is designed to be used with {@link BaseActivity} only.
 */
public abstract class BaseFragment extends Fragment {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseFragment.class);

  private Unbinder unbinder;
  private ProgressDialog progressDialog;
  private BaseActivity baseActivity;

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (!(context instanceof BaseActivity)) {
      throw new IllegalArgumentException(BaseFragment.class.getSimpleName() + " is designed to be used with " + BaseActivity.class.getSimpleName() + " only.");
    }
    baseActivity = (BaseActivity)context;

  }

  @Override
  public void onDetach() {
    baseActivity = null;
    super.onDetach();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setComponent(((App)getActivity().getApplication()).getAppComponent());
    setToolbarVisible(hasToolbar());
    if (hasToolbar()) {
      baseActivity.getToolbar().setTitle(getTitle());
    }
  }

  protected abstract void setComponent(ApplicationComponent appComponent);

  /**
   * 
   * @return
   */
  public boolean hasToolbar() {
    return true;
  }

  /**
   * Call this method in subclass to either show or hide toolbar. Toolbar is shown by default.
   * @param isVisible when set to true toolbar is shown, otherwise toolbar is hidden
   */
  protected void setToolbarVisible(boolean isVisible) {
    if (isDetached()) {
      LOGGER.warn("Trying to set toolbar visibility on fragment detached from it's activity. No action done.");
      return;
    }
    baseActivity.getToolbar().setVisibility(isVisible ? View.VISIBLE : View.GONE);
  }

  /**
   * Call this method to set fragment title. By default title is set to app name.
   */
  protected String getTitle() {
    return getContext().getString(R.string.app_name);
  }

  /**
   * Displays a dialog with indeterminate progressbar with specified title and message
   * @param title title of the dialog
   * @param msg message shown on dialog
   */
  protected void showProgressBar(String title, String msg) {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(getContext());
      progressDialog.setIndeterminate(true);
    }
    progressDialog.setTitle(title);
    progressDialog.setMessage(msg);
    progressDialog.show();
  }

  /**
   * Hides progress dialog if shown.
   */
  protected void hideProgressDialog() {
    if (progressDialog == null) {
      return;
    }
    progressDialog.dismiss();
  }


}
