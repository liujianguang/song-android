package com.song1.musicno1.models.play;

import com.song1.musicno1.helpers.List8;

/**
 * Created by windless on 4/1/14.
 */
public class Playlist {
  protected List8<Audio> audios;
  protected Audio        currentAudio;

  public Playlist() {
  }

  public Playlist(List8<Audio> audios, Audio currentAudio) {
    this.audios = audios;
    this.currentAudio = currentAudio;
  }

  public void setAudios(List8<Audio> audios) {
    this.audios = audios;
  }

  public void setCurrentAudio(Audio audio) {
    currentAudio = audio;
  }

  public Audio getAutoNext(int playMode) {
    int i = audios.indexOf(currentAudio);
    return audios.get(i + 1);
  }

  public Audio getCurrentAudio() {
    return currentAudio;
  }

  public List8<Audio> getAudios() {
    return audios;
  }
}
