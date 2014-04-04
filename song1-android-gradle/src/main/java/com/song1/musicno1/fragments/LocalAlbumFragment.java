package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.AlbumAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.loader.LocalAlbumLoader;

import javax.inject.Inject;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM4:41
 */
public class LocalAlbumFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Album>>, AdapterView.OnItemClickListener {
  LocalAlbumLoader loader;
  AlbumAdapter     adapter;


  @InjectView(R.id.album_gridlist) GridView gridView;

  @Inject
  public LocalAlbumFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loader = new LocalAlbumLoader(getActivity());
    adapter = new AlbumAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    gridView.setAdapter(adapter);
    gridView.setOnItemClickListener(this);

    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.local_albums_frag, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public Loader<List<Album>> onCreateLoader(int id, Bundle args) {
    return loader;
  }

  @Override
  public void onLoadFinished(Loader<List<Album>> loader, List<Album> data) {
    adapter.setDataList(data);
  }

  @Override
  public void onLoaderReset(Loader<List<Album>> loader) {
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Album album = (Album) gridView.getItemAtPosition(position);
    MainActivity activity = (MainActivity) getActivity();
    LocalAudioFragment localAudioFragment = App.get(LocalAudioFragment.class);
    localAudioFragment.setAlbum(album);
    localAudioFragment.setTitle(album.title);
    activity.push(LocalAudioFragment.class.getName(), localAudioFragment);
  }
}
