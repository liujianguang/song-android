package com.song1.musicno1.util;

/**
 * Created by kate on 14-3-25.
 */
public class IPUtil {


  public static String intToIp(int i) {
    return (i & 0xFF) + "." +

        ((i >> 8) & 0xFF) + "." +

        ((i >> 16) & 0xFF) + "." +

        (i >> 24 & 0xFF);
  }
}
