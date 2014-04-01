package com.song1.musicno1.entity;

import com.song1.musicno1.models.play.Audio;

/**
 * Created with IntelliJ IDEA.
 * User: SV
 * Date: 13-10-17
 * Time: 下午4:32
 * To change this template use File | Settings | File Templates.
 */
public class SongInfo {
  public String musicId;
  public String musicName;
  public String miguArtistName;
  public String musicUrl;

  public String albumName;
  public String albumId;
  public String picUrl;
  public int from = -1;


  public String dubi;


  public boolean isDubi() {
    return dubi.equalsIgnoreCase("true");
  }

  public Audio toAudio() {

    Audio audio = new Audio();
    audio.setId(this.musicId);
    audio.setTitle(this.musicName);
    audio.setArtist(this.miguArtistName);
    audio.setAlbum(this.albumName);
//    audio.album_id = this.albumId;
//    audio.album_art = this.picUrl;

    audio.setLocalPlayUri(this.musicUrl);
    audio.setRemotePlayUrl(this.musicUrl);
//    audio.download_url = null;

    // 通过咪咕音乐获得的 SongInfo 是没有 from 属性的。
    // 本来 SongInfo 只用与咪咕音乐，现在也用于甲壳虫。
    // 如果 from = Audio.MIGU 的话，播放时会先通过咪咕接口获取播放地址。甲壳虫音乐不需要这部动作
    if (from == -1) {
      audio.setFrom(Audio.MIGU);
    } else {
      audio.setFrom(from);
    }

    return audio;

  }

}

 /*

public String id;
public String title;
public String artist;
public String album;
public int    from;
public String play_uri;
public String album_id;
public String remote_play_uri;
public String album_art;
public String download_url;

  */