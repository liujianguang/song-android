package com.song1.musicno1.dialogs;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.constants.Constants;
import com.song1.musicno1.entity.DeviceConfig;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.models.events.upnp.DeviceChangeEvent;
import com.song1.musicno1.models.events.upnp.SearchDeviceEvent;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.setting.RemoteSetting;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;

import java.util.List;

/**
 * Created by kate on 14-3-17.
 */
public class DeviceSettingDialog extends SpecialDialog implements WifiModel.ConnectListener, WifiModel.ScanListener {


  private static final int STATUS_CONNECT_DEVICE = 1;

  private static final int STATUS_CONNECT_WIFI            = 3;
  private static final int STATUS_CONNECT_DEVICE_TIMEOUT  = 4;
  private static final int STATUS_CONNECT_WIFI_TIMEOUT    = 5;
  private static final int STATUS_CONNECT_DEVICE_SUCC     = 6;
  private static final int STATUS_CONNECT_WIFI_SUCC       = 7;
  private static final int STATUS_SET_DEVICE              = 8;
  private static final int STATUS_SET_DEVICE_FINISH       = 9;
  private static final int STATUS_WAIT_DEVICE_SET_SUCCESS = 10;

  @InjectView(R.id.titleTextView)       TextView    titleTextView;
  @InjectView(R.id.deviceNameSpinner)   Spinner     deviceNameSpinner;
  @InjectView(R.id.networkSpinner)      Spinner     networkSpinner;
  @InjectView(R.id.networkPassEditText) EditText    networkPassView;
  @InjectView(R.id.confirm)             Button      confirm;
  @InjectView(R.id.status)              TextView    status;
  @InjectView(R.id.progressBar)         ProgressBar progressBar;

  List<String> deviceNameList = Lists.newArrayList();
  List<String> networkList    = Lists.newArrayList();

  ArrayAdapter deviceNameAdapter;
  ArrayAdapter networkAdapter;

  Context   context;
  WifiModel wifiModel;

  String currentWifiSSID;
  String deviceSSID;
  DeviceConfig deviceConfig = null;

