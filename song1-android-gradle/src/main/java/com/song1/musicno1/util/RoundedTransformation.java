package com.song1.musicno1.util;

/**
 * Created by leovo on 2014/4/16.
 */
import android.graphics.*;
import android.graphics.Bitmap.Config;

// enables hardware accelerated rounded corners
// original idea here : http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/
public class RoundedTransformation implements com.squareup.picasso.Transformation {
  private final int radius;
  private final int margin;  // dp

  // radius is corner radii in dp
  // margin is the board in dp
  public RoundedTransformation(final int radius, final int margin) {
    this.radius = radius;
    this.margin = margin;
  }

  public RoundedTransformation(){
    this.radius = 0;
    this.margin = 5;
  }
  @Override
  public Bitmap transform(final Bitmap source) {
    final Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
    Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);
    Canvas canvas = new Canvas(output);
    canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), source.getWidth() /2, source.getWidth()/2, paint);
    if (source != output) {
      source.recycle();
    }

    return output;
  }

  @Override
  public String key() {
    return "rounded";
  }
}
