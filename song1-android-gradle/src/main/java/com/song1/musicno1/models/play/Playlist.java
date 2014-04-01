package com.song1.musicno1.models.play;

import com.google.common.collect.Lists;
import com.song1.musicno1.helpers.List8;

import java.util.LinkedList;

/**
 * Created by windless on 4/1/14.
 */
public class Playlist {
  protected List8<Audio> audios;
  protected Audio        currentAudio;
  protected LinkedList<Audio> historyStack = Lists.newLinkedList();

  public Playlist() {
  }

  public Playlist(List8<Audio> audios, Audio currentAudio) {
    this.audios = audios;
    this.currentAudio = currentAudio;
  }

  public Audio getAutoNext(int playMode) {
    switch (playMode) {
      case Player.MODE_NORMAL:
      case Player.MODE_REPEAT_ALL:
      case Player.MODE_REPEAT_ONE:
      case Player.MODE_SHUFFLE:
    }
    return null;
  }

  public Audio getNext(int playMode) {
    if (audios.size() == 0) return null;

    if (playMode != Player.MODE_SHUFFLE) {
      int i = audios.indexOf(currentAudio);
      i++;
      if (i >= audios.size()) {
        i = 0;
      }
      return audios.get(i);
    } else {
      return null;
    }
  }

  public Audio getPrevious() {
    if (historyStack.size() == 0) return null;

    Audio previous = historyStack.pop();
    currentAudio = previous;
    return previous;
  }

  public Audio getCurrentAudio() {
    return currentAudio;
  }

  public void setCurrentAudio(Audio audio) {
    historyStack.add(currentAudio);
    currentAudio = audio;
  }

  public List8<Audio> getAudios() {
    return audios;
  }

  public void setAudios(List8<Audio> audios) {
    this.audios = audios;
  }
}
