package com.song1.musicno1.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import butterknife.ButterKnife;
import com.song1.musicno1.R;
import de.akquinet.android.androlog.Log;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM4:40
 */
public class MainActivity extends ActionBarActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.init();

    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
  }
}
