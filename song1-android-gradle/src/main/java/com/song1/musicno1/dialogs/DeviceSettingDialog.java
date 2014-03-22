package com.song1.musicno1.dialogs;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.models.WifiModel;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by kate on 14-3-17.
 */
public class DeviceSettingDialog extends BaseDialog implements WifiModel.ScanListener {

  @InjectView(R.id.deviceNameSpinner) Spinner deviceNameSpinner;
  @InjectView(R.id.deviceSpinner)     Spinner deviceSpinner;
  @InjectView(R.id.networkSpinner)    Spinner networkSpinner;

  List<String> deviceNameList = Lists.newArrayList();
  List<String> deviceList     = Lists.newArrayList();
  List<String> networkList    = Lists.newArrayList();

  ArrayAdapter deviceNameAdapter;
  ArrayAdapter deviceAdapter;
  ArrayAdapter networkAdapter;

  Context   context;
  WifiModel wifiModel;

  public DeviceSettingDialog(){

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getSherlockActivity();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(this, "onActivityCreated...");
    super.onActivityCreated(savedInstanceState);

    ButterKnife.inject(this, getView());
    deviceNameAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, deviceNameList);
    deviceAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, deviceList);
    networkAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, networkList);
    deviceNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    deviceNameSpinner.setAdapter(deviceNameAdapter);
    deviceSpinner.setAdapter(deviceAdapter);
    networkSpinner.setAdapter(networkAdapter);

    wifiModel = WifiModel.newInstance(getSherlockActivity());
    wifiModel.addScanListener(this);
    wifiModel.scan();

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d(this,"onCreateView...");
    return inflater.inflate(R.layout.frament_device,null);
  }

//    @Override
//  public Dialog onCreateDialog(Bundle savedInstanceState) {
//    Log.d(this,"onCreateDialog...");
//    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//    View titleView = LayoutInflater.from(context).inflate(R.layout.dialog_title,null);
//    View contentView = LayoutInflater.from(context).inflate(R.layout.frament_device, null);
//    ButterKnife.inject(this, contentView);
//    builder.setCustomTitle(titleView).setView(contentView);
//    return builder.create();
//  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    wifiModel.removeScanListener(this);
    wifiModel.stop();
  }

  @Override
  public void scanResult(List<ScanResult> scanResultList) {
    networkList.clear();
    for (ScanResult scanResult : scanResultList) {
      if (scanResult.SSID.startsWith("yy")) {
        deviceList.add(scanResult.SSID);
      } else {
        networkList.add(scanResult.SSID);
      }
      deviceAdapter.notifyDataSetChanged();
      networkAdapter.notifyDataSetChanged();
    }
  }
}
