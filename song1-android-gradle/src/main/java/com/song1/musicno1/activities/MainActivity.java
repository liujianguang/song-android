package com.song1.musicno1.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.dialogs.PromptDialog;
import com.song1.musicno1.event.Event;
import com.song1.musicno1.fragments.*;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.ViewHelper;
import com.song1.musicno1.models.events.ExitEvent;
import com.song1.musicno1.models.events.play.ActivityExitEvent;
import com.song1.musicno1.models.events.play.CurrentPlayerOccupiedEvent;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.services.HttpService;
import com.song1.musicno1.services.PlayService;
import com.song1.musicno1.services.UpnpService;
import com.song1.musicno1.stores.PlayerStore;
import com.song1.musicno1.vender.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;

import javax.inject.Inject;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM4:40
 */
public class MainActivity extends BaseActivity implements SlidingUpPanelLayout.PanelSlideListener {
  protected                         PlayBarFragment      playBarFragment;
  @InjectView(R.id.drawer)          DrawerLayout         drawerLayout;
  @InjectView(R.id.sling_up)        SlidingUpPanelLayout slidingUpPanel;
  @InjectView(R.id.play_bar)        View                 playBarView;
  @InjectView(R.id.main)            View                 mainView;
  @InjectView(R.id.playing_section) View                 playingSectionView;

  @Inject LeftFragment leftFragment;

  private   PlayingFragment       playingFragment;
  protected ActionBarDrawerToggle actionBarDrawerToggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.init();
    App.inject(this);
    MainBus.register(this);

    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    startService(new Intent(this, UpnpService.class));
    startService(new Intent(this, PlayService.class));
    startService(new Intent(this, HttpService.class));

    playBarFragment = new PlayBarFragment();
    playingFragment = new PlayingFragment();

    slidingUpPanel.setDragView(playBarView);
    slidingUpPanel.setPanelSlideListener(this);
    slidingUpPanel.setEnableDragViewTouchEvents(true);
    slidingUpPanel.setPanelHeight(ViewHelper.dp2pixels(this, 60f));

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.navigation, leftFragment)
        .replace(R.id.play_bar, playBarFragment)
        .replace(R.id.main, new TestFragment())
        .replace(R.id.playing, playingFragment)
        .commit();

    actionBarDrawerToggle = new ActionBarDrawerToggle(
        this,
        drawerLayout,
        R.drawable.ic_navigation_drawer,
        R.string.drawer_open,
        R.string.drawer_close
    );
    drawerLayout.setDrawerListener(actionBarDrawerToggle);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    MainBus.post(new ActivityExitEvent());
    MainBus.unregister(this);
    Log.d(this, "Main activity destroyed");
  }

  @Subscribe
  public void onCurrentPlayerOccupied(CurrentPlayerOccupiedEvent event) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.current_player_is_occupied);
    builder.setTitle(R.string.notice);
    builder.setNegativeButton(android.R.string.ok, (dialog, which) -> {
    });
    builder.create().show();
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
    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main);
    if (fragment instanceof LocalAudioContainerFragment) {
      LocalAudioContainerFragment localAudioContainerFragment = (LocalAudioContainerFragment) fragment;
      FragmentPagerAdapter adapter = localAudioContainerFragment.getAdapter();
      ViewPager pager = localAudioContainerFragment.getViewPager();
      Fragment fragment1 = adapter.getItem(pager.getCurrentItem());
      if (fragment1 instanceof LocalAudioFragment) {
        LocalAudioFragment localAudioFragment = (LocalAudioFragment) fragment1;
        localAudioFragment.refreshData();
      }
    }
  }

  @Override
  public void onPanelExpanded(View panel) {
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
  }

  @Override
  public void onPanelAnchored(View panel) {

  }

  public void collapsePlayingPanel() {
    slidingUpPanel.collapsePane();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer == null) return super.onKeyDown(keyCode, event);

    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        currentPlayer.volumeDown(!slidingUpPanel.isExpanded());
        playingFragment.updateVolume();
        return true;
      case KeyEvent.KEYCODE_VOLUME_UP:
        currentPlayer.volumeUp(!slidingUpPanel.isExpanded());
        playingFragment.updateVolume();
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

  @Subscribe
  public void onExit(ExitEvent event) {
    finish();
  }

  @Subscribe
  public void showExitDailog(Event.ShowExitDialogEvent event) {
    PromptDialog dialog = new PromptDialog(this);
    dialog.setTitle(R.string.notice).setMessage(R.string.exitMsg).setCancelClick(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        handler.removeCallbacks(exitRunnable);
        dialog.dismiss();
      }
    }).setConfirmClick(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        handler.removeCallbacks(exitRunnable);
        handler.post(exitRunnable);
      }
    });
    Log.d(this, "Show exit dialog");
    dialog.show(getSupportFragmentManager(), "exitDialog");
    handler.postDelayed(exitRunnable, 1000 * 30);
  }

  Handler handler = new Handler();
  private Runnable exitRunnable = new Runnable() {
    @Override
    public void run() {
      MainBus.post(new ExitEvent());
    }
  };
}
