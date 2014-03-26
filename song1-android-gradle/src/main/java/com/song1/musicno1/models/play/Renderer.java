package com.song1.musicno1.models.play;

/**
 * Created by windless on 3/26/14.
 */
public interface Renderer {
  public void setUri(String uri) throws RendererException;

  public void play() throws RendererException;

  public void pause() throws RendererException;

  public boolean isPlaying() throws RendererException;

  public PositionInfo getPositionInfo() throws RendererException;
}
