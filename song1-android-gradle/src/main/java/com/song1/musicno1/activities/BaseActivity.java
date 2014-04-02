package com.song1.musicno1.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

/**
 * User: windless
 * Date: 13-12-11
 * Time: PM7:21
 */
public abstract class BaseActivity extends ActionBarActivity {

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_DOWN:
//        getPlayerAction().volumeDown();
        return true;
      case KeyEvent.KEYCODE_VOLUME_UP:
//        getPlayerAction().volumeUp();
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}
