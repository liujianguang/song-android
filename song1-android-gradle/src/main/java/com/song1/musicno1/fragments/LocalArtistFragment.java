package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.song1.musicno1.App;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.adapter.LocalArtistAdapter;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.models.LocalAudioStore;

import javax.inject.Inject;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM4:12
 */
public class LocalArtistFragment extends ListFragment<Artist> implements AdapterView.OnItemClickListener {
  @Inject LocalAudioStore localAudioStore;

  @Inject
  public LocalArtistFragment() {
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
  }

  @Override
  protected List<Artist> onLoad(int loadPage) {
    setTotalPage(1);
    return localAudioStore.all_artists();
  }

  @Override
  protected DataAdapter<Artist> newAdapter() {
    return new LocalArtistAdapter(getActivity());
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Artist artist = getDataItem(position);
    MainActivity mainActivity = (MainActivity) getActivity();
    LocalAudioFragment localAudioFragment = App.get(LocalAudioFragment.class);
    localAudioFragment.setArtist(artist);
    localAudioFragment.setTitle(artist.name);
    mainActivity.push(LocalAudioFragment.class.getName(), localAudioFragment);
  }
}
