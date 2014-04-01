package com.song1.musicno1.entity;

import java.util.List;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM3:19
 */
public class LoadResult<T> {
  private boolean failed = false;

  private List<T> dataList;
  private int     totalCount;
  private int     totalPage;
  private int     loadPage;

  public int getTotalPage() {
    return totalPage;
  }

  public void setTotalPage(int totalPage) {
    this.totalPage = totalPage;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }


  public boolean isFailed() {
    return failed;
  }

  public List<T> getDataList() {
    return dataList;
  }

  public void setDataList(List<T> dataList) {
    this.dataList = dataList;
  }

  public void fail() {
    failed = true;
  }

  public int getLoadPage() {
    return loadPage;
  }

  public void setLoadPage(int loadPage) {
    this.loadPage = loadPage;
  }
}
