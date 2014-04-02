package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.LocalAlbumAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.loader.MiguMusicLoaders;
import com.song1.musicno1.ui.XMListView;

import javax.inject.Inject;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-9
 * Time: PM4:57
 */
public class CloudAlbumsFrag extends Fragment
    implements LoaderManager.LoaderCallbacks<Object>, ListView.OnItemClickListener, XMListView.Listener {
  @Inject XMListView        list_view;
  @Inject MiguMusicLoaders  migu_loaders;
  @Inject LocalAlbumAdapter adapter;

  private Artist artist;

  private int     page        = 1;
  private boolean is_finished = false;
  private boolean is_loading  = false;

  private CloudArtistDetailFrag parent_frag;

  @Inject
  public CloudAlbumsFrag() {
    App.inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.endless_listview, container, false);
    list_view.view(view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    list_view.adapter(adapter);
    list_view.listen_item_click(this);
    list_view.be_listened(this);
    getLoaderManager().initLoader(0, null, this);
  }

  public void load(Artist artist) {
    this.artist = artist;
  }

  @Override
  public Loader<Object> onCreateLoader(int id, Bundle args) {
    is_loading = true;
    if (page == 1) list_view.show_loading();
    return migu_loaders.albums_by(artist, page);
  }

  @Override
  public void onLoadFinished(Loader<Object> loader, Object data) {
    if (!is_loading) {
      list_view.show_content();
      return;
    }

    if (data == null) {
      if (page == 1) list_view.show_error();
    } else {
      List<Album> albums = (List<Album>) data;
      if (albums.size() < 20) {
        is_finished = true;
      }
      adapter.add(albums);
      list_view.show_content();
    }
    is_loading = false;
  }

  @Override
  public void onLoaderReset(Loader<Object> loader) {
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Album album = (Album) list_view.item(position);
    CloudAudiosFrag frag = new CloudAudiosFrag();
    frag.setTitle(album.title);
    frag.load(album);

    MainActivity activity = (MainActivity) getActivity();
    activity.replaceMain(frag);
  }

  @Override
  public void on_load_more() {
    if (!is_loading && !is_finished) {
      page++;
      getLoaderManager().restartLoader(0, null, this);
    }
  }

  @Override
  public void on_reload() {
    getLoaderManager().restartLoader(0, null, this);
  }

  public void parent(CloudArtistDetailFrag frag) {
    parent_frag = frag;
  }
}
