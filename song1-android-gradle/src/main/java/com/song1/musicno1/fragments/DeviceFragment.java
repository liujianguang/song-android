package com.song1.musicno1.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.BaseAdapter;
import com.song1.musicno1.constants.Constants;
import com.song1.musicno1.dialogs.DeviceListDialog;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.NetworkHelp;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.models.events.play.CurrentPlayerEvent;
import com.song1.musicno1.models.events.play.SelectPlayerEvent;
import com.song1.musicno1.models.events.upnp.DeviceChangeEvent;
import com.song1.musicno1.models.events.upnp.SearchDeviceEvent;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.ui.SlingUpDialog;
import com.song1.musicno1.util.DeviceUtil;
import com.squareup.otto.Subscribe;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;

import java.util.List;

/**
 * Created by kate on 14-3-17.
 */
public class DeviceFragment extends SlingUpDialog implements AdapterView.OnItemClickListener, WifiModel.ScanListener {

  protected                         NetworkHelp                     networkHelp;
  protected                         Player                          selectedPlayer;
  @InjectView(R.id.gridView)        GridView                        gridView;
  @InjectView(R.id.current_network) TextView                        currentNetworkView;
  private                           BaseAdapter<Player, ViewHolder> adapter;
  private                           WifiManager                     wifi;

  private Handler handler = new Handler();

  List<String> nameList;
  List<Integer> icoNormalList;
  List<Integer> icoSelectedList;

  WifiModel wifiModel;
  int newDeviceCount = 0;

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

    nameList = Lists.newArrayList(getResources().getStringArray(R.array.deviceNames));
    icoNormalList = Lists.newArrayList(Constants.DEVICE_IOC_NORMAL);
    icoSelectedList = Lists.newArrayList(Constants.DEVICE_IOC_SELECTED);
    System.out.println("nameList : " + nameList);

    wifiModel = new WifiModel(getActivity());
    wifiModel.setScanListener(this);
    wifiModel.scan();
  }

  private void newAdapter() {
    adapter = new BaseAdapter<Player, ViewHolder>(getActivity(), R.layout.item_device)
        .bind(() -> new ViewHolder())
        .setData((i, player, holder) -> {
          if (player == null) {
            holder.imageView.setImageResource(R.drawable.addnewdevice_ic_butoon_normal);
            holder.textView.setTextColor(Color.WHITE);
            holder.textView.setText(getString(R.string.newDevice));
            holder.deviceNumView.setText(newDeviceCount + "");
            holder.deviceNumView.setVisibility(View.VISIBLE);
            return;
          }
          String[] strArr = player.getName().split("-");
          String name = strArr[0].trim();
          int imgNormalResId = R.drawable.systemdefault_ic_butoon_nor;
          int imgSelectedResId = R.drawable.systemdefault_ic_butoon_press;
          int position = nameList.indexOf(name);
          System.out.println("name position : " + position);

          if (player.getName().equals(getString(R.string.this_phone))){
            imgNormalResId = R.drawable.player_ic_butoon_nor;
            imgSelectedResId = R.drawable.player_ic_butoon_press;
          } else if (position != -1){
            imgNormalResId = icoNormalList.get(position);
            imgSelectedResId = icoSelectedList.get(position);
          }

          if (player == selectedPlayer) {
            holder.imageView.setImageResource(imgSelectedResId);

            holder.textView.setTextColor(Color.RED);
          } else {
            holder.imageView.setImageResource(imgNormalResId);
            holder.textView.setTextColor(Color.WHITE);
          }
          holder.textView.setText(name);
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
    wifiModel.stop();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.frament_device_list, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Subscribe
  public void onDeviceChanged(DeviceChangeEvent event) {
    List<Player> players = Lists.newArrayList(event.players);
    players.add(null);
    adapter.setList(players);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Player player = adapter.getElement(i);
    if (player == null) {
      DeviceListDialog deviceListDialog = new DeviceListDialog();
      deviceListDialog.show(getFragmentManager(), "deviceListDialog");
    } else {
      MainBus.post(new SelectPlayerEvent(player));
      selectedPlayer = player;
      adapter.notifyDataSetChanged();
      handler.postDelayed(() -> dismiss(), 100);
    }
  }

  @Subscribe
  public void selectPlayer(CurrentPlayerEvent event) {
    selectedPlayer = event.getCurrentPlayer();
    adapter.notifyDataSetChanged();
  }

  @Override
  public void scanResult(List<ScanResult> scanResults) {
    List<String> ssidList = DeviceUtil.filterScanResultList(scanResults);
    newDeviceCount = ssidList.size();
    adapter.notifyDataSetChanged();
  }

  class ViewHolder extends BaseAdapter.ViewHolder {
    @InjectView(R.id.text)  TextView  textView;
    @InjectView(R.id.image) ImageView imageView;
    @InjectView(R.id.deviceNumView) TextView deviceNumView;

    @Override
    public void inject(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
