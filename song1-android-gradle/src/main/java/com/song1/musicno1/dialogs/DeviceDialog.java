package com.song1.musicno1.dialogs;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.models.WifiModel;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by kate on 14-3-17.
 */
public class DeviceDialog extends SherlockDialogFragment implements WifiModel.ScanListener{

  @InjectView(R.id.deviceNameSpinner) Spinner deviceNameSpinner;
  @InjectView(R.id.deviceSpinner)     Spinner deviceSpinner;
  @InjectView(R.id.networkSpinner)    Spinner networkSpinner;

  List<String> deviceNameList = Lists.newArrayList();
  List<String> deviceList     = Lists.newArrayList();
  List<String> networkList    = Lists.newArrayList();

  SpinnerAdapter deviceNameAdapter;
  SpinnerAdapter deviceAdapter;
  SpinnerAdapter networkAdapter;

  Context context;
  WifiModel wifiModel;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d(this, "onCreateView...");
    return inflater.inflate(R.layout.frament_device, container);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(this, "onActivityCreated...");
    super.onActivityCreated(savedInstanceState);

    ButterKnife.inject(this, getView());

    initData();
    initAdapter();
    deviceNameSpinner.setAdapter(deviceNameAdapter);
    deviceSpinner.setAdapter(deviceAdapter);
    networkSpinner.setAdapter(networkAdapter);
  }

  private void initData() {
    context = getSherlockActivity();
    wifiModel = new WifiModel(context);
    deviceNameList.addAll(Lists.newArrayList("书房设备", "客厅设备", "卧室设备"));
    deviceList.addAll(Lists.newArrayList("yy199C4", "yy5975B", "yy37858"));
    networkList.addAll(Lists.newArrayList("PengWuZhuanYoing", "DontUseThis", "QISHENG"));
  }

  private void initAdapter() {
    deviceNameAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, deviceNameList);
    deviceAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, deviceList);
    networkAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, networkList);
  }

  @Override
  public void scanResult(List<ScanResult> scanResultList) {

  }
}
