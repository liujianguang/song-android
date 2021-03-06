package com.song1.musicno1.models.play;

import android.content.Context;
import com.google.common.base.Strings;
import com.song1.musicno1.R;
import com.song1.musicno1.models.LocalAudioStore;

/**
 * Created by windless on 3/27/14.
 */
public class Audio {
  public static final int LOCAL = 0;
  public static final int MIGU  = 1;
  public static final int OTHER = 2;

  private String id;
  private String title;
  private String albumId;
  private String album;
  private String artist;
  private int    from;
  private String remotePlayUrl;
  private String localPlayUri;
  private String albumArt;
  private String mimeType;
  private long   duration;
  private long   size;

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

  public String getAlbumId() {
    return albumId;
  }

  public String getAlbumArt(LocalAudioStore store) {
    if (from == LOCAL && Strings.isNullOrEmpty(albumArt)) {
      albumArt = store.find_album_path_by(albumId);
    }
    return albumArt;
  }

  public void setAlbumArt(String albumArt) {
    this.albumArt = albumArt;
  }

  public void setAlbumId(String albumId) {
    this.albumId = albumId;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public boolean isEqual(Audio audio) {
    return audio != null && id.equals(audio.getId()) && from == audio.from;

  }

  public String getAlbumArt() {
    return albumArt;
  }

  public String getSubtitle(Context context) {
    String albumStr = Strings.isNullOrEmpty(album) ? context.getString(R.string.unknown) : album;
    String artistStr = Strings.isNullOrEmpty(artist) ? context.getString(R.string.unknown) : artist;
    return albumStr + " - " + artistStr;
  }

  public boolean isLossless() {
    return "audio/flac".equals(mimeType) ||
        "audio/wav".equals(mimeType) ||
        "audio/x-wav".equals(mimeType);
  }
}
