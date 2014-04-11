package com.song1.musicno1.helpers;

import com.activeandroid.ActiveAndroid;

/**
 * Created by windless on 14-4-11.
 */
public class ActiveHelper {
  public static void transition(Runnable runnable) {
    ActiveAndroid.beginTransaction();
    try {
      runnable.run();
      ActiveAndroid.setTransactionSuccessful();
    } catch (Exception ignored) {
    } finally {
      ActiveAndroid.endTransaction();
    }
  }
}
