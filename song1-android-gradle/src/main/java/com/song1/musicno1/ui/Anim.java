package com.song1.musicno1.ui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by kate on 14-3-20.
 */
public class Anim {

  private static final int DURATION     = 500;
  private static final int START_OFFSET = 100;

  public static void alphaAnim(View view, Animation.AnimationListener animationListener, float from, float to) {
    AlphaAnimation animation = new AlphaAnimation(from, to);
    animation.setDuration(DURATION);
    animation.setStartOffset(START_OFFSET);
    animation.setFillAfter(true);
    animation.setAnimationListener(animationListener);
    view.startAnimation(animation);
  }

  //float fromYDelta 动画开始的点离当前View Y坐标上的差值
  //float toYDelta   动画结束的点离当前View Y坐标上的差值
  public static void translateY(View view, Animation.AnimationListener animationListener, float fromYDelta, float toYDelta) {
//    System.out.println(" y1 : " + fromYDelta);
//    System.out.println(" y2 : " + toYDelta);
    TranslateAnimation animation = new TranslateAnimation(0, 0, fromYDelta, toYDelta);
    animation.setDuration(DURATION);
    animation.setStartOffset(START_OFFSET);
    animation.setAnimationListener(animationListener);
    view.startAnimation(animation);
  }


}
