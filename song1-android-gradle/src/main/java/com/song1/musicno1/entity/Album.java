package com.song1.musicno1.entity;

import com.google.common.collect.Lists;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM8:37
 */
public class Album {
  public static final int LOCAL = 0;
  public String id;
  public String title;
  public String album_art;
  public String artist;
  public int number_of_songs;
  public int from;
  public List<Audio> audios;

  public void addAudio(Audio audio) {
    if (audios == null) {
      audios = Lists.newArrayList();
    }
    audios.add(audio);
  }

  public List<Audio> getAudios() {
    return audios;
  }
}
