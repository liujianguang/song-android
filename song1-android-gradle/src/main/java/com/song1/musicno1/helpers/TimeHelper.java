package com.song1.musicno1.helpers;

/**
 * User: windless
 * Date: 13-5-23
 * Time: 下午5:52
 */
public class TimeHelper {
  public static String milli2str(int milliseconds) {
    int seconds = milliseconds / 1000 % 60;
    int minutes = (milliseconds / (1000 * 60)) % 60;
    int hours = (milliseconds / (1000 * 60 * 60)) % 24;
    if (hours == 0) {
      return String.format("%02d:%02d", minutes, seconds);
    } else {
      return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
  }

  public static String mill2lstr(int milliseconds) {
    int seconds = milliseconds / 1000 % 60;
    int minutes = (milliseconds / (1000 * 60)) % 60;
    int hours = (milliseconds / (1000 * 60 * 60)) % 24;
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }

  public static String secondToString(int seconds) {
    return String.format("%02d:%02d", seconds / 60, seconds % 60);
  }

  public static int str2milli(String str) {
    String[] item = str.split(":");
    String second_item = item[item.length - 1];
    int index_of_dot = second_item.indexOf(".");
    if (index_of_dot != -1) {
      item[item.length - 1] = second_item.substring(0, index_of_dot);
    }
    if (item.length == 2) {
      return Integer.valueOf(item[0]) * 60 * 1000 +
          Integer.valueOf(item[1]) * 1000;
    } else if (item.length == 3) {
      return Integer.valueOf(item[0]) * 60 * 60 * 1000 +
          Integer.valueOf(item[1]) * 60 * 1000 +
          Integer.valueOf(item[2]) * 1000;
    }
    return 0;
  }
}
