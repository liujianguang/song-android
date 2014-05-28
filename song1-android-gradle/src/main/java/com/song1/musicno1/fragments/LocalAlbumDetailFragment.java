package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.base.Strings;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioAdapter;
import com.song1.musicno1.entity.Album;
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
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by windless on 14-5-27.
 */
public class LocalAlbumDetailFragment extends BaseFragment implements AdapterView.OnItemClickListener {
  @InjectView(R.id.list) ListView     listView;
  protected              Album        album;
  protected              AudioAdapter audioAdapter;
  private                ImageView    imageView;
  private                TextView     numberView;

  public void setAlbum(Album album) {
    this.album = album;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_only_list, container, false);
    ButterKnife.inject(this, view);
    View headerView = View.inflate(getActivity(), R.layout.header_album, null);
    imageView = (ImageView) headerView.findViewById(R.id.album_art);
    numberView = (TextView) headerView.findViewById(R.id.audio_number);
    listView.addHeaderView(headerView);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(album.title);
    audioAdapter = new AudioAdapter(getActivity(), App.get(LocalAudioStore.class));
    audioAdapter.setHasIndex(true);
    audioAdapter.setDataList(album.getAudios());
    listView.setAdapter(audioAdapter);
    numberView.setText(getString(R.string.audio_total, album.audios.size()));

    if (!Strings.isNullOrEmpty(album.album_art)) {
      Picasso.with(getActivity()).load(new File(album.album_art)).placeholder(R.drawable.album_art_default_big).into(imageView);
    } else {
      imageView.setImageResource(R.drawable.album_art_default_big);
    }

    listView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    if (i > 0) {
      Audio audio = audioAdapter.getDataItem(i - 1); // 为什么 header view 要影响 position????
      Playlist playlist = new Playlist(List8.newList(audioAdapter.getDataList()), audio);
      Players.setPlaylist(playlist, getFragmentManager());
      audioAdapter.setSelectedAudio(null);
    }
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
