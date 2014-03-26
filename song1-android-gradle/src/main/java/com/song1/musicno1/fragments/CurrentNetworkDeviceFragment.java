package com.song1.musicno1.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.dialogs.DeviceListDialog;
import com.song1.musicno1.dialogs.DeviceSettingDialog;
import com.song1.musicno1.entity.DeviceConfig;
import com.song1.musicno1.models.UpnpModel;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.models.setting.RemoteSetting;
import org.cybergarage.upnp.Device;

import java.util.List;
import java.util.Map;


/**
 * Created by kate on 14-3-19.
 */
public class CurrentNetworkDeviceFragment extends Fragment implements UpnpModel.UpnpChangeListener, WifiModel.WifiModleListener {

  private static final int STATUS_CONNECT_SUCC = 0;
  private static final int STATUS_SHOW_DIALOG  = 1;
  private static final int STATUS_HIDE_DIALOG  = 2;
  @InjectView(R.id.titleIconView)
  ImageView titleIconView;
  @InjectView(R.id.titleTextView)
  TextView  titleTextView;

  @InjectView(R.id.gridView)
  GridView gridView;

  WifiManager  wifiManager;
  WifiInfo     wifiInfo;
  List<Device> dataList;
  ViewAdapter  viewAdapter;

  WifiModel           wifiModel;
  UpnpModel           upnpModel;
  RemoteSetting       remoteSetting;
  DeviceListDialog    deviceListDialog;
  DeviceSettingDialog deviceSettingDialog;
  ProgressDialog      progressDialog;


  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case STATUS_CONNECT_SUCC:
          load();
          break;
        case STATUS_SHOW_DIALOG:
          progressDialog.show();
          break;
        case STATUS_HIDE_DIALOG:
          progressDialog.hide();
          break;
        case 3:
          String message = msg.obj.toString();
          progressDialog.setMessage(message);
          break;
      }
    }
  };

  OnCancelListener cancelListener = new OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }
  };

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.current_network_device, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    dataList = Lists.newArrayList();
    viewAdapter = new ViewAdapter();
    gridView.setAdapter(viewAdapter);

    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setMessage("连接新设备中...");
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setOnCancelListener(cancelListener);

    wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
    wifiModel = new WifiModel(getActivity());
    wifiModel.setListener(this);
    load();
  }

  private void load() {
    dataList.clear();
    dataList.add(null);
    viewAdapter.notifyDataSetChanged();
    if (upnpModel != null) {
      upnpModel.stop();
    }
    upnpModel = new UpnpModel();
    upnpModel.setListener(this);
    upnpModel.start();
    upnpModel.search();
    wifiInfo = wifiManager.getConnectionInfo();
    if (wifiInfo != null) {
      titleTextView.setText(wifiInfo.getSSID());
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (upnpModel != null) {
      upnpModel.stop();
    }
    if (wifiModel != null) {
      wifiModel.stop();
    }
  }

  @Override
  public void onUpnpChangeListener(Map<String, Device> deviceMap) {
    dataList.clear();
    dataList.addAll(deviceMap.values());
    dataList.add(null);
    viewAdapter.notifyDataSetChanged();
  }

  @Override
  public void scanResult(List<ScanResult> scanResultList) {

  }

  @Override
  public void connectSucc() {
    canMove = true;
  }

  class ViewAdapter extends BaseAdapter {

    @Override
    public int getCount() {
      return dataList.size();
    }

    @Override
    public Object getItem(int i) {
      return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
      return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      ViewHolder viewHolder;
      if (view == null) {
        view = getActivity().getLayoutInflater().inflate(R.layout.device, null);
        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) view.getTag();
      }
      Device device = (Device) getItem(i);

      if (device == null) {
        viewHolder.deviceLayout.setTag(null);
        viewHolder.deviceIcon.setImageResource(R.drawable.addnewdevice_ic_butoon_normal);
        viewHolder.deviceName.setText(getString(R.string.newDevice));
        return view;
      }

      viewHolder.deviceLayout.setTag(device);
      viewHolder.deviceIcon.setImageResource(R.drawable.kids_room_ic_butoon_normal);
      viewHolder.deviceName.setText(device.getFriendlyName());
      return view;
    }
  }

  class ViewHolder implements DeviceListDialog.OnClickListener {
    @InjectView(R.id.deviceLayout)
    LinearLayout deviceLayout;

    @InjectView(R.id.deviceIcon)
    ImageView deviceIcon;
    @InjectView(R.id.deviceName)
    TextView  deviceName;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.deviceLayout)
    public void deviceClick(View view) {
      final Device device = (Device) view.getTag();
      System.out.println("device : " + device);
      if (device == null) {
        deviceListDialog = new DeviceListDialog();
        deviceListDialog.setCurrentNetworkDevicelist(dataList);
        deviceListDialog.setListener(this);
        deviceListDialog.show(getFragmentManager(), "deviceListDialog");
      } else {
//        upnpModel.setAVTransport(device);
        upnpModel.play(device);
      }
    }

    @Override
    public void onCancle() {
      deviceListDialog.dismiss();
    }

    @Override
    public void onConfirm(String ssid) {
      deviceListDialog.dismiss();
      deviceSettingDialog = new DeviceSettingDialog(ssid);
      deviceSettingDialog.setListener(new DeviceSettingDialog.OnClickListener() {
        @Override
        public void onCancle() {
          deviceSettingDialog.dismiss();

        }

        @Override
        public void onConfirm(DeviceConfig deviceConfig) {
          deviceSettingDialog.dismiss();
          connectDevice(deviceConfig);
        }
      });
      deviceSettingDialog.show(getFragmentManager(), "deviceFragmentDialg");
    }
  }

  boolean canMove = true;

  private void connectDevice(final DeviceConfig deviceConfig) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        handler.sendEmptyMessage(STATUS_SHOW_DIALOG);
        connectDevice(deviceConfig.getSsid());
        setDevice(deviceConfig);
        connectWifi(deviceConfig.getWifiSsid(), deviceConfig.getWifiPass());
        handler.sendEmptyMessage(STATUS_HIDE_DIALOG);
        handler.sendEmptyMessage(STATUS_CONNECT_SUCC);
      }
    }).start();
  }

  private void connectDevice(String ssid) {
    System.out.println("start connect : " + ssid);
    canMove = false;
    wifiModel.connect(ssid, "12345678");
    while (!canMove) {
    }
    System.out.println("end connect : " + ssid);
  }

  private void setDevice(DeviceConfig deviceConfig) {
    RemoteSetting remoteSetting = new RemoteSetting("192.168.198.1");
    int mode = remoteSetting.getCurrentMode();
    System.out.println("mode : " + remoteSetting.getCurrentMode());
    boolean isSuc = remoteSetting.setCurrentMode(false);
    System.out.println("isSuc : " + isSuc);
    isSuc = remoteSetting.setSsidAndPass(deviceConfig.getWifiSsid(), deviceConfig.getWifiPass());
    System.out.println("isSuc : " + isSuc);
    canMove = false;
    while(!canMove){

    }
  }

  private void connectWifi(String ssid, String pass) {
    System.out.println("start connect : " + ssid);
    canMove = false;
    wifiModel.connect(ssid, pass);
    while (!canMove) {
    }
    System.out.println("end connect : " + ssid);
  }
}
