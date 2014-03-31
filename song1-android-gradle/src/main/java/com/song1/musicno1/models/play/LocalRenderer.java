package com.song1.musicno1.models.play;

import android.content.Context;
import android.media.MediaPlayer;
import com.song1.musicno1.R;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by windless on 3/31/14.
 */
public class LocalRenderer implements Renderer {
  protected final MediaPlayer mediaPlayer;
  protected final Context     context;
  protected       String      currentUri;

  @Inject
  public LocalRenderer(Context context) {
    mediaPlayer = new MediaPlayer();
    this.context = context;
  }

  @Override
  public void setUri(String uri) throws RendererException {
    currentUri = uri;
    try {
      mediaPlayer.reset();
      mediaPlayer.setDataSource(uri);
      mediaPlayer.prepare();
    } catch (IOException e) {
      throw new RendererException("");
    }
  }

  @Override
  public void play() throws RendererException {
    mediaPlayer.start();
  }

  @Override
  public void pause() throws RendererException {
    mediaPlayer.pause();
  }

  @Override
  public boolean isPlaying() throws RendererException {
    return mediaPlayer.isPlaying();
  }

  @Override
  public PositionInfo getPositionInfo() throws RendererException {
    PositionInfo info = new PositionInfo();
    info.setUri(currentUri);
    info.setPosition(mediaPlayer.getCurrentPosition());
    info.setDuration(mediaPlayer.getDuration());
    return info;
  }

  @Override
  public void stop() throws RendererException {
    mediaPlayer.stop();
    mediaPlayer.reset();
  }

  @Override
  public String getName() {
    return context.getString(R.string.this_phone);
  }

  @Override
  public String getId() {
    return "0";
  }

  @Override
  public void seek(int seekTo) throws RendererException {
    mediaPlayer.seekTo(seekTo);
    mediaPlayer.start();
  }
}
