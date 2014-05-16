package com.song1.musicno1.models.play;

import com.google.common.base.Strings;
import com.song1.musicno1.services.HttpService;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.std.av.renderer.AVTransport;
import org.cybergarage.upnp.std.av.renderer.RenderingControl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by windless on 14-5-14.
 */
public class RemotePlayer implements Player {
  private final static int TIMEOUT = 20;

  protected final RemoteRenderingControl renderingControl;
  protected final Device                 device;
  protected final ExecutorService        playExecutor;
  protected final ExecutorService        volumeExecutors;

  protected     RemoteRenderer renderer = null;
  private final Object         lock     = new Object();

  protected Playlist playlist;
  private   int      volume;
  protected int      state;
  protected Callback callback;
  protected Audio    playingAudio;
  private   int      duration;
  private   int      position;
  private   Thread   positionThread;

  Runnable runnable = () -> {
    while (!Thread.currentThread().isInterrupted() && state == State.PLAYING) {
      try {
        PositionInfo positionInfo = renderer.getPositionInfo();
        if (positionInfo.getDuration() != 0 && !Strings.isNullOrEmpty(positionInfo.getUri())) {
          position = positionInfo.getPosition();
          duration = positionInfo.getDuration();
          Log.d(this, "update position: " + position + " - " + duration);

          if (playingAudio != null &&
              playingAudio.getRemotePlayUrl() != null &&
              !playingAudio.getRemotePlayUrl().equals(positionInfo.getUri())) {
            setState(State.STOPPED);
            if (callback != null) {
              callback.onOccupied(this);
            }
            return;
          }

          if (positionInfo.getPosition() != 0 &&
              positionInfo.getDuration() == positionInfo.getPosition()) {
            setState(State.STOPPED);
            if (callback != null) {
              callback.onCompletion(this, false);
            }
          }
        }
      } catch (RendererException ignored) {
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        return;
      }
    }
  };
  protected int playMode;

  public static RemotePlayer newInstance(Device device) {
    Service av = device.getService(AVTransport.SERVICE_TYPE);
    if (av == null) return null;
    Service renderingControl = device.getService(RenderingControl.SERVICE_TYPE);
    if (renderingControl == null) return null;

    return new RemotePlayer(device, new RemoteRenderer(av), new RemoteRenderingControl(renderingControl));
  }

  private RemotePlayer(Device device, RemoteRenderer renderer, RemoteRenderingControl renderingControl) {
    this.device = device;
    this.renderer = renderer;
    this.renderingControl = renderingControl;
    playExecutor = Executors.newFixedThreadPool(1);
    volumeExecutors = Executors.newFixedThreadPool(1);
    try {
      volume = renderingControl.getVolume().getCurrent();
    } catch (RendererException ignored) {
    }
  }

  @Override
  public String getId() {
    return device.getUDN();
  }

  @Override
  public String getName() {
    return device.getFriendlyName();
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
    duration = 0;
    position = 0;

    if (audio == null) {
      stop();
      return;
    }


    if (playlist != null) {
      playlist.setCurrentAudio(audio);
    }
    setPlayingAudio(audio);

    setState(State.PREPARING);

    if (Strings.isNullOrEmpty(audio.getRemotePlayUrl()) && audio.getFrom() == Audio.LOCAL) {
      audio.setRemotePlayUrl(HttpService.instance().share(audio.getLocalPlayUri()));
    }
    try {
      playExecutor.submit(() -> {
        try {
          renderer.setUri(audio.getRemotePlayUrl());
          renderer.play();
          checkStatus(State.PLAYING, () -> !renderer.isPlaying());
          if (state == State.STOPPED) {
            stop();
          }
        } catch (RendererException e) {
          setState(State.STOPPED);
          error();
        }
      });
    } catch (RejectedExecutionException ignored) {
    }
  }

  @Override
  public void pause() {
    try {
      playExecutor.submit(() -> {
        setState(State.PREPARING);
        try {
          renderer.pause();
          checkStatus(State.PAUSED, () -> renderer.isPlaying());
        } catch (RendererException e) {
        }
      });
    } catch (RejectedExecutionException ignored) {
    }

  }

