package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.events.play.PlayModeEvent;
import com.song1.musicno1.models.events.play.PositionEvent;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.squareup.otto.Subscribe;
import de.akquinet.android.androlog.Log;

/**
 * Created by windless on 3/31/14.
 */
public class AudioActionsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
  @InjectView(R.id.position)        TextView positionView;
  @InjectView(R.id.duration)        TextView durationView;
  @InjectView(R.id.position_seeker) SeekBar  positionSeeker;
  @InjectView(R.id.actions_section) View     actionsSection;
  protected int playMode;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_audio_actions, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    positionSeeker.setEnabled(false);
    positionSeeker.setOnSeekBarChangeListener(this);
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
  public void onPositionChanged(PositionEvent event) {
    if (event.getAudio() == null) {
      actionsSection.setVisibility(View.GONE);
    } else {
      actionsSection.setVisibility(View.VISIBLE);
    }

    positionSeeker.setEnabled(event.getAudio() != null);

    positionSeeker.setMax(event.getDuration());
    positionSeeker.setProgress(event.getPosition());
    durationView.setText(TimeHelper.milli2str(event.getDuration()));
  }

  @Subscribe
  public void onPlayModeChanged(PlayModeEvent event) {
    playMode = event.getPlayMode();
    Log.d(this, "Play mode changed: " + playMode);
    switch (playMode) {
      case Player.MODE_NORMAL:
      case Player.MODE_REPEAT_ALL:
      case Player.MODE_REPEAT_ONE:
      case Player.MODE_SHUFFLE:
    }
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    positionView.setText(TimeHelper.milli2str(i));
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    Players.seek(seekBar.getProgress());
  }

  @OnClick(R.id.play_mode)
  public void onPlayModeClick() {
    Players.nextPlayMode();
  }
}
