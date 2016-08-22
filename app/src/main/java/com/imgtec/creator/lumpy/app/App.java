package com.imgtec.creator.lumpy.app;

import android.app.Application;

public class App extends Application {

  private ApplicationComponent appComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    this.appComponent = ApplicationComponent.Initializer.init(this);
  }

  public ApplicationComponent getAppComponent() {
    return appComponent;
  }
}