  @Override
  public void resume() {
    try {
      playExecutor.submit(() -> {
        setState(State.PREPARING);
        try {
          renderer.play();
          checkStatus(State.PLAYING, () -> !renderer.isPlaying());
        } catch (RendererException e) {
          setState(State.STOPPED);
        }
      });
    } catch (RejectedExecutionException ignored) {
    }

  }

  @Override
  public void seekTo(int seconds) {
    if (seconds == duration && seconds != 0) {
      if (callback != null) {
        boolean noError = false;
        callback.onCompletion(this, noError);
      }
      return;
    }

    position = seconds;
    try {
      playExecutor.submit(() -> {
        setState(State.PREPARING);
        try {
          renderer.seek(seconds);
          checkStatus(State.PLAYING, () -> !checkPosition(seconds));
        } catch (RendererException e) {
          setState(State.STOPPED);
        }
      });
    } catch (RejectedExecutionException ignored) {
    }

  }

  @Override
  public void stop() {
    try {
      playExecutor.submit(() -> {
        try {
          renderer.stop();
        } catch (RendererException ignored) {
        }
        setState(State.STOPPED);
      });
    } catch (RejectedExecutionException ignored) {
    }
  }

  @Override
  public void release() {
    stop();
    playExecutor.shutdown();
    volumeExecutors.shutdown();
  }

  @Override
  public void next() {
    if (playlist != null) {
      playlist.next(playMode);
      playWithAudio(playlist.getCurrentAudio());
    }
  }

  @Override
  public void previous() {
    if (playlist != null) {
      playlist.previous(playMode);
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
    this.volume = volume;
    try {
      volumeExecutors.submit(() -> {
        try {
          renderingControl.setVolume(volume);
        } catch (RendererException ignored) {
        }
      });
    } catch (RejectedExecutionException ignored) {
    }
  }

  @Override
  public Volume getVolume() {
    return new Volume(volume, 100);
  }

  @Override
  public void volumeUp(boolean showPanel) {
    volume = volume + 3;
    if (volume > 100) {
      volume = 100;
    }
    setVolume(volume, false);
  }

  @Override
  public void volumeDown(boolean showPanel) {
    volume = volume - 3;
    if (volume < 0) {
      volume = 0;
    }
    setVolume(volume, false);
  }

  @Override
  public void setPlayMode(int playMode) {
    this.playMode = playMode;
  }

  @Override
  public int getPlayMode() {
    return playMode;
  }

  @Override
  public void nextPlayMode() {
    int index = PlayMode.MODES.indexOf(playMode);
    index = (index + 1) % PlayMode.MODES.size();
    playMode = PlayMode.MODES.get(index);
  }

  private void setState(int state) {
    this.state = state;

    if (callback != null) {
      callback.onStateChanged(this, state);
    }

    if (state == State.PLAYING) {
      startPositionThread();
    } else {
      stopPositionThread();
    }
  }

  private void setPlayingAudio(Audio audio) {
    if (playingAudio != audio) {
      playingAudio = audio;
      if (callback != null) {
        callback.onPlayingAudioChanged(this, audio);
      }
    }
  }

  private void stopPositionThread() {
    synchronized (lock) {
      if (positionThread != null) {
        positionThread.interrupt();
        try {
          positionThread.join();
        } catch (InterruptedException ignored) {
        }
      }
    }
  }

  private void startPositionThread() {
    synchronized (lock) {
      if (positionThread != null) {
        positionThread.interrupt();
        try {
          positionThread.join();
        } catch (InterruptedException ignored) {
        }
      }

      positionThread = new Thread(runnable);
      positionThread.start();
    }
  }

  private void checkStatus(int completeState, CheckRunnable runnable) {
    int i = 0;
    try {
      while (i <= TIMEOUT && state == State.PREPARING && runnable.check()) {
        i++;
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          return;
        }
      }
      if (i > TIMEOUT) {
        setState(State.STOPPED);
        error();
      } else {
        setState(completeState);
      }
    } catch (RendererException e) {
      setState(State.STOPPED);
      error();
    }
  }

  private void error() {
    if (callback != null) callback.onCompletion(this, true);
  }

  private boolean checkPosition(int seekTo) {
    try {
      PositionInfo info = renderer.getPositionInfo();
      return Math.abs(info.getPosition() - seekTo) <= 3000;
    } catch (RendererException e) {
      return false;
    }
  }

  private interface CheckRunnable {
    boolean check() throws RendererException;
  }
}

