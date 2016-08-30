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
import android.content.SharedPreferences;

/**
 * Keeps persistent data used by {@link ApiService}
 */
class ApiServicePreferences {

  private static final String PREFS_NAME = "API_SERVICE_PREFS";
  private static final String REFRESH_TOKEN_KEY = "refresh_token";
  private static final String ACCESS_TOKEN_EXPIRY_KEY = "access_token_expiry";
  private static final String ACCESS_TOKEN_KEY = "access_token_key";
  private static final String USER_NAME_KEY = "user_name_key";
  private static final String AUTO_LOGIN_KEY = "auto_login";

  private final SharedPreferences sharedPreferences;

  ApiServicePreferences(Context appContext) {
    this.sharedPreferences = appContext.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  /**
   * Saves refresh token in shared preferences. Refresh token is used to log user in automatically
   * without the need of providing user name and password.
   * @param refreshToken that be saved
   */
  void saveRefreshToken(String refreshToken) {
    sharedPreferences.edit().putString(REFRESH_TOKEN_KEY, refreshToken).apply();
  }

  /**
   * Return previously saved refresh token.
   * @return refresh token or empty string if there is no refresh tken saved.
   */
  String getRefreshToken() {
    return sharedPreferences.getString(REFRESH_TOKEN_KEY, "");
  }

  /**
   * Saves access token expiry time in shared preferences. Access token expiry time is used
   * in {@link ApiService} logic to automatically renew access token when it expires.
   * @param expiryTime represented as number of  milliseconds since 1970.01.01
   */
  void saveAccessTokenExpiryTime(long expiryTime) {
    sharedPreferences.edit().putLong(ACCESS_TOKEN_EXPIRY_KEY, expiryTime).apply();
  }

  /**
   * Return previously saved access token expiry time.
   * @return expiry time represented as number of milliseconds since 1970.01.01 or 0 if no expiry time has been saved
   */
  long getAccessTokenExpiryTime() {
    return sharedPreferences.getLong(ACCESS_TOKEN_EXPIRY_KEY, 0);
  }

  /**
   * Saves access token in shared preferences. Access token is used to sign {@link ApiService} request
   * with proper Authorization header.
   * @param accessToken to be saved
   */
  void saveAccessToken(String accessToken) {
    sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, accessToken).apply();
  }

  /**
   * Returns previously saved access token. Access token is used to sign {@link ApiService} request
   * with proper Authorization header.
   * @return previously saved access token or empty string if none has been saved.
   */
  String getAccessToken() {
    return sharedPreferences.getString(ACCESS_TOKEN_KEY, "");
  }

  /**
   * Saves user name in shared preferences for later use in app. User name comes from server when user logs in.
   * @param userName to be saved
   */
  void saveUserName(String userName) {
    sharedPreferences.edit().putString(USER_NAME_KEY, userName).apply();
  }

  /**
   * Returns previously saved user name.
   * @return user name or empty String if no user name has been saved.
   */
  String getUserName() {
    return sharedPreferences.getString(USER_NAME_KEY, "");
  }

  /**
   * Saves whether app should try to auto login user using refresh token on next launch.
   */
  void setAutologin(boolean autologin) {
    sharedPreferences.edit().putBoolean(AUTO_LOGIN_KEY, autologin).apply();
  }

  /**
   * Returns auto login status. Auto login set to true means that app will try to log user in
   * automatically using saved refresh token.
   * @return true if auto login is enabled, false otherwise.
   */
  boolean getAutologin() {
    return sharedPreferences.getBoolean(AUTO_LOGIN_KEY, false);
  }


}
