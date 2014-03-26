package com.song1.musicno1.models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.google.common.base.Strings;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by kate on 14-3-17.
 */
public class WifiModel {

  String ssid;
  String pass;

  Context           context;
  WifiManager       wifiManager;
  WifiModleListener listener;
  boolean isConnect = false;

  public interface WifiModleListener {
    public void scanResult(List<ScanResult> scanResultList);

    public void connectSucc();
  }

  BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      Log.d(WifiModel.this, "action : " + action);
      if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
        if (listener != null) {
          listener.scanResult(wifiManager.getScanResults());
        }
      } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        System.out.println("networkInfo : " + networkInfo.getState());
        if (networkInfo.isConnected()) {
          isConnect = false;
          ;
          if (listener != null) {
            listener.connectSucc();
          }
        }
      }
    }
  };

  public WifiModel(Context context) {
    this.context = context;
    wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    context.registerReceiver(wifiReceiver, intentFilter);
  }

  public void scan() {
    wifiManager.startScan();
  }

  public void stop() {
    context.unregisterReceiver(wifiReceiver);
  }

  public void connect(final String ssid, final String password) {
    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
      openWifi();
    }
    if (wifiManager.getConnectionInfo() != null) {
      if (ssid.equals(wifiManager.getConnectionInfo().getSSID())) {
        if (listener != null) {
          listener.connectSucc();
        }
        return;
      }
    }
    if (isConnect) {
      return;
    }
    this.ssid = ssid;
    this.pass = password;
    isConnect = true;
    WifiConfiguration wifiConfiguration = getConfig(ssid, password);
    int id = wifiManager.addNetwork(wifiConfiguration);
    if (id != -1) {
      wifiManager.enableNetwork(id, true);
    }
  }

  public void openWifi() {
    wifiManager.setWifiEnabled(true);
    while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private WifiConfiguration getConfig(String ssid, String password) {
    ssid = "\"" + ssid + "\"";
    System.out.println(ssid);
    List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
    for (WifiConfiguration config : configs) {
      if (ssid.equals(config.SSID)) {
        System.out.println("*****************");
        return config;
      }
    }
    return newConfig(ssid, password);
  }

  private WifiConfiguration newConfig(String ssid, String password) {
    WifiConfiguration config = new WifiConfiguration();

    config.SSID = "\"" + ssid + "\"";
    config.status = WifiConfiguration.Status.ENABLED;

    if (!Strings.isNullOrEmpty(password)) {
    } else {
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      config.preSharedKey = "\"" + password + "\"";
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
      config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
      config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

    }
    return config;
  }

  public void setListener(WifiModleListener listener) {
    this.listener = listener;
  }
}
