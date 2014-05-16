package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.adapter.MiguRankingAduioAdapter;
import com.song1.musicno1.entity.GetRankingSongResp;
import com.song1.musicno1.entity.RankingListInfo;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-6
 * Time: PM4:58
 */
public class MiguRankingDetailFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener {
  private RankingListInfo info;

  @Inject
  public MiguRankingDetailFragment() {
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(info.name);
    getListView().setOnItemClickListener(this);
  }

  @Override
  protected List<Audio> onLoad(int loadPage) {
    try {
      setTotalPage(1);
      GetRankingSongResp rsp = MiguService.getInstance().Init(getActivity()).fetchSongByRankingListId(info.id, 0, 100);
      return List8.newList(rsp.listPageObject).map((songInfo) -> songInfo.toAudio());
    } catch (IOException e) {
      return null;
    } catch (RspException e) {
      return null;
    }
  }

  @Override
  protected DataAdapter<Audio> newAdapter() {
    return new MiguRankingAduioAdapter(getActivity());
  }

  public void setRankingInfo(RankingListInfo info) {
    this.info = info;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Playlist playlist = new Playlist(List8.newList(getDataList()), getDataItem(position));
    Players.setPlaylist(playlist, getFragmentManager());
  }
}
