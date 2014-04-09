package com.song1.musicno1.models.play;

import com.google.common.collect.Lists;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.controller.MediaController;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;

import java.util.List;

/**
 * Created by windless on 14-4-8.
 */
public class MediaServerImpl implements MediaServer {
  protected final Device          device;
  protected final MediaController mediaController;

  public MediaServerImpl(MediaController mediaController, Device device) {
    this.mediaController = mediaController;
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

  @Override
  public List<Object> browse(String id) {
    ContainerNode node = mediaController.browse(device, id);
    List<Object> nodes = Lists.newArrayList();
    List<Audio> audios = Lists.newArrayList();

    for (int i = 0; i < node.getChildCount(); i++) {
      ContentNode contentNode = node.getContentNode(i);
      if (contentNode.isItemNode()) {
        ItemNode itemNode = (ItemNode) contentNode;
        Audio audio = new Audio();
        audio.setTitle(contentNode.getTitle());
        audio.setFrom(Audio.OTHER);
        if (itemNode.getResourceNodeList().size() > 0) {
          audio.setLocalPlayUri(itemNode.getResourceNode(0).getURL());
          audio.setRemotePlayUrl(itemNode.getResourceNode(0).getURL());
        }
        audios.add(audio);
      } else if (node.isContainerNode()) {
        MediaNode mediaNode = new MediaNode();
        mediaNode.setTitle(contentNode.getTitle());
        mediaNode.setId(contentNode.getID());
        nodes.add(mediaNode);
      }
    }
    nodes.addAll(audios);
    return nodes;
  }
}
