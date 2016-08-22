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

package com.imgtec.creator.lumpy.data.api;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imgtec.creator.lumpy.app.App;
import com.imgtec.creator.lumpy.db.DBManager;
import com.imgtec.di.PerApp;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 *
 */
@Module
public class ApiModule {

  private static final long CACHE_DISK_SIZE = 50 * 1024 * 1024;

  @Provides
  @PerApp
  @Named("apiService")
  HttpUrl provideApiServiceURL() {
    return HttpUrl.parse("https://deviceserver.creatordev.io");
  }

  @Provides
  @PerApp
  Gson provideGson() {
    Gson gson = new GsonBuilder()
        .create();
    return gson;
  }

  @Provides
  @PerApp
  RequestBuilder provideRequestBuilder() {
    return new RequestBuilder();
  }

  @Provides
  @PerApp
  ApiServicePreferences provideApiServicePreferences(Context appContext) {
    return new ApiServicePreferences(appContext);
  }

  @Provides
  @PerApp
  OAuthInterceptor provideOauthInterceptor(ApiServicePreferences preferences) {
    return new OAuthInterceptor(preferences);
  }

  @Provides
  @PerApp
  RequestExecutor provideRequestExecutor(OkHttpClient client, Gson gson) {
    return new RequestExecutor(client, gson);
  }

  @Provides
  @PerApp
  ApiServiceAuthenticator provideApiServiceAuthenticator(ApiServicePreferences preferences) {
    return new ApiServiceAuthenticator(preferences);
  }

  @Provides
  @PerApp
  OkHttpClient provideOkHttpClient(App app, OAuthInterceptor oauthInterceptor, ApiServiceAuthenticator authenticator) {
    File cacheDir = new File(app.getCacheDir(), "http");
    Cache cache = new Cache(cacheDir, CACHE_DISK_SIZE);

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient okHttpClient = new OkHttpClient
        .Builder()
        .cache(cache)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(oauthInterceptor)
        .authenticator(authenticator)
        .build();
    return okHttpClient;
  }

  @Provides
  @PerApp
  ApiService provideApiService(final Context appContext,
                               @Named("apiService") final HttpUrl url,
                               final RequestBuilder requestBuilder,
                               final RequestExecutor requestExecutor,
                               final Gson gson) {
    return new ApiService(appContext, url, requestBuilder, requestExecutor, gson);
  }

  @Provides
  @PerApp
  ResourceManager provideResourceManager(ApiService apiService, @Named("Main") Handler handler, DBManager dbManager) {
    return new ResourceManager(apiService, handler, dbManager);
  }
}
