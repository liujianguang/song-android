package com.song1.musicno1.dialogs;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.models.WifiModel;
import org.cybergarage.upnp.Device;

import java.util.List;

/**
 * Created by kate on 14-3-21.
 */
public class DeviceListDialog extends SpecialDialog implements WifiModel.ScanListener {

  @InjectView(R.id.deviceSpinner)
  Spinner spinner;
  @InjectView(R.id.confirm)
  Button  confirmButton;

  @OnClick(R.id.cancle)
  public void cancleClick() {
    dismiss();
  }

  @OnClick(R.id.confirm)
  public void confirmClick() {
    dismiss();
//    wifiModel.connect(spinner.getSelectedItem().toString(),"12345678");
    Object deviceName = spinner.getSelectedItem();
    if (deviceName == null) {
      return;
    }
    deviceSettingDialog = new DeviceSettingDialog(deviceName.toString());
    deviceSettingDialog.show(getFragmentManager(), "deviceFragmentDialg");
  }

  DeviceSettingDialog  deviceSettingDialog;
  List<Device>         currentNetworkDevicelist;
  List<String>         allNetworkDeviceList;
  ArrayAdapter<String> deviceAdapter;
  WifiModel            wifiModel;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.device_list, null);
    ButterKnife.inject(this, view);
    confirmButton.setEnabled(false);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    currentNetworkDevicelist = Lists.newArrayList();
    allNetworkDeviceList = Lists.newArrayList();
    deviceAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, allNetworkDeviceList);
    spinner.setAdapter(deviceAdapter);

    wifiModel = new WifiModel(getActivity());
    wifiModel.setScanListener(this);
    wifiModel.scan();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (wifiModel != null){
      wifiModel.stop();
    }
  }

  @Override
  public void scanResult(List<ScanResult> scanResultList) {
    allNetworkDeviceList.clear();
    for (ScanResult scanResult : scanResultList) {
//      System.out.println(scanResult.SSID);
      if (isDevice(scanResult.SSID)) {
        System.out.println(scanResult.SSID);
        System.out.println(scanResult.BSSID);
        if (!isExist(scanResult.BSSID)) {
          allNetworkDeviceList.add(scanResult.SSID);
        }
      }
      deviceAdapter.notifyDataSetChanged();
      confirmButton.setEnabled(true);
    }
  }

  private boolean isDevice(String ssid){
    if (ssid.startsWith("yy")){
      return true;
    }
    if (ssid.startsWith("Domigo") && ssid.endsWith("ONE"))
    {
      return true;
    }
    return false;
  }

  private boolean isExist(String bssid) {
    bssid.trim();
    bssid = bssid.replaceAll(":", "");
    for (Device device : currentNetworkDevicelist) {
      if (device == null){
        continue;
      }
      if (device.getUDN().contains(bssid)) {
        return true;
      }
    }
    return false;
  }

  public void setCurrentNetworkDevicelist(List<Device> currentNetworkDevicelist) {
    this.currentNetworkDevicelist = currentNetworkDevicelist;
  }
}
