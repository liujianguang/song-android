package com.song1.musicno1.entity;

/**
 * Created with IntelliJ IDEA.
 * User: SV
 * Date: 13-10-17
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */
public class ArtistInfo {
  public String id;
  public String name;
  public String img;
  public String desc;

  public Artist toArtist() {

      Artist a = new Artist();
      a.id = this.id;
      a.name = this.name;
      a.image = this.img;
      a.desc = this.desc;

    return  a;
  }
}


