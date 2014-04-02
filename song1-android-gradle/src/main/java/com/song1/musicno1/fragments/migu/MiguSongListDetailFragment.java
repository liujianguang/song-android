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
import com.song1.musicno1.entity.LoadResult;
import com.song1.musicno1.entity.SongInfo;
import com.song1.musicno1.entity.SubjectInfo;
import com.song1.musicno1.fragments.PageLoadFragment;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-6
 * Time: PM6:01
 */
public class MiguSongListDetailFragment extends PageLoadFragment<SongInfo> implements AdapterView.OnItemClickListener {
  private SubjectInfo subjectInfo;
  MiguAudioAdapter adapter;
  @InjectView(R.id.list) ListView listView;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter = new MiguAudioAdapter(getActivity());
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
      SongInfo[] rsp = MiguService.getInstance().Init(getActivity()).fetchSongBySubjectId(subjectInfo.id);
      result.setDataList(Lists.newArrayList(rsp));
      result.setLoadPage(page);
      result.setTotalCount(rsp.length);
      result.setTotalPage(1);
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

  public void setSubjectInfo(SubjectInfo subjectInfo) {
    this.subjectInfo = subjectInfo;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SongInfo info = adapter.getDataItem(position);
  }
}
