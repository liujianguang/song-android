package com.song1.musicno1.models.cmmusic;

import android.content.Context;
import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.FullSongManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.OnlineListenerMusicInterface;
import com.cmsc.cmmusic.common.data.*;
import com.cmsc.cmmusic.common.data.AlbumInfo;
import com.song1.musicno1.entity.*;
import com.song1.musicno1.models.BeetleException;
import com.song1.musicno1.models.BeetleService;
import com.song1.musicno1.models.RspException;
import com.song1.musicno1.models.play.Audio;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CMMusicStore {

  private Context context;

  @Inject
  public CMMusicStore(Context context) {
    this.context = context;
  }

  //获取榜单信息
  public ArrayList<Chart> charts(int pageNumber, int numberPerPage) throws CMException {
    ChartListRsp c = MusicQueryInterface.getChartInfo(
        context, pageNumber, numberPerPage);
    if (c == null) {
      throw new CMException("-1", "未知错误");
    }
    if (c.getChartInfos() == null) {
      throw new CMException(c.getResCode(), c.getResMsg());
    }
    ArrayList<Chart> musicCharts = new ArrayList<Chart>();
    for (ChartInfo info : c.getChartInfos()) {
      Chart chart = new Chart(info.getChartName(), info.getChartCode());
      musicCharts.add(chart);
    }
    return musicCharts;
  }

  //根据榜单代码获取榜单音乐信息
  public ArrayList<Audio> audios_by_chart(String chartCode, int pageNumber, int numberPerPage)
      throws CMException {
    MusicListRsp m = MusicQueryInterface.getMusicsByChartId(context, chartCode, pageNumber, numberPerPage);
    if (m == null) {
      throw new CMException("-1", "未知错误");
    }
    if (m.getMusics() == null) {
      throw new CMException(m.getResCode(), m.getResMsg());
    }
    return getAudios(m);
  }

  //获取移动顶级歌手分类列表
  public List<Category> root_artist_categories() throws BeetleException {
    return artist_categories("11", 1);
  }

  //获取歌手分类列表
  public List<Category> artist_categories(String id, int page) throws BeetleException {
    SingerCategoryRsp[] rsps;
    try {
      rsps = BeetleService.getInstance().fetchSingerCategory(id);
    } catch (IOException e) {
      throw new BeetleException(555001, e.getMessage());
    } catch (RspException e) {
      if (e instanceof RspException.Not200Exception) {
        throw new BeetleException(555002, "服务器返回" + ((RspException.Not200Exception) e).getHttpCode() + "错误。");
      } else {
        throw new BeetleException(666001, "返回结果解析失败");
      }
    }

    if (rsps == null) return null;

    List<Category> categories = new ArrayList<Category>();
    for (SingerCategoryRsp rsp : rsps) {
      Category category = new Category();
      category.id = rsp.getId();
      category.name = rsp.getName();
      category.image = rsp.getImage();
      category.is_leaf = rsp.isLeaf();
      categories.add(category);
    }

    return categories;
  }

  //获取歌手列表
  public ArrayList<Artist> artists_by_category(String id, int page) throws BeetleException {
    try {
      SingerRsp[] rsps = BeetleService.getInstance().fetchSinger(id, page);
      if (rsps == null) {
        return null;
      }
      ArrayList<Artist> result = new ArrayList<Artist>();
      for (SingerRsp rsp : rsps) {
        Artist artist = new Artist();
        artist.id = rsp.miguId;
        artist.name = rsp.name;
        artist.image = rsp.image;
        result.add(artist);
      }
      return result;
    } catch (IOException e) {
      throw new BeetleException(555001, e.getMessage());
    } catch (RspException e) {
      if (e instanceof RspException.Not200Exception) {
        throw new BeetleException(555002, "服务器返回" + ((RspException.Not200Exception) e).getHttpCode() + "错误。");
      } else {
        throw new BeetleException(666001, "返回结果解析失败");
      }
    }
  }

  //根据歌手ID获取专辑列表信息
  public ArrayList<Album> albums_by_artist(String artist_id, int page_num, int num_per_page)
      throws CMException {
    AlbumListRsp a = MusicQueryInterface.getAlbumsBySingerId(context, artist_id, page_num, num_per_page);
    if (a == null) {
      throw new CMException("-1", "未知错误");
    }
    if (a.getAlbumInfos() == null) {
      throw new CMException(a.getResCode(), a.getResMsg());
    }
    ArrayList<Album> albums = new ArrayList<Album>();
    for (AlbumInfo info : a.getAlbumInfos()) {
      Album album = new Album();
      album.id = info.getAlbumId();
      album.album_art = info.getImgUrl();
      album.title = info.getName();
      albums.add(album);
    }
    return albums;
  }

  //根据专辑ID获取音乐信息
  public ArrayList<Audio> audios_by_album(String albumId, int pageNumber, int numberPerPage)
      throws CMException {

    MusicListRsp m = MusicQueryInterface.getMusicsByAlbumId(context, albumId, pageNumber, numberPerPage);
    if (m == null) {
      throw new CMException("-1", "未知错误");
    }
    if (m.getMusics() == null) {
      throw new CMException(m.getResCode(), m.getResMsg());
    }
    return getAudios(m);
  }

  //根据歌手ID获取音乐信息
  public ArrayList<Audio> audios_by_artist(String artistId, int pageNumber, int numberPerPage) throws CMException {
    MusicListRsp m = MusicQueryInterface.getMusicsBySingerId(context, artistId, pageNumber, numberPerPage);
    if (m == null) {
      throw new CMException("-1", "未知错误");
    }
    if (m.getMusics() == null) {
      throw new CMException(m.getResCode(), m.getResMsg());
    }
    return getAudios(m);
  }

  //根据关键字搜索歌曲
  public ArrayList<Audio> search(String key, int pageNumber, int numberPerPage) throws CMException {
    MusicListRsp m = MusicQueryInterface.getMusicsByKey(context, URLEncoder.encode(key), pageNumber, numberPerPage);
    if (m == null) {
      throw new CMException("-1", "未知错误");
    }
    if (m.getMusics() == null) {
      throw new CMException(m.getResCode(), m.getResMsg());
    }
    return getAudios(m);
  }

  //获取在线听歌地址
  public String getOnLineListenerSongUrl(String musicId) throws CMException {
    StreamRsp s = OnlineListenerMusicInterface.getStream(context, musicId);
    if (s == null) {
      throw new CMException("-1", "未知错误");
    }
    if (!s.getResCode().equals("000000")) {
      throw new CMException(s.getResCode(), s.getResMsg());
    }
    return s.getStreamUrl();
  }

  //网络获取全曲下载地址
  public void download_url(Context context, final Audio audio, final GetDownloadUrlCallback callback) {
    FullSongManagerInterface.getFullSongDownloadUrlByNet(context, audio.getId(), false,
        new CMMusicCallback<DownloadResult>() {
          @Override
          public void operationResult(final DownloadResult downloadResult) {
            if (null != downloadResult) {
              callback.OnUrlGot(downloadResult.getResCode(), downloadResult.getResMsg(), downloadResult.getDownUrl());
            } else {
              callback.OnUrlGot(null, null, null);
            }
          }
        });
  }

  //短信获取全曲下载地址
//  public String getFullSongDownloadUrlBySms(String musicId, String downLoadUrlType, String songName, String singerName) {
//    String url = "";
//
//    //TO DO
//
//    return url;
//  }


  private ArrayList<Audio> getAudios(MusicListRsp rsp) {
    ArrayList<Audio> audios = new ArrayList<Audio>();
    for (MusicInfo info : rsp.getMusics()) {
      Audio audio = new Audio();
      audio.setId(info.getMusicId());
      audio.setTitle(info.getSongName());
      audio.setLocalPlayUri(info.getSongListenDir());
      audio.setArtist(info.getSingerName());
      audio.setAlbum(info.getAlbumPicDir());
//      audio.setLrcUrl(info.getLrcDir());
      audio.setFrom(Audio.MIGU);
      audio.setRemotePlayUrl(audio.getLocalPlayUri());
      audios.add(audio);
    }
    return audios;
  }
}
