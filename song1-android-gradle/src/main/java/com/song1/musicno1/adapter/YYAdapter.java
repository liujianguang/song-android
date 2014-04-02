package com.song1.musicno1.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM3:50
 */
public abstract class YYAdapter<T> extends BaseAdapter {
  protected final Context context;

  private List<T> dataList;

  public YYAdapter(Context context) {
    this.context = context;
  }

  @Override
  public int getCount() {
    return dataList == null ? 0 : dataList.size();
  }

  public T getDataItem(int pos) {
    return dataList.get(pos);
  }

  public void setDataList(List<T> dataList) {
    this.dataList = dataList;
    notifyDataSetChanged();
  }

  public List<T> getDataList() {
    return this.dataList;
  }

  @Override
  public Object getItem(int position) {
    return getDataItem(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }
}
