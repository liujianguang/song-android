package com.song1.musicno1.models.play;

import com.song1.musicno1.helpers.List8;

import java.util.Random;

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

  public void next(int playMode) {
    if (audios.size() == 0) return;

    if (playMode != Player.PlayMode.SHUFFLE) {
      int i = audios.indexOf(currentAudio);
      i++;
      if (i >= audios.size()) {
        i = 0;
      }
      Audio audio = audios.get(i);
      if (audio != null) {
        setCurrentAudio(audio);
      }
    } else {
      randomNext();
    }
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
    i++;
    switch (playMode) {
      case Player.PlayMode.NORMAL:
        if (i >= audios.size()) {
          currentAudio = null;
        } else {
          setCurrentAudio(audios.get(i));
        }
        break;
      case Player.PlayMode.REPEAT_ALL:
        if (i >= audios.size()) {
          i = 0;
        }
        setCurrentAudio(audios.get(i));
        break;
      case Player.PlayMode.SHUFFLE:
        randomNext();
        break;
    }
  }

  public void previous(int playMode) {
    if (playMode == Player.PlayMode.SHUFFLE) {
      randomNext();
    } else {
      int i = audios.indexOf(currentAudio);
      i--;
      if (i < 0) {
        i = audios.size() - 1;
      }
      Audio audio = audios.get(i);
      if (audio != null) {
        setCurrentAudio(audio);
      }
    }
  }

  public Audio getCurrentAudio() {
    return currentAudio;
  }

  public void setCurrentAudio(Audio audio) {
    currentAudio = audio;
  }

  public List8<Audio> getAudios() {
    return audios;
  }

  public void setAudios(List8<Audio> audios) {
    this.audios = audios;
  }
}
