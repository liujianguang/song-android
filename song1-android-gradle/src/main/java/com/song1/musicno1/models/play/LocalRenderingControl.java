package com.song1.musicno1.models.play;

import android.content.Context;
import android.media.AudioManager;

import javax.inject.Inject;

/**
 * Created by windless on 14-4-9.
 */
public class LocalRenderingControl implements RenderingControl {
  private final AudioManager audioManager;

  @Inject
  public LocalRenderingControl(Context context) {
    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
  }

  @Override
  public void updateVolume() {
  }

  @Override
  public void setVolume(int volume) {
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
  }

  @Override
  public void volumeUp() throws RendererException {
    Volume volume = getVolume();
    int setVolume = volume.getCurrent() + 1;
    if (setVolume > volume.getMax()) {
      setVolume = volume.getMax();
    }
    setVolume(setVolume);
  }

  @Override
  public void volumeDown() throws RendererException {
    Volume volume = getVolume();
    int setVolume = volume.getCurrent() - 1;
    if (setVolume < 0) {
      setVolume = 0;
    }
    setVolume(setVolume);
  }

  @Override
  public Volume getVolume() throws RendererException {
    int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    return new Volume(volume, max);
  }
}
