package com.song1.musicno1.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.ui.SlingUpLayout;

/**
 * Created by kate on 14-3-20.
 */
public abstract class SlingUpActivity extends ActionBarActivity implements SlingUpLayout.SlingUpListener {


  @InjectView(R.id.slingUpLayout)
  SlingUpLayout slingUpLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.sling_up);
    ButterKnife.inject(this);

    Fragment fragment = getFragment();
    getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();
    slingUpLayout.setListener(this);
    slingUpLayout.expand();
  }

  public abstract Fragment getFragment();

  @Override
  public void onBackPressed() {
    slingUpLayout.collapse();
  }

  @Override
  public void expandFinish() {
  }

  @Override
  public void collapseFinish() {
    super.onBackPressed();
  }
}
