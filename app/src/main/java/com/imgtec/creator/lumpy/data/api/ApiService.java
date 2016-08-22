/*
 * Copyright (c) 2016, Imagination Technologies Limited and/or its affiliated group companies
 * and/or licensors
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *     and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *     endorse or promote products derived from this software without specific prior written
 *     permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.imgtec.creator.lumpy.data.api;


import android.content.Context;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imgtec.creator.lumpy.data.api.exceptions.DeviceServerException;
import com.imgtec.creator.lumpy.data.api.exceptions.NetworkException;
import com.imgtec.creator.lumpy.data.api.exceptions.NotFoundException;
import com.imgtec.creator.lumpy.data.api.exceptions.UnauthorizedException;
import com.imgtec.creator.lumpy.data.api.exceptions.UnknownException;
import com.imgtec.creator.lumpy.data.api.pojo.Api;
import com.imgtec.creator.lumpy.data.api.pojo.Bootstrap;
import com.imgtec.creator.lumpy.data.api.pojo.Client;
import com.imgtec.creator.lumpy.data.api.pojo.Clients;
import com.imgtec.creator.lumpy.data.api.pojo.Configuration;
import com.imgtec.creator.lumpy.data.api.pojo.CreatorVoid;
import com.imgtec.creator.lumpy.data.api.pojo.EmptyResponse;
import com.imgtec.creator.lumpy.data.api.pojo.IDPResult;
import com.imgtec.creator.lumpy.data.api.pojo.Identities;
import com.imgtec.creator.lumpy.data.api.pojo.Instances;
import com.imgtec.creator.lumpy.data.api.pojo.OauthToken;
import com.imgtec.creator.lumpy.data.api.pojo.ObjectType;
import com.imgtec.creator.lumpy.data.api.pojo.ObjectTypes;
import com.imgtec.creator.lumpy.data.api.pojo.PSK;
import com.imgtec.creator.lumpy.data.api.pojo.PSKs;
import com.imgtec.creator.lumpy.data.api.pojo.Pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Encapsulates interacting with Creator Device Server. All methods doing http requests return {@link ListenableFuture}
 * that can by used by developer in either synchronous or asynchronous way.
 *
 * This class uses {@link OkHttpClient} to make http requests and {@link Gson} to serialize/deserialize payload.
 */
public class ApiService {

  private final Gson gson;
  private final String deviceServerURL;

  private final ApiServicePreferences preferences;
  private final RequestBuilder requestBuilder;
  private final RequestExecutor requestExecutor;

