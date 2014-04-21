package com.song1.musicno1.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.models.play.Audio;

import java.util.List;
import java.util.Map;

/**
 * Created by leovo on 2014/4/17.
 */
public class AudioUtil {

  char[] chars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

  public static Map<Character,List<Audio>> doAudioGroup(List<Audio> audioList){
    Map<Character,List<Audio>> audioGroupMap = Maps.newHashMap();
    for (Audio audio : audioList){
      String title = audio.getTitle();
      Character character = FirstLetterUtil.getFirstLetter(title).toUpperCase().charAt(0);
      //System.out.println((int)character);
      if (character < 'A' || character > 'Z'){
        character = '#';
      }
      List<Audio> tempList = audioGroupMap.get(character);
      if (tempList == null){
        tempList = Lists.newArrayList();
        tempList.add(audio);
        audioGroupMap.put(character,tempList);
      }else{
        tempList.add(audio);
        audioGroupMap.put(character,tempList);
      }
    }
    return audioGroupMap;
  }
}
