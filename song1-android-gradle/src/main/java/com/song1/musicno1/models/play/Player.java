package com.song1.musicno1.models.play;


import de.akquinet.android.androlog.Log;

/**
 * Created by windless on 3/27/14.
 */
public class Player {
  public static final int STOPPED   = 0;
  public static final int PLAYING   = 1;
  public static final int PAUSED    = 2;
  public static final int PREPARING = 3;

  public static final int MODE_NORMAL     = 0;
  public static final int MODE_REPEAT_ALL = 1;
  public static final int MODE_REPEAT_ONE = 2;
  public static final int MODE_SHUFFLE    = 3;

  private final static int TIMEOUT = 20;

  private final int[] PLAY_MODES = new int[]{MODE_NORMAL, MODE_REPEAT_ALL, MODE_REPEAT_ONE, MODE_SHUFFLE};

  protected OnPositionChangedListener positionListener;
  protected int                       state;
  protected int                       position;
  protected int                       duration;

  protected int playMode = MODE_REPEAT_ALL;

  Audio                  currentAudio;
  OnStateChangedListener stateListener;
  Thread                 positionThread;
  Renderer               renderer;
  OnCompleteListener     completeListener;

  Runnable runnable = () -> {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        PositionInfo positionInfo = renderer.getPositionInfo();
        if (positionInfo.getDuration() != 0) {
          Log.d(this, "Position: " + positionInfo.getPosition() + "-" + positionInfo.getDuration());
          setPosition(positionInfo.getPosition(), positionInfo.getDuration());
          if (positionInfo.getPosition() == positionInfo.getDuration()) {
            state = STOPPED;
            if (stateListener != null) {
              stateListener.onStateChanged(this, state);
            }
            if (completeListener != null) {
              completeListener.onComplete(this);
            }
            return;
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

  public Player(Renderer renderer) {
    this.renderer = renderer;
    if (renderer instanceof LocalRenderer) {
      LocalRenderer localRenderer = (LocalRenderer) renderer;
      localRenderer.onComplete((mediaPlayer) -> {
        if (completeListener != null) {
          completeListener.onComplete(this);
        }
      });
    }
  }

  public int getPlayMode() {
    return playMode;
  }

  public void setPlayMode(int playMode) {
    this.playMode = playMode;
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
    if (seekTo == duration && seekTo != 0) {
      if (completeListener != null) {
        completeListener.onComplete(this);
      }
      return;
    }

    setState(PREPARING);
    setPosition(seekTo, duration);
    try {
      renderer.seek(seekTo);

      checkStatus(PLAYING, () -> !checkPosition(seekTo));
    } catch (RendererException e) {
      error(e);
    }
  }

  public void onPlayComplete(OnCompleteListener listener) {
    completeListener = listener;
  }

  private boolean checkPosition(int seekTo) {
    try {
      PositionInfo info = renderer.getPositionInfo();
      return Math.abs(info.getPosition() - seekTo) <= 3000;
    } catch (RendererException e) {
      return false;
    }
  }

  public void nextPlayMode() {
    playMode = (playMode + 1) % PLAY_MODES.length;
  }

  public interface OnStateChangedListener {
    void onStateChanged(Player player, int state);
  }

  public interface OnPositionChangedListener {
    void onPositionChanged(Player player, long position, long duration);
  }

  public interface OnCompleteListener {
    void onComplete(Player player);
  }

  private interface CheckRunnable {
    boolean check() throws RendererException;
  }
}
