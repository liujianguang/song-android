package com.song1.musicno1.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.models.play.Audio;

import java.util.List;
import java.util.Map;

/**
 * Created by leovo on 2014/4/17.
 */
public class AudioUtil {
  public static List<Audio> doAudioGroup(List<Audio> audioList) {
    Map<Character, List<Audio>> audioGroupMap = Maps.newTreeMap();
    List<Audio> otherAudios = Lists.newArrayList();

    for (Audio audio : audioList) {
      Character character = FirstLetterUtil.getFirstLetter(audio.getTitle()).toUpperCase().charAt(0);

      List<Audio> audioGroup = audioGroupMap.get(character);

      if (character < 'A' || character > 'Z') {
        character = '#';
        audioGroup = otherAudios;
      }

      if (audioGroup == null) {
        audioGroup = Lists.newArrayList();
      }
      audioGroup.add(audio);
      audioGroupMap.put(character, audioGroup);
    }

    List<Audio> audios = Lists.newArrayList();

    for (Character character : audioGroupMap.keySet()) {
      if (character != '#') {
        audios.add(new AudioGroup(character.toString()));
        audios.addAll(audioGroupMap.get(character));
      }
    }

    if (otherAudios != null) {
      audios.add(new AudioGroup("#"));
      audios.addAll(otherAudios);
    }
    return audios;
  }
}
