package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.adapter.MiguSongListAdapter;
import com.song1.musicno1.entity.GetSubjectResp;
import com.song1.musicno1.entity.SubjectInfo;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;

import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM4:24
 */
public class MiguSongListFragment extends ListFragment<SubjectInfo> implements AdapterView.OnItemClickListener {
  private static final int PAGE_SIZE = 20;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
  }

  @Override
  protected List<SubjectInfo> onLoad(int loadPage) {
    try {
      GetSubjectResp rsp = MiguService.getInstance().fetchSubjectList(getDataCount(), PAGE_SIZE, "00");
      setTotalPage(rsp.pageNum);
      return rsp.listPageObject;
    } catch (IOException e) {
      return null;
    } catch (RspException e) {
      return null;
    }
  }

  @Override
  protected DataAdapter<SubjectInfo> newAdapter() {
    return new MiguSongListAdapter(getActivity());
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SubjectInfo subjectItem = getDataItem(position);

    MiguSongListDetailFragment frag = new MiguSongListDetailFragment();
    frag.setSubjectInfo(subjectItem);

    if ("<unknown>".equals(subjectItem.name)) {
      frag.setTitle(getString(R.string.unknown));
    } else {
      frag.setTitle(subjectItem.name);
    }
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.push(MiguSongListFragment.class.getName(), frag);
  }
}
