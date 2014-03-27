package com.song1.musicno1.activities;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.models.UpnpModel;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.services.PlayService;
import com.song1.musicno1.services.UpnpService;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM4:40
 */
public class MainActivity extends ActionBarActivity implements WifiModel.WifiModleListener {

  UpnpModel upnpModel;
  WifiModel wifiModel;

  @OnClick(R.id.show)
  public void show(View view) {
    startActivity(new Intent(this, CurrentNotworkDeviceActivity.class));
//    wifiModel.connect("songTest","songtest");
//    wifiModel.connect("PengwuZhuanYong","12345678");
//    if (upnpModel != null) {
//      upnpModel.stop();
//    }
//    upnpModel = new UpnpModel();
//    upnpModel.start();
//    upnpModel.search();
  }

  @OnClick(R.id.connectTest)
  public void connectTest() {
    wifiModel.connect("songTest", "songtest");
  }

  @OnClick(R.id.connectPengWu)
  public void connectPengWu() {
    wifiModel.connect("PengWuZhuanYong", "12345678");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startService(new Intent(this, UpnpService.class));
    startService(new Intent(this, PlayService.class));

    Log.init();
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    wifiModel = new WifiModel(this);
    wifiModel.setListener(this);
//    upnpModel = new UpnpModel();
//    upnpModel.start();
//    upnpModel.search();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (upnpModel != null) {
      upnpModel.stop();
    }
    if (wifiModel != null) {
      wifiModel.stop();
    }
  }

  @Override
  public void scanResult(List<ScanResult> scanResultList) {

  }

  @Override
  public void connectSucc() {

  }
}
