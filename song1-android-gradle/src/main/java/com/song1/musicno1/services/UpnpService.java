package com.song1.musicno1.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.App;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.NetworkHelp;
import com.song1.musicno1.models.events.ExitEvent;
import com.song1.musicno1.models.events.play.RemoteRenderingControl;
import com.song1.musicno1.models.events.upnp.DeviceChangeEvent;
import com.song1.musicno1.models.events.upnp.MediaServerEvent;
import com.song1.musicno1.models.events.upnp.SearchDeviceEvent;
import com.song1.musicno1.models.play.*;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.std.av.controller.MediaController;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;
import org.cybergarage.upnp.std.av.server.MediaServer;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM5:14
 */
public class UpnpService extends Service implements DeviceChangeListener {
  @Inject LocalRenderer localRenderer;

  private Player                    localPlayer;
  private MediaController           mediaController;
  private NetworkHelp               networkHelp;
  private ExecutorService           executorService;
  private WifiManager.MulticastLock lock;
  private List8<Player>             playerList;

  private Map<String, com.song1.musicno1.models.play.MediaServer> mediaServerMap = Maps.newHashMap();

  @Override

  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.init();
    App.inject(this);

    localPlayer = new Player(this, localRenderer, new LocalRenderingControl(this));
    playerList = List8.newList();
    playerList.add(localPlayer);

    MainBus.register(this);

    executorService = Executors.newSingleThreadExecutor();

    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    lock = wifiManager.createMulticastLock("com.song1.musicno1.upnpservice");
    lock.acquire();

    networkHelp = new NetworkHelp();
    networkHelp.onConnected(() -> startController())
        .onDisconnected(() -> stopController())
        .register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MainBus.unregister(this);
    networkHelp.unregister();
    lock.release();
    stopController();
    executorService.shutdown();
  }

  private void startController() {
    executorService.submit((Runnable) () -> {
      if (mediaController == null) {
        mediaController = new MediaController();
        mediaController.addDeviceChangeListener(this);
      }

      while (true) {
        try {
          if (mediaController.start()) {
            Log.d(this, "Start control point..");
            mediaController.search();
            break;
          }
        } catch (Exception e) {
          Log.d(this, "Start control point exception", e);
        }
      }
    });
  }

  private void stopController() {
    executorService.submit(() -> {
      if (mediaController != null) {
        mediaController.removeDeviceChangeListener(this);
        mediaController.stop();
        removeAllDevice(mediaController.getDeviceList());
        mediaController = null;
        Log.d(this, "Stop control point...");
      }
    });
  }

  private void removeAllDevice(DeviceList deviceList) {
    for (int i = 0; i < deviceList.size(); i++) {
      deviceRemoved(deviceList.getDevice(i));
    }
  }

  @Override
  public void deviceAdded(Device device) {
    if (MediaRenderer.DEVICE_TYPE.equals(device.getDeviceType())) {
      addMediaRenderer(device);
    } else if (MediaServer.DEVICE_TYPE.equals(device.getDeviceType())) {
      addMediaServer(device);
    }
  }

  private void addMediaServer(Device device) {
    Log.d(this, "Device added " + device.getFriendlyName() + " " + device.getDeviceType());
    mediaServerMap.put(device.getUDN(), new MediaServerImpl(mediaController, device));
    MainBus.post(mediaServers());
  }

  @Produce
  public MediaServerEvent mediaServers() {
    return new MediaServerEvent(Lists.newArrayList(mediaServerMap.values()));
  }

  private void addMediaRenderer(Device device) {
    Log.d(this, "Device added " + device.getFriendlyName() + " " + device.getDeviceType());
    playerList.add(new Player(this, new RemoteRenderer(device), new RemoteRenderingControl(device)));
    MainBus.post(produceDeviceList());
  }

  @Override
  public void deviceRemoved(Device removedDevice) {
    Log.d(this, "Device removed " + removedDevice.getFriendlyName() + " " + removedDevice.getDeviceType());
    playerList.deleteIf((player) -> player.getId().equals(removedDevice.getUDN()));
    MainBus.post(produceDeviceList());
  }

  @Produce
  public DeviceChangeEvent produceDeviceList() {
    Log.d(this, "Produce device list");
    return new DeviceChangeEvent(playerList);
  }

  @Subscribe
  public void searchDevice(SearchDeviceEvent event) {
    Log.d(this, "Search device");
    executorService.submit(() -> mediaController.search());
  }

  @Subscribe
  public void onExit(ExitEvent event) {
    stopSelf();
  }
}

