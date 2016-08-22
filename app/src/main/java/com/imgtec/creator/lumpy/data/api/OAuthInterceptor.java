package com.imgtec.creator.lumpy.data.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An OkHttp interceptor that adds 'Authorization' header to requests.
 */
class OAuthInterceptor implements Interceptor {

  private final ApiServicePreferences preferences;

  OAuthInterceptor(ApiServicePreferences preferences) {
    this.preferences = preferences;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    long accesTokenExpiryTime = preferences.getAccessTokenExpiryTime();
    if (((Boolean) originalRequest.tag())) {
      if (accesTokenExpiryTime <= System.currentTimeMillis() - 10 * 1000) {
        return new Response.Builder().code(401).build();
      }
      Request authorisedRequest = originalRequest.newBuilder()
          .header("Authorization", "Bearer " + preferences.getAccessToken())
          .build();
      return chain.proceed(authorisedRequest);
    }
    return chain.proceed(originalRequest);
  }


}
