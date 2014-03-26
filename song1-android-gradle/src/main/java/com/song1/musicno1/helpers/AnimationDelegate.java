package com.song1.musicno1.helpers;

import android.view.View;
import android.view.animation.Interpolator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by windless on 3/25/14.
 */
public class AnimationDelegate implements Animator.AnimatorListener {
  private ViewPropertyAnimator animator;
  private Listener             onStart;
  private Listener             onEnd;
  private Listener             onCancel;
  private Listener             onRepeat;

  public AnimationDelegate(View view) {
    animator = ViewPropertyAnimator.animate(view);
    animator.setListener(this);
  }

  public static AnimationDelegate animate(View view) {
    return new AnimationDelegate(view);
  }

  public void cancel() {
    animator.cancel();
  }

  public AnimationDelegate xBy(float value) {
    animator.xBy(value);
    return this;
  }

  public AnimationDelegate rotationBy(float value) {
    animator.rotationBy(value);
    return this;
  }

  public AnimationDelegate rotationYBy(float value) {
    animator.rotationYBy(value);
    return this;
  }

  public AnimationDelegate scaleY(float value) {
    animator.scaleY(value);
    return this;
  }

  public AnimationDelegate rotationY(float value) {
    animator.rotationY(value);
    return this;
  }

  public AnimationDelegate x(float value) {
    animator.x(value);
    return this;
  }

  public AnimationDelegate rotation(float value) {
    animator.rotation(value);
    return this;
  }

  public AnimationDelegate y(float value) {
    animator.y(value);
    return this;
  }

  public long getStartDelay() {
    return animator.getStartDelay();
  }

  public AnimationDelegate setStartDelay(long startDelay) {
    animator.setStartDelay(startDelay);
    return this;
  }

  public AnimationDelegate translationXBy(float value) {
    animator.translationXBy(value);
    return this;
  }

  public AnimationDelegate alpha(float value) {
    animator.alpha(value);
    return this;
  }

  public AnimationDelegate rotationX(float value) {
    animator.rotationX(value);
    return this;
  }

  public AnimationDelegate translationY(float value) {
    animator.translationY(value);
    return this;
  }

  public AnimationDelegate translationYBy(float value) {
    animator.translationYBy(value);
    return this;
  }

  public AnimationDelegate scaleXBy(float value) {
    animator.scaleXBy(value);
    return this;
  }

  public AnimationDelegate scaleYBy(float value) {
    animator.scaleYBy(value);
    return this;
  }

  public AnimationDelegate yBy(float value) {
    animator.yBy(value);
    return this;
  }

  public AnimationDelegate setInterpolator(Interpolator interpolator) {
    animator.setInterpolator(interpolator);
    return this;
  }

  public void start() {
    animator.start();
  }

  public long getDuration() {
    return animator.getDuration();
  }

  public AnimationDelegate setDuration(long duration) {
    animator.setDuration(duration);
    return this;
  }

  public AnimationDelegate rotationXBy(float value) {
    animator.rotationXBy(value);
    return this;
  }

  public AnimationDelegate translationX(float value) {
    animator.translationX(value);
    return this;
  }

  public AnimationDelegate alphaBy(float value) {
    animator.alphaBy(value);
    return this;
  }

  public AnimationDelegate scaleX(float value) {
    animator.scaleX(value);
    return this;
  }

  @Override
  public void onAnimationStart(Animator animation) {
    if (onStart != null) {
      onStart.call(animation);
    }
  }

  @Override
  public void onAnimationEnd(Animator animation) {
    if (onEnd != null) {
      onEnd.call(animation);
    }
  }

  @Override
  public void onAnimationCancel(Animator animation) {
    if (onCancel != null) {
      onCancel.call(animation);
    }
  }

  @Override
  public void onAnimationRepeat(Animator animation) {
    if (onRepeat != null) {
      onRepeat.call(animation);
    }
  }

  public AnimationDelegate onStart(Listener listener) {
    onStart = listener;
    return this;
  }

  public AnimationDelegate onEnd(Listener listener) {
    onEnd = listener;
    return this;
  }

  public AnimationDelegate onCancel(Listener listener) {
    onCancel = listener;
    return this;
  }

  public AnimationDelegate onRepeat(Listener listener) {
    onRepeat = listener;
    return this;
  }

  public interface Listener {
    void call(Animator animator);
  }
}
