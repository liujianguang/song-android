package com.song1.musicno1.modules;

import android.app.Application;
import android.content.Context;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.services.UpnpService;
import dagger.Module;
import dagger.Provides;

/**
 * Created by windless on 3/31/14.
 */
@Module(
    injects = {
        UpnpService.class,
        MainActivity.class
    }
)
public class AppModule {
  protected final Application app;

  public AppModule(Application app) {
    this.app = app;
  }

  @Provides
  public Context context() {
    return app.getApplicationContext();
  }
}
