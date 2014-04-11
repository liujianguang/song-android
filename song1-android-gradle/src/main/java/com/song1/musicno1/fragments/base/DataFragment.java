package com.song1.musicno1.fragments.base;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.loader.LoadData;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by windless on 14-4-4.
 */
public abstract class DataFragment<T> extends BaseFragment implements LoaderManager.LoaderCallbacks<LoadData<T>> {
  private LoadData<T> loadData = new LoadData<>();

  private DataAdapter<T> adapter;
  private boolean        isLoading;
  private List<T>        currentPageData;

  public T getDataItem(int pos) {
    return adapter.getDataItem(pos);
  }

  public List<T> getDataList() {
    return adapter.getDataList();
  }

  protected DataAdapter<T> getAdapter() {
    return adapter;
  }

  protected int getDataCount() {
    return adapter.getCount();
  }

  public void reload() {
    loadData = new LoadData<>();
    adapter.getDataList().clear();
    getLoaderManager().restartLoader(0, null, this);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter = newAdapter();
    adapter.setDataList(loadData.getDataList());
    if (loadData.getDataList().size() > 0) {
      showContent();
    }

    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public Loader<LoadData<T>> onCreateLoader(int id, Bundle args) {
    return new AsyncTaskLoader<LoadData<T>>(getActivity()) {

      @Override
      protected void onStartLoading() {
        Log.d(this, "Start loading...");

        if (loadData.isNeedLoad()) {
          Log.d(this, "Force load...");
          showLoading();
          forceLoad();
        } else {
          Log.d(this, "Deliver result directly");
          deliverResult(loadData);
        }
      }

      @Override
      public LoadData<T> loadInBackground() {
        Log.d(this, "Load page " + loadData.getLoadPage());
        currentPageData = onLoad(loadData.getLoadPage());
        if (currentPageData != null) {
          return loadData;
        } else {
          return null;
        }
      }
    };
  }

  @Override
  public void onLoadFinished(Loader<LoadData<T>> loader, LoadData<T> data) {
    if (data != null) {
      loadData.setCurrentPage(loadData.getLoadPage());

      if (currentPageData != null) {
        loadData.getDataList().addAll(currentPageData);
        currentPageData = null;
      }

      if (!data.isEmpty()) {
        adapter.setDataList(loadData.getDataList());
        showContent();
      } else {
        showEmpty();
      }
      adapter.notifyDataSetChanged();
    } else {
      showError();
    }
    isLoading = false;
  }

  @Override
  public void onLoaderReset(Loader<LoadData<T>> loader) {
  }

  protected void setTotalPage(int totalPage) {
    loadData.setTotalPage(totalPage);
  }

  protected void retry() {
    isLoading = true;
    getLoaderManager().restartLoader(0, null, this);
  }

  protected void loadMore() {
    loadData.setLoadPage(loadData.getCurrentPage() + 1);
    isLoading = true;
    getLoaderManager().restartLoader(0, null, this);
  }

  protected boolean isLoading() {
    return isLoading;
  }

  protected boolean isDataFull() {
    return loadData.isFull();
  }

  protected boolean isDataEmpty() {
    return loadData.isEmpty();
  }

  protected void showContent() {
  }

  protected void showError() {
  }

  protected void showEmpty() {
  }

  protected void showLoading() {
  }

  protected abstract List<T> onLoad(int loadPage);

  protected abstract DataAdapter<T> newAdapter();
}
