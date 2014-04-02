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
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.LocalArtistAdapter;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.loader.LocalArtistLoader;

import java.util.List;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM4:12
 */
public class LocalArtistFrag extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Artist>>, AdapterView.OnItemClickListener {
  LocalArtistAdapter adapter;
  LocalArtistLoader  loader;

  @InjectView(R.id.artist_list) ListView artist_list;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    has_home_button(false);
    adapter = new LocalArtistAdapter(getActivity());
    loader = new LocalArtistLoader(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    artist_list.setAdapter(adapter);
    artist_list.setOnItemClickListener(this);
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.local_artist_frag, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {
    return loader;
  }

  @Override
  public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> data) {
    adapter.artists(data);
  }

  @Override
  public void onLoaderReset(Loader<List<Artist>> loader) {
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Artist artist = (Artist) artist_list.getItemAtPosition(position);
    MainActivity mainActivity = (MainActivity) getActivity();
    LocalAudioFrag localAudioFrag = new LocalAudioFrag();
    localAudioFrag.setArtist(artist);
    localAudioFrag.setTitle(artist.name);
    mainActivity.push(LocalAudioFrag.class.getName(), localAudioFrag);
  }
}
