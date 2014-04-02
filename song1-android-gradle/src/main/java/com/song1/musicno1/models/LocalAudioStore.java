package com.song1.musicno1.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.models.play.Audio;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Audio.AudioColumns.*;
import static android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Audio.Media.TITLE;
import static android.provider.MediaStore.MediaColumns.DATA;

public class LocalAudioStore {
  private final   ContentResolver contentResolver;
  protected final Context         context;

  public LocalAudioStore(Context context) {
    this.context = context;
    this.contentResolver = context.getContentResolver();
  }

  public List<Audio> all_audios() {

    Cursor cursor = contentResolver.query(
        EXTERNAL_CONTENT_URI,
        new String[]{TITLE, DURATION, ARTIST, _ID, ALBUM, DATA, ALBUM_ID, MIME_TYPE},
        MIME_TYPE + " IN (?,?,?,?)",
        new String[]{"audio/mpeg", "audio/wav", "audio/x-wav", "audio/flac"},
        TITLE
    );

    if (cursor == null) return null;
    return parse_audios(cursor);
  }

  public List<Audio> audios(String col, String selection) {
    Cursor cursor = contentResolver.query(
        EXTERNAL_CONTENT_URI,
        new String[]{TITLE, DURATION, ARTIST, _ID, ALBUM, DATA, ALBUM_ID, MIME_TYPE},
        MIME_TYPE + " IN (?,?,?,?) AND " + col + "=?",
        new String[]{"audio/mpeg", "audio/wav", "audio/x-wav", "audio/flac", selection},
        TITLE
    );

    if (cursor == null) return null;
    return parse_audios(cursor);
  }

  public List<Audio> parse_audios(Cursor cursor) {
    List<Audio> audios = new ArrayList<Audio>();

    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      Audio audio = new Audio();
      audio.setId(cursor.getString(cursor.getColumnIndex(_ID)));
      audio.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));

      String album = cursor.getString(cursor.getColumnIndex(ALBUM));
      if ("<unknown>".equals(album)) {
        audio.setAlbum(context.getString(R.string.unknown));
      } else {
        audio.setAlbum(album);
      }

      String artist = cursor.getString(cursor.getColumnIndex(ARTIST));
      if ("<unknown>".equals(artist)) {
        audio.setArtist(context.getString(R.string.unknown));
      } else {
        audio.setArtist(artist);
      }

      audio.setLocalPlayUri(cursor.getString(cursor.getColumnIndex(DATA)));
      audio.setFrom(Audio.LOCAL);
//      audio.album_id = cursor.getString(cursor.getColumnIndex(ALBUM_ID));

      audios.add(audio);
    }
    cursor.close();
    return audios;
  }

  public String find_album_path_by(String album_id) {
    Cursor cursor = contentResolver.query(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        new String[]{MediaStore.Audio.Albums.ALBUM_ART},
        MediaStore.Audio.Albums._ID + "=?",
        new String[]{album_id}, null
    );

    String album_path = null;
    if (cursor != null && cursor.moveToFirst()) {
      album_path = cursor.getString(0);
    }

    if (cursor != null) cursor.close();

    return album_path;
  }

  public int audios_count() {
    Cursor cursor = contentResolver.query(
        EXTERNAL_CONTENT_URI,
        new String[]{"count(*) AS count"},
        MIME_TYPE + " IN (?,?,?,?)",
        new String[]{"audio/mpeg", "audio/wav", "audio/x-wav", "audio/flac"}, null
    );
    if (cursor == null) return 0;

    cursor.moveToFirst();
    return cursor.getInt(0);
  }

  public int lossless_count() {
    Cursor cursor = contentResolver.query(
        EXTERNAL_CONTENT_URI,
        new String[]{"count(*) AS count"},
        MIME_TYPE + " IN (?,?,?)",
        new String[]{"audio/wav", "audio/x-wav", "audio/flac"}, null
    );
    if (cursor == null) return 0;
    cursor.moveToFirst();
    return cursor.getInt(0);
  }

  public List<Artist> all_artists() {
    Cursor cursor = contentResolver.query(
        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        new String[]{
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        },
        null, null, MediaStore.Audio.Artists.ARTIST
    );

    if (cursor == null) return null;

    List<Artist> artists = new ArrayList<Artist>();
    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      Artist artist = new Artist();
      artist.id = cursor.getString(0);

      String name = cursor.getString(1);
      if ("<unknown>".equals(name)) {
        artist.name = context.getString(R.string.unknown);
      } else {
        artist.name = name;
      }

      artist.album_count = cursor.getInt(2);
      artist.audio_count = cursor.getInt(3);
      artists.add(artist);
    }
    cursor.close();
    return artists;
  }

  public List<Album> all_albums() {
    Cursor cursor = contentResolver.query(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        new String[]{
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        },
        null, null, MediaStore.Audio.Albums.ALBUM
    );
    if (cursor == null) return null;

    List<Album> albums = new ArrayList<Album>();

    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      Album album = new Album();
      album.id = cursor.getString(0);
      album.title = (cursor.getString(1));
      album.album_art = (cursor.getString(2));

      String artist = cursor.getString(3);
      if ("<unknown>".equals(artist)) {
        album.artist = context.getString(R.string.unknown);
      } else {
        album.artist = artist;
      }

      album.number_of_songs = (cursor.getInt(4));
      album.from = Album.LOCAL;
      albums.add(album);
    }
    cursor.close();
    return albums;
  }

  public List<Audio> audios_by_artist(Artist artist) {
    return audios(ARTIST_ID, artist.id);
  }


  public List<Audio> get_audios_by_album(Album album) {
    return audios(ALBUM_ID, album.id);
  }

  public List<Audio> get_all_lossless() {
    Cursor cursor = contentResolver.query(
        EXTERNAL_CONTENT_URI,
        new String[]{TITLE, DURATION, ARTIST, _ID, ALBUM, DATA, ALBUM_ID, MIME_TYPE},
        MIME_TYPE + " IN (?,?,?)",
        new String[]{"audio/wav", "audio/x-wav", "audio/flac"},
        TITLE);
    if (cursor == null) {
      return null;
    }

    List<Audio> audios = parse_audios(cursor);
//    List<Audio> fileAudios = getLossLessFromSDCard();
//    audios.addAll(fileAudios);

    return audios;
  }

//  private List<Audio> getLossLessFromSDCard() {
//    String sdPath = FileHelper.getSDPath();
//    List<File> files = FileHelper.listDir(new File(sdPath), new FileFilter() {
//      @Override
//      public boolean accept(File pathname) {
//        if (pathname.getName().startsWith(".")) return false;
//
//        String ext = FileHelper.getExtension(pathname);
//        return "flac".equals(ext);
//      }
//    });
//
//    List<Audio> audios = new ArrayList<Audio>();
//    for (File file : files) {
//      Audio audio = new Audio();
//      audio.setName(file.getName());
//      audio.setId("0");
//      audio.setPlayUri(file.getAbsolutePath());
//      audio.setFrom(Audio.LOCAL);
//      audio.setRemotePlayRes(httpServerManager.generateRemotePlayUriWithPath(file.getAbsolutePath()));
//      audios.add(audio);
//    }
//    return audios;
//  }
}
