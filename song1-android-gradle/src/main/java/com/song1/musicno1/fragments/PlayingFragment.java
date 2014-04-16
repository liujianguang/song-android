package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.events.play.*;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Volume;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by windless on 3/28/14.
 */

public class PlayingFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
  protected int   state;
  protected Audio currentAudio;

  @InjectView(R.id.volume_bar) SeekBar             volumeBar;
  @InjectView(R.id.play)       ImageButton         playBtn;
  @InjectView(R.id.pager)      ViewPager           pager;
  @InjectView(R.id.indicator)  CirclePageIndicator indicator;
  @InjectView(R.id.favorite)   ImageButton         favoriteBtn;

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
    indicator.setViewPager(pager);

    volumeBar.setOnSeekBarChangeListener(this);
    volumeBar.setEnabled(false);
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
  }

  @Subscribe
  public void currentPlayerChanged(CurrentPlayerEvent event) {
    volumeBar.setEnabled(event.getCurrentPlayer() != null);
  }

  @Subscribe
  public void onPositionInfoChanged(PositionEvent event) {
    setCurrentAudio(event.getAudio());
  }

  @Subscribe
  public void onCurrentPlayerStateChanged(CurrentPlayerStateEvent event) {
    state = event.state;
    switch (event.state) {
      case Player.PAUSED:
      case Player.STOPPED:
        playBtn.setImageResource(R.drawable.ic_play_large);
        playBtn.setEnabled(true);
        break;
      case Player.PLAYING:
        playBtn.setImageResource(R.drawable.ic_pause_large);
        playBtn.setEnabled(true);
        break;
      case Player.PREPARING:
        playBtn.setEnabled(false);
    }
  }

  @Subscribe
  public void onCurrentPlayerVolumeChanged(VolumeEvent event) {
    Volume volume = event.getVolume();
    volumeBar.setMax(volume.getMax());
    volumeBar.setProgress(volume.getCurrent());
  }

  @OnClick(R.id.play)
  public void onPlayClick() {
    switch (state) {
      case Player.PLAYING:
        Players.pause();
        break;
      case Player.PAUSED:
        Players.resume();
        break;
      case Player.STOPPED:
        Players.play();
    }
  }

  @OnClick(R.id.player_list)
  public void onPlayerListClick() {
    DeviceFragment deviceFragment = new DeviceFragment();
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
      MainBus.post(new UpdateVolumeEvent(new Volume(i, seekBar.getMax())));
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
  }

  public Audio getCurrentAudio() {
    return currentAudio;
  }

  public void setCurrentAudio(Audio currentAudio) {
    if (this.currentAudio != currentAudio) {
      this.currentAudio = currentAudio;
      if (currentAudio != null) {
        if (currentAudio.canFavorite()) {
          if (FavoriteAudio.isFavorite(currentAudio)) {
            favoriteBtn.setBackgroundResource(R.color.red);
          } else {
            favoriteBtn.setBackgroundResource(android.R.color.transparent);
          }
        } else {
          favoriteBtn.setBackgroundResource(android.R.color.transparent);
        }
      }
    }
    favoriteBtn.setEnabled(currentAudio != null && currentAudio.canFavorite());
  }

  @OnClick(R.id.favorite)
  public void favorite() {
    if (currentAudio == null) return;

    if (FavoriteAudio.isFavorite(currentAudio)) {
      FavoriteAudio.removeFromFavorite(currentAudio);
      favoriteBtn.setBackgroundResource(android.R.color.transparent);
    } else {
      FavoriteAudio.addToFavorite(currentAudio);
      favoriteBtn.setBackgroundResource(R.color.red);
    }

    Fragment mainFragment = getFragmentManager().findFragmentById(R.id.main);
    if (mainFragment instanceof FavoriteAudioFragment) {
      FavoriteAudioFragment heartFragment = (FavoriteAudioFragment) mainFragment;
      heartFragment.reload();
    }
  }


  class Adapter extends FragmentPagerAdapter {

    public Adapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return new PlaylistFragment();
        default:
          return new AudioActionsFragment();
      }
    }

    @Override
    public int getCount() {
      return 2;
    }
  }
}
