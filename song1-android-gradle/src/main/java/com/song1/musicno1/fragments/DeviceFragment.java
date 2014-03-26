package com.song1.musicno1.fragments;

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

  @InjectView(R.id.gridView) GridView gridView;

  private BaseAdapter<Device, ViewHolder> adapter;

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
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
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
