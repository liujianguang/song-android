package com.song1.musicno1.fragments;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
import com.song1.musicno1.models.UpnpModel;
import com.song1.musicno1.models.WifiModel;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.Device;

import java.util.List;
import java.util.Map;


/**
 * Created by kate on 14-3-19.
 */
public class CurrentNetworkDeviceFragment extends Fragment implements UpnpModel.UpnpChangeListener, WifiModel.WifiModleListener {



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
  DeviceListDialog    deviceListDialog;


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
    Log.d(this,"onDestory...");
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

  class ViewHolder{
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
        deviceListDialog.show(getFragmentManager(), "deviceListDialog");
      } else {
//        upnpModel.setAVTransport(device);
        upnpModel.play(device);
      }
    }
  }
}
