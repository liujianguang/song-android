package com.song1.musicno1.loader;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by windless on 14-4-4.
 */
public class LoadData<T> {
  protected final List<T> list;

  protected int currentPage;
  protected int totalPage;
  private   int loadPage;

  public LoadData() {
    list = Lists.newArrayList();
    loadPage = 1;
    totalPage = 1;
  }

  public List<T> getDataList() {
    return list;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getTotalPage() {
    return totalPage;
  }

  public boolean isNeedLoad() {
    return totalPage == 0 || loadPage > currentPage;
  }

  public boolean isEmpty() {
    return list.size() == 0;
  }

  public boolean isFull() {
    return totalPage != 0 && currentPage == totalPage;
  }

  public int getLoadPage() {
    return loadPage;
  }

  public void setLoadPage(int loadPage) {
    this.loadPage = loadPage;
  }

  public void setTotalPage(int totalPage) {
    this.totalPage = totalPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }
}
