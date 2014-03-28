package com.song1.musicno1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.fragments.BaseFragment;
import com.song1.musicno1.fragments.LeftFragment;
import com.song1.musicno1.fragments.PlayBarFragment;
import com.song1.musicno1.fragments.TestFragment;
import com.song1.musicno1.helpers.ViewHelper;
import com.song1.musicno1.services.PlayService;
import com.song1.musicno1.services.UpnpService;
import com.song1.musicno1.vender.SlidingUpPanelLayout;
import de.akquinet.android.androlog.Log;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM4:40
 */
public class MainActivity extends ActionBarActivity implements SlidingUpPanelLayout.PanelSlideListener {
  @InjectView(R.id.drawer)   DrawerLayout         drawerLayout;
  @InjectView(R.id.sling_up) SlidingUpPanelLayout slidingUpPanel;
  @InjectView(R.id.play_bar) View                 playBarView;

  @Override

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.init();
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    startService(new Intent(this, UpnpService.class));
    startService(new Intent(this, PlayService.class));

    slidingUpPanel.setDragView(playBarView);
    slidingUpPanel.setPanelSlideListener(this);
    slidingUpPanel.setEnableDragViewTouchEvents(true);
    slidingUpPanel.setPanelHeight(ViewHelper.dp2pixels(this, 68f));

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.play_bar, new PlayBarFragment())
        .replace(R.id.navigation, new LeftFragment())
        .replace(R.id.main, new TestFragment())
        .commit();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.main);
    Fragment parent = (BaseFragment) fragment.getParent();
    if (parent != null) {
      show(parent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void show(Fragment fragment) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.main, fragment).commit();
    drawerLayout.closeDrawers();
  }

  @Override
  public void onPanelSlide(View panel, float slideOffset) {
    if (slideOffset >= 0.5) {
      getSupportActionBar().show();
    } else {
      getSupportActionBar().hide();
    }
  }

  @Override
  public void onPanelCollapsed(View panel) {

  }

  @Override
  public void onPanelExpanded(View panel) {

  }

  @Override
  public void onPanelAnchored(View panel) {

  }
}
