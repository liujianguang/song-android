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
import android.support.v7.app.ActionBar;
import android.support.v7.internal.widget.ActionBarView;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.Toast;
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
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import javax.inject.Inject;

/**
 * User: windless
 * Date: 14-3-5
 * Time: PM4:40
 */
public class MainActivity extends BaseActivity implements SlidingUpPanelLayout.PanelSlideListener {
  protected
  @InjectView(R.id.drawer)
  DrawerLayout drawerLayout;
  @InjectView(R.id.sling_up)
  SlidingUpPanelLayout slidingUpPanel;
  @InjectView(R.id.play_bar)
  View playBarView;
  @InjectView(R.id.main)
  View mainView;
  @InjectView(R.id.playing_section)
  View playingSectionView;


  private MenuDrawer mMenuDrawer;
  @Inject
  LeftFragment leftFragment;
  private PlayingFragment playingFragment;
  private PlayBarFragment playBarFragment;


//  protected ActionBarDrawerToggle actionBarDrawerToggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initActionBar();

    Log.init();
    App.inject(this);

    //setContentView(R.layout.activity_main);

    initMenuDrawer();
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


    getSupportFragmentManager().beginTransaction()
        .replace(R.id.leftContainer, leftFragment)
        .replace(R.id.play_bar, playBarFragment)
        .replace(R.id.playing, playingFragment)
        .commit();
//    drawerLayout.setDrawerListener(actionBarDrawerToggle);
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    // initActionBar();
  }

  private void initActionBar() {

    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
    getSupportActionBar().setIcon(R.drawable.point);

    View view = findViewById(android.R.id.home);
//    view.setVisibility(View.GONE);

//    View parent = (View) view.getParent();
//    parent.setVisibility(View.GONE);
//    System.out.println("_________________________________" + parent);


//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.custom_title);
  }


  private void initMenuDrawer() {
    mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
    mMenuDrawer.setMenuView(R.layout.left);
    mMenuDrawer.setContentView(R.layout.activity_main);
    mMenuDrawer.setDropShadow(R.drawable.shadow);
//    mMenuDrawer.setMaxAnimationDuration(3000);
//    mMenuDrawer.setHardwareLayerEnabled(false);
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    System.out.println("&&&&&&&&&&&&&&&&&&&&" + dm.widthPixels);

    int width = 50;
    System.out.println(width);
    System.out.println(dm.density);
    System.out.println(dm.density * width);
    System.out.println(ViewHelper.dp2pixels(this,width));
    int menuWidth = dm.widthPixels - ViewHelper.dp2pixels(this,width);

    mMenuDrawer.setMenuSize(menuWidth);
    mMenuDrawer.peekDrawer();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    for (int i = 0; i < menu.size(); i++) {
      menu.getItem(i).setVisible(!drawerLayout.isDrawerOpen(Gravity.LEFT));
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  protected void onStart() {
    super.onStart();
    MainBus.register(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    MainBus.unregister(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    MainBus.post(new ActivityExitEvent());
    Log.d(this, "Main activity destroyed");
  }

  @Subscribe
  public void onCurrentPlayerOccupied(CurrentPlayerOccupiedEvent event) {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle(R.string.notice)
        .setMessage(R.string.current_player_is_occupied)
        .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> dialog.dismiss())
        .show();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
          mMenuDrawer.openMenu();
          return true;
        }
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
//    actionBarDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
//    actionBarDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public void onBackPressed() {

    int state = mMenuDrawer.getDrawerState();
    if (state == MenuDrawer.STATE_OPEN || state == MenuDrawer.STATE_OPENING) {
      mMenuDrawer.closeMenu();
      return;
    }

    if (slidingUpPanel.isExpanded()) {
      slidingUpPanel.collapsePane();
      if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
      }
    } else {
      super.onBackPressed();
      if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_home);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
      }

    }
  }

  public void replaceMain(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main, fragment)
        .commit();
    mMenuDrawer.closeMenu();
    drawerLayout.closeDrawers();
  }

  public void push(String stackName, Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main, fragment)
        .addToBackStack(stackName)
        .commit();
//    actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
    // getSupportActionBar().setIcon(R.drawable.home_back);
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.home_back);
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
    //ToastUtil.show(this,"onPanelCollapsed");
    mMenuDrawer.setMenuSize((int) getResources().getDimension(R.dimen.left_fragment_width));
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
//    ToastUtil.show(this, "onPanelExpanded");
    mMenuDrawer.setMenuSize(0);
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    playingFragment.updatePlayerInfo(null);
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
  public void onPlayError(PlayerStore.PlayErrorEvent event) {
    Toast.makeText(this, getString(R.string.invalid_audio, event.getAudio().getTitle()), Toast.LENGTH_LONG).show();
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
