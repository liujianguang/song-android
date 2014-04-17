package com.song1.musicno1.models.play;


import android.content.Context;
import com.google.common.base.Strings;
import com.song1.musicno1.models.cmmusic.CMMusicStore;
import com.song1.musicno1.services.HttpService;
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
  protected final Context          context;
  protected final RenderingControl renderingControl;

  protected OnPositionChangedListener positionListener;
  protected int                       state;
  protected int                       position;
  protected int                       duration;

  protected boolean isOccupied = false;
  protected int     playMode   = MODE_REPEAT_ALL;

  Audio                  currentAudio;
  OnStateChangedListener stateListener;
  Thread                 positionThread;
  Renderer               renderer;
  OnCompleteListener     completeListener;

  Runnable runnable = () -> {
    while (!Thread.currentThread().isInterrupted() && state == PLAYING) {
      try {
        PositionInfo positionInfo = renderer.getPositionInfo();
        if (positionInfo.getDuration() != 0 && !Strings.isNullOrEmpty(positionInfo.getUri())) {
          setPosition(positionInfo.getPosition(), positionInfo.getDuration());

          if (currentAudio != null &&
              currentAudio.getRemotePlayUrl() != null &&
              !currentAudio.getRemotePlayUrl().equals(positionInfo.getUri())) {
            // this device is occupied by other control point
            state = STOPPED;
            isOccupied = true;
            if (stateListener != null) {
              stateListener.onStateChanged(this, state);
            }
            occupied();
            return;
          }

          if (positionInfo.getPosition() == positionInfo.getDuration()) {
            // on playing completely
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
  protected OnOccupiedListener onOccupiedListener;

  public void onOccupied(OnOccupiedListener onOccupiedListener) {
    this.onOccupiedListener = onOccupiedListener;
  }

  private void occupied() {
    if (onOccupiedListener != null) onOccupiedListener.onOccupied(this);
  }

  public Player(Context context, Renderer renderer, RenderingControl renderingControl) {
    this.renderer = renderer;
    this.context = context;
    this.renderingControl = renderingControl;

    if (renderer instanceof LocalRenderer) {
      LocalRenderer localRenderer = (LocalRenderer) renderer;
      localRenderer.onComplete((mediaPlayer) -> {
        if (completeListener != null) {
          completeListener.onComplete(this);
        }
      });
    }
  }

  public boolean isOccupied() {
    return this.isOccupied;
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
    isOccupied = false;
    setState(PREPARING);

    if (audio != null) {
      currentAudio = audio;
      setPosition(0, 0);
    }


    try {
      if (audio != null) {
        if (audio.getFrom() == Audio.MIGU) {
          String playUrl = getPlayUrl(audio);
          if (playUrl != null) {
            audio.setRemotePlayUrl(playUrl);
            audio.setLocalPlayUri(playUrl);
            Log.d(this, "Get " + audio.getTitle() + " play url :" + playUrl);
          } else {
            Log.d(this, "Get " + audio.getTitle() + " play url failed.");
            stop();
            error(new RendererException(""));
            return;
          }
        }
        if (renderer instanceof LocalRenderer) {
          renderer.setUri(audio.getLocalPlayUri());
        } else {
          if (Strings.isNullOrEmpty(audio.getRemotePlayUrl()) && audio.getFrom() == Audio.LOCAL) {
            audio.setRemotePlayUrl(HttpService.instance().share(audio.getLocalPlayUri()));
          }

          renderer.setUri(audio.getRemotePlayUrl());
        }
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

  private String getPlayUrl(final Audio audio) {
    CMMusicStore ms = new CMMusicStore(context);
    String songUrl = null;
    try {
      songUrl = ms.getOnLineListenerSongUrl(audio.getId());
    } catch (Exception ignored) {
    }
    return songUrl;
  }

  public void nextPlayMode() {
    playMode = (playMode + 1) % PLAY_MODES.length;
  }

  public void setVolume(int volume) {
    try {
      renderingControl.setVolume(volume);
    } catch (RendererException ignored) {
    }
  }

  public Volume getVolume() {
    try {
      return renderingControl.getVolume();
    } catch (RendererException e) {
      return null;
    }
  }

  public void volumeUp() {
    try {
      renderingControl.volumeUp();
    } catch (RendererException ignored) {
    }
  }

  public void volumeDown() {
    try {
      renderingControl.volumeDown();
    } catch (RendererException ignored) {
    }
  }

  public void updateVolume() {
    try {
      renderingControl.updateVolume();
    } catch (RendererException ignored) {
    }
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

  public interface OnOccupiedListener {
    void onOccupied(Player player);
  }

  private interface CheckRunnable {
    boolean check() throws RendererException;
  }

}
