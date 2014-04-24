package com.song1.musicno1.ui;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import com.song1.musicno1.R;
import org.w3c.dom.Text;

/**
 * Created by leovo on 2014/4/19.
 */
public class ArrowView extends TextView {


  public ArrowView(Context context) {
    super(context);
  }

  public ArrowView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  View firstView;
  float topX        = 50;
  int   topY        = 0;
  int   w           = 16;
  int   h           = 10;
  int   borderWidth = 2;

  protected void onDraw(Canvas canvas) {
    // TODO Auto-generated method stub
    System.out.println("onDraw...");
    super.onDraw(canvas); /*设置背景为白色*/
    if (firstView != null) {
      topX = firstView.getWidth() / 2 + firstView.getX();
      firstView = null;
    }
    //canvas.drawColor(getResources().getColor(R.color.title_color));
    Paint paint = new Paint(); /*去锯齿*/
    paint.setAntiAlias(true); /*设置paint的颜色*/
    paint.setColor(Color.WHITE); /*设置paint的 style 为STROKE：空心*/
    paint.setStyle(Paint.Style.STROKE); /*设置paint的外框宽度*/
    paint.setStrokeWidth(borderWidth); /*画一个空心三角形*/
    Path path = new Path();

    path.moveTo(topX, topY);
    path.lineTo(topX - (w / 2), topY + h);
    path.moveTo(topX, topY);
    path.lineTo(topX + (w / 2), topY + h);
    path.close();
    //canvas.drawColor(getResources().getColor(R.color.content_color));
    canvas.drawPath(path, paint); /*设置paint 的style为 FILL：实心*/

    paint.setStrokeWidth(1);
    paint.setStyle(Paint.Style.FILL); /*设置paint的颜色*/
    paint.setColor(getResources().getColor(R.color.green)); /*画一个实心三角形*/
    Path path2 = new Path();

    path2.moveTo(topX, topY + borderWidth / 2);
    path2.lineTo(topX - (w / 2) + borderWidth / 2, topY + h + borderWidth);
    path2.moveTo(topX, topY + borderWidth / 2);
    path2.lineTo(topX + (w / 2) - borderWidth / 2, topY + h + borderWidth);
    path2.lineTo(topX - (w / 2) + borderWidth / 2, topY + h + borderWidth);
    path2.close();
    canvas.drawPath(path2, paint);

    int width = getWidth();
    int height = getHeight();

    Paint linePaint = new Paint();
    linePaint.setStrokeWidth(borderWidth / 2);
    linePaint.setColor(Color.WHITE);
    canvas.drawLine(0, topY + h, topX - (w / 2), topY + h, linePaint);
    canvas.drawLine(topX + (w / 2), topY + h, width, topY + h, linePaint);
  }

  public void setFristPoint(View view) {
    this.firstView = view;
  }

  float newTopx;
  int   duration = 500;
  int   times    = 100;
  float interval = 0;

  public void move(View view) {
    this.newTopx = view.getWidth() / 2 + view.getX();
    interval = (newTopx - topX) / times;
    synchronized (runnable) {
      new Thread(runnable).start();
    }
  }

  Runnable runnable = new Runnable() {
    @Override
    public void run() {
      for (int i = 0; i < times; i++) {
        topX += interval;
        handler.post(invalidate);
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  };

  Handler  handler    = new Handler();
  Runnable invalidate = new Runnable() {
    @Override
    public void run() {
      invalidate();
    }
  };
}
