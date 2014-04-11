package com.song1.musicno1.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

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


  public static void create(String name) {
    Favorite favorite = new Favorite();
    favorite.name = name;
    favorite.save();
  }

  public void destroy() {
    new Delete().from(FavoriteAudio.class).where("favorite = ?", getId());
    delete();
  }
}
