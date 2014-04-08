package com.song1.musicno1.models.play;

import org.cybergarage.upnp.Device;

/**
 * Created by windless on 14-4-8.
 */
public class MediaServerImpl implements MediaServer {
  protected final Device device;

  public MediaServerImpl(Device device) {
    this.device = device;
  }

  @Override
  public String getName() {
    return device.getFriendlyName();
  }

  @Override
  public String getId() {
    return device.getUDN();
  }
}
