package com.song1.musicno1.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by kate on 14-3-19.
 */
public class SlingUpLayout extends RelativeLayout implements Animation.AnimationListener {

  View bgView;
  View contentView;

  boolean isCreated = false;
  boolean isExpand  = false;

  float toYDelta = 0;
  SlingUpListener listener;

  public void setListener(SlingUpListener listener) {
    this.listener = listener;
  }

  public interface SlingUpListener {
    void expandFinish();

    void collapseFinish();
  }

  public SlingUpLayout(Context context) {
    super(context);
  }

  public SlingUpLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int paddingLeft = getPaddingLeft();
    int paddingTop = getPaddingTop();

    final int childCount = getChildCount();
//    System.out.println("childCount : " + childCount);
    if (childCount != 2) {
      exception("child must be two!");
    }

    bgView = getChildAt(0);
    contentView = getChildAt(1);
    for (int i = 0; i < childCount; i++) {
      final View child = getChildAt(i);
      if (i == 1) {
        paddingTop += getMeasuredHeight();
      }
      int childHeight = child.getMeasuredHeight();
      int childWidth = child.getMeasuredWidth();
      final int childTop = paddingTop;
      final int childBottom = childTop + childHeight;
      final int childLeft = paddingLeft;
      final int childRight = childLeft + childWidth;
      child.layout(childLeft, childTop, childRight, childBottom);
    }
    isCreated = true;
  }

  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      toYDelta = -contentView.getMeasuredHeight();
      Anim.alphaAnim(bgView, SlingUpLayout.this, 0.0f, 0.5f);
      Anim.translateY(contentView, SlingUpLayout.this, 0, toYDelta);
    }
  };

  public void expand() {
    isExpand = true;
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          if (isCreated)
            break;
        }
        handler.sendEmptyMessage(0);
      }
    }).start();
  }

  public void collapse() {
    isExpand = false;
    toYDelta = contentView.getMeasuredHeight();
    Anim.alphaAnim(bgView, SlingUpLayout.this, 0.5f, 0.0f);
    Anim.translateY(contentView, this, 0, toYDelta);
  }

  @Override
  public void onAnimationStart(Animation animation) {
    if (animation instanceof AlphaAnimation) {
      bgView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onAnimationEnd(Animation animation) {
    if (animation instanceof TranslateAnimation) {
      int left = contentView.getLeft();
      int top = contentView.getTop() + (int) toYDelta;
//      System.out.println("left : " + left);
//      System.out.println("top :  " + top);
      contentView.clearAnimation();
      contentView.setX(left);
      contentView.setY(top);
      contentView.requestLayout();
      if (listener != null) {
        if (isExpand) {
          listener.expandFinish();
        } else {
          listener.collapseFinish();
        }
      }
    }
  }

  @Override
  public void onAnimationRepeat(Animation animation) {

  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    System.out.println("onTouchEvent...");
    super.onTouchEvent(event);
    float x = event.getX();
    float y = event.getY();
    float left = contentView.getX();
    float top = contentView.getY();
    float right = left + contentView.getMeasuredWidth();
    float bottom = top + contentView.getMeasuredHeight();
//    System.out.println(" x: " + x + "," + " y: " + y);
//    System.out.println("l : " + left + ",t : " + top + ",right : " + right + ",bottom : " + bottom);
    if ((x >= left && x <= right) && (y >= top && y <= bottom)) {
      return false;
    } else {
      collapse();
      return true;
    }
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
//    System.out.println("onKeyUp...");
    boolean b = super.onKeyUp(keyCode, event);
//    System.out.println("b :  " + b);
    return b;
  }

  public View getContentView() {
    return contentView;
  }

  private void exception(String msg) {
    try {
      throw new Exception(msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
