package com.song1.musicno1.models;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.google.common.collect.Maps;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.std.av.controller.MediaController;

import java.util.Map;

/**
 * Created by kate on 14-3-19.
 */
public class UpnpModel implements DeviceChangeListener {

  private static final int STATUS_DATA_CHNAGE = 0x1;
  static UpnpModel _instance;
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

  private UpnpModel() {
    handlerThread = new HandlerThread("upnp");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
    mediaController = new MediaController();
    mediaController.addDeviceChangeListener(this);
  }

  public static UpnpModel getInstance() {
    if (_instance == null) {
      _instance = new UpnpModel();
    }
    return _instance;
  }

  public void start() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        mediaController.start();
      }
    });
  }

  public void search() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        mediaController.search();
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
        mediaController.stop();
        handlerThread.quit();
        _instance = null;
      }
    });
  }

  public Map<String, Device> getDeviceMap() {
    return deviceMap;
  }

  Map<String, Device> deviceMap = Maps.newHashMap();

  @Override
  public void deviceAdded(Device device) {
    Log.d(this, "deviceAdded...");
    Log.d("发现设备： " + device.getFriendlyName());
    deviceMap.put(device.getFriendlyName(), device);
    if (listener != null) {
      notifyHandler.sendEmptyMessage(STATUS_DATA_CHNAGE);
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
