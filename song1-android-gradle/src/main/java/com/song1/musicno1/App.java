package com.song1.musicno1;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;
import com.song1.musicno1.models.Favorite;
import com.song1.musicno1.modules.AppModule;
import dagger.ObjectGraph;

/**
 * Created by windless on 3/25/14.
 */
public class App extends Application {
  private static ObjectGraph _objectGraph;

  public static <T> T inject(T instance) {
    return _objectGraph.inject(instance);
  }

  public static <T> T get(Class<T> klass) {
    return _objectGraph.get(klass);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    _objectGraph = ObjectGraph.create(new AppModule(this));
    ActiveAndroid.initialize(this);

    Favorite.init();
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    ActiveAndroid.dispose();
    _objectGraph = null;
  }
}
