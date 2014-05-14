package com.song1.musicno1.models.play;

import com.google.common.collect.Lists;
import com.song1.musicno1.helpers.List8;

import java.util.LinkedList;
import java.util.Random;

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

  public void next(int playMode) {
    if (audios.size() == 0) return;

    autoNext(playMode);
//    if (playMode != Player.MODE_SHUFFLE) {
//      int i = audios.indexOf(currentAudio);
//      i++;
//      if (i >= audios.size()) {
//        i = 0;
//      }
//      Audio audio = audios.get(i);
//      if (audio != null) {
//        setCurrentAudio(audio);
//      }
//    } else {
//      randomNext();
//    }
  }

  private void randomNext() {
    if (audios.size() == 1) {
      return;
    }

    Random random = new Random(System.currentTimeMillis());
    int i = random.nextInt(audios.size());
    Audio audio;
    while ((audio = audios.get(i)) == currentAudio) {
      i = random.nextInt(audios.size());
    }
    setCurrentAudio(audio);
  }

  public void autoNext(int playMode) {
    int i = audios.indexOf(currentAudio);
    //i++;
    switch (playMode) {
      case Player.MODE_NORMAL:
        if (i != (audios.size() - 1)) {
          setCurrentAudio(audios.get(++i));
        }
        break;
      case Player.MODE_REPEAT_ALL:
        if (i != (audios.size() - 1)) {
          i++;
        }else{
          i = 0;
        }
        setCurrentAudio(audios.get(i));
        break;
      case Player.MODE_SHUFFLE:
        randomNext();
        break;
    }
  }

//  public void previous() {
//    if (historyStack.size() == 0) {
//      currentAudio = null;
//    } else {
//      currentAudio = historyStack.pop();
//    }
//  }

  public void previous(int playMode){
    int i = audios.indexOf(currentAudio);
    //i++;
    switch (playMode) {
      case Player.MODE_NORMAL:
        if (i != 0) {
          setCurrentAudio(audios.get(--i));
        }
        break;
      case Player.MODE_REPEAT_ALL:
        if (i != 0) {
          i--;
        }else{
          i = audios.size() - 1;
        }
        setCurrentAudio(audios.get(i));
        break;
      case Player.MODE_SHUFFLE:
        randomNext();
    }
  }

  public Audio getCurrentAudio() {
    return currentAudio;
  }

  public void setCurrentAudio(Audio audio) {
    if (currentAudio != null) {
      historyStack.addFirst(currentAudio);
    }
    currentAudio = audio;
  }

  public List8<Audio> getAudios() {
    return audios;
  }

  public void setAudios(List8<Audio> audios) {
    this.audios = audios;
  }
}
