package com.song1.musicno1.helpers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by windless on 3/27/14.
 */
public class List8<E> extends ArrayList<E> {
  public static <E> List8<E> newList() {
    return new List8<>();
  }

  public static <E> List8<E> newList(List<E> elements) {
    List8<E> list = new List8<>();
    list.addAll(elements);
    return list;
  }

  public static <E> List8<E> newList(E... elements) {
    List8<E> list = new List8<>();
    list.addAll(Lists.newArrayList(elements));
    return list;
  }

  public List8<E> each(Block<E> block) {
    for (E e : this) {
      block.call(e);
    }
    return this;
  }

  public <T> List8<T> map(Function<E, T> function) {
    List8<T> newList = List8.newList();
    for (E e : this) {
      newList.add(function.apply(e));
    }
    return newList;
  }

  public List8<E> deleteIf(Predicate<E> predicate) {
    for (E e : this) {
      if (predicate.apply(e)) {
        remove(e);
        return this;
      }
    }
    return this;
  }

  public List8<E> select(Predicate<E> predicate) {
    List8<E> newList = List8.newList();
    for (E e : this) {
      if (predicate.apply(e)) {
        newList.add(e);
      }
    }
    return newList;
  }

  public List8<E> reject(Predicate<E> predicate) {
    List8<E> newList = List8.newList();
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
