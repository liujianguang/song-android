package com.song1.musicno1.entity;


import com.song1.musicno1.models.play.Audio;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM2:26
 */
public class JustingAudio {
  public String title;
  public String author;
  public String actor;
  public String url;

  public Audio toAudio() {
    Audio audio = new Audio();
    audio.setTitle(title);
    audio.setFrom(Audio.OTHER);
    audio.setArtist(actor);
    audio.setRemotePlayUrl(url);
    audio.setLocalPlayUri(url);
    return audio;
  }
}
