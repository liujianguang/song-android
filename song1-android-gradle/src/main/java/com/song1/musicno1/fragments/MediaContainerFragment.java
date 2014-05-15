package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.play.*;

import java.util.List;

/**
 * Created by windless on 14-4-8.
 */
public class MediaContainerFragment extends ListFragment<Object> implements AdapterView.OnItemClickListener {
  private final static String CONTAINER_ID = "container_id";
  private final static String TITLE        = "title";

  protected String      containerId;
  private   MediaServer mediaServer;

  public static MediaContainerFragment newInstance(String containerId, String title) {
    MediaContainerFragment mediaContainerFragment = new MediaContainerFragment();
    Bundle bundle = new Bundle();
    bundle.putString(CONTAINER_ID, containerId);
    bundle.putString(TITLE, title);
    mediaContainerFragment.setArguments(bundle);
    return mediaContainerFragment;
  }

  @Override
  protected List<Object> onLoad(int loadPage) {
    setTotalPage(1);
    return mediaServer.browse(containerId);
  }

  @Override
  protected DataAdapter<Object> newAdapter() {
    return new DataAdapter<Object>(getActivity()) {
      @Override
      public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
          view = View.inflate(getActivity(), R.layout.item_media_node, null);
          holder = new ViewHolder(view);
          view.setTag(holder);
        } else {
          holder = (ViewHolder) view.getTag();
        }

        Object node = getDataItem(i);
        if (node instanceof Audio) {
          Audio audio = (Audio) node;
          holder.title.setText(audio.getTitle());
        } else if (node instanceof MediaNode) {
          MediaNode mediaNode = (MediaNode) node;
          holder.title.setText(mediaNode.getTitle());
        }
        return view;
      }
    };
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle arguments = getArguments();
    if (arguments != null) {
      containerId = arguments.getString(CONTAINER_ID);
      setTitle(arguments.getString(TITLE));
    }
  }

  public void setMediaServer(MediaServer mediaServer) {
    this.mediaServer = mediaServer;
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Object node = getDataItem(i);
    if (node instanceof MediaNode) {
      MediaNode mediaNode = (MediaNode) node;
      MediaContainerFragment fragment = MediaContainerFragment.newInstance(mediaNode.getId(), mediaNode.getTitle());
      fragment.setMediaServer(mediaServer);
      ((MainActivity) getActivity()).push(MediaContainerFragment.class.getName(), fragment);
    } else if (node instanceof Audio) {
      List8<Audio> audios = List8.newList(getDataList())
          .select((object) -> object instanceof Audio)
          .map((object) -> (Audio) object);
      Audio audio = (Audio) node;
      Playlist playlist = new Playlist(audios, audio);
      Players.setPlaylist(playlist, getFragmentManager());
    }
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
