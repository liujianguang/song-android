package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.MiguRankingAduioAdapter;
import com.song1.musicno1.entity.GetRankingSongResp;
import com.song1.musicno1.entity.LoadResult;
import com.song1.musicno1.entity.RankingListInfo;
import com.song1.musicno1.entity.SongInfo;
import com.song1.musicno1.fragments.PageLoadFragment;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-6
 * Time: PM4:58
 */
public class MiguRankingDetailFragment extends PageLoadFragment<SongInfo> implements AdapterView.OnItemClickListener {
  @InjectView(R.id.list) ListView listView;

  MiguRankingAduioAdapter adapter;

  private RankingListInfo info;

  @Inject
  public MiguRankingDetailFragment() {

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new MiguRankingAduioAdapter(getActivity());
    adapter.setActivity(getActivity());
//    playerAction.context(getSherlockActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(info.name);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  @Override
  protected View getContentView(LayoutInflater inflater, ViewGroup viewGroup) {
    View view = inflater.inflate(R.layout.fragment_migu_ranking_detail, viewGroup, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  protected LoadResult<SongInfo> getLoadResult(int page) {
    LoadResult<SongInfo> result = new LoadResult<SongInfo>();
    try {
      GetRankingSongResp rsp = MiguService.getInstance().Init(getActivity()).fetchSongByRankingListId(info.id, 0, 100);
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
  protected void didLoadFailed(LoadResult<SongInfo> data) {
  }

  @Override
  protected void didLoadFinish(List<SongInfo> dataList) {
    adapter.setDataList(dataList);
  }

  public void setRankingInfo(RankingListInfo info) {
    this.info = info;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SongInfo info = adapter.getDataItem(position);
//    playerAction.play(info.toAudio());
  }
}
