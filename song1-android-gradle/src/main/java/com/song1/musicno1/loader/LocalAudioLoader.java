package com.song1.musicno1.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM8:45
 */
public class LocalAudioLoader extends AsyncTaskLoader<List<Audio>> {
  private final LocalAudioStore _audio_store;
  private       List<Audio>     _audios;
  private       Album           _album;
  private       Artist          _artist;

  public LocalAudioLoader(Context context) {
    super(context);
    _audio_store = new LocalAudioStore(context);
  }

  @Override
  protected void onStartLoading() {
    if (_audios == null) {
      forceLoad();
    } else {
      deliverResult(_audios);
    }
  }

  @Override
  public List<Audio> loadInBackground() {
    if (_album != null) {
      _audios = _audio_store.get_audios_by_album(_album);
    } else if (_artist != null) {
      _audios = _audio_store.audios_by_artist(_artist);
    }
    else {
    _audios = _audio_store.all_audios();
    }
    return _audios;
  }

  public void set_album(Album album) {
    _album = album;
  }

  public void set_artist(Artist artist) {
    _artist = artist;
  }
}
