package com.song1.musicno1.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.models.play.Audio;
import de.akquinet.android.androlog.Log;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.List;
import java.util.Map;

/**
 * Created by leovo on 2014/4/17.
 */
public class AudioUtil {
  public static List<Audio> doAudioGroup(List<Audio> audioList) {
    Map<Character, List<Audio>> audioGroupMap = Maps.newTreeMap();

    System.out.println("************************************");
    for (Audio audio : audioList) {
      Character character = getFirstLetter(audio.getTitle().trim().toUpperCase());
      Log.d("" + character);

      System.out.println(character + "");
      System.out.println(audio.getTitle());
      List<Audio> audioGroup = audioGroupMap.get(character);

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

    List<Audio> otherAudios = audioGroupMap.get('#');
    if (otherAudios != null) {
      audios.add(new AudioGroup("#"));
      audios.addAll(otherAudios);
    }
    return audios;
  }

  private static Character getFirstLetter(String input) {
    char c = input.toCharArray()[0];
    if (c >= 'A' && c <= 'Z') {
      return c;
    }

    HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
    defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
    c = input.charAt(0);
    try {
      String[] strings = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
      if (strings == null) {
        return '#';
      }
      return strings[0].toUpperCase().toCharArray()[0];
    } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
      return '#';
    }
  }
}
