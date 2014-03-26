package com.song1.musicno1.dialogs;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
public class DeviceListDialog extends DialogFragment implements WifiModel.WifiModleListener {

  @InjectView(R.id.deviceSpinner)
  Spinner spinner;

  @OnClick(R.id.cancle)
  public void cancleClick() {
    if (listener != null){
      listener.onCancle();
    }
  }

  @OnClick(R.id.confirm)
  public void confirmClick() {
    if (listener != null){
      listener.onConfirm(spinner.getSelectedItem().toString());
    }
  }
  private OnClickListener listener;
  public void setListener(OnClickListener listener){
    this.listener = listener;
  }
  public interface OnClickListener{
    void onCancle();
    void onConfirm(String ssid);
  }
  List<Device>         currentNetworkDevicelist;
  List<String>         allNetworkDeviceList;
  ArrayAdapter<String> deviceAdapter;
  WifiModel            wifiModel;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.device_list, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    allNetworkDeviceList = Lists.newArrayList();
    deviceAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, allNetworkDeviceList);
    spinner.setAdapter(deviceAdapter);

    wifiModel = new WifiModel(getActivity());
    wifiModel.setListener(this);
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
      if (scanResult.SSID.startsWith("yy")) {
        System.out.println(scanResult.BSSID);
        if (!isExist(scanResult.BSSID)) {
          allNetworkDeviceList.add(scanResult.SSID);
        }
      }
      deviceAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void connectSucc() {

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
