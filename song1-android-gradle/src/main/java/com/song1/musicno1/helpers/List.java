package com.song1.musicno1.helpers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.util.ArrayList;

/**
 * Created by windless on 3/27/14.
 */
public class List<E> extends ArrayList<E> {
  public static <E> List<E> newList() {
    return new List<>();
  }

  public List<E> each(Block<E> block) {
    for (E e : this) {
      block.call(e);
    }
    return this;
  }

  public <T> List<T> map(Function<E, T> function) {
    List<T> newList = List.newList();
    for (E e : this) {
      newList.add(function.apply(e));
    }
    return newList;
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

  public List<E> select(Predicate<E> predicate) {
    List<E> newList = List.newList();
    for (E e : this) {
      if (predicate.apply(e)) {
        newList.add(e);
      }
    }
    return newList;
  }

  public List<E> reject(Predicate<E> predicate) {
    List<E> newList = List.newList();
    for (E e : this) {
      if (!predicate.apply(e)) {
        newList.add(e);
      }
    }
    return newList;
  }

  public interface Block<E> {
    void call(E e);
  }
}
