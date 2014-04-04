package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.adapter.RankingListAdapter;
import com.song1.musicno1.entity.GetRankingListResp;
import com.song1.musicno1.entity.RankingListInfo;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;

import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM3:38
 */
public class MiguRankingListFragment extends ListFragment<RankingListInfo> implements AdapterView.OnItemClickListener {

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
  }

  @Override
  protected List<RankingListInfo> onLoad(int loadPage) {
    try {
      GetRankingListResp rsp = MiguService.getInstance().fetchRankingList(getDataCount(), 20);
      setTotalPage(rsp.pageNum);
      return rsp.listPageObject;
    } catch (IOException e) {
      return null;
    } catch (RspException e) {
      return null;
    }
  }

  @Override
  protected DataAdapter<RankingListInfo> newAdapter() {
    return new RankingListAdapter(getActivity());
  }


  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    RankingListInfo ranking = getDataItem(position);

    MiguRankingDetailFragment frag = new MiguRankingDetailFragment();

    if ("<unknown>".equals(ranking.name)) {
      frag.setTitle(getString(R.string.unknown));
    } else {
      frag.setTitle(ranking.name);
    }
    frag.setRankingInfo(ranking);
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.push(MiguRankingDetailFragment.class.getName(), frag);
  }
}
