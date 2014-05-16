package com.song1.musicno1.models.play;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by windless on 14-5-14.
 */
public interface Player {
  public String getId();

  public String getName();

  public void setPlaylist(Playlist playlist);

  public void playWithAudio(Audio audio);

  public void pause();

  public void resume();

  public void seekTo(int seconds);

  public void stop();

  public void release();

  public void next();

  public void previous();

  public Playlist getPlaylist();

  public int getDuration();

  public int getPosition();

  public int getState();

  public Audio getPlayingAudio();

  public void setCallback(Callback callback);

  public void setVolume(int volume, boolean showPanel);

  public Volume getVolume();

  public void volumeUp(boolean showPanel);

  public void volumeDown(boolean showPanel);

  public void setPlayMode(int playMode);

  public int getPlayMode();

  public void nextPlayMode();

  public interface State {
    int STOPPED   = 0;
    int PREPARING = 1;
    int PLAYING   = 2;
    int PAUSED    = 3;
  }

  public interface PlayMode {
    int NORMAL     = 0;
    int REPEAT_ALL = 1;
    int REPEAT_ONE = 2;
    int SHUFFLE    = 3;

    List<Integer> MODES = Lists.newArrayList(NORMAL, REPEAT_ALL, REPEAT_ONE, SHUFFLE);
  }

  public interface Callback {
    public void onStateChanged(Player player, int state);

    public void onCompletion(Player player, boolean isError);

    public void onPlayingAudioChanged(Player player, Audio audio);

    public void onPlaylistChanged(Player player, Playlist playlist);

    void onOccupied(Player remotePlayer);
  }
}
