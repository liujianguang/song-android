package com.song1.musicno1.helpers;

import android.os.Handler;
import com.squareup.otto.Bus;

/**
 * Created by windless on 3/26/14.
 */
public class MainBus {
  private static MainBus _instance = new MainBus();
  private static Handler _handler  = new Handler();

  private final Bus bus;

  public MainBus() {
    bus = new Bus();
  }

  public static void register(Object o) {
    _instance.getBus().register(o);
  }

  public static void unregister(Object o) {
    _instance.getBus().unregister(o);
  }

  public static void post(Object o) {
    _handler.post(() -> _instance.getBus().post(o));
  }

  public Bus getBus() {
    return bus;
  }
}
