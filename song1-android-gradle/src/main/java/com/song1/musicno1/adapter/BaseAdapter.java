package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by windless on 3/27/14.
 */
public class BaseAdapter<E, H extends BaseAdapter.ViewHolder> extends android.widget.BaseAdapter {
  protected final Context context;
  protected final int     layoutId;

  private List<E>           list;
  private InflateProc<H>    inflateProc;
  private SetDataProc<E, H> setDataProc;

  public BaseAdapter(Context context, int layoutId) {
    this.context = context;
    this.layoutId = layoutId;
  }

  public static <E, H extends ViewHolder> BaseAdapter<E, H> newAdapter(Context context, int layoutId) {
    return new BaseAdapter<E, H>(context, layoutId);
  }

  @Override
  public int getCount() {
    return list == null ? 0 : list.size();
  }

  public BaseAdapter<E, H> bind(InflateProc<H> proc) {
    inflateProc = proc;
    return this;
  }

  public E getElement(int i) {
    return list.get(i);
  }

  public List<E> getList() {
    return list;
  }

  public void setList(List<E> list) {
    this.list = list;
  }

  @Override
  public Object getItem(int i) {
    return list.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  public BaseAdapter<E, H> setData(SetDataProc<E, H> proc) {
    setDataProc = proc;
    return this;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    H holder;
    if (view == null) {
      view = View.inflate(context, layoutId, null);
      holder = inflateProc.call();

      holder.inject(view);
      view.setTag(holder);
    } else {
      holder = (H) view.getTag();
    }

    E element = getElement(i);
    try {
      setDataProc.call(i, element, holder);
    } catch (IllegalStateException ignored) {
      // 处理退出应用后 not attached to Activity 的问题
    }
    return view;
  }

  public interface InflateProc<H> {
    public H call();
  }

  public interface SetDataProc<E, H> {
    public void call(int index, E element, H holder);
  }

  public static abstract class ViewHolder {
    abstract public void inject(View view);
  }
}
