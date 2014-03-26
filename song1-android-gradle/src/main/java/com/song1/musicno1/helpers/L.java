package com.song1.musicno1.helpers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by windless on 3/25/14.
 */
public class L<E> implements Iterable<E> {
  private final List<E> list;

  private L() {
    list = Lists.newArrayList();
  }

  private L(E[] elements) {
    list = Lists.newArrayList(elements);
  }

  public static <E> L<E> newList() {
    return new L<>();
  }

  public static <E> L<E> newList(E... elements) {
    return new L<>(elements);
  }

  public L<E> add(E element) {
    list.add(element);
    return this;
  }

  public E get(int index) {
    return list.get(index);
  }

  public boolean remove(E element) {
    return list.remove(element);
  }

  public E remove(int index) {
    return list.remove(index);
  }

  public L<E> addAll(Collection<E> elements) {
    list.addAll(elements);
    return this;
  }

  public L<E> each(Block<E> block) {
    for (E e : list) {
      block.call(e);
    }
    return this;
  }

  public <T> L<T> map(Function<E, T> function) {
    L<T> newList = new L<>();
    for (E e : list) {
      newList.add(function.apply(e));
    }
    return newList;
  }

  public L<E> select(Predicate<E> predicate) {
    L<E> newList = new L<>();
    for (E e : list) {
      if (predicate.apply(e)) {
        newList.add(e);
      }
    }
    return newList;
  }

  public L<E> reject(Predicate<E> predicate) {
    L<E> newList = new L<>();
    for (E e : list) {
      if (!predicate.apply(e)) {
        newList.add(e);
      }
    }
    return newList;
  }

  @Override
  public Iterator<E> iterator() {
    return list.iterator();
  }

  public interface Block<E> {
    public void call(E e);
  }
}
