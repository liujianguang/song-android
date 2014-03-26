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
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.DeviceConfig;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.models.setting.RemoteSetting;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by kate on 14-3-17.
 */
public class DeviceSettingDialog extends BaseDialog implements WifiModel.WifiModleListener {


  private static final int STATUS_CONNECT_SUCC = 0;
  private static final int STATUS_CONNECT_DEVICE  = 1;
  private static final int STATUS_SET_DEVICE  = 2;
  private static final int STATUS_CONNECT_WIFI = 3;

  @InjectView(R.id.titleTextView)       TextView titleTextView;
  @InjectView(R.id.deviceNameSpinner)   Spinner  deviceNameSpinner;
  @InjectView(R.id.networkSpinner)      Spinner  networkSpinner;
  @InjectView(R.id.networkPassEditText) EditText networkPassView;
  @InjectView(R.id.confirm)             Button   confirm;
  @InjectView(R.id.status) TextView status;
  @InjectView(R.id.progressBar) ProgressBar progressBar;

  List<String> deviceNameList = Lists.newArrayList();
  List<String> networkList    = Lists.newArrayList();

  ArrayAdapter deviceNameAdapter;
  ArrayAdapter networkAdapter;

  Context   context;
  WifiModel wifiModel;

  String ssid;
  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case STATUS_CONNECT_SUCC:
          progressBar.setProgress(90);
          dismiss();
          break;
        case STATUS_CONNECT_DEVICE:
          status.setText(getString(R.string.connectDeviceMsg));
          break;
        case STATUS_SET_DEVICE:
          status.setText(getString(R.string.setDeviceMsg));
          progressBar.setProgress(30);
          break;
        case STATUS_CONNECT_WIFI:
          status.setText(getString(R.string.connectWifiMsg));
          progressBar.setProgress(60);
          break;
      }
    }
  };

  public DeviceSettingDialog(String ssid) {
    this.ssid = ssid;
  }

  @OnClick(R.id.cancle)
  public void cancleClick() {
    stopConnect();
  }

  @OnClick(R.id.confirm)
  public void confirmClick() {
    setEnable();
    DeviceConfig deviceConfig = new DeviceConfig();
    deviceConfig.setSsid(ssid);
    deviceConfig.setWifiSsid(networkSpinner.getSelectedItem().toString());
    deviceConfig.setWifiPass(networkPassView.getText().toString());
    startConnect(deviceConfig);
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
    titleTextView.setText(ssid);
    progressBar.setMax(90);
    deviceNameAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, deviceNameList);
    networkAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, networkList);
    deviceNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    deviceNameSpinner.setAdapter(deviceNameAdapter);
    networkSpinner.setAdapter(networkAdapter);

    wifiModel = new WifiModel(getActivity());
    wifiModel.setListener(this);
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
//    wifiModel.removeScanListener(this);
    if (wifiModel != null){
      wifiModel.stop();
    }
  }

  @Override
  public void scanResult(List<ScanResult> scanResultList) {
    networkList.clear();
    for (ScanResult scanResult : scanResultList) {
      if (scanResult.SSID.startsWith("yy")) {
//        deviceList.add(scanResult.SSID);
      } else {
        networkList.add(scanResult.SSID);
      }
//      deviceAdapter.notifyDataSetChanged();
      networkAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void connectSucc() {
    canMove = true;
  }
  public void setEnable(){
    getDialog().setCanceledOnTouchOutside(false);
    deviceNameSpinner.setEnabled(false);
    networkSpinner.setEnabled(false);
    networkPassView.setEnabled(false);
    confirm.setEnabled(false);
  }

  boolean canMove = true;

  private void startConnect(final DeviceConfig deviceConfig) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        connectDevice(deviceConfig.getSsid());
        setDevice(deviceConfig);
        connectWifi(deviceConfig.getWifiSsid(), deviceConfig.getWifiPass());
        handler.sendEmptyMessage(STATUS_CONNECT_SUCC);
      }
    }).start();
  }

  private void connectDevice(String ssid) {
    handler.sendEmptyMessage(STATUS_CONNECT_DEVICE);
    System.out.println("start connect : " + ssid);
    canMove = false;
    wifiModel.connect(ssid, "12345678");
    while (!canMove) {
    }
    System.out.println("end connect : " + ssid);
  }

  private void setDevice(DeviceConfig deviceConfig) {
    handler.sendEmptyMessage(STATUS_SET_DEVICE);
    RemoteSetting remoteSetting = new RemoteSetting("192.168.198.1");
    int mode = remoteSetting.getCurrentMode();
    System.out.println("mode : " + remoteSetting.getCurrentMode());
    boolean isSuc = remoteSetting.setCurrentMode(false);
    System.out.println("isSuc : " + isSuc);
    isSuc = remoteSetting.setSsidAndPass(deviceConfig.getWifiSsid(), deviceConfig.getWifiPass());
    System.out.println("isSuc : " + isSuc);
    while(true){
      Optional<String> ssid = remoteSetting.getCurrentSSID();
      System.out.println("***********************" + ssid);
      if (ssid.isPresent() && ssid.get().equals(deviceConfig.getWifiSsid())) {
        break;
      }
//      System.out.println("***********************" + ssid.get());
    }
  }

  private void connectWifi(String ssid, String pass) {
    handler.sendEmptyMessage(STATUS_CONNECT_WIFI);
    System.out.println("start connect : " + ssid);
    canMove = false;
    wifiModel.connect(ssid, pass);
    while (!canMove) {
    }
    System.out.println("end connect : " + ssid);
  }
  private void stopConnect(){
    this.dismiss();
    try
    {
      throw new Exception("stop connect!");
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
