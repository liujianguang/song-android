package com.song1.musicno1.entity;

import com.song1.musicno1.models.play.Audio;

/**
 * Created by leovo on 2014/4/17.
 */
public class AudioGroup extends Audio {

  private String name = "";


  public AudioGroup(){

  }

  public AudioGroup(String name){
    this.name = name;
  }

  public void setName(String name){
    this.name = name;
  }
  public String getName(){
    return name;
  }
}
