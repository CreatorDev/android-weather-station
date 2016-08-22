package com.imgtec.creator.lumpy.data.api;


import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Reacts on 401 response from server and requests for new OAuth token from Device Server.
 */
class ApiServiceAuthenticator implements Authenticator {

  private final ApiServicePreferences preferences;

  ApiServiceAuthenticator(ApiServicePreferences preferences) {
    this.preferences = preferences;
  }

  @Override
  public Request authenticate(Route route, Response response) throws IOException {
    if (!response.request().url().toString().contains("oauth")) {
      preferences.saveAccessToken("");
      preferences.saveAccessTokenExpiryTime(0);
      return response.request();
    }
    return null;
  }

}
