package com.song1.musicno1.loader;

import android.content.Context;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.entity.Category;
import com.song1.musicno1.entity.Chart;
import com.song1.musicno1.models.BeetleException;
import com.song1.musicno1.models.cmmusic.CMException;
import com.song1.musicno1.models.cmmusic.CMMusicStore;
import com.song1.musicno1.models.play.Audio;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * User: windless
 * Date: 13-9-9
 * Time: PM3:13
 */
public class MiguMusicLoaders {
  private final CMMusicStore store;
  private final Context      context;

  @Inject
  public MiguMusicLoaders(Context context, CMMusicStore store) {
    this.context = context;
    this.store = store;
  }

  public XMLoader artist_categories(final Category category) {
    XMLoader loader = new XMLoader(context);

    loader.call(new Callable<Object>() {
      @Override
      public Object call() {
        try {
          if (category == null) {
            return store.root_artist_categories();
          } else {
            return store.artist_categories(category.id, 1);
          }
        } catch (BeetleException e) {
          return null;
        }
      }
    });

    return loader;
  }

  public XMLoader charts() {
    XMLoader loader = new XMLoader(context);
    loader.call(new Callable<Object>() {
      @Override
      public Object call() {
        try {
          return store.charts(1, 20);
        } catch (CMException e) {
          return null;
        }
      }
    });
    return loader;
  }

  public XMLoader artists(final Category category, final int page) {
    XMLoader loader = new XMLoader(context);
    loader.call(new Callable<Object>() {
      @Override
      public Object call() {
        try {
          return store.artists_by_category(category.id, page);
        } catch (BeetleException e) {
          return null;
        }
      }
    });
    return loader;
  }

  public XMLoader audios(final Artist artist, final int page) {
    XMLoader loader = new XMLoader(context);
    loader.call(new Callable<Object>() {
      @Override
      public Object call() {
        try {
          return store.audios_by_artist(artist.id, page, 20);
        } catch (CMException e) {
          if (e.code() == 300002) {
            return new ArrayList<Audio>();
          }
          return null;
        }
      }
    });
    return loader;
  }

  public XMLoader albums_by(final Artist artist, final int page) {
    XMLoader loader = new XMLoader(context);
    loader.call(new Callable<Object>() {
      @Override
      public Object call() {
        try {
          return store.albums_by_artist(artist.id, page, 20);
        } catch (CMException e) {
          if (e.code() == 300002) {
            return new ArrayList<Album>();
          }
          return null;
        }
      }
    });
    return loader;
  }

  public XMLoader audios(final Album album, final int page) {
    XMLoader loader = new XMLoader(context);
    loader.call(new Callable<Object>() {
      @Override
      public Object call() {
        try {
          return store.audios_by_album(album.id, page, 20);
        } catch (CMException e) {
          if (e.code() == 300002) {
            return new ArrayList<Audio>();
          }
          return null;
        }
      }
    });
    return loader;
  }

  public XMLoader search(final String search, final int page) {
    XMLoader loader = new XMLoader(context);
    loader.call(new Callable<Object>() {
      @Override
      public Object call() {
        try {
          return store.search(search, page, 20);
        } catch (CMException e) {
          if (e.code() == 300002) return new ArrayList<Audio>();
          return null;
        }
      }
    });
    return loader;
  }

  public XMLoader audios(final Chart chart, final int page) {
    final XMLoader loader = new XMLoader(context);
    loader.call(new Callable<Object>() {
      @Override
      public Object call() {
        try {
          return store.audios_by_chart(chart.id, page, 20);
        } catch (CMException e) {
          if (e.code() == 300002) return new ArrayList<Audio>();
          return null;
        }
      }
    });
    return loader;
  }
}
