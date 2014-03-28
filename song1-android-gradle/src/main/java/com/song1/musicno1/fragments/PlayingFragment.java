package com.song1.musicno1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.CurrentNotworkDeviceActivity;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.CurrentPlayerStateEvent;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.squareup.otto.Subscribe;

/**
 * Created by windless on 3/28/14.
 */
public class PlayingFragment extends Fragment {
  protected int state;

  @InjectView(R.id.play) ImageButton playBtn;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_playing, container, false);
    ButterKnife.inject(this, view);
    return view;
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

  @OnClick(R.id.play)
  public void onPlayClick() {
    switch (state) {
      case Player.PLAYING:
        Players.pause();
        break;
      case Player.PAUSED:
        Players.play();
        break;
    }
  }

  @OnClick(R.id.player_list)
  public void onPlayerListClick() {
    startActivity(new Intent(getActivity(), CurrentNotworkDeviceActivity.class));
  }
}
