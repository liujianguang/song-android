package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.RankingListAdapter;
import com.song1.musicno1.entity.GetRankingListResp;
import com.song1.musicno1.entity.LoadResult;
import com.song1.musicno1.entity.RankingListInfo;
import com.song1.musicno1.fragments.PageLoadFragment;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;

import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM3:38
 */
public class MiguRankingListFragment extends PageLoadFragment<RankingListInfo> implements AdapterView.OnItemClickListener {
  @InjectView(R.id.ranking_list) ListView listView;

  private RankingListAdapter adapter;

  @Override
  protected LoadResult<RankingListInfo> getLoadResult(int page) {
    LoadResult<RankingListInfo> result = new LoadResult<RankingListInfo>();
    try {
      GetRankingListResp rsp = MiguService.getInstance().fetchRankingList(adapter.getCount(), 20);
      result.setDataList(rsp.listPageObject);
      result.setTotalCount(rsp.recordCount);
      result.setTotalPage(rsp.pageNum);
      result.setLoadPage(page);
    } catch (IOException e) {
      result.fail();
    } catch (RspException e) {
      result.fail();
    }

    return result;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    has_touch_mode(false);
    has_home_button(false);
    adapter = new RankingListAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  @Override
  protected View getContentView(LayoutInflater inflater, ViewGroup viewGroup) {
    View view = inflater.inflate(R.layout.migu_ranking_list, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  protected void didLoadFailed(LoadResult<RankingListInfo> data) {
  }

  @Override
  protected void didLoadFinish(List<RankingListInfo> dataList) {
    adapter.setDataList(dataList);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    RankingListInfo ranking = adapter.getDataItem(position);

    MiguRankingDetailFragment frag = new MiguRankingDetailFragment();
    frag.setParent(this.getParent());  // 有返回按钮

    if ("<unknown>".equals(ranking.name)) {
      frag.setTitle(getString(R.string.unknown));
    } else {
      frag.setTitle(ranking.name);
    }
    frag.setRankingInfo(ranking);
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.show(frag);
  }
}
