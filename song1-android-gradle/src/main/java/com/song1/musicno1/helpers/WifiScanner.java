package com.song1.musicno1.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by windless on 3/26/14.
 */
public class WifiScanner extends BroadcastReceiver {
  private Context      context;
  private ScanListener listener;
  private WifiManager  wifi;

  @Override
  public void onReceive(Context context, Intent intent) {
    context.unregisterReceiver(this);
    List<ScanResult> scanResults = wifi.getScanResults();
    if (listener != null) listener.onScanned(scanResults);
  }

  public WifiScanner bind(Context context) {
    this.context = context;
    this.wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    return this;
  }

  public void scan(ScanListener listener) {
    context.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    this.listener = listener;
    wifi.startScan();
  }

  public interface ScanListener {
    public void onScanned(List<ScanResult> scanResult);
  }
}
