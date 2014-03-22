package com.song1.musicno1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.song1.musicno1.R;
import com.song1.musicno1.models.UpnpModel;
import de.akquinet.android.androlog.Log;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM4:40
 */
public class MainActivity extends SherlockFragmentActivity {

  UpnpModel       upnpModel;


  @OnClick(R.id.up)
  public void upClick(View view) {
    startActivity(new Intent(this, CurrentNotworkDeviceActivity.class));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.init();

    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
