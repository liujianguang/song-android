package com.song1.musicno1.ui;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;

import javax.inject.Inject;

/**
 * User: windless
 * Date: 13-9-8
 * Time: PM5:44
 */
public class XMListView implements AbsListView.OnScrollListener {

  private Listener listener;

  @InjectView(R.id.loading)   View     loading;
  @InjectView(R.id.content)   View     content;
  @InjectView(R.id.error)     View     error;
  @InjectView(R.id.empty)     TextView empty;
  @InjectView(R.id.list)      ListView list;
  @InjectView(R.id.error_msg) TextView error_msg;

  @Inject
  public XMListView() {
  }

  public void view(View view) {
    ButterKnife.inject(this, view);
    list.setOnScrollListener(this);
  }

  public void show_loading() {
    loading.setVisibility(View.VISIBLE);
    content.setVisibility(View.GONE);
    error.setVisibility(View.GONE);
    empty.setVisibility(View.GONE);
  }

  public void show_content() {
    loading.setVisibility(View.GONE);
    content.setVisibility(View.VISIBLE);
    error.setVisibility(View.GONE);
    empty.setVisibility(View.GONE);
  }

  public void show_empty() {
    loading.setVisibility(View.GONE);
    content.setVisibility(View.GONE);
    error.setVisibility(View.GONE);
    empty.setVisibility(View.VISIBLE);
  }

  public void show_error() {
    loading.setVisibility(View.GONE);
    content.setVisibility(View.GONE);
    error.setVisibility(View.VISIBLE);
    empty.setVisibility(View.GONE);
  }

  public void adapter(ListAdapter adapter) {
    list.setAdapter(adapter);
  }

  public void listen_item_click(ListView.OnItemClickListener listener) {
    list.setOnItemClickListener(listener);
  }

  public void be_listened(Listener listener) {
    this.listener = listener;
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE) {
      if (list.getLastVisiblePosition() + 1 >= list.getCount() &&
          listener != null) {
        listener.on_load_more();
      }
    }
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
  }

  public Object item(int pos) {
    return list.getItemAtPosition(pos);
  }

  public ListView list_view() {
    return list;
  }

  public void show_loading_more() {
  }

  public void show_error_more() {
  }

  public void hide_loading_more() {
  }

  @OnClick(R.id.retry_btn)
  public void retry() {
    if (listener != null) listener.on_reload();
  }

  public interface Listener {
    void on_load_more();

    void on_reload();
  }
}
