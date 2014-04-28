package com.song1.musicno1.models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import com.google.common.base.Strings;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by kate on 14-3-17.
 */
public class WifiModel {

  public static final int CONNECT_SUCC     = 0;
  public static final int CONNECT_TIME_OUT = 1;
  String ssid;
  String pass;

  Context         context;
  WifiManager     wifiManager;
  ScanListener    scanListener;
  ConnectListener connectListener;
  boolean isConnect = false;

  public interface ScanListener {
    public void scanResult(List<ScanResult> scanResults);
  }

  public interface ConnectListener {
    public void connectResult(String ssid, int state);
  }

  public void setScanListener(ScanListener scanListener) {
    this.scanListener = scanListener;
  }

  public void setConnectListener(ConnectListener connectListener) {
    this.connectListener = connectListener;
  }


  Handler           handler      = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case CONNECT_SUCC:
        case CONNECT_TIME_OUT:
          if (connectListener != null) {
            connectListener.connectResult(ssid, msg.what);
          }
          break;
      }
    }
  };
  BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      Log.d(WifiModel.this, "action : " + action);
      if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
        if (scanListener != null) {
          scanListener.scanResult(wifiManager.getScanResults());
        }
      } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        System.out.println("networkInfo : " + networkInfo.getState());
        if (networkInfo.isConnected()) {
          handler.removeMessages(CONNECT_TIME_OUT);
          handler.sendEmptyMessage(CONNECT_SUCC);
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

  public String getCurrentSSID(){
    try {
      return wifiManager.getConnectionInfo().getSSID();
    }catch (Exception e){
      return null;
    }
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

    this.ssid = ssid;
    this.pass = password;

    if (wifiManager.getConnectionInfo() != null) {
      if (ssid.equals(wifiManager.getConnectionInfo().getSSID())) {
        handler.sendEmptyMessage(CONNECT_SUCC);
        return;
      }
    }
    WifiConfiguration tempConfig = getConfig(ssid);
    if(tempConfig != null)
    {
      wifiManager.removeNetwork(tempConfig.networkId);
    }

    WifiConfiguration wifiConfiguration;
    if (Strings.isNullOrEmpty(password)) {
      wifiConfiguration = newConfig(ssid, password, NO_PASS);
    }else{
      wifiConfiguration = newConfig(ssid, password, WPA);
    }
    System.out.println(wifiConfiguration);
    int id = wifiManager.addNetwork(wifiConfiguration);
    Log.d(this, "id : " + id);
    if (id != -1) {
      handler.sendEmptyMessageDelayed(CONNECT_TIME_OUT,20000);
      boolean b = wifiManager.enableNetwork(id, true);
      System.out.println("connect succ? : " + b);
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

  private WifiConfiguration getConfig(String ssid) {
    System.out.println(ssid);
    List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
    for (WifiConfiguration config : configs) {
      System.out.println(config.SSID);
      if (("\"" + ssid + "\"").equals(config.SSID)) {
        System.out.println(config);
        System.out.println("*****************");
        return config;
      }
    }
    return null;
  }

  public static final int NO_PASS = 0;
  public static final int WEP = 1;
  public static final int WPA = 2;

  private WifiConfiguration newConfig(String SSID, String password, int type) {
    System.out.println("newConfig...");
    WifiConfiguration config = new WifiConfiguration();
    config.allowedAuthAlgorithms.clear();
    config.allowedGroupCiphers.clear();
    config.allowedKeyManagement.clear();
    config.allowedPairwiseCiphers.clear();
    config.allowedProtocols.clear();
    System.out.println("ssid : " + ssid);
    config.SSID = "\"" + SSID + "\"";
    System.out.println("ssid : " + config.SSID);
    if (type == NO_PASS) {
      //config.wepKeys[0] = "";
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      //config.wepTxKeyIndex = 0;
    }else if (type == WEP) {
      config.preSharedKey = "\"" + password + "\"";
      config.hiddenSSID = true;
      config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      config.wepTxKeyIndex = 0;
    }else if (type == WPA) {
      config.preSharedKey = "\"" + password + "\"";
      config.hiddenSSID = true;
      config.allowedAuthAlgorithms
          .set(WifiConfiguration.AuthAlgorithm.OPEN);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
      config.allowedPairwiseCiphers
          .set(WifiConfiguration.PairwiseCipher.TKIP);
      // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      config.allowedPairwiseCiphers
          .set(WifiConfiguration.PairwiseCipher.CCMP);
      config.status = WifiConfiguration.Status.ENABLED;
    } else {
      return null;
    }
    return config;
  }
}
