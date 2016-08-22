package com.imgtec.creator.lumpy;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.imgtec.creator.lumpy.app.App;
import com.imgtec.creator.lumpy.app.ApplicationComponent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

  private Unbinder unbinder;
  @BindView(R.id.toolbar) Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setComponent(((App)getApplication()).getAppComponent());
    setContentView(R.layout.actv_base);
    unbinder = ButterKnife.bind(this);
    setSupportActionBar(toolbar);
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }

  protected abstract void setComponent(ApplicationComponent appComponent);

  public final Toolbar getToolbar() {
    return toolbar;
  }
}
