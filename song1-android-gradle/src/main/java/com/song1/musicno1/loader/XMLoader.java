package com.song1.musicno1.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import javax.inject.Inject;
import java.util.concurrent.Callable;

/**
 * User: windless
 * Date: 13-9-9
 * Time: PM2:59
 */
public class XMLoader extends AsyncTaskLoader<Object> {

  private Callable<Object> callable;
  private Object           data;

  @Inject
  public XMLoader(Context context) {
    super(context);
  }

  @Override
  protected void onStartLoading() {
    if (data == null) {
      forceLoad();
    } else {
      deliverResult(data);
    }
  }

  public void call(Callable<Object> callable) {
    this.callable = callable;
  }

  @Override
  public Object loadInBackground() {
    try {
      data = callable.call();
    } catch (Exception e) {
      return null;
    }

    return data;

  }
}
