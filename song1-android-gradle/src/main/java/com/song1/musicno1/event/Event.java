package com.song1.musicno1.event;

/**
 * Created by leovo on 2014/5/8.
 */
public class Event {

  public static class RePlayEvent{
  }
  public static class ShowExitDialogEvent{
  }
  public static class SetPlayModeEvent{
    int mode;
    public SetPlayModeEvent(int mode){
      this.mode = mode;
    }
    public int getMode(){
      return mode;
    }
    public void setMode(int mode){
      this.mode = mode;
    }
  }
}
