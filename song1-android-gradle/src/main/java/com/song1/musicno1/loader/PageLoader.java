package com.song1.musicno1.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.song1.musicno1.entity.LoadResult;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM3:18
 */
public class PageLoader<T> extends AsyncTaskLoader<LoadResult<T>> {
  protected LoadResult<T> result;

  public PageLoader(Context context) {
    super(context);
  }

  @Override
  protected void onStartLoading() {
    if (result == null || result.isFailed()) {
      forceLoad();
    } else {
      deliverResult(result);
    }
  }

  @Override
  public LoadResult<T> loadInBackground() {
    return null;
  }
}
