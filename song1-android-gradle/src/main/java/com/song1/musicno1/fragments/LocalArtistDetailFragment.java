package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioAdapter;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.fragments.base.BaseFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;
import com.song1.musicno1.stores.PlayerStore;
import com.squareup.otto.Subscribe;

/**
 * Created by windless on 14-5-27.
 */
public class LocalArtistDetailFragment extends BaseFragment implements AdapterView.OnItemClickListener {
  @InjectView(R.id.list) ListView listView;

  private Artist       artist;
  private AudioAdapter audioAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_only_list, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  public void setArtist(Artist artist) {
    this.artist = artist;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(artist.name);
    audioAdapter = new AudioAdapter(getActivity(), App.get(LocalAudioStore.class));
    audioAdapter.setHasIndex(true);
    audioAdapter.setDataList(artist.audios);
    listView.setAdapter(audioAdapter);

    listView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Audio audio = audioAdapter.getDataItem(i);
    Playlist playlist = new Playlist(List8.newList(audioAdapter.getDataList()), audio);
    Players.setPlaylist(playlist, getFragmentManager());
    audioAdapter.setSelectedAudio(null);
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
    updateCurrentPlayerState(null);
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
  }

  @Subscribe
  public void updateCurrentPlayerState(PlayerStore.CurrentPlayerChangedEvent event) {
    updatePlayingAudio(null);
  }

  @Subscribe
  public void updatePlayingAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      audioAdapter.setPlayingAudio(currentPlayer.getPlayingAudio());
    }
  }
}
