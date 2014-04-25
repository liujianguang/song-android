package com.song1.musicno1.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;
import org.w3c.dom.Text;

/**
 * Created by leovo on 2014/4/25.
 */
public class IocTextView extends TextView {


  public IocTextView(Context context) {
    super(context);
  }

  public IocTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public IocTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.RED);
    canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2 - 4,paint);
    super.onDraw(canvas);
  }
}
