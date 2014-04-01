package com.song1.musicno1.models.play;


/**
 * Created by windless on 3/27/14.
 */
public class Player {
  public static final int STOPPED   = 0;
  public static final int PLAYING   = 1;
  public static final int PAUSED    = 2;
  public static final int PREPARING = 3;

  private final static int TIMEOUT = 20;

  protected OnPositionChangedListener positionListener;
  protected int                       state;
  protected int                       position;
  protected int                       duration;
  Audio                  currentAudio;
  OnStateChangedListener stateListener;
  Renderer renderer = null;

  Thread positionThread;

  Runnable runnable = () -> {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        PositionInfo positionInfo = renderer.getPositionInfo();
        if (positionInfo.getDuration() != 0) {
          setPosition(positionInfo.getPosition(), positionInfo.getDuration());
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

  public Player(Renderer renderer) {
    this.renderer = renderer;
  }

  private void setPosition(int position, int duration) {
    this.position = position;
    this.duration = duration;
    if (positionListener != null) {
      positionListener.onPositionChanged(this, position, duration);
    }
  }

  public void play(Audio audio) {
    setState(PREPARING);

    if (audio != null) {
      currentAudio = audio;
      setPosition(0, 0);
    }

    try {
      if (audio != null) {
        renderer.setUri(audio.getRemotePlayUrl());
      }
      renderer.play();

      checkStatus(PLAYING, () -> !renderer.isPlaying());
    } catch (RendererException e) {
      stop();
      error(e);
    }
  }

  public void play() {
    play(null);
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

  private void checkStatus(int completeState, CheckRunnable runnable) {
    int i = 0;
    try {
      while (i <= TIMEOUT && state == PREPARING && runnable.check()) {
        i++;
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          return;
        }
      }
      if (i > TIMEOUT) {
        error(new RendererException(RendererException.POST_ACTION_FAILED));
      } else {
        setState(completeState);
      }
    } catch (RendererException e) {
      error(e);
    }
  }

  public void pause() {
    setState(PREPARING);
    try {
      renderer.pause();
      checkStatus(PAUSED, () -> renderer.isPlaying());
    } catch (RendererException e) {
      error(e);
    }
  }

  public void stop() {
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

    if (state == PLAYING) {
      startPositionThread();
    } else {
      stopPositionThread();
    }
  }

  private void startPositionThread() {
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

  public Audio getCurrentAudio() {
    return currentAudio;
  }

  public int getPosition() {
    return position;
  }

  public int getDuration() {
    return duration;
  }

  public String getName() {
    return renderer.getName();
  }

  public String getId() {
    return renderer.getId();
  }

  public void seek(int seekTo) {
    setState(PREPARING);
    setPosition(seekTo, duration);
    try {
      renderer.seek(seekTo);

      checkStatus(PLAYING, () -> !checkPosition(seekTo));
    } catch (RendererException e) {
      error(e);
    }
  }

  private boolean checkPosition(int seekTo) {
    try {
      PositionInfo info = renderer.getPositionInfo();
      return Math.abs(info.getPosition() - seekTo) <= 3000;
    } catch (RendererException e) {
      return false;
    }
  }

  public interface OnStateChangedListener {
    void onStateChanged(Player player, int state);
  }

  public interface OnPositionChangedListener {
    void onPositionChanged(Player player, long position, long duration);
  }

  private interface CheckRunnable {
    boolean check() throws RendererException;
  }
}
