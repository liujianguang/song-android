package com.song1.musicno1.helpers;

import android.content.Context;
import android.widget.ImageView;
import com.google.common.base.Strings;
import com.song1.musicno1.util.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by windless on 14-4-22.
 */
public class AlbumArtHelper {
  public static void loadAlbumArtRounded(Context context, String albumArt, ImageView imageView, int errorDrawable) {
    if (Strings.isNullOrEmpty(albumArt)) {
      Picasso.with(context).load(errorDrawable).transform(new RoundedTransformation()).into(imageView);
    } else {
      if (albumArt.startsWith("http")) {
        Picasso.with(context)
            .load(albumArt)
            .transform(new RoundedTransformation())
            .error(errorDrawable)
            .into(imageView);
      } else {
        File file = new File(albumArt);
        if (file.exists()) {
          Picasso.with(context)
              .load(file)
              .transform(new RoundedTransformation())
              .error(errorDrawable)
              .into(imageView);
        } else {
          Picasso.with(context)
              .load(errorDrawable)
              .transform(new RoundedTransformation())
              .into(imageView);
        }
      }
    }
  }

  public static void loadAlbumArt(Context context, String albumArt, ImageView imageView, int errorDrawable) {
    if (Strings.isNullOrEmpty(albumArt)) {
      Picasso.with(context).load(errorDrawable).into(imageView);
    } else {
      if (albumArt.startsWith("http")) {
        Picasso.with(context)
            .load(albumArt)
            .error(errorDrawable)
            .into(imageView);
      } else {
        File file = new File(albumArt);
        if (file.exists()) {
          Picasso.with(context)
              .load(file)
              .error(errorDrawable)
              .into(imageView);
        } else {
          Picasso.with(context)
              .load(errorDrawable)
              .into(imageView);
        }
      }
    }

  }
}
