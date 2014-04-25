package com.song1.musicno1.util;

import android.net.wifi.ScanResult;
import com.google.common.collect.Lists;
import org.cybergarage.upnp.Device;

import java.util.List;

/**
 * Created by leovo on 2014/4/25.
 */
public class DeviceUtil {


  public static List<String> filterScanResultList(List<ScanResult> scanResultList){
    List<String> newList = Lists.newArrayList();
    for (ScanResult scanResult : scanResultList) {
//      System.out.println(scanResult.SSID);
      if (isDevice(scanResult.SSID)) {
        System.out.println(scanResult.SSID);
        System.out.println(scanResult.BSSID);
        newList.add(scanResult.SSID);
      }
    }
    return newList;
  }
  private static boolean isDevice(String ssid){
    if (ssid.startsWith("yy")){
      return true;
    }
    if (ssid.startsWith("Domigo") && ssid.endsWith("ONE"))
    {
      return true;
    }
    return false;
  }
}
