package com.song1.musicno1.models.play;

import java.util.List;

/**
 * Created by windless on 14-4-8.
 */
public interface MediaServer {
  String getName();

  String getId();

  List<Object> browse(String id);
}
