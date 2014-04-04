package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.loader.LoadData;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by windless on 14-4-3.
 */
public abstract class DataFragment<T> extends Fragment implements LoaderManager.LoaderCallbacks<LoadData<T>>, AbsListView.OnScrollListener {
  @InjectView(R.id.loading) View     loadingView;
  @InjectView(R.id.empty)   View     emptyView;
  @InjectView(R.id.list)    ListView listView;
  @InjectView(R.id.retry)   View     retryView;
  private                   Button   footerRetryBtn;
  private                   View     footerLoadingView;

  DataAdapter<T> adapter;
  private LoadData<T> loadData = new LoadData<>();
  protected boolean isLoading;
  private   List<T> currentPageData;
  private   View    footerView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_data, container, false);
    ButterKnife.inject(this, view);
    footerView = View.inflate(getActivity(), R.layout.footer_data, null);
    footerLoadingView = footerView.findViewById(R.id.footer_loading);
    footerRetryBtn = (Button) footerView.findViewById(R.id.footer_retry);
    footerRetryBtn.setOnClickListener((btn) -> retry());
    listView.addFooterView(footerView);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    listView.setOnScrollListener(this);

    adapter = newAdapter();
    adapter.setDataList(loadData.getDataList());
    listView.setAdapter(adapter);

    if (loadData.getDataList().size() > 0) {
      showList();
    }
    getLoaderManager().initLoader(0, null, this);
  }

  public void showEmpty() {
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.VISIBLE);
    listView.setVisibility(View.GONE);
    retryView.setVisibility(View.GONE);
  }

  public void showList() {
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
    listView.setVisibility(View.VISIBLE);
    retryView.setVisibility(View.GONE);
  }

  public void showError() {
    if (loadData.getDataList().size() == 0) {
      listView.setVisibility(View.GONE);
      retryView.setVisibility(View.VISIBLE);
      loadingView.setVisibility(View.GONE);
      emptyView.setVisibility(View.GONE);
    } else {
      footerRetryBtn.setVisibility(View.VISIBLE);
      footerLoadingView.setVisibility(View.GONE);
    }
  }

  public void showLoading() {
    if (loadData.getDataList().size() == 0) {
      loadingView.setVisibility(View.VISIBLE);
      emptyView.setVisibility(View.GONE);
      listView.setVisibility(View.GONE);
      retryView.setVisibility(View.GONE);
    } else {
      footerRetryBtn.setVisibility(View.GONE);
      footerLoadingView.setVisibility(View.VISIBLE);
    }
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

  protected void setTotalPage(int totalPage) {
    loadData.setTotalPage(totalPage);
  }

  protected abstract List<T> onLoad(int loadPage);

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
        adapter.notifyDataSetChanged();
        showList();

        if (loadData.isFull()) {
          listView.removeFooterView(footerView);
        }
      } else {
        showEmpty();
      }
    } else {
      showError();
    }
    isLoading = false;
  }

  @Override
  public void onLoaderReset(Loader<LoadData<T>> loader) {

  }

  protected abstract DataAdapter<T> newAdapter();

  @Override
  public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE) {
      if (listView.getLastVisiblePosition() + 1 >= listView.getCount()
          && !isLoading
          && !loadData.isFull()) {
        isLoading = true;
        loadData.setLoadPage(loadData.getCurrentPage() + 1);
        getLoaderManager().restartLoader(0, null, this);
      }
    }
  }

  @Override
  public void onScroll(AbsListView absListView, int i, int i2, int i3) {

  }

  @OnClick(R.id.retry_btn)
  public void retry() {
    isLoading = true;
    getLoaderManager().restartLoader(0, null, this);
  }
}
