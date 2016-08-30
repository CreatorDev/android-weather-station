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
