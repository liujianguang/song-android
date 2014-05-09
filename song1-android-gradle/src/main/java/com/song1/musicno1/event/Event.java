package com.song1.musicno1.event;

import com.song1.musicno1.models.play.Audio;

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
  public static class PlayingAudioEvent{
    Audio audio;
    public PlayingAudioEvent(Audio audio){
      this.audio = audio;
    }
    public Audio getAudio(){
      return audio;
    }
  }
}
