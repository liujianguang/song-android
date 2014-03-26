package com.song1.musicno1.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Created by windless on 3/26/14.
 */
public class NetworkHelp extends BroadcastReceiver {

  private NetworkStateListener onConnected;
  private NetworkStateListener onDisconnected;
  private Context              context;

  public NetworkHelp onConnected(NetworkStateListener listener) {
    onConnected = listener;
    return this;
  }

  public NetworkHelp onDisconnected(NetworkStateListener listener) {
    onDisconnected = listener;
    return this;
  }


  @Override
  public void onReceive(Context context, Intent intent) {
    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    if (info == null) {
      return;
    }

    switch (info.getState()) {
      case CONNECTED:
        onConnected.call();
        break;
      case DISCONNECTED:
        onDisconnected.call();
    }
  }

  public NetworkHelp register(Context context) {
    this.context = context;
    context.registerReceiver(this, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    return this;
  }

  public void unregister() {
    context.unregisterReceiver(this);
  }

  public interface NetworkStateListener {
    void call();
  }
}
