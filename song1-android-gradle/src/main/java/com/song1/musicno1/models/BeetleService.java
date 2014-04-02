package com.song1.musicno1.models;

import com.song1.musicno1.entity.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: amongothers
 * Date: 13-6-12
 * Time: 下午11:44
 */
public class BeetleService {

  private BeetleService() {
  }

  private static Object sInstanceLock = new Object();
  private static BeetleService sInstance;
  public static BeetleService getInstance() {
    synchronized (sInstanceLock) {
      return sInstance == null ? (sInstance = new BeetleService()) : sInstance;
    }
  }

  static final String HOST = "http://42.121.122.171";
  static final String BACKUP_HOST = "http://42.121.1.186:8866";
  static final String SONG_API_URL = HOST + "/mobile2/song.php";
//  static final String RADIO_API_URL = HOST + "/mobile2/radio.php";
  static final String RADIO_API_URL = BACKUP_HOST + "/radio";
  static final String CHART_CATEGORY_API_URL = HOST + "/mobile2/chart_category.php";
  static final String CHART_LIST_API_URL = HOST + "/mobile2/chart_list.php";
  static final String SINGER_CATEGORY_API_URL = HOST + "/mobile2/singer_category.php";
//  static final String RADIO_CATEGORY_API_URL = HOST + "/mobile2/radio_category.php";
  static final String RADIO_CATEGORY_API_URL = BACKUP_HOST + "/radiotype";
  static final String SINGER_API_URL = HOST + "/mobile2/singer.php";
  static final String LOGIN_API_URL = HOST + "/mobile2/user.php?act=login";
  static final String REGISTER_API_URL = HOST + "/mobile2/user.php?act=login";


  public ChartRsp[] fetchChartListBySinger(final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    if(id != null) {
      opt = new HashMap<String, String>(){{
        put("sid", id);
      }};
    }
    return BeetleRequest.requestReturnRsp(CHART_LIST_API_URL, opt, null, ChartRsp[].class);
  }

  public ChartRsp[] fetchChartListByCategory(final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    if(id != null) {
      opt = new HashMap<String, String>(){{
        put("cid", id);
      }};
    }
    return BeetleRequest.requestReturnRsp(CHART_LIST_API_URL, opt, null, ChartRsp[].class);
  }

  public SongRsp[] fetchSong(final String id, final int page) throws IOException, RspException {
    Map<String, String> opt = new HashMap<String, String>(){{
      put("page", Integer.toString(page));
    }};
    if(id != null) {
      opt.put("sid", id);
    }
    return BeetleRequest.requestReturnRsp(SONG_API_URL, opt, null, SongRsp[].class);
  }

  public SingerCategoryRsp[] fetchRadioCategory(final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    if(id != null) {
      opt = new HashMap<String, String>(){{
        put("id", id);
      }};
    }
//    return BeetleRequest.requestReturnRsp(RADIO_CATEGORY_API_URL, opt, null, SingerCategoryRsp[].class);
    return BeetleRequest.getReturnRsp(RADIO_CATEGORY_API_URL, opt, SingerCategoryRsp[].class);
  }

  public RadioRsp[] fetchRadio(final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    if(id != null) {
      opt = new HashMap<String, String>(){{
        put("id", id);
      }};
    }
//    return BeetleRequest.requestReturnRsp(RADIO_API_URL, opt, null, RadioRsp[].class);
    return BeetleRequest.getReturnRsp(RADIO_API_URL, opt, RadioRsp[].class);
  }

  public ChartCategoryRsp[] fetchChartCategory(final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    if(id != null) {
      opt = new HashMap<String, String>(){{
        put("id", id);
      }};
    }
    return BeetleRequest.requestReturnRsp(CHART_CATEGORY_API_URL, opt, null, ChartCategoryRsp[].class);
  }

  public SingerCategoryRsp[] fetchSingerCategory(final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    if(id != null) {
      opt = new HashMap<String, String>(){{
        put("id", id);
      }};
    }
    return BeetleRequest.requestReturnRsp(SINGER_CATEGORY_API_URL, opt, null, SingerCategoryRsp[].class);
  }

  public SingerRsp[] fetchSinger(final String categoryId, final int page) throws IOException, RspException {
    Map<String, String> opt = new HashMap<String, String>(){{
      put("category_id", categoryId);
      put("page", Integer.toString(page));
    }};
    return BeetleRequest.requestReturnRsp(SINGER_API_URL, opt, null, SingerRsp[].class);
  }

  public String login(final String userName, final String password) throws IOException, RspException {
    Map<String, String> opt = new HashMap<String, String>(){{
      put("username", userName);
      put("password", password);
    }};
    return BeetleRequest.requesReturnStr(LOGIN_API_URL, opt, null);
  }

  public RegisterRsp register(final String userName, final String password, final String email)
      throws IOException, RspException {
    Map<String, String> opt = new HashMap<String, String>(){{
      put("username", userName);
      put("password", password);
      put("email", email);
    }};
    return BeetleRequest.requestReturnRsp(REGISTER_API_URL, opt, null, RegisterRsp.class);
  }
}
