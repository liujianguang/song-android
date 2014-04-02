package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.MiguAlbumAdapter;
import com.song1.musicno1.entity.AlbumInfo;
import com.song1.musicno1.entity.GetAlbumResp;
import com.song1.musicno1.entity.LoadResult;
import com.song1.musicno1.fragments.PageLoadFragment;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;

import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM4:56
 */
public class MiguAlbumListFragment extends PageLoadFragment<AlbumInfo> implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
  private static final int PAGE_SIZE = 40;

  @InjectView(R.id.album_gridlist) GridView gridView;

  private MiguAlbumAdapter adapter;

  @Override
  protected LoadResult<AlbumInfo> getLoadResult(int page) {
    LoadResult<AlbumInfo> result = new LoadResult<AlbumInfo>();
    try {
      GetAlbumResp rsp = MiguService.getInstance().fetchAlbumList(adapter.getCount(), PAGE_SIZE, "0");
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
  protected void didLoadFailed(LoadResult<AlbumInfo> data) {
  }

  @Override
  protected void didLoadFinish(List<AlbumInfo> dataList) {
    adapter.setDataList(dataList);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new MiguAlbumAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    gridView.setAdapter(adapter);
    gridView.setOnItemClickListener(this);
    gridView.setOnScrollListener(this);
  }

  @Override
  protected View getContentView(LayoutInflater inflater, ViewGroup viewGroup) {
    View view = inflater.inflate(R.layout.migu_album_list, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    AlbumInfo albumItem = adapter.getDataItem(position);
    MiguAlbumDetailFragment frag = new MiguAlbumDetailFragment();
    frag.setAlbumInfo(albumItem);
    if ("<unknown>".equals(albumItem.name)) {
      frag.setTitle(getString(R.string.unknown));
    } else {
      frag.setTitle(albumItem.name);
    }
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.push(MiguAlbumDetailFragment.class.getName(), frag);
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE) {
      if (gridView.getLastVisiblePosition() + 1 >= gridView.getCount() && hasNextPage()) {
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
