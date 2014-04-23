package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioAdapter;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM3:07
 */
public class LocalAudioFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener {
  @Inject LocalAudioStore localAudioStore;
  private Album           album;
  private Artist          artist;
  private TextView        audioTotalTextView;

  int audioTotal = 0;

  @Inject
  public LocalAudioFragment() {
  }

  @Override
  protected List<Audio> onLoad(int loadPage) {
    setTotalPage(1);

    List<Audio> audioList;
    if (album != null) {
      audioList = localAudioStore.get_audios_by_album(album);
      audioTotal = audioList.size();
    } else if (artist != null) {
      audioList = localAudioStore.audios_by_artist(artist);
      audioTotal = audioList.size();
    } else {
      audioList = localAudioStore.getAudiosWithIndex();
      audioTotal = localAudioStore.audios_count();
//      audioList = Lists.newArrayList();
    }

    return audioList;
  }

  @Override
  public void showContent() {
    super.showContent();
    if (!isDataEmpty() && getListView().getHeaderViewsCount() == 0) {
      View headerView = View.inflate(getActivity(), R.layout.header_local_audio, null);
      headerView.setOnClickListener((view) -> {
        List<Audio> dataList = getDataList();
        Random random = new Random();
        int randomIndex = random.nextInt(dataList.size());
        Players.setPlaylist(new Playlist(List8.newList(dataList), dataList.get(randomIndex)));
      });
      getListView().addHeaderView(headerView);
      audioTotalTextView = (TextView) headerView.findViewById(R.id.audioTotal);
    }

    String str = String.format(getString(R.string.allAudios), audioTotal);
    audioTotalTextView.setText(str);
  }

  @Override
  protected DataAdapter<Audio> newAdapter() {
    return new AudioAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Audio audio = getDataItem(position - 1); // 为什么 header view 要影响 position????
    Playlist playlist = new Playlist(List8.newList(getDataList()), audio);
    Players.setPlaylist(playlist);
  }

  public void setAlbum(Album album) {
    this.album = album;
  }

  public void setArtist(Artist artist) {
    this.artist = artist;
  }
}
