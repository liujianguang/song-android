package com.song1.musicno1.models;

import com.github.kevinsawicki.http.HttpRequest;
import com.song1.musicno1.entity.BeetleRsp;
import com.song1.musicno1.entity.Utf8CodingFucker;

import java.io.IOException;
import java.util.Map;

/**
 * User: amongothers
 * Date: 13-6-12
 * Time: 下午11:22
 */
public class BeetleRequest {

  public static String requesReturnStr(String url, Map<String, String> opt, String content)
      throws IOException, RspException.Not200Exception {
    HttpRequest request = HttpRequest.post(url);
    if (opt != null) {
      try {
        request.form(opt);
      } catch (HttpRequest.HttpRequestException e) {
        throw new RspException.Not200Exception(404);
      }
    }
    /*
    if (content != null && !content.isEmpty()) {
      request.setEntity(new StringEntity(content));
      request.setHeader("Content-type", "application/json");
    }
    */
    if (request.ok()) {
      String rspStr = request.body();
      return Utf8CodingFucker.fuck(rspStr);
    } else {
      throw new RspException.Not200Exception(request.code());
    }
  }

  public static String getReturnStr(String url, Map<String, String> opt)
      throws IOException, RspException.Not200Exception {
    HttpRequest request = HttpRequest.get(url,opt,true);

    /*
    if (content != null && !content.isEmpty()) {
      request.setEntity(new StringEntity(content));
      request.setHeader("Content-type", "application/json");
    }
    */
    if (request.ok()) {
      String rspStr = request.body();
//      return Utf8CodingFucker.fuck(rspStr);
      return  rspStr;
    } else {

      throw new RspException.Not200Exception(request.code());
    }
  }
  public static <T> T getReturnRsp(String url, Map<String, String> opt, Class<T> classOfT)
      throws IOException, RspException {
    String rspStr = getReturnStr(url, opt);
    return BeetleRsp.parse(rspStr, classOfT);
  }

  public static <T> T requestReturnRsp(String url, Map<String, String> opt, String content, Class<T> classOfT)
      throws IOException, RspException {
    String rspStr = requesReturnStr(url, opt, content);
    return BeetleRsp.parse(rspStr, classOfT);
  }
}
