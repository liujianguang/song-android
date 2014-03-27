package com.song1.musicno1.models.play;

import javax.xml.namespace.QName;

/**
 * Created by windless on 3/27/14.
 */
public class Player {
  public static final int STOPPED   = 0;
  public static final int PLAYING   = 1;
  public static final int PAUSED    = 2;
  public static final int PREPARING = 3;
  protected OnPositionChangedListener positionListener;
  protected int                       state;
  protected long                      position;
  protected long                      duration;
  Audio                  currentAudio;
  OnStateChangedListener stateListener;
  Renderer renderer       = null;
  Thread   positionThread = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        PositionInfo positionInfo = renderer.getPositionInfo();
        setPosition(positionInfo.getPosition(), positionInfo.getDuration());
      } catch (RendererException ignored) {
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        return;
      }
    }
  });

  public Player(Renderer renderer) {
    this.renderer = renderer;
  }

  private void setPosition(long position, long duration) {
    this.position = position;
    this.duration = duration;
    if (positionListener != null) {
      positionListener.onPositionChanged(this, position, duration);
    }
  }

  public void play(Audio audio) {
    currentAudio = audio;
    setState(PREPARING);
    stopPositionThread();
    try {
      renderer.setUri(audio.getRemotePlayUrl());
      renderer.play();

      int i = 0;
      while (i <= 5 || !renderer.isPlaying()) {
        i++;
        Thread.sleep(1000);
      }

      setState(PLAYING);
    } catch (RendererException e) {
      stop();
      error(e);
    } catch (InterruptedException ignored) {
    }
  }

  private void stopPositionThread() {
    if (positionThread != null) {
      positionThread.interrupt();
      try {
        positionThread.join();
      } catch (InterruptedException ignored) {
      }
    }
  }

  private void error(RendererException e) {

  }

  public void pause() {
    setState(PREPARING);
    try {
      stopPositionThread();
      renderer.pause();
      setState(PAUSED);
    } catch (RendererException e) {
      error(e);
    }
  }

  public void stop() {
    stopPositionThread();
    try {
      renderer.stop();
    } catch (RendererException ignored) {
    }
    setState(STOPPED);
  }

  public void onStateChanged(OnStateChangedListener listener) {
    this.stateListener = listener;
  }

  public void onPositionChanged(OnPositionChangedListener listener) {
    positionListener = listener;
  }

  public int getState() {
    return state;
  }

  private void setState(int state) {
    this.state = state;
    if (stateListener != null) {
      stateListener.onStateChanged(this, state);
    }
  }

  public Audio getCurrentAudio() {
    return currentAudio;
  }

  public long getPosition() {
    return position;
  }

  public long getDuration() {
    return duration;
  }

  public String getName() {
    return renderer.getName();
  }

  public String getId() {
    return renderer.getId();
  }

  public interface OnStateChangedListener {
    void onStateChanged(Player player, int state);
  }

  public interface OnPositionChangedListener {
    void onPositionChanged(Player player, long position, long duration);
  }
}
