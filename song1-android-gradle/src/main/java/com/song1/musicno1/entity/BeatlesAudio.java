package com.song1.musicno1.entity;

import com.song1.musicno1.models.play.Audio;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM3:38
 */
public class BeatlesAudio {
  public String title;
  public String author;
  public String actor;
  public String url;

  public SongInfo toSongInfo() {
    SongInfo info = new SongInfo();
    info.musicName = title;
    info.musicUrl = url;
    info.miguArtistName = author;
    info.from = Audio.OTHER;
    return info;
  }

  public Audio toAudio() {
    Audio audio = new Audio();
    audio.setTitle(title);
    audio.setAlbum(author);
    audio.setArtist(actor);
    audio.setRemotePlayUrl(url);
    audio.setLocalPlayUri(url);
    return audio;
  }
}
