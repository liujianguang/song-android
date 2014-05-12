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
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.event.Event;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.models.events.play.*;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Volume;
import com.song1.musicno1.ui.IocTextView;
import com.song1.musicno1.util.DeviceUtil;
import com.song1.musicno1.util.ToastUtil;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.CirclePageIndicator;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by windless on 3/28/14.
 */

public class PlayingFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, WifiModel.ScanListener {
  protected int   state;
  protected Audio currentAudio;
  private int playMode = Player.MODE_REPEAT_ALL;

  @InjectView(R.id.volume_bar)    SeekBar             volumeBar;
  @InjectView(R.id.play)          ImageButton         playBtn;
  @InjectView(R.id.pager)         ViewPager           pager;
  @InjectView(R.id.indicator)     CirclePageIndicator indicator;
  @InjectView(R.id.favorite)      ImageButton         favoriteBtn;
  @InjectView(R.id.player_list)   ImageButton         playerListBtn;
  @InjectView(R.id.deviceNumView) IocTextView         deviceNumView;
  @InjectView(R.id.previous)      ImageButton         prevButton;
  @InjectView(R.id.play)          ImageButton         playButton;
  @InjectView(R.id.next)          ImageButton         nextButton;
  @InjectView(R.id.volume_min)    ImageButton         volumeMinButton;
  @InjectView(R.id.volume_max)    ImageButton         volumeMaxButton;

  WifiModel wifiModel;
  int newDeviceCount = 0;


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

    wifiModel = new WifiModel(getActivity());
    wifiModel.setScanListener(this);
    wifiModel.scan();

    setEnabled(false);
    favoriteBtn.setEnabled(false);
    volumeMinButton.setEnabled(false);
    volumeMaxButton.setEnabled(false);
  }

  @Override
  public void onPause() {
    Log.d(this,"onResume...");
    super.onPause();
    MainBus.unregister(this);
  }

  @Override
  public void onResume() {
    Log.d(this,"onResume...");
    super.onResume();
    MainBus.register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    wifiModel.stop();
  }

  @Subscribe
  public void currentPlayerChanged(CurrentPlayerEvent event) {
    if (event.getCurrentPlayer() != null && !event.getCurrentPlayer().getId().equals("0")) {
      playerListBtn.setImageResource(R.drawable.ic_device_list_large_press);
    } else {
      playerListBtn.setImageResource(R.drawable.ic_device_list_large_nor);
    }
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
        playBtn.setImageResource(R.drawable.play_disable);
        playBtn.setEnabled(true);
        setEnabled(true);
        break;
      case Player.PLAYING:
        playBtn.setImageResource(R.drawable.stop_disable);
        playBtn.setEnabled(true);
        setEnabled(true);
        break;
      case Player.PREPARING:
        playBtn.setEnabled(false);
        setEnabled(false);
    }
  }
  private void setEnabled(boolean enabled){
    playButton.setEnabled(enabled);
    prevButton.setEnabled(enabled);
    nextButton.setEnabled(enabled);

  }

  @Subscribe
  public void onCurrentPlayerVolumeChanged(VolumeEvent event) {
    Log.d(this,"onCurrentPlayerVolumeChanged...");
    setFavoriteBtn();
    Volume volume = event.getVolume();
    volumeBar.setMax(volume.getMax());
    volumeBar.setProgress(volume.getCurrent());
  }

  @Subscribe
  public void onPlayModeChanged(PlayModeEvent event) {
    playMode = event.getPlayMode();
    //ToastUtil.show(getActivity(),event.getPlayMode()+"");
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
        //ToastUtil.show(getActivity(),currentAudio + "");
        if (playMode == Player.MODE_NORMAL){
          Players.rePlay();
        }else{
          Players.play();
        }
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
      setFavoriteBtn();
      volumeMinButton.setEnabled(true);
      volumeMaxButton.setEnabled(true);
    }
    favoriteBtn.setEnabled(currentAudio != null && currentAudio.canFavorite());

  }
  private void setFavoriteBtn(){
    if (currentAudio != null) {
      if (currentAudio.canFavorite()) {
        if (FavoriteAudio.isFavorite(currentAudio)) {
          favoriteBtn.setImageResource(R.drawable.ic_red_heat_selected);
        } else {
          favoriteBtn.setImageResource(R.drawable.ic_red_heat_nor);
        }
      } else {
        favoriteBtn.setImageResource(R.drawable.ic_red_heat_nor);
      }
    }
  }

  @OnClick(R.id.favorite)
  public void favorite() {
    if (currentAudio == null) return;

    if (FavoriteAudio.toggleRedHeart(currentAudio)) {
      favoriteBtn.setImageResource(R.drawable.ic_red_heat_selected);
      Toast.makeText(getActivity(), R.string.added_to_red_heart, Toast.LENGTH_SHORT).show();
    } else {
      favoriteBtn.setImageResource(R.drawable.ic_red_heat_nor);
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
    MainBus.post(new UpdateVolumeEvent(UpdateVolumeEvent.DOWN, false));
  }

  @OnClick(R.id.volume_max)
  public void volumeUp() {
    MainBus.post(new UpdateVolumeEvent(UpdateVolumeEvent.UP, false));
  }

  @Override
  public void scanResult(List<ScanResult> scanResults) {
    List<String> ssidList = DeviceUtil.filterScanResultList(scanResults);
    newDeviceCount = ssidList.size();
    if (newDeviceCount != 0){
      deviceNumView.setText(newDeviceCount + "");
      deviceNumView.setVisibility(View.VISIBLE);
    }else{
      deviceNumView.setVisibility(View.GONE);
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
