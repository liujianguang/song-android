package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.song1.musicno1.R;
import com.song1.musicno1.helpers.AlbumArtHelper;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.events.play.CurrentPlayerEvent;
import com.song1.musicno1.models.events.play.CurrentPlayerStateEvent;
import com.song1.musicno1.models.events.play.PlayModeEvent;
import com.song1.musicno1.models.events.play.PositionEvent;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.OldPlayer;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.stores.PlayerStore;
import com.song1.musicno1.util.RoundedTransformation;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

/**
 * Created by windless on 3/31/14.
 */
public class AudioActionsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
  @InjectView(R.id.position)        TextView    positionView;
  @InjectView(R.id.duration)        TextView    durationView;
  @InjectView(R.id.position_seeker) SeekBar     positionSeeker;
  @InjectView(R.id.actions_section) View        actionsSection;
  @InjectView(R.id.album_art)       ImageView   albumArtImageView;
  @InjectView(R.id.play_mode)       ImageButton playModeBtn;

  private LocalAudioStore localAudioStore;
  private ObjectAnimator  rotation;

  private Handler  handler          = new Handler();
  private Runnable positionRunnable = new Runnable() {
    @Override
    public void run() {
      Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
      if (currentPlayer != null) {
        positionSeeker.setMax(currentPlayer.getDuration());
        positionSeeker.setProgress(currentPlayer.getPosition());
        durationView.setText(TimeHelper.milli2str(currentPlayer.getDuration()));
      }
      handler.postDelayed(this, 1000);
    }
  };

  private float rotationStart = 0;
  protected Audio playingAudio;
  protected int   state;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_audio_actions, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    localAudioStore = new LocalAudioStore(getActivity());
    positionSeeker.setEnabled(false);
    positionSeeker.setOnSeekBarChangeListener(this);

    newRotationAnimator();

    updatePlayerState(null);
    updatePlayingAudio(null);
  }

  private void newRotationAnimator() {
    rotation = ObjectAnimator.ofFloat(albumArtImageView, "rotation", rotationStart, 360f + rotationStart);
    rotation.setRepeatCount(ValueAnimator.INFINITE);
    rotation.setRepeatMode(ValueAnimator.RESTART);
    rotation.setInterpolator(new LinearInterpolator());
    rotation.setDuration(30000);
    rotation.addUpdateListener(animation -> rotationStart = (float) animation.getAnimatedValue()
    );
  }

  @Subscribe
  public void updatePlayerState(PlayerStore.PlayerStateChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      state = currentPlayer.getState();
      if (state == Player.State.PLAYING) {
        handler.post(positionRunnable);

        if (rotation != null) {
          rotation.cancel();
        }
        newRotationAnimator();
        rotation.start();
      } else {
        handler.removeCallbacks(positionRunnable);

        rotation.cancel();
      }
    }
  }

  @Subscribe
  public void updatePlayingAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      playingAudio = currentPlayer.getPlayingAudio();
      if (playingAudio == null) {
        Picasso.with(getActivity()).load(R.drawable.default_album_art).transform(new RoundedTransformation()).into(albumArtImageView);
      } else {
        AlbumArtHelper.loadAlbumArtRounded(
            getActivity(),
            playingAudio.getAlbumArt(localAudioStore),
            albumArtImageView,
            R.drawable.default_album_art
        );
      }
      positionSeeker.setEnabled(playingAudio != null);
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
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(positionRunnable);
  }

  @Subscribe
  public void onPositionChanged(PositionEvent event) {
    positionSeeker.setMax(0);
    positionSeeker.setProgress(0);
    Audio audio = event.getAudio();
    if (playingAudio != audio) {
      playingAudio = audio;
      if (playingAudio == null) {
        Picasso.with(getActivity()).load(R.drawable.default_album_art).transform(new RoundedTransformation()).into(albumArtImageView);
      } else {
        AlbumArtHelper.loadAlbumArtRounded(
            getActivity(),
            playingAudio.getAlbumArt(localAudioStore),
            albumArtImageView,
            R.drawable.default_album_art
        );
      }
      positionSeeker.setEnabled(event.getAudio() != null);
    }

    positionSeeker.setMax(event.getDuration());
    positionSeeker.setProgress(event.getPosition());
    durationView.setText(TimeHelper.milli2str(event.getDuration()));
  }

  @Subscribe
  public void onPlayModeChanged(PlayModeEvent event) {
    updatePlayModeBtn(event.getPlayMode());
  }

  private void updatePlayModeBtn(int playMode) {
    switch (playMode) {
      case OldPlayer.MODE_NORMAL:
        playModeBtn.setImageResource(R.drawable.ic_play_mode_normal);
        break;
      case OldPlayer.MODE_REPEAT_ALL:
        playModeBtn.setImageResource(R.drawable.ic_play_mode_repeat_all);
        break;
      case OldPlayer.MODE_REPEAT_ONE:
        playModeBtn.setImageResource(R.drawable.ic_play_mode_repeat_one);
        break;
      case OldPlayer.MODE_SHUFFLE:
        playModeBtn.setImageResource(R.drawable.ic_play_mode_random);
    }
  }

  @Subscribe
  public void onPlayStateChanged(CurrentPlayerStateEvent event) {
    if (event.state == OldPlayer.PLAYING) {
      if (rotation != null) {
        rotation.cancel();
      }
      newRotationAnimator();
      rotation.start();
    } else {
      rotation.cancel();
    }
  }

  @Subscribe
  public void onCurrentPlayerChanged(CurrentPlayerEvent event) {
    updatePlayModeBtn(event.getCurrentPlayer().getPlayMode());
  }

  @OnClick(R.id.album_art)
  public void toggleActionsView() {
    if (actionsSection.getVisibility() == View.GONE) {
      actionsSection.setVisibility(View.VISIBLE);
    } else {
      actionsSection.setVisibility(View.GONE);
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
