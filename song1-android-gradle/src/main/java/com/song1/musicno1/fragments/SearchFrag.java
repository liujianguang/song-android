package com.song1.musicno1.fragments;

import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.common.base.Strings;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioAdapter;
import com.song1.musicno1.loader.MiguMusicLoaders;
import com.song1.musicno1.models.cmmusic.CMMusicStore;
import com.song1.musicno1.models.cmmusic.GetDownloadUrlCallback;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.ui.XMListView;

import javax.inject.Inject;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-9
 * Time: PM11:38
 */
public class SearchFrag extends BaseFragment implements TextWatcher, LoaderManager.LoaderCallbacks<Object>, AudioAdapter.MoreMenuListener, ListView.OnItemClickListener, XMListView.Listener, View.OnFocusChangeListener, View.OnTouchListener {
  XMListView       list_view;
  MiguMusicLoaders loaders;
  AudioAdapter     adapter;
  CMMusicStore     music_store;

  private EditText edit_view;
  private String   search;

  private boolean is_loading;

  private int page = 1;

  public SearchFrag() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.endless_listview, container, false);
    list_view.view(view);
    return view;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    list_view = new XMListView();
    adapter = new AudioAdapter(getActivity());
    music_store = new CMMusicStore(getActivity());
    loaders = new MiguMusicLoaders(getActivity(),music_store);
    has_home_button(false);
  }

  @Override
  public void onStart() {
    super.onStart();
    ActionBar action_bar = getActivity().getActionBar();
    action_bar.setCustomView(R.layout.search_view);
    action_bar.setDisplayShowCustomEnabled(true);

    edit_view = (EditText) action_bar.getCustomView().findViewById(R.id.search);
    edit_view.addTextChangedListener(this);
    edit_view.requestFocus();
    edit_view.setOnFocusChangeListener(this);
    edit_view.setText(search);
    edit_view.setHint(R.string.input_song_title_or_artist_name);

    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    manager.showSoftInput(edit_view, InputMethodManager.SHOW_IMPLICIT);
  }

  @Override
  public void onStop() {
    super.onStop();
    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    manager.hideSoftInputFromWindow(edit_view.getWindowToken(), 0);
    getActivity().getActionBar().setDisplayShowCustomEnabled(false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
//    player_action.context(getSherlockActivity());

    adapter.activity(getActivity());
    adapter.listen_menu_item_click(this);
    adapter.drop_down(R.menu.migu_audio_action);

    list_view.adapter(adapter);
    list_view.listen_item_click(this);
    list_view.be_listened(this);
    list_view.show_content();
    list_view.list_view().setOnTouchListener(this);


    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    if (edit_view.getText().toString().equals(search)) {
      return;
    }

    reset_list_view_position();

    list_view.list_view().setSelectionAfterHeaderView();
    list_view.show_loading();
    search = edit_view.getText().toString();
    adapter.clear();
    if (!Strings.isNullOrEmpty(search)) {
      getLoaderManager().restartLoader(0, null, this);
    } else {
      list_view.show_empty();
    }
  }

  private void reset_list_view_position() {
    ListView list = list_view.list_view();
    if (!list.isStackFromBottom()) {
      list.setStackFromBottom(true);
    }
    list.setStackFromBottom(false);
  }

  @Override
  public void afterTextChanged(Editable s) {
  }

  @Override
  public Loader<Object> onCreateLoader(int id, Bundle args) {
    is_loading = true;
    return loaders.search(search, page);
  }

  @Override
  public void onLoadFinished(Loader<Object> loader, Object data) {
    if (data != null) {
      List<Audio> audios = (List<Audio>) data;
      adapter.add_audios(audios);
      if (adapter.getCount() == 0) {
        list_view.show_empty();
      } else {
        list_view.show_content();
      }
    } else {
      if (adapter.getCount() == 0) {
        list_view.show_empty();
      }
    }
    is_loading = false;
  }

  @Override
  public void onLoaderReset(Loader<Object> loader) {
  }

  @Override
  public void on_menu_item_click(int menu_item_id, Audio audio) {
    switch (menu_item_id) {
      case R.id.add_to_playlist:
//        player_action.add_to_playlist(audio);
        break;
      case R.id.download:
        start_download_task(audio);
    }
  }

  private void start_download_task(final Audio audio) {
//    music_store.download_url(getActivity(), audio, new GetDownloadUrlCallback() {
//      @Override
//      public void OnUrlGot(String code, String msg, String url) {
//        if (url != null) {
//          audio.download_url = url;
//          try {
//            DownloadManager.Request request = downloadStore.newRequest(Uri.parse(url), audio.title);
//            downloadStore.start(getSherlockActivity(), request, audio.title);
//          } catch (IllegalStateException e) {
//            Toast.makeText(getSherlockActivity(), getString(R.string.download_failed_no_sdcard, audio.title), Toast.LENGTH_LONG).show();
//          }
//        }
//      }
//    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Audio audio = (Audio) list_view.item(position);
    Toast.makeText(getActivity(), getString(R.string.start_playing, audio.getTitle()), Toast.LENGTH_SHORT).show();
//    player_action.play(audio);
  }

  @Override
  public void on_load_more() {
    if (!is_loading) {
      page++;
      getLoaderManager().restartLoader(0, null, this);
    }
  }

  @Override
  public void on_reload() {
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
      if (manager.isActive()) {
        manager.hideSoftInputFromWindow(edit_view.getWindowToken(), 0);
      }
    }
    return false;
  }
}
