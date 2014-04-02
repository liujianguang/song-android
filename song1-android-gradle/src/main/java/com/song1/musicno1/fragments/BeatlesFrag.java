package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.BeatlesModelAdapter;
import com.song1.musicno1.entity.BeatlesModel;
import com.song1.musicno1.entity.JsonStore;
import com.song1.musicno1.entity.LoadResult;

import java.util.List;

/**
 * User: windless
 * Date: 13-9-5
 * Time: AM10:45
 */
public class BeatlesFrag extends PageLoadFragment<BeatlesModel> implements AdapterView.OnItemClickListener {
  private static final String BEATLES_URL = "http://glmusic.oss-cn-hangzhou.aliyuncs.com/jkc/%E7%94%B2%E5%A3%B3%E8%99%AB%E5%9B%BE%E7%89%87/Json/beatles.json";

  BeatlesModelAdapter adapter;

  @InjectView(R.id.album_gridlist) GridView gridView;


  @Override
  protected View getContentView(LayoutInflater inflater, ViewGroup viewGroup) {
    View view = inflater.inflate(R.layout.migu_album_list, viewGroup, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  protected LoadResult<BeatlesModel> getLoadResult(int page) {
    LoadResult<BeatlesModel> result = new LoadResult<BeatlesModel>();
    JsonStore jsonStore = new JsonStore();
    BeatlesModel[] beatles = jsonStore.getList(BEATLES_URL, BeatlesModel[].class);
    if (beatles == null) {
      result.fail();
    } else {
      result.setDataList(Lists.newArrayList(beatles));
      result.setLoadPage(1);
      result.setTotalCount(beatles.length);
      result.setTotalPage(1);
    }

    return result;
  }

  @Override
  protected void didLoadFailed(LoadResult<BeatlesModel> data) {

  }

  @Override
  protected void didLoadFinish(List<BeatlesModel> dataList) {
    adapter.setDataList(dataList);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getString(R.string.beatles_music));
    adapter = new BeatlesModelAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    gridView.setAdapter(adapter);
    gridView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    BeatlesModel model = adapter.getDataItem(position);
    BeatlesDetailFragment fragment = new BeatlesDetailFragment();
    fragment.setTitle(model.title);
    fragment.setAudios(model.audios);
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.push(BeatlesDetailFragment.class.getName(), fragment);
  }
}