  private ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());


  ApiService(Context appContext, HttpUrl deviceServerUrl, RequestBuilder requestBuilder, RequestExecutor requestExecutor, Gson gson) {
    this.deviceServerURL = deviceServerUrl.toString();
    this.preferences = new ApiServicePreferences(appContext);
    this.requestBuilder = requestBuilder;
    this.requestExecutor = requestExecutor;
    this.gson = gson;
  }

  /**
   * Checks whether auto login option is enabled.
   * @return true if auto login is enabled, false otherwise
   */
  public boolean isAutoLoginEnabled() {
    return preferences.getAutologin();
  }


  /**
   * Tries to login use using previously saved refresh token. Should be used when
   * {@link #isAutoLoginEnabled()} returns true.
   */
  public ListenableFuture<CreatorVoid> login() {
    final Runner<CreatorVoid> runner = new Runner<CreatorVoid>() {
      @Override
      public CreatorVoid action() {
        return loginInternal(preferences.getRefreshToken());
      }

    };
    return executor.submit(new Callable<CreatorVoid>() {
      @Override
      public CreatorVoid call() throws Exception {
        return runner.call();
      }
    });
  }

  /**
   * Logs user in to Device Server using token returned by browser after SSO login.
   * Once logged in, 'refresh token is saved in SharedPreferences and future attempts to login
   * should be sent to overloaded version of ths method {@link #login()}.
   * @param token returned by browser after SSO login.
   * @return {@link CreatorVoid} pojo that represents an void result.
   * @throws UnauthorizedException when password is invalid
   * @throws NetworkException in case of communication error
   */
  public ListenableFuture<CreatorVoid> login(final String token, final boolean rememberMe) {
    final Runner<CreatorVoid> runner = new Runner<CreatorVoid>() {
      @Override
      public CreatorVoid action() {
        return loginInternal(token, rememberMe);
      }

    };
    return executor.submit(new Callable<CreatorVoid>() {
      @Override
      public CreatorVoid call() throws Exception {
        return runner.call();
      }
    });
  }

  public void logout() {
    preferences.saveRefreshToken("");
    preferences.saveUserName("");
    preferences.saveAccessTokenExpiryTime(0);
    preferences.saveAccessToken("");
    preferences.setAutologin(false);
  }

  /**
   * Looks if client with specified is connected to Device Server and returns it.
   * @param clientName name of client to get
   * @return client object
   * @throws NotFoundException when there is no client with specified name connected to server
   * @throws NetworkException in case of communication error.
   */
  public ListenableFuture<Client> getClient(final String clientName) {
    final Runner<Client> runner = new Runner<Client>() {
      @Override
      public Client action() {
        return getClientInternal(clientName);
      }
    };

    return executor.submit(new Callable<Client>() {
      @Override
      public Client call() throws Exception {
        return runner.call();
      }
    });
  }

  /**
   * Gets list of client currently connected to Device Server.
   * It's developer responsibility to handle paging.
   * @param startIndex
   * @param pageSize maximum number of items that should be returned
   * @return List of connected clients or empty list if no clients are online.
   * @throws NetworkException in case of communication error
   * @return {@link Clients} object that contains list of connected clients as well as {@link com.imgtec.creator.lumpy.data.api.pojo.PageInfo} object
   */
  public ListenableFuture<Clients> getClients(final int startIndex, final int pageSize) {

    final Runner<Clients> runner = new Runner<Clients>() {
      @Override
      public Clients action() {
        return clientsInternal(startIndex, pageSize);
      }

    };

    return executor.submit(new Callable<Clients>() {
      @Override
      public Clients call() throws Exception {
        return runner.call();
      }
    });
  }

  /**
   * Generates new PSK and returns it.
   * @return newly generated PSK
   * @throws NetworkException in case of communication error.
   */
  public ListenableFuture<PSK> generatePSK() {
    final Runner<PSK> runner = new Runner<PSK>() {
      @Override
      public PSK action() {
        return generatePSKInternal();
      }
    };

    return executor.submit(new Callable<PSK>() {
      @Override
      public PSK call() throws Exception {
        return runner.call();
      }
    });
  }

  /**
   * Returns Bootstrap Server information
   * @return {@link Bootstrap} pojo containing information about Bootstrap Server.
   */
  public ListenableFuture<Bootstrap> getBootstrap() {
    final Runner<Bootstrap> runner = new Runner<Bootstrap>() {
      @Override
      public Bootstrap action() {
        return getBootstrapInternal();
      }
    };

    return executor.submit(new Callable<Bootstrap>() {
      @Override
      public Bootstrap call() throws Exception {
        return runner.call();
      }
    });
  }


  /**
   * Returns {@link Instances} object that contains list of queried object instances.
   * In order to properly deserialize retrieved items {@link TypeToken} must be provided.
   * @param client instance of client from which objects will be queried.
   * @param objectID in IPSO standard
   * @param typeToken
   * @param <T> type of item
   * @return
   */
  public <T extends Pojo> ListenableFuture<Instances<T>> getInstances(final Client client, final int objectID, final TypeToken<Instances<T>> typeToken) {

    final Runner<Instances<T>> runner = new Runner<Instances<T>>() {
      @Override
      public Instances<T> action() {
        return getInstancesInternal(client, objectID, typeToken);
      }
    };

    return executor.submit(new Callable<Instances<T>>() {
      @Override
      public Instances<T> call() throws Exception {
        return runner.call();
      }
    });
  }

  /**
   * Updates instance of specified object on Device Server.
   * @param client instance
   * @param objectID in IPSO standard
   * @param instanceID number of object instance
   * @param data data to send
   * @param typeToken
   * @param <T>
   * @return
   */
  public <T extends Pojo> ListenableFuture<EmptyResponse> updateInstance(final Client client, final int objectID, final int instanceID, final T data, final TypeToken<T> typeToken) {
    final Runner<EmptyResponse> runner = new Runner<EmptyResponse>() {
      @Override
      public EmptyResponse action() {
        updateInstanceInternal(client, objectID, instanceID, data, typeToken);
        return new EmptyResponse();
      }
    };

    return executor.submit(new Callable<EmptyResponse>() {
      @Override
      public EmptyResponse call() throws Exception {
        return runner.call();
      }
    });
  }

  private CreatorVoid loginInternal(String token, boolean rememberMe) {
    clearAccessToken();
    Map<String, String> params = new HashMap<>();

    params.put("id_token", token);
    Request request = requestBuilder.buildRequest("https://developer-id.flowcloud.systems", null, "POST", null, params, null, false);
    IDPResult idpResult = null;

    idpResult = requestExecutor.execute(request, IDPResult.class);

    if (idpResult == null) {
      throw new UnauthorizedException();
    }

    request = requestBuilder.buildRequest(deviceServerURL, null, "GET", null, null, null, false);
    Api api = requestExecutor.execute(request, Api.class);

    params = new HashMap<>();
    params.put("username", idpResult.getKey());
    params.put("password", idpResult.getSecret());
    params.put("grant_type", "password");
    request = requestBuilder.buildRequest(api.getLinkByRel("authenticate").getHref(), null, "POST", null, params, null, false);
    OauthToken oauthToken = requestExecutor.execute(request, OauthToken.class);
    saveAccessToken(oauthToken);
    preferences.saveUserName(idpResult.getName());
    if (rememberMe) {
      preferences.setAutologin(true);
    }

    return new CreatorVoid();
  }

  private CreatorVoid loginInternal(String refreshToken) {
    clearAccessToken();


    Request request = requestBuilder.buildRequest(deviceServerURL, null, "GET", null, null, null, false);
    Api api = requestExecutor.execute(request, Api.class);

    Map<String,String> params = new HashMap<>();
    params.put("refresh_token", refreshToken);
    params.put("grant_type", "refresh_token");
    request = requestBuilder.buildRequest(api.getLinkByRel("authenticate").getHref(), null, "POST", null, params, null, false);
    OauthToken oauthToken = requestExecutor.execute(request, OauthToken.class);
    saveAccessToken(oauthToken);

    return new CreatorVoid();
  }

  private Api requestDeviceServerApi(boolean needAuthorization) {
    Request request = requestBuilder.buildRequest(deviceServerURL, null, "GET", null, null, null, needAuthorization);
    return requestExecutor.execute(request, Api.class);
  }

  private PSK generatePSKInternal() {
    Api api = requestDeviceServerApi(true);

    Request request = requestBuilder.buildRequest(api.getLinkByRel("identities").getHref(), null, "GET", null, null, null, true);
    Identities identities = requestExecutor.execute(request, Identities.class);

    request = requestBuilder.buildRequest(identities.getLinkByRel("psk").getHref(), null, "GET", null, null, null, true);
    PSKs psks = requestExecutor.execute(request, PSKs.class);

    request = requestBuilder.buildRequest(psks.getLinkByRel("add").getHref(), null, "POST", null, null, "", true);
    PSK psk = requestExecutor.execute(request, PSK.class);
    return psk;
  }

  private Bootstrap getBootstrapInternal() {
    Api api = requestDeviceServerApi(true);

    Request request = requestBuilder.buildRequest(api.getLinkByRel("configuration").getHref(), null, "GET", null, null, null, true);
    Configuration configuration = requestExecutor.execute(request, Configuration.class);

    request = requestBuilder.buildRequest(configuration.getLinkByRel("bootstrap").getHref(), null, "GET", null, null, null, true);
    Bootstrap bootstrap = requestExecutor.execute(request, Bootstrap.class);

    return bootstrap;
  }

  private <T extends Pojo> Instances<T> getInstancesInternal(Client client, int objectID, TypeToken<Instances<T>> typeToken) {
    Request request = requestBuilder.buildRequest(client.getLinkByRel("objecttypes").getHref() + "?pageSize=1000", null, "GET", null, null, null, true);
    ObjectTypes objectTypes = requestExecutor.execute(request, ObjectTypes.class);
    ObjectType objectType = null;
    for (ObjectType objectTypeTmp : objectTypes.getItems()) {
      if (objectTypeTmp.getObjectTypeID().equals(Integer.toString(objectID))) {
        objectType = objectTypeTmp;
        break;
      }
    }
    if (objectType == null) {
      throw new NotFoundException("Could not find object with id " + objectID);
    }
    request = requestBuilder.buildRequest(objectType.getLinkByRel("instances").getHref(), null, "GET", null, null, null, true);

    Instances<T> instances = requestExecutor.execute(request, typeToken);
    return instances;
  }

  private <T extends Pojo> void updateInstanceInternal(Client client, int objectID, int instanceID, T data, TypeToken<T> typeToken) {
    Request request = requestBuilder.buildRequest(client.getLinkByRel("objecttypes").getHref() + "?pageSize=1000", null, "GET", null, null, null, true);
    ObjectTypes objectTypes = requestExecutor.execute(request, ObjectTypes.class);
    ObjectType objectType = null;
    for (ObjectType objectTypeTmp : objectTypes.getItems()) {
      if (objectTypeTmp.getObjectTypeID().equals(Integer.toString(objectID))) {
        objectType = objectTypeTmp;
        break;
      }
    }
    if (objectType == null) {
      throw new NotFoundException("Could not find object with id " + objectID);
    }
    String rawData = gson.toJson(data, typeToken.getType());
    request = requestBuilder.buildRequest(objectType.getLinkByRel("instances").getHref() + "/" + instanceID, null, "PUT", null, null, rawData, true);
    requestExecutor.execute(request, EmptyResponse.class);
  }




  private Client getClientInternal(String clientName) {
    Clients clients = clientsInternal(0, 1000);
    for (Client client : clients.getItems()) {
      if (client.getName().equals(clientName)) {
        return client;
      }
    }
    throw new NotFoundException("Could not find client with name : [" + clientName + "]");
  }


  private Clients clientsInternal(int startIndex, int pageSize) {

    Request request = requestBuilder.buildRequest(deviceServerURL, null, "GET", null, null, null, true);
    Api api = requestExecutor.execute(request, Api.class);
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("startIndex", Integer.toString(startIndex));
    queryParams.put("pageSize", Integer.toString(pageSize));
    request = requestBuilder.buildRequest(api.getLinkByRel("clients").getHref(), queryParams, "GET", null, null, null, true);
    Clients clients = requestExecutor.execute(request, Clients.class);
    return clients;
  }



  void saveAccessToken(OauthToken oauthToken) {
    preferences.saveAccessToken(oauthToken.getAccessToken());
    preferences.saveAccessTokenExpiryTime(System.currentTimeMillis() + 1000 * oauthToken.getExpiresIn());
    preferences.saveRefreshToken(oauthToken.getRefreshToken());
  }

  private void clearAccessToken() {
    preferences.saveAccessToken("");
    preferences.saveAccessTokenExpiryTime(0);
    preferences.saveRefreshToken("");
  }

  static abstract class Runner<T extends Pojo> implements Callable {


    public Runner() {
    }

    public abstract T action();


    @Override
    public T call() {
      try {
        return action();
      } catch (DeviceServerException e) {
        throw e;
      } catch (Exception e) {
        throw new UnknownException(e);
      }
    }
  }





}
