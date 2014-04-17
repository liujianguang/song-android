package com.song1.musicno1.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.song1.musicno1.R;

/**
 * Created by leovo on 2014/4/17.
 */
public class RoundImageView extends ImageView {


  private final Context mContext;
  private int defaultWidth = 0;
  private int defaultHeight    = 0;
  private int mBorderThickness = 0;
  private int mBorderColor = 0;
  public RoundImageView(Context context) {
    super(context);
    mContext = context;
  }

  public RoundImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    setCustomAttributes(attrs);
  }

  public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mContext = context;
    setCustomAttributes(attrs);
  }

  private void setCustomAttributes(AttributeSet attrs) {
    TypedArray a = mContext.obtainStyledAttributes(attrs,
        R.styleable.roundImage);
    mBorderThickness = a.getDimensionPixelSize(
        R.styleable.roundImage_mBorderThickness, 0);
    mBorderColor = a.getColor(R.styleable.roundImage_mBorderColor,Color.TRANSPARENT);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (defaultWidth == 0) {
      defaultWidth = getWidth();

    }
    if (defaultHeight == 0) {
      defaultHeight = getHeight();
    }

    int radius = 0;
    radius = (defaultWidth < defaultHeight ? defaultWidth
        : defaultHeight) / 2 - mBorderThickness;
    drawCircleBorder(canvas, radius + mBorderThickness / 2,mBorderColor);
  }


  private void drawCircleBorder(Canvas canvas, int radius, int color) {
    Paint paint = new Paint();
        /* 去锯齿 */
    paint.setAntiAlias(true);
    paint.setFilterBitmap(true);
    paint.setDither(true);
    paint.setColor(color);
        /* 设置paint的　style　为STROKE：空心 */
    paint.setStyle(Paint.Style.STROKE);
        /* 设置paint的外框宽度 */
    paint.setStrokeWidth(mBorderThickness);
    canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
  }
}
