package com.song1.musicno1.models.play;

/**
 * Created by windless on 3/27/14.
 */
public class Audio {
  public static final int LOCAL = 0;
  public static final int MIGU  = 1;
  public static final int OTHER = 2;

  private String id;
  private String title;
  private String album;
  private String artist;
  private int    from;
  private String remotePlayUrl;
  private String localPlayUri;

  public String getRemotePlayUrl() {
    return remotePlayUrl;
  }

  public void setRemotePlayUrl(String remotePlayUrl) {
    this.remotePlayUrl = remotePlayUrl;
  }

  public String getLocalPlayUri() {
    return localPlayUri;
  }

  public void setLocalPlayUri(String localPlayUri) {
    this.localPlayUri = localPlayUri;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAlbum() {
    return album;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public int getFrom() {
    return from;
  }

  public void setFrom(int from) {
    this.from = from;
  }

  public boolean canFavorite() {
    return from == LOCAL || from == MIGU;
  }
}
