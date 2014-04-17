package com.song1.musicno1.models.play;

/**
 * Created by windless on 14-4-9.
 */
public interface RenderingControl {
  void updateVolume() throws RendererException;

  void setVolume(int volume) throws RendererException;

  void volumeUp() throws RendererException;

  void volumeDown() throws RendererException;

  Volume getVolume() throws RendererException;
}
