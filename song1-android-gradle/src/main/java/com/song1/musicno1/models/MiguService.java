package com.song1.musicno1.models;

import android.content.Context;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.SingerInfo;
import com.cmsc.cmmusic.common.data.SingerInfoRsp;
import com.github.kevinsawicki.http.HttpRequest;
import com.song1.musicno1.entity.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: amongothers
 * Date: 13-6-12
 * Time: 下午11:44
 */
public class MiguService {

  private MiguService() {
  }

  private static Object sInstanceLock = new Object();
  private static MiguService sInstance;

  public static MiguService getInstance() {
    synchronized (sInstanceLock) {
      return sInstance == null ? (sInstance = new MiguService()) : sInstance;
    }
  }

  private static Context mContext;

  public MiguService Init(Context context) {
    mContext = context;
    return this;
  }

  static final String HOST                  = " http://112.124.44.10:8080";
  static final String SINGER_CATEGORY_URL   = HOST + "/song1/api/migu/artist/sort";
  static final String SINGER_INFO_URL       = HOST + "/song1/api/migu/artist";
  static final String ALBUM_CATEGORY_URL    = HOST + "/song1/api/migu/album/sort";
  static final String ALBUM_INFO_URL        = HOST + "/song1/api/migu/album";
  static final String ALBUM_SONG_URL        = HOST + "/song1/api/migu/album/song";
  static final String SUBJECT_CATEGORY_URL  = HOST + "/song1/api/migu/list/sort";
  static final String SUBJECT_INFO_URL      = HOST + "/song1/api/migu/list";
  static final String SUBJECT_SONG_URL      = HOST + "/song1/api/migu/list/song";
  static final String RANKING_LIST_INFO_URL = HOST + "/song1/api/migu/top";
  static final String RANKING_LIST_SONG_URL = HOST + "/song1/api/migu/top/song";

  // 排行榜列表，分页
  public GetRankingListResp fetchRankingList(final int start, final int limit) throws IOException, RspException {
    Map<String, String> opt = null;
    opt = new HashMap<String, String>() {{
      put("start", Integer.toString(start));
      put("limit", Integer.toString(limit));
    }};
    return getReturnRsp(RANKING_LIST_INFO_URL, opt, GetRankingListResp.class);
  }

  // 一个排行榜的歌曲列表，分页
  public GetRankingSongResp fetchSongByRankingListId(final String id, final int start, final int limit) throws IOException, RspException {
    Map<String, String> opt = new HashMap<String, String>() {{
      put("start", Integer.toString(start));
      put("limit", Integer.toString(limit));
      if (id != null) {
        put("id", id);
      }
    }};
    return getReturnRsp(RANKING_LIST_SONG_URL, opt, GetRankingSongResp.class);
  }

  // 留待扩展
  public ArtistCategory fetchArtistCategory() throws IOException, RspException {
    Map<String, String> opt = null;
    return getReturnRsp(SINGER_CATEGORY_URL, opt, ArtistCategory.class);
  }

  // 歌手列表（歌手分类固定为9个，先写死在程序中）
  public GetArtistResp fetchArtistList(final int start, final int limit, final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    opt = new HashMap<String, String>() {{
      put("start", Integer.toString(start));
      put("limit", Integer.toString(limit));
      if (id != null) {
        put("sort", id);
      }
    }};
    return getReturnRsp(SINGER_INFO_URL, opt, GetArtistResp.class);
  }

  // 歌手信息
  public SingerInfo fetchArtistDetail(final String id) {
    SingerInfoRsp resp = MusicQueryInterface.getSingerInfo(mContext, id);
    if (resp == null || !resp.getResCode().equals("000000"))
      return null;
    SingerInfo info = resp.getSingerInfo();
    return info;
  }

  // 歌手的歌曲列表 ， 使用MusicQueryInterface类

  // 歌手的专辑列表 + 一个专辑的歌曲列表


  // 专辑分类列表，暂时只有一个默认分类
  public AlbumCategory fetchAlbumCategory() throws IOException, RspException {
    Map<String, String> opt = null;
    return getReturnRsp(ALBUM_CATEGORY_URL, opt, AlbumCategory.class);
  }

  // 专辑列表 （暂时：默认分类列表id）
  public GetAlbumResp fetchAlbumList(final int start, final int limit, final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    opt = new HashMap<String, String>() {{
      put("start", Integer.toString(start));
      put("limit", Integer.toString(limit));
      if (id != null) {
        put("sort", id);
      }
    }};
    return getReturnRsp(ALBUM_INFO_URL, opt, GetAlbumResp.class);
  }


  // 一个专辑中的歌曲列表
  public SongInfo[] fetchSongByAlbumId(final String id) throws IOException, RspException {
    Map<String, String> opt = new HashMap<String, String>() {{
      if (id != null) {
        put("id", id);
      }
    }};
    return getReturnRsp(ALBUM_SONG_URL, opt, SongInfo[].class);
  }

  // 歌单分类  （跟上面的专辑一样）
  public SubjectCategory fetchSubjectCategory() throws IOException, RspException {
    Map<String, String> opt = null;
    return getReturnRsp(SUBJECT_CATEGORY_URL, opt, SubjectCategory.class);
  }

  // 一个歌单分类（ = 1 个歌单列表）
  public GetSubjectResp fetchSubjectList(final int start, final int limit, final String id) throws IOException, RspException {
    Map<String, String> opt = null;
    opt = new HashMap<String, String>() {{
      put("start", Integer.toString(start));
      put("limit", Integer.toString(limit));
      if (id != null) {
        put("sort", id);
      }
    }};
    return getReturnRsp(SUBJECT_INFO_URL, opt, GetSubjectResp.class);
  }


  // 一个歌单的歌曲列表：是否分页？？？
  public SongInfo[] fetchSongBySubjectId(final String id) throws IOException, RspException {
    Map<String, String> opt = new HashMap<String, String>() {{
      if (id != null) {
        put("id", id);
      }
    }};
    return getReturnRsp(SUBJECT_SONG_URL, opt, SongInfo[].class);
  }


  private static String getReturnStr(String url, Map<String, String> opt)
      throws IOException, RspException.Not200Exception {
    HttpRequest request;
    try {
      request = HttpRequest.get(url, opt, true);
    } catch (HttpRequest.HttpRequestException ex) {
      throw new IOException();
    }

    /*
    if (content != null && !content.isEmpty()) {
      request.setEntity(new StringEntity(content));
      request.setHeader("Content-type", "application/json");
    }
    */
    try {
      if (request.ok()) {
        return request.body();
      } else {
        throw new RspException.Not200Exception(request.code());
      }
    } catch (HttpRequest.HttpRequestException ex) {
      throw  new IOException();
    }
  }

  private static <T> T getReturnRsp(String url, Map<String, String> opt, Class<T> classOfT)
      throws IOException, RspException {
    String rspStr = getReturnStr(url, opt);
    return MiguRsp.parse(rspStr, classOfT);
  }


  private static String postReturnStr(String url, Map<String, String> opt, String content)
      throws IOException, RspException.Not200Exception {
    HttpRequest request = HttpRequest.post(url);
    if (opt != null) {
      request.form(opt);
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

  private static <T> T postReturnRsp(String url, Map<String, String> opt, String content, Class<T> classOfT)
      throws IOException, RspException {
    String rspStr = postReturnStr(url, opt, content);
    return MiguRsp.parse(rspStr, classOfT);
  }
}
