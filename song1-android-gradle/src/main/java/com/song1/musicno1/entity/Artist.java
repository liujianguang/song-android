package com.song1.musicno1.entity;

import com.google.common.collect.Lists;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM8:36
 */
public class Artist {
  public String      id;
  public String      name;
  public int         album_count;
  public int         audio_count;
  public String      image;
  public String      desc;
  public List<Audio> audios;

  public void addAudio(Audio audio) {
    if (audios == null) {
      audios = Lists.newArrayList();
    }
    audios.add(audio);
  }
}
