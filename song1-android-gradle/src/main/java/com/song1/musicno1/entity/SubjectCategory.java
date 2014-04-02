package com.song1.musicno1.entity;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: SV
 * Date: 13-10-21
 * Time: 上午11:09
 * To change this template use File | Settings | File Templates.
 */
public class SubjectCategory {
  public String code;
  public String text;
  public String leaf;
  public boolean isLeaf()
  {
    return leaf.equalsIgnoreCase("true");
  }

  public ArrayList<SubjectCategory> children;
}
