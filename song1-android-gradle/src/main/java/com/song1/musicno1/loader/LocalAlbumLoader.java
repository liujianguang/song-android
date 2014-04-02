package com.song1.musicno1.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.models.LocalAudioStore;

import java.util.List;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM5:06
 */
public class LocalAlbumLoader extends AsyncTaskLoader<List<Album>> {

  private final LocalAudioStore _store;

  private List<Album> _albums;

  public LocalAlbumLoader(Context context) {
    super(context);
    _store = new LocalAudioStore(context);
  }

  @Override
  protected void onStartLoading() {
    if (_albums == null) {
      forceLoad();
    } else {
      deliverResult(_albums);
    }
  }

  @Override
  public List<Album> loadInBackground() {
    _albums = _store.all_albums();
    return _albums;
  }
}
