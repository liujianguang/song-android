package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioWithIndexAdapter;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import javax.inject.Inject;
import java.util.List;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM3:07
 */
public class LocalAudioFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener {
  @Inject LocalAudioStore localAudioStore;

  private Album    album;
  private Artist   artist;
  private TextView audioTotalTextView;

  AudioWithIndexAdapter audioAdapter;
  int audioTotal = 0;

  @Inject
  public LocalAudioFragment() {
  }

  @Override
  protected List<Audio> onLoad(int loadPage) {
    setTotalPage(1);

    List<Audio> audioList;
    if (album != null) {
      audioList = album.getAudios();
      audioTotal = audioList.size();
    } else if (artist != null) {
      audioList = artist.audios;
      audioTotal = audioList.size();
    } else {
      audioList = localAudioStore.getAudiosWithIndex();
      audioTotal = localAudioStore.audios_count();
    }
    return audioList;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    View headerView = View.inflate(getActivity(), R.layout.item_local_header, null);
    audioTotalTextView = (TextView) headerView.findViewById(R.id.audioTotal);
    getListView().addHeaderView(headerView);
    return view;
  }

  @Override
  public void showContent() {
    super.showContent();
    audioTotalTextView.setText(getString(R.string.allAudios, audioTotal));
  }


  @Override
  protected DataAdapter<Audio> newAdapter() {
    audioAdapter = new AudioWithIndexAdapter(getActivity(), App.get(LocalAudioStore.class));
    return audioAdapter;
  }

  public void refreshData() {
    audioAdapter.notifyDataSetChanged();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);

    MainBus.register(audioAdapter);
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MainBus.unregister(audioAdapter);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (position == 0) {
      List<Audio> dataList = getDataList();
      if (dataList.size() > 0) {
        Players.randomPlay(new Playlist(List8.newList(dataList), dataList.get(0)), getFragmentManager());
      }
    } else {
      Audio audio = getDataItem(position - 1); // 为什么 header view 要影响 position????
      Playlist playlist = new Playlist(List8.newList(getDataList()), audio);
      Players.setPlaylist(playlist, getFragmentManager());
    }
  }

  public void setAlbum(Album album) {
    this.album = album;
  }

  public void setArtist(Artist artist) {
    this.artist = artist;
  }
}
