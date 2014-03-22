package com.song1.musicno1.activities;

import android.support.v4.app.Fragment;
import com.song1.musicno1.fragments.CurrentNetworkDeviceFragment;

/**
 * Created by kate on 14-3-20.
 */
public class CurrentNotworkDeviceActivity extends SlingUpActivity {

  @Override
  public Fragment getFragment() {
    return new CurrentNetworkDeviceFragment();
  }
}
