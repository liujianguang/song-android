package com.song1.musicno1.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by windless on 14-4-4.
 */
public class BaseLoader<T> extends AsyncTaskLoader<LoadData<T>> {
//  protected final LoadData<T> loadData;

  public BaseLoader(Context context) {
    super(context);
  }

  @Override
  protected void onStartLoading() {
//    if (loadData.isNeedLoad()) {
//      forceLoad();
//    } else {
//      deliverResult(loadData);
//    }
  }

  @Override
  public LoadData<T> loadInBackground() {
    return null;
  }
}
