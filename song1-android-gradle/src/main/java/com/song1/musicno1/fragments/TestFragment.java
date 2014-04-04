package com.song1.musicno1.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.DataAdapter;

import java.util.List;
import java.util.Random;

/**
 * Created by windless on 3/28/14.
 */
public class TestFragment extends DataFragment<Integer> {
  @Override
  protected List<Integer> onLoad(int loadPage) {
    setTotalPage(10);
    List<Integer> list = Lists.newArrayList();
    for (int i = 0; i < 20; i++) {
      list.add(loadPage * 100 + i);
    }
    try {
      Thread.sleep(5000);
    } catch (InterruptedException ignored) {
    }

    Random random = new Random(System.currentTimeMillis());
    int num = random.nextInt(2);
    if (num % 2 == 0) {
      return list;
    }

    return null;
  }

  @Override
  protected DataAdapter<Integer> newAdapter() {
    return new Adapter(getActivity());
  }

  class Adapter extends DataAdapter<Integer> {

    public Adapter(Context context) {
      super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      if (view == null) {
        view = View.inflate(getActivity(), R.layout.item_text, null);
      }
      TextView title = (TextView) view.findViewById(R.id.title);
      title.setText("" + getDataItem(i));
      return view;
    }
  }
}


