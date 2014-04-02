package com.song1.musicno1.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.models.LocalAudioStore;

import java.util.List;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM8:26
 */
public class LocalArtistLoader extends AsyncTaskLoader<List<Artist>> {

  private final LocalAudioStore _store;

  private List<Artist> _artists;

  public LocalArtistLoader(Context context) {
    super(context);
    _store = new LocalAudioStore(context);
  }

  @Override
  protected void onStartLoading() {
    if (_artists == null) {
      forceLoad();
    } else {
      deliverResult(_artists);
    }
  }

  @Override
  public List<Artist> loadInBackground() {
    _artists = _store.all_artists();
    return _artists;
  }
}
