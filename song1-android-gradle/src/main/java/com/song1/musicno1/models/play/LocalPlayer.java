package com.song1.musicno1.models.play;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import com.song1.musicno1.R;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by windless on 14-5-14.
 */
public class LocalPlayer implements Player, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
  protected final MediaPlayer     mediaPlayer;
  protected final ExecutorService executor;
  protected final Context         context;
  protected final AudioManager audioManager;
  protected Playlist playlist;
  protected int      state;
  protected Callback callback;
  protected Audio    playingAudio;
  protected int      position;
  protected int      duration;

  public LocalPlayer(Context context) {
    this.context = context;
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setOnCompletionListener(this);
    mediaPlayer.setOnSeekCompleteListener(this);
    mediaPlayer.setOnPreparedListener(this);
    executor = Executors.newFixedThreadPool(1);
    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
  }

  @Override
  public String getId() {
    return "0";
  }

  @Override
  public String getName() {
    return context.getString(R.string.this_phone);
  }

  @Override
  public void setPlaylist(Playlist playlist) {
    this.playlist = playlist;
    if (playlist.getCurrentAudio() != null) {
      playWithAudio(playlist.getCurrentAudio());
    }
    if (callback != null) {
      callback.onPlaylistChanged(this, playlist);
    }
  }

  @Override
  public void playWithAudio(Audio audio) {
    if (audio == null) {
      duration = 0;
      position = 0;
      mediaPlayer.stop();
      setState(State.STOPPED);
      return;
    }

    if (playlist != null) {
      playlist.setCurrentAudio(audio);
    }
    
    setPlayingAudio(audio);
    setState(State.PREPARING);
    try {
      executor.submit(() -> {
        try {
          mediaPlayer.reset();
          mediaPlayer.setDataSource(audio.getLocalPlayUri());
          mediaPlayer.prepareAsync();
        } catch (IOException e) {
          stop();
        }
      });
    } catch (RejectedExecutionException ignored) {
    }
  }

  @Override
  public void pause() {
    mediaPlayer.pause();
    setState(State.PAUSED);
  }

  @Override
  public void resume() {
    if (state == State.PAUSED) {
      mediaPlayer.start();
      setState(State.PLAYING);
    }
  }

  @Override
  public void seekTo(int seconds) {
    setState(State.PREPARING);
    mediaPlayer.seekTo(seconds);
  }

  @Override
  public void stop() {
    mediaPlayer.stop();
    setState(State.STOPPED);
  }

  @Override
  public void release() {
    mediaPlayer.release();
    callback = null;
  }

  @Override
  public void next() {
    if (playlist != null) {
      playlist.next(0);
      playWithAudio(playlist.getCurrentAudio());
    }
  }

  @Override
  public void previous() {
    if (playlist != null) {
      playlist.previous();
      playWithAudio(playlist.getCurrentAudio());
    }
  }

  @Override
  public Playlist getPlaylist() {
    return playlist;
  }

  @Override
  public int getDuration() {
    return duration;
  }

  @Override
  public int getPosition() {
    if (mediaPlayer.isPlaying()) {
      position = mediaPlayer.getCurrentPosition();
    }
    return position;
  }

  @Override
  public int getState() {
    return state;
  }

  @Override
  public Audio getPlayingAudio() {
    return playingAudio;
  }

  @Override
  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  @Override
  public void setVolume(int volume, boolean showPanel) {
    if (showPanel) {
      audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
    } else {
      audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_ALLOW_RINGER_MODES);
    }
  }

  @Override
  public Volume getVolume() {
    int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    return new Volume(volume, max);
  }

  @Override
  public void volumeUp(boolean showPanel) {
    Volume volume = getVolume();
    int setVolume = volume.getCurrent() + 1;
    if (setVolume > volume.getMax()) {
      setVolume = volume.getMax();
    }
    setVolume(setVolume, showPanel);

  }

  @Override
  public void volumeDown(boolean showPanel) {
    Volume volume = getVolume();
    int setVolume = volume.getCurrent() - 1;
    if (setVolume < 0) {
      setVolume = 0;
    }
    setVolume(setVolume, showPanel);
  }

  private void setState(int state) {
    this.state = state;
    if (callback != null) {
      callback.onStateChanged(this, state);
    }
  }

  private void setPlayingAudio(Audio audio) {
    position = 0;
    duration = 0;
    playingAudio = audio;
    if (callback != null) callback.onPlayingAudioChanged(this, audio);
  }

  @Override
  public void onCompletion(MediaPlayer mediaPlayer) {
    if (callback != null) {
      boolean noError = false;
      callback.onCompletion(this, noError);
    }
  }

  @Override
  public void onPrepared(MediaPlayer mediaPlayer) {
    duration = mediaPlayer.getDuration();
    mediaPlayer.start();
    setState(State.PLAYING);
  }

  @Override
  public void onSeekComplete(MediaPlayer mediaPlayer) {
    mediaPlayer.start();
    setState(State.PLAYING);
  }
}
