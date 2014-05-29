package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.song1.musicno1.App;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.AlbumAdapter;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.fragments.base.GridFragment;
import com.song1.musicno1.models.LocalAudioStore;

import javax.inject.Inject;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM4:41
 */
public class LocalAlbumFragment extends GridFragment<Album> implements AdapterView.OnItemClickListener {
  LocalAudioStore localAudioStore;

  @Inject
  public LocalAlbumFragment() {
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    localAudioStore = App.get(LocalAudioStore.class);
    getGridView().setOnItemClickListener(this);
  }

  @Override
  protected List<Album> onLoad(int loadPage) {
    return localAudioStore.getAlbums();
  }

  @Override
  protected DataAdapter<Album> newAdapter() {
    return new AlbumAdapter(getActivity());
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Album album = getDataItem(position);
    MainActivity activity = (MainActivity) getActivity();
    LocalAlbumDetailFragment fragment = new LocalAlbumDetailFragment();
    fragment.setAlbum(album);
    activity.push(LocalAudioFragment.class.getName(), fragment);
  }
}
