package com.song1.musicno1.models.play;

/**
 * Created by windless on 3/27/14.
 */
public class RendererException extends Exception {
  public static final String NO_SERVICE         = "no service";
  public static final String NO_ACTION          = "no action";
  public static final String POST_ACTION_FAILED = "post action failed";
  public static final String NO_ARGUMENT        = "no argument";

  public RendererException(String msg) {
    super(msg);
  }
}
