package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.entity.Chart;
import com.song1.musicno1.fragments.base.BaseFragment;
import com.song1.musicno1.loader.MiguMusicLoaders;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.ui.XMListView;

import javax.inject.Inject;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-9
 * Time: PM4:56
 */
public class CloudAudiosFrag extends BaseFragment
    implements LoaderManager.LoaderCallbacks<Object>,
    XMListView.Listener,
    AudioAdapter.MoreMenuListener,
    ListView.OnItemClickListener {

  XMListView       list_view;
  MiguMusicLoaders migu_loaders;
  AudioAdapter     adapter;
//  @Inject PlayerAction     player_action;
//  @Inject CMMusicStore     music_store;
//  @Inject MainBus          bus;
//  @Inject DownloadStore    downloadStore;

  private Object loading_object;

  private int     page        = 1;
  private boolean is_finished = false;
  private boolean is_loading  = false;

  @Inject
  public CloudAudiosFrag() {
    App.inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.album_song_frag, container, false);
    list_view.view(view);
    // 去除全部播放，多选
//    Views.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
//    player_action.context(getSherlockActivity());


    list_view.adapter(adapter);
    list_view.be_listened(this);
    list_view.listen_item_click(this);

    adapter.drop_down(R.menu.migu_audio_action);
    adapter.listen_menu_item_click(this);
    adapter.activity(getActivity());

    getLoaderManager().initLoader(0, null, this);
  }

  public void load(Object loading_object) {
    this.loading_object = loading_object;
  }

  // 去除全部播放，多选
//  @OnClick(R.id.allaudioplay)
//  public void allAudiosPlay() {
//    YesOrNoDialog dialog = new YesOrNoDialog(new Callback<Boolean>() {
//      @Override
//      public void call(Boolean isOk) {
//        if (isOk) {
//          player_action.clearPlaylistAndPlay(adapter.getAudiosList());
//        } else {
//          player_action.playAudios(adapter.getAudiosList());
//        }
//      }
//    });
//    dialog.show(getSherlockActivity().getSupportFragmentManager(), null);
//  }
//
//  @OnClick(R.id.allaudioselect)
//  public void allAudiosSelect() {
//    List<Audio> audios = adapter.getAudiosList();
//    Intent intent = new Intent(getSherlockActivity(), MoreSongsActivity.class);
//    intent.putExtra(MoreSongsActivity.INTENT_KEY_STRING, audios.get(0).album);
//
//    intent.putParcelableArrayListExtra(MoreSongsActivity.INTENT_KEY_LIST, Lists.newArrayList(audios));
//    startActivity(intent);
//  }

  @Override
  public Loader<Object> onCreateLoader(int id, Bundle args) {
    is_loading = true;
    if (loading_object instanceof Artist) {
      Artist artist = (Artist) loading_object;
      return migu_loaders.audios(artist, page);
    } else if (loading_object instanceof Album) {
      Album album = (Album) loading_object;
      return migu_loaders.audios(album, page);
    } else if (loading_object instanceof Chart) {
      Chart chart = (Chart) loading_object;
      return migu_loaders.audios(chart, page);
    }
    return null;
  }

  @Override
  public void onLoadFinished(Loader<Object> loader, Object data) {
    if (!is_loading) {
      list_view.show_content();
      return;
    }

    if (data == null) {
      if (page == 1) {
        list_view.show_error();
      }
    } else {
      List<Audio> audios = (List<Audio>) data;
      if (audios.size() < 20) {
        is_finished = true;
      }
      adapter.add_audios(audios);
      list_view.show_content();
    }

    is_loading = false;
  }

  @Override
  public void onLoaderReset(Loader<Object> loader) {
  }

  @Override
  public void on_load_more() {
    if (!is_loading && !is_finished) {
      page++;
      getLoaderManager().restartLoader(0, null, this);
    }
  }

  @Override
  public void on_reload() {
    getLoaderManager().restartLoader(0, null, this);
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
//    if (!FileHelper.is_sd_ok()) {
//      Toast.makeText(getActivity(), getString(R.string.no_sd_card), Toast.LENGTH_SHORT).show();
//      return;
//    }

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
//    player_action.play(audio);
  }
}
