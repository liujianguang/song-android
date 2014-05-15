package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.MiguAudioAdapter;
import com.song1.musicno1.entity.AlbumInfo;
import com.song1.musicno1.entity.LoadResult;
import com.song1.musicno1.entity.SongInfo;
import com.song1.musicno1.fragments.PageLoadFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-6
 * Time: PM5:38
 */
public class MiguAlbumDetailFragment extends PageLoadFragment<SongInfo> implements AdapterView.OnItemClickListener {
  @InjectView(R.id.list) ListView listView;
  MiguAudioAdapter adapter;

  private AlbumInfo albumInfo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new MiguAudioAdapter(getActivity());
    adapter.setActivity(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  @Override
  protected View getContentView(LayoutInflater inflater, ViewGroup viewGroup) {
    View view = inflater.inflate(R.layout.fragment_migu_audio, viewGroup, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  protected LoadResult<SongInfo> getLoadResult(int page) {
    LoadResult<SongInfo> result = new LoadResult<SongInfo>();
    try {
      SongInfo[] rsp = MiguService.getInstance().Init(getActivity()).fetchSongByAlbumId(albumInfo.id);
      result.setDataList(Lists.newArrayList(rsp));
      result.setLoadPage(page);
      result.setTotalPage(1);
      result.setTotalCount(rsp.length);
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
    List<Audio> audios = List8.newList(dataList).map((songInfo) -> songInfo.toAudio());
    adapter.setDataList(audios);
  }

  public void setAlbumInfo(AlbumInfo albumInfo) {
    this.albumInfo = albumInfo;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Audio audio = adapter.getDataItem(position);
    Playlist playlist = new Playlist(List8.newList(adapter.getDataList()), audio);
    Players.setPlaylist(playlist, getFragmentManager());
  }
}
