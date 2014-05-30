package com.song1.musicno1.fragments;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.PlaylistItemAdapter;
import com.song1.musicno1.helpers.AlbumArtHelper;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.WifiModel;
import com.song1.musicno1.models.play.*;
import com.song1.musicno1.stores.PlayerStore;
import com.song1.musicno1.ui.IocTextView;
import com.song1.musicno1.util.DeviceUtil;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by windless on 3/28/14.
 */

public class PlayingFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, WifiModel.ScanListener, ViewTreeObserver.OnGlobalLayoutListener, AdapterView.OnItemClickListener {
  protected int   state;
  protected Audio currentAudio;

  @InjectView(R.id.list)             ListView    listView;
  @InjectView(R.id.volume_bar)       SeekBar     volumeBar;
  @InjectView(R.id.player_list)      ImageButton playerListBtn;
  @InjectView(R.id.deviceNumView)    TextView    deviceNumView;
  @InjectView(R.id.previous)         ImageButton prevButton;
  @InjectView(R.id.play)             ImageButton playButton;
  @InjectView(R.id.next)             ImageButton nextButton;
  @InjectView(R.id.collectionButton) Button      favoriteButton;
  @InjectView(R.id.position_seeker)  SeekBar     positionSeeker;
  @InjectView(R.id.position)         TextView    positionView;
  @InjectView(R.id.duration)         TextView    durationView;
  @InjectView(R.id.play_mode)        ImageButton playModeBtn;

  ImageView albumArtView;

  int newDeviceCount = 0;

  WifiModel wifiModel;
  protected boolean             isDeviceFragmentShow;
  protected PlaylistItemAdapter playlistItemAdapter;
  private   Playlist            playlist;
  protected LocalAudioStore     localAudioStore;

  private Handler handler = new Handler();

  private Runnable positionRunnable = new Runnable() {
    @Override
    public void run() {
      updatePosition();
      handler.postDelayed(this, 1000);
    }
  };

  private void updatePosition() {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      positionSeeker.setMax(currentPlayer.getDuration());
      positionSeeker.setProgress(currentPlayer.getPosition());
      durationView.setText(TimeHelper.milli2str(currentPlayer.getDuration()));
    } else {
      positionSeeker.setProgress(0);
      durationView.setText(TimeHelper.milli2str(0));
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_playing, container, false);
    ButterKnife.inject(this, view);
    View headerView = View.inflate(getActivity(), R.layout.header_playlist, null);
    albumArtView = (ImageView) headerView.findViewById(R.id.image);
    listView.addHeaderView(headerView);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    localAudioStore = App.get(LocalAudioStore.class);

    positionSeeker.setEnabled(false);
    positionSeeker.setOnSeekBarChangeListener(this);
    volumeBar.setOnSeekBarChangeListener(this);

    playlistItemAdapter = new PlaylistItemAdapter(getActivity());
    listView.setOnItemClickListener(this);
    listView.setAdapter(playlistItemAdapter);

    wifiModel = new WifiModel(getActivity());
    wifiModel.setScanListener(this);
    wifiModel.scan();

