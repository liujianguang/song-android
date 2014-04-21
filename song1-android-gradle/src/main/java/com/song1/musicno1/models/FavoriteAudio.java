package com.song1.musicno1.models;

import android.database.sqlite.SQLiteException;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * Created by windless on 14-4-9.
 */
@Table(name = "favorite_audios")
public class FavoriteAudio extends Model {
  @Column(name = "audio_id")
  public String audioId;

  @Column(name = "title")
  public String title;

  @Column(name = "album")
  public String album;

  @Column(name = "artist")
  public String artist;

  @Column(name = "from_type")
  public int from;

  @Column(name = "remote_url", index = true)
  public String remotePlayUrl;

  @Column(name = "local_uri")
  public String localPlayUri;

  @Column(name = "favorite")
  public Favorite favorite;

  @Column(name = "album_id")
  public String albumId;

  public FavoriteAudio() {

  }

  public FavoriteAudio(Audio audio) {
    audioId = audio.getId();
    title = audio.getTitle();
    album = audio.getAlbum();
    artist = audio.getArtist();
    from = audio.getFrom();
    remotePlayUrl = audio.getRemotePlayUrl();
    localPlayUri = audio.getLocalPlayUri();
    albumId = audio.getAlbumId();
  }

  public Audio toAudio() {
    Audio audio = new Audio();
    audio.setId(audioId);
    audio.setTitle(title);
    audio.setAlbum(album);
    audio.setArtist(artist);
    audio.setLocalPlayUri(localPlayUri);
    audio.setRemotePlayUrl(remotePlayUrl);
    audio.setAlbumId(albumId);
    return audio;
  }

  public static boolean isFavorite(Audio audio) {
    try {
      List<FavoriteAudio> audios =
          new Select().from(FavoriteAudio.class)
              .where("favorite = 1 AND audio_id = ? AND from_type = ?", audio.getId(), audio.getFrom()).execute();
      if (audios != null && audios.size() > 0) return true;
    } catch (SQLiteException e) {
      return false;
    }
    return false;
  }

  public static void addToFavorite(Audio audio) {
    Favorite favorite = Favorite.load(Favorite.class, 1);
    FavoriteAudio favoriteAudio = new FavoriteAudio(audio);
    favoriteAudio.favorite = favorite;
    favoriteAudio.save();
  }

  public static void removeFromFavorite(Audio audio) {
    new Delete().from(FavoriteAudio.class)
        .where("favorite = 1 AND audio_id = ? AND from_type = ?", audio.getId(), audio.getFrom())
        .execute();
  }

  public static boolean toggleRedHeart(Audio audio) {
    boolean isRedHeart;
    if (isFavorite(audio)) {
      removeFromFavorite(audio);
      isRedHeart = false;
    } else {
      addToFavorite(audio);
      isRedHeart = true;
    }
    return isRedHeart;
  }
}
