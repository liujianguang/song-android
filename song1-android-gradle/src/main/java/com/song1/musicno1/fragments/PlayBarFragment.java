package com.song1.musicno1.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
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
import com.google.common.base.Strings;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.helpers.AlbumArtHelper;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.events.play.*;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.util.RoundedTransformation;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by windless on 3/27/14.
 */
public class PlayBarFragment extends Fragment {
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

  LocalAudioStore localAudioStore;
  protected int timerValueIndex;

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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_play_bar, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
  }

  @OnClick(R.id.bottom_player_list)
  public void showPlayerList() {
    DeviceFragment deviceFragment = new DeviceFragment();
    deviceFragment.show(getFragmentManager(), "deviceFragment");
  }

  @OnClick(R.id.bottom_play)
  public void onPlayButtonClick() {
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

  @Subscribe
  public void onPlayerSelected(SelectPlayerEvent event) {
    if (event.player != null) {
      playerListBtn.setImageResource(R.drawable.ic_player_selected);
    }
  }

  @Subscribe
  public void onCurrentPlayerStateChanged(CurrentPlayerStateEvent event) {
    state = event.state;
    switch (event.state) {
      case Player.PAUSED:
      case Player.STOPPED:
        bottomPlayBtn.setImageResource(R.drawable.ic_play);
        bottomPlayBtn.setEnabled(true);
        positionBar.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        break;
      case Player.PLAYING:
        bottomPlayBtn.setImageResource(R.drawable.ic_pause);
        bottomPlayBtn.setEnabled(true);
        positionBar.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        break;
      case Player.PREPARING:
        bottomPlayBtn.setEnabled(false);
        positionBar.setVisibility(View.GONE);
        refreshLayout.setRefreshing(true);
    }
  }

  @Subscribe
  public void onCurrentPositionChanged(PositionEvent event) {
    Audio audio = event.getAudio();
    if (event.getAudio() == null) {
      bottomTitleView.setText("");
      bottomSubtitleView.setText("");
      topTitleView.setText("");
      topSubtitleView.setText("");
      Picasso.with(getActivity()).load(R.drawable.default_album_art_small).transform(new RoundedTransformation()).into(albumArtImageView);
    } else {
      if (bottomTitleView.getText() == null) {
        bottomTitleView.setText(event.getAudio().getTitle());
        topTitleView.setText(event.getAudio().getTitle());
      } else if (!bottomTitleView.getText().toString().equals(event.getAudio().getTitle())) {
        bottomTitleView.setText(event.getAudio().getTitle());
        bottomSubtitleView.setText(event.getAudio().getArtist());

        topTitleView.setText(event.getAudio().getTitle());
        topSubtitleView.setText(event.getAudio().getArtist());

        bottomSubtitleView.setText(event.getAudio().getArtist() + " -- " + event.getAudio().getAlbum());
        topSubtitleView.setText(event.getAudio().getArtist() + " -- " + event.getAudio().getAlbum());

        AlbumArtHelper.loadAlbumArtRounded(
            getActivity(),
            audio.getAlbumArt(localAudioStore),
            albumArtImageView,
            R.drawable.default_album_art_small);
      }
      positionBar.setMax(event.getDuration());
      positionBar.setProgress(event.getPosition());
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
    new AlertDialog.Builder(getActivity())
        .setSingleChoiceItems(getResources().getStringArray(R.array.timer_values), timerValueIndex, (dialog, which) -> {
          dialog.dismiss();
          timerValueIndex = which;
          MainBus.post(new StartTimerEvent(timerValueIndex));
          if (timerValueIndex == 0) {
            timerTextView.setVisibility(View.GONE);
          }
        }).show();
  }

  @Subscribe
  public void onShowDeviceFragment(ShowDeviceFragmentEvent event) {
    showPlayerList();
  }

  @Subscribe
  public void timerCountDown(TimerEvent event) {
    if (event.getTimerValue() == 0) {
      timerTextView.setVisibility(View.GONE);
    } else {
      timerTextView.setVisibility(View.VISIBLE);
    }
    timerTextView.setText(TimeHelper.secondToString(event.getTimerValue()));
  }
}
