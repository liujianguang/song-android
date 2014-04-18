package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
    List<Audio> audioList;
    if (album != null) {
      audioList = localAudioStore.get_audios_by_album(album);
    } else if (artist != null) {
      audioList = localAudioStore.audios_by_artist(artist);
    }else {
      audioList = localAudioStore.all_audios();
    }
    return audioList;
  }

  @Override
  public void showContent() {
    super.showContent();
    headerView.setVisibility(View.VISIBLE);
    LinearLayout headerLayout = (LinearLayout)headerView;
    headerLayout.addView(LayoutInflater.from(getActivity()).inflate(R.layout.header,null));
  }

  @Override
  protected DataAdapter<Audio> newAdapter() {
    return new AudioAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
    //getLoaderManager().initLoader(0, null, this);

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
