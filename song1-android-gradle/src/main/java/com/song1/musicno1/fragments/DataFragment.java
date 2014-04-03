package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;

/**
 * Created by windless on 14-4-3.
 */
public class DataFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
  @InjectView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
  @InjectView(R.id.list)           ListView           listView;

  Handler handler = new Handler();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_data, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    refreshLayout.setOnRefreshListener(this);
    refreshLayout.setColorScheme(android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light);

    listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, Lists.newArrayList("1", "2", "3")));
  }

  @Override
  public void onRefresh() {
    refreshLayout.setEnabled(false);
    handler.postDelayed(() -> {
      refreshLayout.setRefreshing(false);
      refreshLayout.setEnabled(true);
    }, 2000);
  }
}