  boolean firstScan = true;
  Handler handler   = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case STATUS_CONNECT_DEVICE:
          status.setText(getString(R.string.connectDeviceMsg));
          break;
        case STATUS_CONNECT_DEVICE_SUCC:
          status.setText(getString(R.string.deviceConnectSucc));
          setDevice();
          break;
        case STATUS_CONNECT_DEVICE_TIMEOUT:
          status.setText(getString(R.string.deviceConnectTimeout));
          break;
        case STATUS_SET_DEVICE:
          status.setText(getString(R.string.setDeviceMsg));
          progressBar.setProgress(20);
          break;
        case STATUS_SET_DEVICE_FINISH:
          startConnect(deviceConfig.getWifiSsid(), deviceConfig.getWifiPass());
          break;
        case STATUS_CONNECT_WIFI:
          status.setText(getString(R.string.connectWifiMsg));
          progressBar.setProgress(40);
          break;
        case STATUS_CONNECT_WIFI_SUCC:
          status.setText(getString(R.string.WifiConnectSucc));
          progressBar.setProgress(60);
          register();
          MainBus.post(new SearchDeviceEvent(MediaRenderer.DEVICE_TYPE));
          status.setText(getString(R.string.wait_device_set_succ));
          break;
        case STATUS_CONNECT_WIFI_TIMEOUT:
          status.setText(getString(R.string.WifiConnectTimeout));
          break;
        case STATUS_WAIT_DEVICE_SET_SUCCESS:
          unregister();
          dismiss();
          break;
      }
    }
  };

  public DeviceSettingDialog(String ssid) {
    this.deviceSSID = ssid;
  }

  @OnClick(R.id.cancle)
  public void cancleClick() {
    stopConnect();
  }

  @OnClick(R.id.confirm)
  public void confirmClick() {
    setEnable(false);
    deviceConfig = new DeviceConfig();
    deviceConfig.setFriendlyName(deviceNameSpinner.getSelectedItem().toString());
    deviceConfig.setSsid(deviceSSID);
    deviceConfig.setWifiSsid(networkSpinner.getSelectedItem().toString());
    deviceConfig.setWifiPass(networkPassView.getText().toString());
    startConnect(deviceConfig.getSsid(), null);
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getActivity();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(this, "onActivityCreated...");
    super.onActivityCreated(savedInstanceState);

    ButterKnife.inject(this, getView());
    titleTextView.setText(deviceSSID);
    progressBar.setMax(80);
    confirm.setEnabled(false);
    deviceNameList = Lists.newArrayList(getResources().getStringArray(R.array.deviceNames));
    deviceNameAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, deviceNameList);
    networkAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, networkList);

    deviceNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    deviceNameSpinner.setAdapter(deviceNameAdapter);
    networkSpinner.setAdapter(networkAdapter);

    wifiModel = new WifiModel(getActivity());
    wifiModel.setConnectListener(this);
    wifiModel.setScanListener(this);
    wifiModel.scan();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d(this, "onCreateView...");
    return inflater.inflate(R.layout.device_setting, null);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (wifiModel != null) {
      wifiModel.stop();
    }
  }

  @Override
  public void scanResult(List<ScanResult> scanResultList) {
    networkList.clear();
    for (ScanResult scanResult : scanResultList) {
      if (isDevice(scanResult.SSID)) {
        continue;
      }
      networkList.add(scanResult.SSID);
    }

//      deviceAdapter.notifyDataSetChanged();
    networkAdapter.notifyDataSetChanged();
    if (deviceConfig != null && deviceConfig.getWifiSsid() != null) {
      networkSpinner.setSelection(networkList.indexOf(deviceConfig.getWifiSsid()));
    }

    if (firstScan) {
      confirm.setEnabled(true);
      firstScan = false;
    }
  }

  private boolean isDevice(String ssid) {
    if (ssid.startsWith("yy")) {
      return true;
    }
    if (ssid.startsWith("Domigo") && ssid.endsWith("ONE")) {
      return true;
    }
    return false;
  }

  @Override
  public void connectResult(String ssid, int state) {
    System.out.println("ssid : " + ssid + " ,state :" + state);
    if (ssid == null) {
      return;
    }
    if (handler == null) {
      return;
    }
    switch (state) {
      case WifiModel.CONNECT_SUCC:
        if (ssid.equals(deviceConfig.getSsid())) {
          handler.sendEmptyMessage(STATUS_CONNECT_DEVICE_SUCC);
        } else {
          handler.sendEmptyMessage(STATUS_CONNECT_WIFI_SUCC);
        }
        break;
      case WifiModel.CONNECT_TIME_OUT:
        if (ssid.equals(deviceConfig.getSsid())) {
          handler.sendEmptyMessage(STATUS_CONNECT_DEVICE_TIMEOUT);
        } else {
          handler.sendEmptyMessage(STATUS_CONNECT_WIFI_TIMEOUT);
        }
        setEnable(true);
        break;
    }
  }

  public void setEnable(boolean isEnable) {
    getDialog().setCanceledOnTouchOutside(isEnable);
    deviceNameSpinner.setEnabled(isEnable);
    networkSpinner.setEnabled(isEnable);
    networkPassView.setEnabled(isEnable);
    confirm.setEnabled(isEnable);
  }

  private void startConnect(String ssid, String password) {
    System.out.println("start connect : " + ssid);
    if (ssid.equals(deviceConfig.getSsid())) {
      handler.sendEmptyMessage(STATUS_CONNECT_DEVICE);
    } else {
      handler.sendEmptyMessage(STATUS_CONNECT_WIFI);
    }
    handler.post(new Runnable() {
      @Override
      public void run() {
        wifiModel.connect(ssid, password);
      }
    });
  }

  private void setDevice() {
    handler.sendEmptyMessage(STATUS_SET_DEVICE);
    new Thread(new Runnable() {
      @Override
      public void run() {
        RemoteSetting remoteSetting = new RemoteSetting("192.168.198.1");
        //System.out.println("mode : " + remoteSetting.getCurrentMode());
        //System.out.println("ssid : " + remoteSetting.getCurrentSSID());
        //System.out.println("name : " + remoteSetting.getDeviceName());
        remoteSetting.setDeviceName(deviceConfig.getFriendlyName());
        remoteSetting.setCurrentMode(false);
        remoteSetting.setSsidAndPass(deviceConfig.getWifiSsid(), deviceConfig.getWifiPass());
        handler.sendEmptyMessage(STATUS_SET_DEVICE_FINISH);
      }
    }).start();
  }

  private void waitDeviceSetSucc() {
    //handler.sendEmptyMessageDelayed(STATUS_SET_DEVICE_SUCC, 12000);
//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        while(true) {
//          RemoteSetting remoteSetting = new RemoteSetting("192.168.198.1");
//
//          System.out.println("mode : " + remoteSetting.getCurrentMode());
//          System.out.println("ssid : " + remoteSetting.getCurrentSSID());
//          System.out.println("name : " + remoteSetting.getDeviceName());
//          try {
//            Thread.sleep(1000);
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
//        }
//      }
//    });
  }

  private void stopConnect() {
    handler = null;
    this.dismiss();
    try {
      throw new Exception("stop connect!");
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

  private void register(){
    MainBus.register(this);
  }
  private void unregister(){
    MainBus.unregister(this);
  }
  @Subscribe
  public void onDeviceChanged(DeviceChangeEvent event) {
//    currentWifiSSID = wifiModel.getCurrentSSID();
//    if (deviceSSID.equals(currentWifiSSID)) {
//      return;
//    }
    List<Player> players = Lists.newArrayList(event.players);
    String tempId;
    if (deviceSSID.startsWith("yy")) {
      tempId = deviceSSID.substring(2, deviceSSID.length());
    } else {
      tempId = deviceSSID.substring(deviceSSID.indexOf("-") + 1, deviceSSID.indexOf("ONE"));
    }
    for (Player player : players) {
      String id = player.getId();
      //System.out.println("id : " + id);
      //System.out.println("tempId : " + tempId.toLowerCase());
      if (id.contains(tempId.toLowerCase())) {
        handler.sendEmptyMessage(STATUS_WAIT_DEVICE_SET_SUCCESS);
      }
    }
  }
}
