package com.imgtec.creator.lumpy.data.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.imgtec.creator.lumpy.data.api.exceptions.ConflictException;
import com.imgtec.creator.lumpy.data.api.exceptions.NetworkException;
import com.imgtec.creator.lumpy.data.api.exceptions.NotFoundException;
import com.imgtec.creator.lumpy.data.api.exceptions.ParseException;
import com.imgtec.creator.lumpy.data.api.exceptions.UnauthorizedException;
import com.imgtec.creator.lumpy.data.api.exceptions.UnknownException;
import com.imgtec.creator.lumpy.data.api.pojo.Pojo;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



class RequestExecutor {

  private OkHttpClient client;
  private Gson gson;

  RequestExecutor(OkHttpClient client, Gson gson) {
    this.client = client;
    this.gson = gson;
  }

  <T extends Pojo> T execute(Request request, TypeToken token) {
    return execute(request, null, token);
  }


  <T extends Pojo> T execute(Request request, Class<T> returnType) {
    return execute(request, returnType, null);
  }

  <T extends Pojo> T execute(Request request, Class<T> returnType, TypeToken token) {
    Response response = null;
    try {
      response = client.newCall(request).execute();
      if (response.code() >= 200 && response.code() < 300) {
        try {
          String responseBody = response.body().string();
          System.out.println(responseBody);
          if (token != null)
            return gson.fromJson(responseBody, token.getType());
          else
            return gson.fromJson(responseBody, returnType);
        } catch (JsonSyntaxException e) {
          throw new ParseException();
        }
      }
      switch (response.code()) {
        case 401:
          throw new UnauthorizedException();
        case 404:
          throw new NotFoundException();
        case 409:
          throw new ConflictException();
        default:
          throw new UnknownException();

      }
    } catch (IOException e) {
      throw new NetworkException();
    }
  }
}
