package com.song1.musicno1.models.events.upnp;

import org.cybergarage.upnp.Device;

import java.util.List;

/**
 * Created by windless on 3/27/14.
 */
public class DeviceChangeEvent {
  public List<Device> devices;

  public DeviceChangeEvent(List<Device> devices) {
    this.devices = devices;
  }
}
