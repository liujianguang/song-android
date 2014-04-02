package com.song1.musicno1.entity;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: SV
 * Date: 13-10-18
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
public class ArtistCategory {
  public String code;
  public String text;
  public String leaf; // 是否子子分类
  public boolean isLeaf()
  {
    return leaf.equalsIgnoreCase("true");
  }

  public ArrayList<ArtistCategory> children;

}
