package com.song1.musicno1.fragments;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.dialogs.TimerDialog;
import com.song1.musicno1.helpers.AlbumArtHelper;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.models.events.play.TimerEvent;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.LocalPlayer;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.stores.PlayerStore;
import com.song1.musicno1.ui.IocTextView;
import com.song1.musicno1.util.DeviceUtil;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by windless on 3/27/14.
 */
public class PlayBarFragment extends Fragment implements WifiModel.ScanListener {
  protected                            int                state;
  @InjectView(R.id.bottom_title)       TextView           bottomTitleView;
  @InjectView(R.id.bottom_subtitle)    TextView           bottomSubtitleView;
  @InjectView(R.id.bottom_play)        ImageButton        bottomPlayBtn;
  @InjectView(R.id.bottom_player_list) ImageButton        playerListBtn;
  @InjectView(R.id.position_progress)  ProgressBar        positionBar;
  @InjectView(R.id.top)                View               topView;
  @InjectView(R.id.bottom)             View               bottomView;
  @InjectView(R.id.top_title)          TextView           topTitleView;
  @InjectView(R.id.top_subtitle)       TextView           topSubtitleView;
  @InjectView(R.id.refresh_layout)     SwipeRefreshLayout refreshLayout;
  @InjectView(R.id.bottom_album_art)   ImageView          albumArtImageView;
  @InjectView(R.id.timer_time)         TextView           timerTextView;
  @InjectView(R.id.deviceNumView)      TextView        deviceNumView;

  LocalAudioStore localAudioStore;
  protected int timerValue;

  WifiModel wifiModel;
  int newDeviceCount = 0;

  private Handler  handler          = new Handler();
  private Runnable positionRunnable = new Runnable() {
    @Override
    public void run() {
      updatePosition();
      handler.postDelayed(this, 1000);
    }
  };
  protected boolean isDeviceFragmentShow;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    localAudioStore = new LocalAudioStore(getActivity());
    refreshLayout.setColorScheme(
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light
    );
    wifiModel = new WifiModel(getActivity());
    wifiModel.setScanListener(this);
    wifiModel.scan();
  }

  @Subscribe
  public void updatePlayerInfo(PlayerStore.CurrentPlayerChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer == null) {
      handler.removeCallbacks(positionRunnable);
      bottomTitleView.setText("");
      bottomSubtitleView.setText("");
      topTitleView.setText("");
      topSubtitleView.setText("");
      bottomPlayBtn.setEnabled(false);

      positionBar.setProgress(0);
      refreshLayout.setRefreshing(false);
      albumArtImageView.setImageResource(R.drawable.album_art_default_small);

      playerListBtn.setImageResource(R.drawable.ic_playerlist);
    } else {
      if (currentPlayer instanceof LocalPlayer) {
        playerListBtn.setImageResource(R.drawable.ic_playerlist);
      } else {
        playerListBtn.setImageResource(R.drawable.ic_playerlist_selected);
      }

      bottomPlayBtn.setEnabled(true);
      updatePlayerState(null);
      updatePosition();
      updatePlayingAudio(null);
    }
  }

  private void updatePosition() {
    Player player = PlayerStore.INSTANCE.getCurrentPlayer();
    if (player != null) {
      positionBar.setMax(player.getDuration());
      positionBar.setProgress(player.getPosition());
    } else {
      positionBar.setProgress(0);
    }
  }

  @Subscribe
  public void updatePlayerState(PlayerStore.PlayerStateChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      state = currentPlayer.getState();
      switch (state) {
        case Player.State.PAUSED:
        case Player.State.STOPPED:
          bottomPlayBtn.setImageResource(R.drawable.ic_play);
          bottomPlayBtn.setEnabled(true);
          positionBar.setVisibility(View.VISIBLE);
          refreshLayout.setRefreshing(false);
          break;
        case Player.State.PLAYING:
          bottomPlayBtn.setImageResource(R.drawable.ic_pause);
          bottomPlayBtn.setEnabled(true);
          positionBar.setVisibility(View.VISIBLE);
          refreshLayout.setRefreshing(false);
          break;
        case Player.State.PREPARING:
          bottomPlayBtn.setEnabled(false);
          positionBar.setVisibility(View.GONE);
          refreshLayout.setRefreshing(true);
      }

      if (state == Player.State.PLAYING) {
        handler.post(positionRunnable);
      } else {
        handler.removeCallbacks(positionRunnable);
      }
    }
  }

  @Subscribe
  public void updatePlayingAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      Audio playingAudio = currentPlayer.getPlayingAudio();
      if (playingAudio == null) {
        bottomTitleView.setText("");
        bottomSubtitleView.setText("");
        topTitleView.setText("");
        topSubtitleView.setText("");
        albumArtImageView.setImageResource(R.drawable.album_art_default_small);
      } else {
        bottomTitleView.setText(playingAudio.getTitle());
        bottomSubtitleView.setText(playingAudio.getSubtitle(getActivity()));

        topTitleView.setText(playingAudio.getTitle());
        topSubtitleView.setText(playingAudio.getSubtitle(getActivity()));
        AlbumArtHelper.loadAlbumArt(
            getActivity(),
            playingAudio.getAlbumArt(localAudioStore),
            albumArtImageView,
            R.drawable.album_art_default_small);
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_play_bar, container, false);
    ButterKnife.inject(this, view);
    bottomPlayBtn.setEnabled(false);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
    updatePlayerInfo(null);
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    wifiModel.stop();
    handler.removeCallbacks(positionRunnable);
  }

  @OnClick(R.id.bottom_player_list)
  public void showPlayerList() {
    if (isDeviceFragmentShow) return;

    isDeviceFragmentShow = true;
    DeviceFragment deviceFragment = new DeviceFragment();
    deviceFragment.onClose(() -> isDeviceFragmentShow = false);
    deviceFragment.show(getFragmentManager(), "deviceFragment");
  }

  @OnClick(R.id.bottom_play)
  public void onPlayButtonClick() {
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

  public void showBottom() {
    topView.setVisibility(View.GONE);
    bottomView.setVisibility(View.VISIBLE);
  }

  public void showTop() {
    topView.setVisibility(View.VISIBLE);
    bottomView.setVisibility(View.GONE);
  }

  @OnClick(R.id.top_slide_down)
  public void slideDown() {
    MainActivity activity = (MainActivity) getActivity();
    activity.collapsePlayingPanel();
  }

  @OnClick(R.id.timer)
  public void showTimerDialog() {
    TimerDialog.newInstance(timerValue).show(getFragmentManager(), "");
  }

  @Subscribe
  public void timerCountDown(TimerEvent event) {
    timerValue = event.getTimerValue();
    if (event.getTimerValue() == 0) {
      timerTextView.setVisibility(View.GONE);
    } else {
      timerTextView.setVisibility(View.VISIBLE);
    }
    timerTextView.setText(TimeHelper.secondToString(event.getTimerValue()));
  }

  @Override
  public void scanResult(List<ScanResult> scanResults) {
    List<String> ssidList = DeviceUtil.filterScanResultList(scanResults);
    newDeviceCount = ssidList.size();
    if (newDeviceCount != 0) {
      //deviceNumView.setText(newDeviceCount + "");
      deviceNumView.setVisibility(View.VISIBLE);
    } else {
      deviceNumView.setVisibility(View.GONE);
    }
  }
}
