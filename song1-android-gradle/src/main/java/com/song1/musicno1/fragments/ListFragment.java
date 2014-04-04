package com.song1.musicno1.fragments;

import android.os.Bundle;
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

/**
 * Created by windless on 14-4-3.
 */
public abstract class ListFragment<T> extends DataFragment<T> implements AbsListView.OnScrollListener {
  @InjectView(R.id.loading) View     loadingView;
  @InjectView(R.id.empty)   View     emptyView;
  @InjectView(R.id.list)    ListView listView;
  @InjectView(R.id.retry)   View     retryView;
  private                   Button   footerRetryBtn;
  private                   View     footerLoadingView;
  private                   View     footerView;

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

  public ListView getListView() {
    return listView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setOnScrollListener(this);
    listView.setAdapter(getAdapter());
  }

  @Override
  public void showEmpty() {
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.VISIBLE);
    listView.setVisibility(View.GONE);
    retryView.setVisibility(View.GONE);
  }

  @Override
  public void showList() {
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
    listView.setVisibility(View.VISIBLE);
    retryView.setVisibility(View.GONE);

    if (isDataFull()) {
      listView.removeFooterView(footerView);
    }
  }

  @Override
  public void showError() {
    if (isDataEmpty()) {
      listView.setVisibility(View.GONE);
      retryView.setVisibility(View.VISIBLE);
      loadingView.setVisibility(View.GONE);
      emptyView.setVisibility(View.GONE);
    } else {
      footerRetryBtn.setVisibility(View.VISIBLE);
      footerLoadingView.setVisibility(View.GONE);
    }
  }

  @Override
  public void showLoading() {
    if (isDataEmpty()) {
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
  public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE) {
      if (listView.getLastVisiblePosition() + 1 >= listView.getCount()
          && !isLoading()
          && !isDataFull()) {
        loadMore();
      }
    }
  }

  @Override
  public void onScroll(AbsListView absListView, int i, int i2, int i3) {

  }

  @OnClick(R.id.retry_btn)
  public void onRetryClick() {
    retry();
  }
}
