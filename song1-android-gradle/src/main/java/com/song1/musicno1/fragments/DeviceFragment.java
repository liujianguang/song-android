package com.song1.musicno1.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.BaseAdapter;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.NetworkHelp;
import com.song1.musicno1.models.events.play.CurrentPlayerEvent;
import com.song1.musicno1.models.events.play.SelectPlayerEvent;
import com.song1.musicno1.models.events.upnp.DeviceChangeEvent;
import com.song1.musicno1.models.events.upnp.SearchDeviceEvent;
import com.song1.musicno1.models.play.Player;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;

/**
 * Created by kate on 14-3-17.
 */
public class DeviceFragment extends Fragment implements AdapterView.OnItemClickListener {

  protected                         NetworkHelp                     networkHelp;
  protected                         Player                          selectedPlayer;
  @InjectView(R.id.gridView)        GridView                        gridView;
  @InjectView(R.id.current_network) TextView                        currentNetworkView;
  private                           BaseAdapter<Player, ViewHolder> adapter;
  private                           WifiManager                     wifi;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    MainBus.post(new SearchDeviceEvent(MediaRenderer.DEVICE_TYPE));

    newAdapter();

    gridView.setAdapter(adapter);
    gridView.setOnItemClickListener(this);

    networkHelp = new NetworkHelp();
    wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
  }

  private void newAdapter() {
    adapter = new BaseAdapter<Player, ViewHolder>(getActivity(), R.layout.item_device)
        .bind(() -> new ViewHolder())
        .setData((i, player, holder) -> {
          if (player == selectedPlayer) {
            holder.imageView.setImageResource(R.drawable.kids_room_ic_butoon_press);
            holder.textView.setTextColor(Color.RED);
          } else {
            holder.imageView.setImageResource(R.drawable.kids_room_ic_butoon_normal);
            holder.textView.setTextColor(Color.WHITE);
          }
          holder.textView.setText(player.getName());
        });
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
    networkHelp.onConnected(() -> {
      WifiInfo info = wifi.getConnectionInfo();
      if (info != null) {
        currentNetworkView.setText(info.getSSID());
      }
    }).onDisconnected(() -> currentNetworkView.setText(R.string.not_network))
        .register(getActivity());
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
    adapter.setList(event.players);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Player player = adapter.getElement(i);
    MainBus.post(new SelectPlayerEvent(player));
  }

  @Subscribe
  public void selectPlayer(CurrentPlayerEvent event) {
    selectedPlayer = event.getCurrentPlayer();
    adapter.notifyDataSetChanged();
  }

  class ViewHolder extends BaseAdapter.ViewHolder {
    @InjectView(R.id.text)  TextView  textView;
    @InjectView(R.id.image) ImageView imageView;

    @Override
    public void inject(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
