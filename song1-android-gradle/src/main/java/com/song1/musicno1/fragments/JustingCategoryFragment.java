package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.adapter.JustingCategoryAdapter;
import com.song1.musicno1.entity.JsonStore;
import com.song1.musicno1.entity.JustingCategory;
import com.song1.musicno1.fragments.base.ListFragment;

import java.util.List;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM2:30
 */
public class JustingCategoryFragment extends ListFragment<JustingCategory> implements AdapterView.OnItemClickListener {
  private static final String JUSTING_URL = "http://justing.oss-cn-hangzhou.aliyuncs.com/Json/justing2.json";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getString(R.string.justing));
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
  }

  @Override
  protected List<JustingCategory> onLoad(int loadPage) {
    setTotalPage(1);
    JsonStore jsonStore = new JsonStore();
    JustingCategory[] listData = jsonStore.getList(JUSTING_URL, JustingCategory[].class);
    if (listData != null) {
      return Lists.newArrayList(listData);
    }
    return null;
  }

  @Override
  protected DataAdapter<JustingCategory> newAdapter() {
    return new JustingCategoryAdapter(getActivity());
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    JustingCategory category = getDataItem(position);
    JustingPodCastFragment fragment = new JustingPodCastFragment();
    fragment.setPodCasts(category.podcasts);
    fragment.setTitle(category.title);
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.push(JustingPodCastFragment.class.getName(), fragment);
  }
}
