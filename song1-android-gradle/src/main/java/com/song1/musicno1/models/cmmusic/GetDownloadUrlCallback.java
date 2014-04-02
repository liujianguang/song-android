package com.song1.musicno1.models.cmmusic;

/**
 * Created with IntelliJ IDEA.
 * User: SV
 * Date: 13-5-17
 * Time: 下午5:18
 */
public interface GetDownloadUrlCallback {
  void OnUrlGot(String code, String msg, String url);
}
