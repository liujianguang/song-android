package com.song1.musicno1.models.play;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.controller.MediaController;
import org.cybergarage.upnp.std.av.renderer.AVTransport;
import org.cybergarage.upnp.std.av.server.object.item.UrlItemNode;

/**
 * Created by windless on 3/26/14.
 */
public class RemoteRenderer implements Renderer {
  private final MediaController controller;
  private final Device          device;

  public RemoteRenderer(MediaController controller, Device device) {
    this.controller = controller;
    this.device = device;
  }


  @Override
  public boolean setUri(String uri) {
    UrlItemNode node = new UrlItemNode();
    node.setUrl(uri);
    node.setUPnPClass("object.item.videoItem.music");
    return controller.setAVTransportURI(device, node);

  }

  @Override
  public boolean play() {
    return controller.play(device);
  }

  @Override
  public boolean pause() {
    return controller.pause(device);
  }

  @Override
  public boolean isPlaying() {
    String state = controller.getTransportState(device);
    return state != null && state.equals(AVTransport.PLAYING);
  }

  @Override
  public PositionInfo getPositionInfo() {
    return null;
  }
}
