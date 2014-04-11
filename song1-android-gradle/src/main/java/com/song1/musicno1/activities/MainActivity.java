package com.song1.musicno1.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.crashlytics.android.Crashlytics;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.fragments.LeftFragment;
import com.song1.musicno1.fragments.PlayBarFragment;
import com.song1.musicno1.fragments.PlayingFragment;
import com.song1.musicno1.fragments.TestFragment;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.ViewHelper;
import com.song1.musicno1.models.events.play.UpdateVolumeEvent;
import com.song1.musicno1.services.HttpService;
import com.song1.musicno1.services.PlayService;
import com.song1.musicno1.services.UpnpService;
import com.song1.musicno1.vender.SlidingUpPanelLayout;
import de.akquinet.android.androlog.Log;

import javax.inject.Inject;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM4:40
 */
public class MainActivity extends ActionBarActivity implements SlidingUpPanelLayout.PanelSlideListener {
  protected                         PlayBarFragment      playBarFragment;
  @InjectView(R.id.drawer)          DrawerLayout         drawerLayout;
  @InjectView(R.id.sling_up)        SlidingUpPanelLayout slidingUpPanel;
  @InjectView(R.id.play_bar)        View                 playBarView;
  @InjectView(R.id.main)            View                 mainView;
  @InjectView(R.id.playing_section) View                 playingSectionView;

  @Inject   LeftFragment          leftFragment;
  protected ActionBarDrawerToggle actionBarDrawerToggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Crashlytics.start(this);

    Log.init();
    App.inject(this);

    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    startService(new Intent(this, UpnpService.class));
    startService(new Intent(this, PlayService.class));
    startService(new Intent(this, HttpService.class));

    playBarFragment = new PlayBarFragment();

    slidingUpPanel.setDragView(playBarView);
    slidingUpPanel.setPanelSlideListener(this);
    slidingUpPanel.setEnableDragViewTouchEvents(true);
    slidingUpPanel.setPanelHeight(ViewHelper.dp2pixels(this, 60f));

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.navigation, leftFragment)
        .replace(R.id.play_bar, playBarFragment)
        .replace(R.id.main, new TestFragment())
        .replace(R.id.playing, new PlayingFragment())
        .commit();

    actionBarDrawerToggle = new ActionBarDrawerToggle(
        this,
        drawerLayout,
        R.drawable.ic_navigation_drawer,
        R.string.drawer_open,
        R.string.drawer_close
    );
    drawerLayout.setDrawerListener(actionBarDrawerToggle);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    actionBarDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    actionBarDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public void onBackPressed() {
    if (slidingUpPanel.isExpanded()) {
      slidingUpPanel.collapsePane();
      if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
      }
    } else {
      super.onBackPressed();
      if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
      }
    }
  }

  public void replaceMain(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main, fragment)
        .commit();
    drawerLayout.closeDrawers();
  }

  public void push(String stackName, Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main, fragment)
        .addToBackStack(stackName)
        .commit();
    actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
  }

  @Override
  public void onPanelSlide(View panel, float slideOffset) {
    if (slideOffset >= 0.5) {
      getSupportActionBar().show();
      playBarFragment.showBottom();
    } else {
      getSupportActionBar().hide();
      playBarFragment.showTop();
    }
  }

  @Override
  public void onPanelCollapsed(View panel) {
    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
      drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
  }

  @Override
  public void onPanelExpanded(View panel) {
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    MainBus.post(new UpdateVolumeEvent());
  }

  @Override
  public void onPanelAnchored(View panel) {

  }

  public void collapsePlayingPanel() {
    slidingUpPanel.collapsePane();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        MainBus.post(new UpdateVolumeEvent(UpdateVolumeEvent.DOWN));
        return true;
      case KeyEvent.KEYCODE_VOLUME_UP:
        MainBus.post(new UpdateVolumeEvent(UpdateVolumeEvent.UP));
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  public void hidePlayBar() {
    playingSectionView.setVisibility(View.GONE);
  }

  public void showPlayBar() {
    playingSectionView.setVisibility(View.VISIBLE);
  }
}
