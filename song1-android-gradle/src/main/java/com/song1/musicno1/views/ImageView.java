package com.song1.musicno1.views;

import android.content.Context;
import android.util.AttributeSet;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by windless on 14-4-12.
 */
public class ImageView extends android.widget.ImageView {
  public ImageView(Context context) {
    super(context);
  }

  public ImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setImageResource(int resId) {
    Picasso.with(getContext()).load(resId);
  }

  public void setImageUri(String uri) {
    if (uri.startsWith("http")) {
      Picasso.with(getContext()).load(uri);
    } else {
      Picasso.with(getContext()).load(new File(uri));
    }
  }

  public void setImageFile(File file) {
    Picasso.with(getContext()).load(file);
  }
}
