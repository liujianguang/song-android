package com.song1.musicno1.entity;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: SV
 * Date: 13-10-21
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public class GetRankingSongResp {
  public int                 offSet;
  public int                 pageSize;
  public int                 recordCount;
  public int                 pageNum;
  public ArrayList<SongInfo> listPageObject;
}
