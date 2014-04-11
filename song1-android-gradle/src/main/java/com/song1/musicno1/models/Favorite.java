package com.song1.musicno1.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.song1.musicno1.helpers.ActiveHelper;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * Created by windless on 14-4-9.
 */
@Table(name = "favorites")
public class Favorite extends Model {
  @Column(name = "name")
  public String name;

  public List<FavoriteAudio> audios() {
    return getMany(FavoriteAudio.class, "favorite");
  }

  public static void createReadHeart() {
    Favorite favorite = new Favorite();
    favorite.name = "Red Heart";
    favorite.save();
  }

  public static void init() {
    Favorite redHeart = Favorite.load(Favorite.class, 1);
    if (redHeart == null) {
      redHeart = new Favorite();
      redHeart.name = "Red Heart";
      redHeart.save();
    }
  }

  public static Favorite create(String name) {
    Favorite favorite = new Favorite();
    favorite.name = name;
    favorite.save();
    return favorite;
  }

  public static List<Favorite> getAll() {
    List<Favorite> favorites = new Select().from(Favorite.class).execute();
    favorites.remove(0);
    return favorites;
  }

  public void destroy() {
    ActiveHelper.transition(() -> {
      List<FavoriteAudio> audios = audios();
      for (FavoriteAudio favoriteAudio : audios) {
        favoriteAudio.delete();
      }
      delete();
    });
  }

  public boolean isContain(Audio audio) {
    List<FavoriteAudio> exist = new Select().from(FavoriteAudio.class)
        .where("audio_id=? AND from_type=?", audio.getId(), audio.getFrom()).execute();
    return exist != null && exist.size() > 0;
  }

  public void add(Audio audio) {
    FavoriteAudio favoriteAudio = new FavoriteAudio(audio);
    favoriteAudio.favorite = this;
    favoriteAudio.save();
  }
}
