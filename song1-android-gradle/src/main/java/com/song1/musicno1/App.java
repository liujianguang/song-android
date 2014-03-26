package com.song1.musicno1;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

/**
 * Created by windless on 3/25/14.
 */
public class App extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    ActiveAndroid.initialize(this);
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    ActiveAndroid.dispose();
  }
}
