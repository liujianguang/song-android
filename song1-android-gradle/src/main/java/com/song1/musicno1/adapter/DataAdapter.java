package com.song1.musicno1.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by windless on 14-4-4.
 */
public abstract class DataAdapter<T> extends android.widget.BaseAdapter {
  protected final Context context;
  private         List<T> dataList;

  public DataAdapter(Context context) {
    this.context = context;
  }

  @Override
  public int getCount() {
    return dataList == null ? 0 : dataList.size();
  }

  public void setDataList(List<T> dataList) {
    this.dataList = dataList;
  }

  public List<T> getDataList() {
    return this.dataList;
  }

  public T getDataItem(int i) {
    return dataList.get(i);
  }

  @Override
  public Object getItem(int i) {
    return dataList.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  public void remove(T t){
    this.dataList.remove(t);
  }
}
