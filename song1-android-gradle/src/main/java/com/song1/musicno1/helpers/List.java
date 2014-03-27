package com.song1.musicno1.helpers;

import com.google.common.base.Predicate;

import java.util.ArrayList;

/**
 * Created by windless on 3/27/14.
 */
public class List<E> extends ArrayList<E> {
  public static <E> List<E> newList() {
    return new List<>();
  }

  public List<E> deleteIf(Predicate<E> predicate) {
    for (E e : this) {
      if (predicate.apply(e)) {
        remove(e);
        return this;
      }
    }
    return this;
  }
}
