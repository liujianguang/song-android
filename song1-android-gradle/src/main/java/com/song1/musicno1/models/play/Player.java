package com.song1.musicno1.models.play;

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

  public interface State {
    int STOPPED   = 0;
    int PREPARING = 1;
    int PLAYING   = 2;
    int PAUSED    = 3;
  }

  public interface Callback {
    public void onStateChanged(Player player, int state);

    public void onCompletion(Player player, boolean isError);

    public void onPlayingAudioChanged(Player player, Audio audio);

    public void onPlaylistChanged(Player player, Playlist playlist);
  }
}
