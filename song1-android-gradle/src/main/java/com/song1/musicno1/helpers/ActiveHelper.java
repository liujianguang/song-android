package com.song1.musicno1.helpers;

import com.activeandroid.ActiveAndroid;
import de.akquinet.android.androlog.Log;

/**
 * Created by windless on 14-4-11.
 */
public class ActiveHelper {
  public static void transition(Runnable runnable) {
    ActiveAndroid.beginTransaction();
    try {
      runnable.run();
      ActiveAndroid.setTransactionSuccessful();
    } catch (Exception e) {
      Log.d("Active exception " + e.toString());
    } finally {
      ActiveAndroid.endTransaction();
    }
  }
}
