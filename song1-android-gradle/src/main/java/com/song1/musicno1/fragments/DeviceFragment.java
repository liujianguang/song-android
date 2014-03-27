package com.song1.musicno1.fragments;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.BaseAdapter;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.NetworkHelp;
import com.song1.musicno1.models.events.upnp.DeviceChangeEvent;
import com.song1.musicno1.models.events.upnp.SearchDeviceEvent;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;

/**
 * Created by kate on 14-3-17.
 */
public class DeviceFragment extends Fragment {

  protected                         NetworkHelp                     networkHelp;
  @InjectView(R.id.gridView)        GridView                        gridView;
  @InjectView(R.id.current_network) TextView                        currentNetworkView;
  private                           BaseAdapter<Device, ViewHolder> adapter;
  private                           WifiManager                     wifi;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    MainBus.post(new SearchDeviceEvent(MediaRenderer.DEVICE_TYPE));

    adapter = new BaseAdapter<Device, ViewHolder>(getActivity(), R.layout.item_device)
        .bind(() -> new ViewHolder())
        .setData((device, holder) -> holder.textView.setText(device.getFriendlyName()));

    gridView.setAdapter(adapter);

    networkHelp = new NetworkHelp();
    wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
    networkHelp.register(getActivity()).onConnected(() -> {
      WifiInfo info = wifi.getConnectionInfo();
      if (info != null) {
        currentNetworkView.setText(info.getSSID());
      }
    }).onDisconnected(() -> {
      currentNetworkView.setText(R.string.not_network);
    });
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
    networkHelp.unregister();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.frament_device_list, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Subscribe
  public void onDeviceChanged(DeviceChangeEvent event) {
    Log.d(this, "Device change event");
    adapter.setList(event.devices);
    adapter.notifyDataSetChanged();
  }

  class ViewHolder extends BaseAdapter.ViewHolder {
    @InjectView(R.id.text) TextView textView;

    @Override
    public void inject(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
