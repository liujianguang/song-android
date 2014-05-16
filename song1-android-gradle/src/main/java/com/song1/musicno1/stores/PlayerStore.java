package com.song1.musicno1.stores;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.CurrentPlayerOccupiedEvent;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Playlist;
import de.akquinet.android.androlog.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by windless on 14-5-14.
 */
public enum PlayerStore implements Player.Callback {
  INSTANCE;

  private   Player              localPlayer;
  private   Map<String, Player> playerMap;
  protected Player              currentPlayer;

  private PlayerStore() {
    playerMap = Maps.newLinkedHashMap();
  }

  public void setLocalPlayer(Player localPlayer) {
    this.localPlayer = localPlayer;
    localPlayer.setCallback(this);
  }

  public void addPlayer(Player newPlayer) {
    newPlayer.setCallback(this);
    playerMap.put(newPlayer.getId(), newPlayer);
    MainBus.post(new PlayerListChangedEvent());
    Log.d(this, "Add new player: " + newPlayer.getName());
  }

  public void removePlayerById(String id) {
    if (playerMap.containsKey(id)) {
      Player player = playerMap.remove(id);
      player.release();
    }
    MainBus.post(new PlayerListChangedEvent());
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(Player player) {
    if (currentPlayer != player) {
      currentPlayer = player;
      MainBus.post(new CurrentPlayerChangedEvent());
    }
  }

  public List<Player> getPlayerList() {
    List<Player> playerList = Lists.newArrayList();
    playerList.add(localPlayer);
    playerList.addAll(playerMap.values());
    return playerList;
  }

  @Override
  public void onStateChanged(Player player, int state) {
    if (player == currentPlayer) {
      MainBus.post(new PlayerStateChangedEvent());
    }
  }

  @Override
  public void onCompletion(Player player, boolean isError) {
    Playlist playlist = player.getPlaylist();
    if (playlist != null) {
      playlist.autoNext(player.getPlayMode());
      player.playWithAudio(playlist.getCurrentAudio());
    }
  }

  @Override
  public void onPlayingAudioChanged(Player player, Audio audio) {
    if (player == currentPlayer) {
      MainBus.post(new PlayerPlayingAudioChangedEvent());
    }
  }

  @Override
  public void onPlaylistChanged(Player player, Playlist playlist) {
    if (player == currentPlayer) {
      MainBus.post(new PlayerPlaylistChangedEvent());
    }
  }

  @Override
  public void onOccupied(Player player) {
    if (player == currentPlayer) {
      MainBus.post(new CurrentPlayerOccupiedEvent());
    }
  }

  public void clear() {
    playerMap.clear();
    currentPlayer = null;
    localPlayer = null;
  }

  /*
   ********************* events **************************
   */

  public class PlayerListChangedEvent {
  }

  public class PlayerStateChangedEvent {
  }

  public class PlayerPlayingAudioChangedEvent {
  }

  public class PlayerPlaylistChangedEvent {
  }

  public class CurrentPlayerChangedEvent {
  }

  public static class PlayerModeChangedEvent {
  }
}
