package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.LocalAudioAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.loader.LocalAudioLoader;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import java.util.List;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM3:07
 */
public class LocalAudioFrag extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Audio>>, AdapterView.OnItemClickListener {
  LocalAudioAdapter adapter;
  LocalAudioLoader  loader;
  Album             album;
  Artist            artist;

  @InjectView(R.id.audio_list) ListView    audio_list;
  protected                    List<Audio> audios;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    if (album != null || artist != null) {
//      has_home_button(true);
//    } else {
//      has_home_button(false);
//    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.local_audios_frag, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
//    player_action.context(getSherlockActivity());
    adapter = new LocalAudioAdapter(getActivity());
    audio_list.setAdapter(adapter);
    audio_list.setOnItemClickListener(this);
    loader = new LocalAudioLoader(getActivity());
    loader.set_album(album);
    loader.set_artist(artist);
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public Loader<List<Audio>> onCreateLoader(int id, Bundle args) {
    return loader;
  }

  @Override
  public void onLoadFinished(Loader<List<Audio>> loader, List<Audio> data) {
    audios = data;
    adapter.setData(data);
  }

  @Override
  public void onLoaderReset(Loader<List<Audio>> loader) {
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Audio audio = (Audio) audio_list.getItemAtPosition(position);
    Playlist playlist = new Playlist(List8.newList(audios), audio);
    Players.setPlaylist(playlist);
  }

  public void setAlbum(Album album) {
    this.album = album;
  }

  public void setArtist(Artist artist) {
    this.artist = artist;
  }
//  public void load(Album album) {
//    this.album = album;
//    loader.album(album);
//  }
//
//  public void artist(Artist artist) {
//    this.artist = artist;
//    loader.artist(artist);
//  }

//  @Override
//  public void add_to_playlist(Audio audio) {
//    player_action.add_to_playlist(audio);
//  }
}
