package com.song1.musicno1.models.play;

/**
 * Created by windless on 3/26/14.
 */
public interface Renderer {
  public boolean setUri(String uri);
  public boolean play();
  public boolean pause();
  public boolean isPlaying();
  public PositionInfo getPositionInfo();
}
