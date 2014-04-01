package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.LoadResult;
import com.song1.musicno1.loader.PageLoader;

import java.util.List;

/**
/**
 * User: windless
 * Date: 13-12-4
 * Time: PM3:03
 */
public abstract class PageLoadFragment<T> extends BaseFragment implements LoaderManager.LoaderCallbacks<LoadResult<T>> {
  private View contentView;
  private View errorView;
  private View loadingView;
  private View emptyView;

  private boolean isLoading   = false;
  private int     currentPage = 0;
  private List<T> dataList    = Lists.newArrayList();

  private int totalPage;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getLoaderManager().initLoader(0, null, this);
  }

  public boolean isLoading() {
    return isLoading;
  }

  public void loadMore() {
    getLoaderManager().restartLoader(0, null, this);
  }

  public boolean hasNextPage() {
    return currentPage < totalPage;
  }

  @Override
  public Loader<LoadResult<T>> onCreateLoader(int id, Bundle args) {
    isLoading = true;
    if (currentPage == 0) {
      showLoading();
    }
    return new PageLoader<T>(getActivity()) {
      @Override
      public LoadResult<T> loadInBackground() {
        this.result = getLoadResult(currentPage + 1);
        return this.result;
      }
    };
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_page_load, container, false);
    contentView = getContentView(inflater, view);
    view.addView(contentView);

    errorView = view.findViewById(R.id.error);
    loadingView = view.findViewById(R.id.loading);
    emptyView = view.findViewById(R.id.empty);

    Button retryBtn = (Button) view.findViewById(R.id.retry);
    retryBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getLoaderManager().restartLoader(0, null, PageLoadFragment.this);
      }
    });

    return view;
  }

  protected abstract View getContentView(LayoutInflater inflater, ViewGroup viewGroup);

  protected abstract LoadResult<T> getLoadResult(int page);

  @Override
  public void onLoadFinished(Loader<LoadResult<T>> loader, LoadResult<T> data) {
    isLoading = false;
    if (!data.isFailed()) {
      if (data.getDataList().size() == 0) {
        if (currentPage == 0) {
          showEmpty();
          currentPage++;
        }
      } else {
        showContent();
        if (currentPage != data.getLoadPage()) {
          dataList.addAll(data.getDataList());
          currentPage++;
        }
        didLoadFinish(dataList);
      }
      totalPage = data.getTotalPage();
    } else {
      if (currentPage == 0) {
        showError();
      }
      didLoadFailed(data);
    }
  }

  protected abstract void didLoadFailed(LoadResult<T> data);

  protected abstract void didLoadFinish(List<T> dataList);

  @Override
  public void onLoaderReset(Loader<LoadResult<T>> loader) {
  }

  public void showContent() {
    contentView.setVisibility(View.VISIBLE);
    errorView.setVisibility(View.GONE);
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
  }

  public void showError() {
    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.VISIBLE);
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
  }

  public void showLoading() {
    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    loadingView.setVisibility(View.VISIBLE);
    emptyView.setVisibility(View.GONE);
  }

  public void showEmpty() {
    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.VISIBLE);
  }
}
