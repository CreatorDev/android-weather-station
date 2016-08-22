package com.imgtec.creator.lumpy.db;

import android.content.Context;

import com.imgtec.di.PerApp;

import dagger.Module;
import dagger.Provides;


@Module
public class DBModule {

  @Provides
  @PerApp
  DBManager provideDBManager(Context appContext) {
    return new DBManager(appContext);
  }
}
