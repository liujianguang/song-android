package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.MiguSongListAdapter;
import com.song1.musicno1.entity.GetSubjectResp;
import com.song1.musicno1.entity.LoadResult;
import com.song1.musicno1.entity.SubjectInfo;
import com.song1.musicno1.fragments.PageLoadFragment;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;

import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM4:24
 */
public class MiguSongListFragment extends PageLoadFragment<SubjectInfo> implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
  private static final int PAGE_SIZE = 20;

  @InjectView(R.id.playlist_list) ListView listView;

  private MiguSongListAdapter adapter;

  @Override
  protected LoadResult<SubjectInfo> getLoadResult(int page) {
    LoadResult<SubjectInfo> result = new LoadResult<SubjectInfo>();
    try {
      GetSubjectResp rsp = MiguService.getInstance().fetchSubjectList(adapter.getCount(), PAGE_SIZE, "00");
      result.setTotalCount(rsp.recordCount);
      result.setDataList(rsp.listPageObject);
      result.setTotalPage(rsp.pageNum);
      result.setLoadPage(page);
    } catch (IOException e) {
      result.fail();
    } catch (RspException e) {
      e.printStackTrace();
    }

    return result;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    has_touch_mode(false);
    has_home_button(false);
    adapter = new MiguSongListAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
    listView.setOnScrollListener(this);
  }

  @Override
  protected View getContentView(LayoutInflater inflater, ViewGroup viewGroup) {
    View view = inflater.inflate(R.layout.migu_playlist_list, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  protected void didLoadFailed(LoadResult<SubjectInfo> data) {
  }

  @Override
  protected void didLoadFinish(List<SubjectInfo> dataList) {
    adapter.setDataList(dataList);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SubjectInfo subjectItem = adapter.getDataItem(position);

    MiguSongListDetailFragment frag = new MiguSongListDetailFragment();
    frag.setSubjectInfo(subjectItem);
    frag.setParent(this.getParent());  // 有返回按钮

    if ("<unknown>".equals(subjectItem.name)) {
      frag.setTitle(getString(R.string.unknown));
    } else {
      frag.setTitle(subjectItem.name);
    }
    MainActivity mainActivity = (MainActivity)getActivity();
    mainActivity.show(frag);
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE) {
      if (listView.getLastVisiblePosition() + 1 >= listView.getCount() && hasNextPage()) {
        if (!isLoading()) {
          loadMore();
        }
      }
    }
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
  }
}
