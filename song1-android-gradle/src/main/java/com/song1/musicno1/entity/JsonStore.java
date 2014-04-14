package com.song1.musicno1.entity;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: windless
 * Date: 13-12-9
 * Time: PM2:23
 */
public class JsonStore {
  public <T> T[] getList(String url, Class<T[]> klass) {
    String json = getJson(url);
    System.out.println(json);
    if (json == null) return null;

    Gson gson = new Gson();
    return gson.fromJson(json, klass);
  }

  protected String getJson(String url) {
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params, 20000);
    HttpConnectionParams.setSoTimeout(params, 20000);
    HttpClient client = new DefaultHttpClient(params);

    HttpGet get = new HttpGet(url);

    HttpResponse response;
    try {
      response = client.execute(get);
      InputStream is = response.getEntity().getContent();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      return sb.toString();
    } catch (IOException e) {
      return null;
    }
  }
}
