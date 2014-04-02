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
import com.google.common.collect.Maps;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.MiguArtistListAdapter;
import com.song1.musicno1.entity.ArtistInfo;
import com.song1.musicno1.entity.GetArtistResp;
import com.song1.musicno1.entity.LoadResult;
import com.song1.musicno1.fragments.CloudArtistDetailFrag;
import com.song1.musicno1.fragments.PageLoadFragment;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: windless
 * Date: 13-12-6
 * Time: AM9:58
 */
public class MiguArtistFragment extends PageLoadFragment<ArtistInfo> implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
  private MiguArtistListAdapter adapter;

  private String artistId;
  private Map<Integer, String>  artistIdMap   = Maps.newHashMap();
  private Map<Integer, Integer> artistNameMap = Maps.newHashMap();

  @InjectView(R.id.list) ListView listView;

  private int categoryId;

  public MiguArtistFragment() {
    artistIdMap.put(R.id.chinese_male, "0000");
    artistIdMap.put(R.id.chinese_female, "0001");
    artistIdMap.put(R.id.chinese_band, "0002");
    artistIdMap.put(R.id.ea_male, "0100");
    artistIdMap.put(R.id.ea_female, "0101");
    artistIdMap.put(R.id.ea_band, "0102");
    artistIdMap.put(R.id.jk_male, "0200");
    artistIdMap.put(R.id.jk_female, "0201");
    artistIdMap.put(R.id.jk_band, "0202");

    artistNameMap.put(R.id.chinese_male, R.string.chinese_male);
    artistNameMap.put(R.id.chinese_female, R.string.chinese_female);
    artistNameMap.put(R.id.chinese_band, R.string.chinese_band);
    artistNameMap.put(R.id.ea_male, R.string.ea_male);
    artistNameMap.put(R.id.ea_female, R.string.ea_female);
    artistNameMap.put(R.id.ea_band, R.string.ea_band);
    artistNameMap.put(R.id.jk_male, R.string.jk_male);
    artistNameMap.put(R.id.jk_female, R.string.jk_female);
    artistNameMap.put(R.id.jk_band, R.string.jk_band);
  }

  public void setCategoryId(int categoryId) {
    artistId = artistIdMap.get(categoryId);
    this.categoryId = categoryId;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new MiguArtistListAdapter(getActivity());
    setTitle(getString(artistNameMap.get(categoryId)));
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setAdapter(adapter);
    listView.setOnScrollListener(this);
    listView.setOnItemClickListener(this);
  }

  @Override
  protected View getContentView(LayoutInflater inflater, ViewGroup viewGroup) {
    View view = inflater.inflate(R.layout.fragment_migu_artist, viewGroup, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  protected LoadResult<ArtistInfo> getLoadResult(int page) {
    LoadResult<ArtistInfo> result = new LoadResult<ArtistInfo>();
    try {
      GetArtistResp rsp = MiguService.getInstance().Init(getActivity()).fetchArtistList(adapter.getCount(), page * 60, artistId);
      result.setDataList(rsp.listPageObject);
      result.setLoadPage(page);
      result.setTotalPage(rsp.pageNum);
      result.setTotalCount(rsp.recordCount);
    } catch (IOException e) {
      result.fail();
    } catch (RspException e) {
      result.fail();
    }
    return result;
  }

  @Override
  protected void didLoadFailed(LoadResult<ArtistInfo> data) {
  }

  @Override
  protected void didLoadFinish(List<ArtistInfo> dataList) {
    adapter.setDataList(dataList);
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

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    ArtistInfo info = adapter.getDataItem(position);

    CloudArtistDetailFrag detail = new CloudArtistDetailFrag();
    detail.artist(info.toArtist());

    if ("<unknown>".equals(info.name)) {
      detail.setTitle(getString(R.string.unknown));
    } else {
      detail.setTitle(info.name);
    }

    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.push(CloudArtistDetailFrag.class.getName(), detail);
  }
}
