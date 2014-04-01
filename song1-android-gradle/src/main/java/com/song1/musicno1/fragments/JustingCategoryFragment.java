package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.JustingCategoryAdapter;
import com.song1.musicno1.entity.JsonStore;
import com.song1.musicno1.entity.JustingCategory;
import com.song1.musicno1.entity.LoadResult;

import java.util.List;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM2:30
 */
public class JustingCategoryFragment extends PageLoadFragment<JustingCategory> implements AdapterView.OnItemClickListener {
  private static final String JUSTING_URL = "http://justing.oss-cn-hangzhou.aliyuncs.com/Json/justing2.json";
  @InjectView(R.id.list) ListView listView;

  private JustingCategoryAdapter adapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getString(R.string.justing));
    adapter = new JustingCategoryAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  @Override
  protected View getContentView(LayoutInflater inflater, ViewGroup viewGroup) {
    View view = inflater.inflate(R.layout.fragment_only_list, viewGroup, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  protected LoadResult<JustingCategory> getLoadResult(int page) {
    LoadResult<JustingCategory> result = new LoadResult<JustingCategory>();
    JsonStore jsonStore = new JsonStore();
    JustingCategory[] listData = jsonStore.getList(JUSTING_URL, JustingCategory[].class);
    if (listData == null) {
      result.fail();
    } else {
      result.setDataList(Lists.newArrayList(listData));
      result.setTotalPage(1);
      result.setTotalCount(listData.length);
      result.setLoadPage(page);
    }
    return result;
//    return null;
  }

  @Override
  protected void didLoadFailed(LoadResult<JustingCategory> data) {
  }

  @Override
  protected void didLoadFinish(List<JustingCategory> dataList) {
    adapter.setDataList(dataList);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    JustingCategory category = adapter.getDataItem(position);
    JustingPodCastFragment fragment = new JustingPodCastFragment();
    fragment.setPodCasts(category.podcasts);
    fragment.setTitle(category.title);
    fragment.setParent(this);
//    show(R.string.justing, fragment);
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.show(fragment);
  }
}
