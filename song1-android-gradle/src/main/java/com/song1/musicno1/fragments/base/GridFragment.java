package com.song1.musicno1.fragments.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;

/**
 * Created by windless on 14-4-4.
 */
public abstract class GridFragment<T> extends DataFragment<T> {
  @InjectView(R.id.loading) View     loadingView;
  @InjectView(R.id.empty)   View     emptyView;
  @InjectView(R.id.grid)    GridView gridView;
  @InjectView(R.id.retry)   View     retryView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_grid, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    gridView.setAdapter(getAdapter());
  }

  protected GridView getGridView() {
    return gridView;
  }

  @Override
  protected void showEmpty() {
    super.showEmpty();
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.VISIBLE);
    gridView.setVisibility(View.GONE);
    retryView.setVisibility(View.GONE);
  }

  @Override
  protected void showError() {
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
    gridView.setVisibility(View.GONE);
    retryView.setVisibility(View.VISIBLE);
  }

  @Override
  protected void showContent() {
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
    gridView.setVisibility(View.VISIBLE);
    retryView.setVisibility(View.GONE);
  }

  @Override
  protected void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
    emptyView.setVisibility(View.GONE);
    gridView.setVisibility(View.GONE);
    retryView.setVisibility(View.GONE);
  }

  @OnClick(R.id.retry_btn)
  public void onRetryClick() {
    retry();
  }
}
