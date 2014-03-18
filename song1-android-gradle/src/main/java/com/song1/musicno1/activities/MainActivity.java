package com.song1.musicno1.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.song1.musicno1.R;
import com.song1.musicno1.dialogs.DeviceDialog;
import de.akquinet.android.androlog.Log;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM4:40
 */
public class MainActivity extends SherlockFragmentActivity {

  FragmentManager fragmentManager;

  @OnClick(R.id.newDeviceButton)
  public void newDeviceButtonClick() {
    DeviceDialog deviceDialog = new DeviceDialog(this);
    deviceDialog.show(fragmentManager,"deviceDialog");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.init();

    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    fragmentManager = getSupportFragmentManager();
  }
}
