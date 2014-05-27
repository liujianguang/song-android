package com.song1.musicno1.util;

/**
 * Created by leovo on 2014/4/9.
 */
public class Global {

  public static String format(long bytes) {
    float kb = bytes / 1024;
    if (kb > 1024) {
      float mb = kb / 1024;
      return String.format("%.2fMB", mb);
    }
    return String.format("%.0fKB", kb);
  }
}
