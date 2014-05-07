package com.song1.musicno1.activities;

import android.support.v7.app.ActionBarActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * User: windless
 * Date: 13-12-11
 * Time: PM7:21
 */
public abstract class BaseActivity extends ActionBarActivity {
  @Override
  protected void onResume() {
    super.onResume();
    MobclickAgent.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    MobclickAgent.onPause(this);
  }
}