    ViewTreeObserver viewTreeObserver = listView.getViewTreeObserver();
    if (viewTreeObserver != null) {
      viewTreeObserver.addOnGlobalLayoutListener(this);
    }
  }

  public void updateVolume() {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      Volume volume = currentPlayer.getVolume();
      volumeBar.setMax(volume.getMax());
      volumeBar.setProgress(volume.getCurrent());
    }
  }

  @Subscribe
  public void updatePlayerInfo(PlayerStore.CurrentPlayerChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer == null) {
      prevButton.setEnabled(false);
      playButton.setEnabled(false);
      nextButton.setEnabled(false);
      volumeBar.setEnabled(false);
      favoriteButton.setEnabled(false);
      playModeBtn.setVisibility(View.INVISIBLE);

      playerListBtn.setImageResource(R.drawable.ic_playerlist);
    } else {
      prevButton.setEnabled(true);
      playButton.setEnabled(true);
      nextButton.setEnabled(true);
      volumeBar.setEnabled(true);
      playModeBtn.setVisibility(View.VISIBLE);

      favoriteButton.setEnabled(true);

      if (currentPlayer instanceof LocalPlayer) {
        playerListBtn.setImageResource(R.drawable.ic_playerlist);
      } else {
        playerListBtn.setImageResource(R.drawable.ic_playerlist_selected);
      }

      updateVolume();
      updatePlayerState(null);
      updatePosition();
      updatePlaylist(null);
      updatePlayingAudio(null);
      updatePlayMode(null);
    }
  }

  @Subscribe
  public void updatePlaylist(PlayerStore.PlayerPlaylistChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      playlist = currentPlayer.getPlaylist();
      if (playlist != null) {
        playlistItemAdapter.setDataList(playlist.getAudios());
      } else {
        playlistItemAdapter.setDataList(null);
        albumArtView.setImageResource(R.drawable.album_art_default_big);
      }
      playlistItemAdapter.notifyDataSetChanged();
    }
  }

  @OnClick(R.id.play_mode)
  public void onPlayModeClick() {
    Players.nextPlayMode();
    updatePlayMode(null);
  }

  @Subscribe
  public void updatePlayMode(PlayerStore.PlayerModeChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      playModeBtn.setVisibility(View.VISIBLE);

      int playMode = currentPlayer.getPlayMode();
      switch (playMode) {
        case Player.PlayMode.NORMAL:
          playModeBtn.setImageResource(R.drawable.ic_play_mode_normal);
          break;
        case Player.PlayMode.REPEAT_ALL:
          playModeBtn.setImageResource(R.drawable.ic_play_mode_repeat_all);
          break;
        case Player.PlayMode.REPEAT_ONE:
          playModeBtn.setImageResource(R.drawable.ic_play_mode_repeat_one);
          break;
        case Player.PlayMode.SHUFFLE:
          playModeBtn.setImageResource(R.drawable.ic_play_mode_random);
      }
    }
  }

  @Subscribe
  public void updatePlayingAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();

    if (currentPlayer == null) return;

    currentAudio = currentPlayer.getPlayingAudio();
    playlistItemAdapter.setPlayingAudio(currentAudio);
    if (currentAudio == null) {
      favoriteButton.setEnabled(false);
      favoriteButton.setText(R.string.collection);
      albumArtView.setImageResource(R.drawable.album_art_default_big);
    } else {
      favoriteButton.setEnabled(currentAudio.canFavorite());
      if (currentAudio.canFavorite()) {
        if (FavoriteAudio.isFavorite(currentAudio)) {
          favoriteButton.setText(R.string.cancel_collection);
        } else {
          favoriteButton.setText(R.string.collection);
        }
      } else {
        favoriteButton.setText(R.string.collection);
      }

      AlbumArtHelper.loadAlbumArt(
          getActivity(),
          currentAudio.getAlbumArt(localAudioStore),
          albumArtView,
          R.drawable.album_art_default_big
      );
    }
  }

  @Subscribe
  public void updatePlayerState(PlayerStore.PlayerStateChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();

    if (currentPlayer == null) return;

    state = currentPlayer.getState();
    switch (state) {
      case Player.State.PAUSED:
      case Player.State.STOPPED:
        playButton.setImageResource(R.drawable.ic_play_large);
        setPlayButtonsEnabled(true);
        break;
      case Player.State.PLAYING:
        playButton.setImageResource(R.drawable.ic_pause_large);
        setPlayButtonsEnabled(true);
        break;
      case Player.State.PREPARING:
        setPlayButtonsEnabled(false);
    }

    if (state == Player.State.PLAYING) {
      handler.post(positionRunnable);
    } else {
      handler.removeCallbacks(positionRunnable);
    }

    switch (state) {
      case Player.State.PLAYING:
      case Player.State.PAUSED:
        positionSeeker.setEnabled(true);
        break;
      default:
        positionSeeker.setEnabled(false);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
    //audioActionsFragment.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
    //audioActionsFragment.onResume();
    updatePlayerInfo(null);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    wifiModel.stop();
  }

  private void setPlayButtonsEnabled(boolean enabled) {
    playButton.setEnabled(enabled);
    prevButton.setEnabled(enabled);
    nextButton.setEnabled(enabled);
  }

  @OnClick(R.id.play)
  public void onPlayClick() {
    switch (state) {
      case Player.State.PLAYING:
        Players.pause();
        break;
      case Player.State.PAUSED:
        Players.resume();
        break;
      case Player.State.STOPPED:
        Players.play();
    }
  }

  @OnClick(R.id.player_list)
  public void onPlayerListClick() {
    if (isDeviceFragmentShow) return;

    isDeviceFragmentShow = true;
    DeviceFragment deviceFragment = new DeviceFragment();
    deviceFragment.onClose(() -> isDeviceFragmentShow = false);
    deviceFragment.show(getFragmentManager(), "deviceFragment");
  }

  @OnClick(R.id.next)
  public void playNext() {
    Players.next();
  }

  @OnClick(R.id.previous)
  public void playPrevious() {
    Players.previous();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
    if (seekBar.getId() == R.id.position_seeker) {
      positionView.setText(TimeHelper.milli2str(i));
    } else {
      if (fromUser) {
        Players.setVolume(seekBar.getProgress(), false);
      }
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    if (seekBar.getId() == R.id.position_seeker) {
      Players.seek(seekBar.getProgress());
    }
  }

  @OnClick(R.id.collectionButton)
  public void favorite() {
    if (currentAudio == null) return;

    if (FavoriteAudio.toggleRedHeart(currentAudio)) {
      favoriteButton.setText(R.string.cancel_collection);
      Toast.makeText(getActivity(), R.string.added_to_red_heart, Toast.LENGTH_SHORT).show();
    } else {
      favoriteButton.setText(R.string.collection);
      Toast.makeText(getActivity(), R.string.removed_frome_red_heart, Toast.LENGTH_SHORT).show();
    }

    Fragment mainFragment = getFragmentManager().findFragmentById(R.id.main);
    if (mainFragment instanceof FavoriteAudioFragment) {
      FavoriteAudioFragment heartFragment = (FavoriteAudioFragment) mainFragment;
      heartFragment.reload();
    }
  }

  @Override
  public void scanResult(List<ScanResult> scanResults) {
    List<String> ssidList = DeviceUtil.filterScanResultList(scanResults);
    newDeviceCount = ssidList.size();
    if (newDeviceCount != 0) {
      //deviceNumView.setText(newDeviceCount + "");
      deviceNumView.setVisibility(View.VISIBLE);
    } else {
      deviceNumView.setVisibility(View.GONE);
    }
  }

  @Override
  public void onGlobalLayout() {
    albumArtView.setMinimumHeight(listView.getHeight());
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    if (i > 0) {
      Audio audio = playlistItemAdapter.getDataItem(i - 1);
      Players.playWithAudio(audio);
    }
  }
}
