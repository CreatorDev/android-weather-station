package com.imgtec.creator.lumpy.data.api;


import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

class RequestBuilder {

  Request buildRequest(String url, Map<String, String> queryParams, String method, Map<String, String> headers, Map<String, String> params, String rawData, boolean needAuthorization) {
    Request.Builder builder = new Request.Builder();
    if (queryParams != null) {
      url += "?";
      for (Map.Entry<String, String> entry : queryParams.entrySet()) {
        url += entry.getKey() + "=" + entry.getValue() + "&";
      }
    }
    builder.url(url);
    if (headers != null) {
      builder.headers(Headers.of(headers));
    }
    if (params != null) {
      FormBody.Builder bodyBuilder = new FormBody.Builder();
      for (Map.Entry<String, String> entry : params.entrySet()) {
        bodyBuilder.addEncoded(entry.getKey(), entry.getValue());
      }
      builder.method(method, bodyBuilder.build());
    } else if (rawData != null) {
      builder.method(method, RequestBody.create(MediaType.parse("application/json"), rawData));
    } else {
      builder.method(method, null);
    }
    builder.tag(needAuthorization);

    return builder.build();

  }

}
