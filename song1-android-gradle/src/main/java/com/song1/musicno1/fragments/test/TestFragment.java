package com.song1.musicno1.fragments.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.fragments.base.BaseFragment;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.upnp.DeviceChangeEvent;
import com.song1.musicno1.models.events.upnp.SearchDeviceEvent;
import com.song1.musicno1.models.play.Player;
import com.squareup.otto.Subscribe;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.std.av.controller.MediaController;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;

import java.util.List;

/**
 * Created by leovo on 2014/5/5.
 */
public class TestFragment extends BaseFragment implements DeviceChangeListener {

  Button searchButton;

  private MediaController mediaController;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.test, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    searchButton = (Button) view.findViewById(R.id.search);
    searchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            mediaController.start();
            mediaController.search();
          }
        }).start();
      }
    });
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mediaController = new MediaController();
    mediaController.addDeviceChangeListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void deviceAdded(Device device) {
    System.out.println("deviceAdd...");
    System.out.println(device.getUDN());
  }

  @Override
  public void deviceRemoved(Device device) {
    System.out.println("deviceRemoved...");
    System.out.println(device.getUDN());
  }
}
