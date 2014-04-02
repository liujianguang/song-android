package com.song1.musicno1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.CurrentNotworkDeviceActivity;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.CurrentPlayerStateEvent;
import com.song1.musicno1.models.events.play.PositionEvent;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.squareup.otto.Subscribe;

/**
 * Created by windless on 3/27/14.
 */
public class PlayBarFragment extends Fragment {
  protected                           int         state;
  @InjectView(R.id.bottom_title)      TextView    bottomTitleView;
  @InjectView(R.id.bottom_subtitle)   TextView    bottomSubtitleView;
  @InjectView(R.id.bottom_play)       ImageButton buttonPlayBtn;
  @InjectView(R.id.prepare_progress)  ProgressBar prepareBar;
  @InjectView(R.id.position_progress) ProgressBar positionBar;
  @InjectView(R.id.top)               View        topView;
  @InjectView(R.id.bottom)            View        bottomView;
  @InjectView(R.id.top_title)         TextView    topTitleView;
  @InjectView(R.id.top_subtitle)      TextView    topSubtitleView;

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
    startActivity(new Intent(getActivity(), CurrentNotworkDeviceActivity.class));
  }

  @OnClick(R.id.bottom_play)
  public void onPlayButtonClick() {
    switch (state) {
      case Player.PLAYING:
        Players.pause();
        break;
      case Player.PAUSED:
        Players.play();
        break;
    }
  }

  @Subscribe
  public void onCurrentPlayerStateChanged(CurrentPlayerStateEvent event) {
    state = event.state;
    switch (event.state) {
      case Player.PAUSED:
      case Player.STOPPED:
        buttonPlayBtn.setImageResource(R.drawable.ic_play);
        buttonPlayBtn.setEnabled(true);
        prepareBar.setVisibility(View.GONE);
        positionBar.setVisibility(View.VISIBLE);
        break;
      case Player.PLAYING:
        buttonPlayBtn.setImageResource(R.drawable.ic_pause);
        buttonPlayBtn.setEnabled(true);
        prepareBar.setVisibility(View.GONE);
        positionBar.setVisibility(View.VISIBLE);
        break;
      case Player.PREPARING:
        buttonPlayBtn.setEnabled(false);
        prepareBar.setVisibility(View.VISIBLE);
        positionBar.setVisibility(View.GONE);
    }
  }

  @Subscribe
  public void onCurrentPositionChanged(PositionEvent event) {
    if (event.getAudio() == null) {
      bottomTitleView.setText("");
      bottomSubtitleView.setText("");
      topTitleView.setText("");
      topSubtitleView.setText("");
    } else {
      if (bottomTitleView.getText() == null) {
        bottomTitleView.setText(event.getAudio().getTitle());
        topTitleView.setText(event.getAudio().getTitle());
      } else if (!bottomTitleView.getText().toString().equals(event.getAudio().getTitle())) {
        bottomTitleView.setText(event.getAudio().getTitle());
        topTitleView.setText(event.getAudio().getTitle());
      }

      bottomSubtitleView.setText(event.getAudio().getArtist());
      topSubtitleView.setText(event.getAudio().getArtist());
    }

    positionBar.setMax(event.getDuration());
    positionBar.setProgress(event.getPosition());
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
}
