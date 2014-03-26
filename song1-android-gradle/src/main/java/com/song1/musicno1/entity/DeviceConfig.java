package com.song1.musicno1.entity;

/**
 * Created by kate on 14-3-22.
 */
public class DeviceConfig {

  private String ssid;
  private String pass;
  private String friendlyName;
  private String wifiSsid;
  private String wifiPass;

  public String getSsid() {
    return ssid;
  }

  public void setSsid(String ssid) {
    this.ssid = ssid;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public String getFriendlyName() {
    return friendlyName;
  }

  public void setFriendlyName(String friendlyName) {
    this.friendlyName = friendlyName;
  }

  public String getWifiSsid() {
    return wifiSsid;
  }

  public void setWifiSsid(String wifiSsid) {
    this.wifiSsid = wifiSsid;
  }

  public String getWifiPass() {
    return wifiPass;
  }

  public void setWifiPass(String wifiPass) {
    this.wifiPass = wifiPass;
  }
}
