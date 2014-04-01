package com.song1.musicno1.models.play;

/**
 * Created by windless on 3/26/14.
 */
public interface Renderer {
  void setUri(String uri) throws RendererException;

  void play() throws RendererException;

  void pause() throws RendererException;

  boolean isPlaying() throws RendererException;

  PositionInfo getPositionInfo() throws RendererException;

  void stop() throws RendererException;

  String getName();

  String getId();

  void seek(int seekTo) throws RendererException;
}
