package com.song1.musicno1.fragments;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.models.play.*;
import com.song1.musicno1.stores.PlayerStore;
import com.song1.musicno1.ui.IocTextView;
import com.song1.musicno1.util.DeviceUtil;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

/**
 * Created by windless on 3/28/14.
 */

public class PlayingFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, WifiModel.ScanListener {
  protected int   state;
  protected Audio currentAudio;

  @InjectView(R.id.volume_bar)       SeekBar     volumeBar;
  @InjectView(R.id.pager)            ViewPager   pager;
  //@InjectView(R.id.indicator)     CirclePageIndicator indicator;
  @InjectView(R.id.favorite)         ImageButton favoriteBtn;
  @InjectView(R.id.player_list)      ImageButton playerListBtn;
  @InjectView(R.id.deviceNumView)    IocTextView deviceNumView;
  @InjectView(R.id.previous)         ImageButton prevButton;
  @InjectView(R.id.play)             ImageButton playButton;
  @InjectView(R.id.next)             ImageButton nextButton;
  @InjectView(R.id.volume_min)       ImageButton volumeMinButton;
  @InjectView(R.id.volume_max)       ImageButton volumeMaxButton;
  @InjectView(R.id.collectionButton) Button      collectionButton;

  WifiModel wifiModel;
  int newDeviceCount = 0;
  protected boolean isDeviceFragmentShow;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_playing, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    pager.setAdapter(new Adapter(getChildFragmentManager()));
    pager.setCurrentItem(1);
    //indicator.setViewPager(pager);

    volumeBar.setOnSeekBarChangeListener(this);

    wifiModel = new WifiModel(getActivity());
    wifiModel.setScanListener(this);
    wifiModel.scan();
  }

  public void updateVolume() {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      Volume volume = currentPlayer.getVolume();
      volumeBar.setMax(volume.getMax());
      volumeBar.setProgress(volume.getCurrent());
    }
  }

  @Subscribe
  public void updatePlayerInfo(PlayerStore.CurrentPlayerChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer == null) {
      favoriteBtn.setEnabled(false);
      prevButton.setEnabled(false);
      playButton.setEnabled(false);
      nextButton.setEnabled(false);
      volumeMinButton.setEnabled(false);
      volumeMaxButton.setEnabled(false);
      volumeBar.setEnabled(false);
      collectionButton.setEnabled(false);

      playerListBtn.setImageResource(R.drawable.ic_sling_up);
    } else {
      favoriteBtn.setEnabled(true);
      prevButton.setEnabled(true);
      playButton.setEnabled(true);
      nextButton.setEnabled(true);
      volumeMinButton.setEnabled(true);
      volumeMaxButton.setEnabled(true);
      volumeBar.setEnabled(true);

      collectionButton.setEnabled(true);

      if (currentPlayer instanceof LocalPlayer) {
        playerListBtn.setImageResource(R.drawable.ic_sling_up);
      } else {
        playerListBtn.setImageResource(R.drawable.ic_sling_up);
      }

      updateVolume();
      updatePlayerState(null);
      updatePlayingAudio(null);
    }
  }

  @Subscribe
  public void updatePlayingAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();

    if (currentPlayer == null) return;

    currentAudio = currentPlayer.getPlayingAudio();
    if (currentAudio == null) {
      favoriteBtn.setEnabled(false);
      collectionButton.setEnabled(false);
    } else {
      favoriteBtn.setEnabled(currentAudio.canFavorite());
      collectionButton.setEnabled(currentAudio.canFavorite());
      if (currentAudio.canFavorite()) {
        if (FavoriteAudio.isFavorite(currentAudio)) {
          favoriteBtn.setImageResource(R.drawable.ic_red_heat_added);
          collectionButton.setText(R.string.cancel_collection);
        } else {
          favoriteBtn.setImageResource(R.drawable.ic_red_heart);
          collectionButton.setText(R.string.collection);
        }
      }
    }
  }

  @Subscribe
  public void updatePlayerState(PlayerStore.PlayerStateChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();

    if (currentPlayer == null) return;

    state = currentPlayer.getState();
    switch (state) {
      case Player.State.PAUSED:
      case Player.State.STOPPED:
        playButton.setImageResource(R.drawable.ic_play_large);
        setPlayButtonsEnabled(true);
        break;
      case Player.State.PLAYING:
        playButton.setImageResource(R.drawable.ic_pause_large);
        setPlayButtonsEnabled(true);
        break;
      case Player.State.PREPARING:
        setPlayButtonsEnabled(false);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
    updatePlayerInfo(null);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    wifiModel.stop();
  }

  private void setPlayButtonsEnabled(boolean enabled) {
    playButton.setEnabled(enabled);
    prevButton.setEnabled(enabled);
    nextButton.setEnabled(enabled);
  }

  @OnClick(R.id.play)
  public void onPlayClick() {
    switch (state) {
      case Player.State.PLAYING:
        Players.pause();
        break;
      case Player.State.PAUSED:
        Players.resume();
        break;
      case Player.State.STOPPED:
        Players.play();
    }
  }

  @OnClick(R.id.player_list)
  public void onPlayerListClick() {
    if (isDeviceFragmentShow) return;

    isDeviceFragmentShow = true;
    DeviceFragment deviceFragment = new DeviceFragment();
    deviceFragment.onClose(() -> isDeviceFragmentShow = false);
    deviceFragment.show(getFragmentManager(), "deviceFragment");
  }

  @OnClick(R.id.next)
  public void playNext() {
    Players.next();
  }

  @OnClick(R.id.previous)
  public void playPrevious() {
    Players.previous();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
    if (fromUser) {
      Players.setVolume(seekBar.getProgress(), false);
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {

  }

  @OnClick(R.id.collectionButton)
  public void favorite() {
    if (currentAudio == null) return;

    if (FavoriteAudio.toggleRedHeart(currentAudio)) {
      favoriteBtn.setImageResource(R.drawable.ic_red_heat_selected);
      collectionButton.setText(R.string.cancel_collection);
      Toast.makeText(getActivity(), R.string.added_to_red_heart, Toast.LENGTH_SHORT).show();
    } else {
      favoriteBtn.setImageResource(R.drawable.ic_red_heart_nor);
      collectionButton.setText(R.string.collection);
      Toast.makeText(getActivity(), R.string.removed_frome_red_heart, Toast.LENGTH_SHORT).show();
    }

    Fragment mainFragment = getFragmentManager().findFragmentById(R.id.main);
    if (mainFragment instanceof FavoriteAudioFragment) {
      FavoriteAudioFragment heartFragment = (FavoriteAudioFragment) mainFragment;
      heartFragment.reload();
    }
  }

  @OnClick(R.id.volume_min)
  public void volumeDown() {
    Players.volumeDown(false);
    updateVolume();
  }

  @OnClick(R.id.volume_max)
  public void volumeUp() {
    Players.volumeUp(false);
    updateVolume();
  }

  @Override
  public void scanResult(List<ScanResult> scanResults) {
    List<String> ssidList = DeviceUtil.filterScanResultList(scanResults);
    newDeviceCount = ssidList.size();
    if (newDeviceCount != 0) {
      deviceNumView.setText(newDeviceCount + "");
      deviceNumView.setVisibility(View.VISIBLE);
    } else {
      deviceNumView.setVisibility(View.GONE);
    }
  }


  class Adapter extends FragmentPagerAdapter {

    public Adapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
//      switch (position) {
//        case 0:
         // return new PlaylistFragment();
//        default:
          return new AudioActionsFragment();
//      }
    }

    @Override
    public int getCount() {
      return 1;
    }
  }
}
