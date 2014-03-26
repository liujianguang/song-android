package com.song1.musicno1.models;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.google.common.collect.Maps;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.std.av.controller.MediaController;
import org.cybergarage.upnp.std.av.server.ConnectionManager;
import org.cybergarage.upnp.std.av.server.object.item.UrlItemNode;

import java.util.Map;

/**
 * Created by kate on 14-3-19.
 */
public class UpnpModel implements DeviceChangeListener {

  private static final int STATUS_DATA_CHNAGE = 0x1;

  Map<String, Device> deviceMap = Maps.newHashMap();
  String              url       = "http://sc.111ttt.com/up/mp3/303005/B610D4BECEE0DFCE5191ABE874C09C83.mp3";
  MediaController mediaController;
  HandlerThread   handlerThread;
  Handler         handler;

  Handler notifyHandler = new Handler() {
    @Override
    public void handleMessage(Message message) {
      switch (message.what) {
        case STATUS_DATA_CHNAGE:
          listener.onUpnpChangeListener(deviceMap);
          break;
      }
    }
  };

  public UpnpModel() {
    handlerThread = new HandlerThread("upnp");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
    mediaController = new MediaController();
    mediaController.addDeviceChangeListener(this);
  }

  public void start() {

    handler.post(new Runnable() {
      @Override
      public void run() {
        System.out.println("start...");
        boolean b = mediaController.start();
        System.out.println("is start success : " + b);
      }
    });
  }

  public void stop() {
    if (handler == null || mediaController == null) {
      return;
    }
    handler.post(new Runnable() {
      @Override
      public void run() {
        System.out.println("stop...");
        mediaController.stop();
        handlerThread.quit();
      }
    });
  }

  public void search() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        System.out.println("search...");
        mediaController.search();
      }
    });
  }

  public void setAVTransport(final Device dev) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        UrlItemNode itemNode = new UrlItemNode();
        itemNode.setUPnPClass("object.item.videoItem.music");
        String mimeType = "audio/mp3";
        String protocol = ConnectionManager.HTTP_GET + ":*:" + mimeType + ":*";
        itemNode.setUrl(url);
        itemNode.setResource(url, protocol);
        boolean b = mediaController.setAVTransportURI(dev, itemNode);
        System.out.println("isSuc : " + b);
      }
    });
  }

  public void play(final Device dev) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        UrlItemNode itemNode = new UrlItemNode();
        itemNode.setUPnPClass("object.item.videoItem.music");
        String mimeType = "audio/mp3";
        String protocol = ConnectionManager.HTTP_GET + ":*:" + mimeType + ":*";
        itemNode.setUrl(url);
        itemNode.setResource(url, protocol);
        boolean b = mediaController.play(dev,itemNode);
        System.out.println("isSuc : " + b);
      }
    });
  }

  @Override
  public void deviceAdded(Device device) {
    Log.d(this, "deviceAdded...");
    Log.d("发现设备： " + device.getFriendlyName());
    deviceMap.put(device.getFriendlyName(), device);
    if (listener != null) {
      notifyHandler.sendEmptyMessage(STATUS_DATA_CHNAGE) ;
    }
  }

  @Override
  public void deviceRemoved(Device device) {
    Log.d("设备离线： " + device.getFriendlyName());
    deviceMap.remove(device.getFriendlyName());
    if (listener != null) {
      notifyHandler.sendEmptyMessage(STATUS_DATA_CHNAGE);
    }
  }

  private UpnpChangeListener listener;

  public void setListener(UpnpChangeListener listener) {
    this.listener = listener;
  }

  public interface UpnpChangeListener {
    void onUpnpChangeListener(Map<String, Device> deviceMap);
  }
}
