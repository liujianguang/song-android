package com.song1.musicno1.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.App;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.NetworkHelp;
import com.song1.musicno1.models.events.ExitEvent;
import com.song1.musicno1.models.events.upnp.MediaServerEvent;
import com.song1.musicno1.models.events.upnp.SearchDeviceEvent;
import com.song1.musicno1.models.play.*;
import com.song1.musicno1.stores.PlayerStore;
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
import java.util.concurrent.RejectedExecutionException;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM5:14
 */
public class UpnpService extends Service implements DeviceChangeListener {
  @Inject LocalRenderer localRenderer;

  private MediaController           mediaController;
  private NetworkHelp               networkHelp;
  private ExecutorService           executorService;
  private WifiManager.MulticastLock lock;

  private Map<String, com.song1.musicno1.models.play.MediaServer> mediaServerMap = Maps.newHashMap();
  protected PowerManager.WakeLock wakeLock;

  @Override

  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.init();
    App.inject(this);

    MainBus.register(this);

    executorService = Executors.newSingleThreadExecutor();

    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    lock = wifiManager.createMulticastLock("com.song1.musicno1.upnpservice");
    lock.acquire();

    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "upnp_service");
    wakeLock.acquire();

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
    wakeLock.release();
    Log.d(this, "Exit UPnP Service");
  }

  private void startController() {
    try {
      executorService.submit((Runnable) () -> {
        if (mediaController == null) {
          mediaController = new MediaController();
          mediaController.addDeviceChangeListener(this);
        }

        while (!Thread.currentThread().isInterrupted()) {
          try {
            if (mediaController.start()) {
              Log.d(this, "Start control point..");
              mediaController.search();
              break;
            }
          } catch (Exception e) {
            Log.d(this, "Start control point exception", e);
            try {
              Thread.sleep(100);
            } catch (InterruptedException e1) {
              return;
            }
          }
        }
        Log.d(this, "Start control point progress stopped.");
      });
    } catch (RejectedExecutionException ignored) {
    }
  }

  private void stopController() {
    try {
      executorService.submit(() -> {
        if (mediaController != null) {
          mediaController.removeDeviceChangeListener(this);
          mediaController.stop();
          removeAllDevice(mediaController.getDeviceList());
          mediaController = null;
          Log.d(this, "Stop control point...");
        }
      });
    } catch (RejectedExecutionException ignored) {
      Log.d(this, "Stop control point failed");
    }
  }

  private void removeAllDevice(DeviceList deviceList) {
    for (int i = 0; i < deviceList.size(); i++) {
      deviceRemoved(deviceList.getDevice(i));
    }
  }

  @Override
  public void deviceAdded(Device device) {
    System.out.println("deviceAdded...");
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
    RemotePlayer remotePlayer = RemotePlayer.newInstance(device);
    if (remotePlayer != null) {
      PlayerStore.INSTANCE.addPlayer(remotePlayer);
    }
  }

  @Override
  public void deviceRemoved(Device removedDevice) {
    Log.d(this, "Device removed " + removedDevice.getFriendlyName() + " " + removedDevice.getDeviceType());
    PlayerStore.INSTANCE.removePlayerById(removedDevice.getUDN());
  }

  @Subscribe
  public void searchDevice(SearchDeviceEvent event) {
    Log.d(this, "Search device");
    System.out.println("searchDevice....");
    executorService.submit(() -> mediaController.search());
  }

  @Subscribe
  public void onExit(ExitEvent event) {
    stopSelf();
  }
}

