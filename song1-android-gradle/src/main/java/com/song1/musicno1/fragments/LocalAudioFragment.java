package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.google.common.collect.Lists;
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
import java.util.Map;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM3:07
 */
public class LocalAudioFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener {
  @Inject LocalAudioStore localAudioStore;
  private Album           album;
  private Artist          artist;

  @Inject
  public LocalAudioFragment() {
  }

  @Override
  protected List<Audio> onLoad(int loadPage) {
    setTotalPage(1);
    if (album != null) {
      return localAudioStore.get_audios_by_album(album);
    } else if (artist != null) {
      return localAudioStore.audios_by_artist(artist);
    }
    Map<Character,List<Audio>> audioMap = localAudioStore.getAudios();
    System.out.println("***********************" + Lists.newArrayList(audioMap.keySet()));
    return localAudioStore.all_audios();
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
    Audio audio = getDataItem(position);
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
