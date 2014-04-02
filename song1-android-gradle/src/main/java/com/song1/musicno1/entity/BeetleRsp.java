package com.song1.musicno1.entity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.song1.musicno1.models.RspException;

/**
 * User: amongothers
 * Date: 13-6-12
 * Time: 下午9:03
 */
public class BeetleRsp {

  public static <T> T parse(String json, Class<T> classOfT) throws RspException {
    Gson gson = new Gson();
    try {
      return gson.fromJson(json, classOfT);
    } catch (JsonSyntaxException e) {
      throw new RspException.SyntaxException(classOfT, json);
    }
  }

}
