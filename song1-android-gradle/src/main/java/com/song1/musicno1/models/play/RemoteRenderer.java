package com.song1.musicno1.models.play;

import com.google.common.base.Strings;
import de.akquinet.android.androlog.Log;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.std.av.renderer.AVTransport;

/**
 * Created by windless on 3/26/14.
 */
public class RemoteRenderer implements Renderer {
  private final Device  device;
  private final Service av;

  public RemoteRenderer(Device device) {
    this.device = device;
    av = device.getService(AVTransport.SERVICE_TYPE);
  }

  @Override
  public void stop() throws RendererException {
    postAction(AVTransport.STOP);
  }

  @Override
  public String getName() {
    return device.getFriendlyName();
  }

  @Override
  public String getId() {
    return device.getUDN();
  }

  @Override
  public void setUri(String uri) throws RendererException {
    postAction(AVTransport.SETAVTRANSPORTURI, (action) -> {
      action.setArgumentValue(AVTransport.CURRENTURI, uri);
    });
  }

  @Override
  public void play() throws RendererException {
    postAction(AVTransport.PLAY, (action) -> {
      action.setArgumentValue(AVTransport.SPEED, "1");
    });
  }

  @Override
  public void pause() throws RendererException {
    postAction(AVTransport.PAUSE);
  }

  @Override
  public boolean isPlaying() throws RendererException {
    Action getInfo = postAction(AVTransport.GETTRANSPORTINFO);
    Argument state = getInfo.getArgument(AVTransport.CURRENTTRANSPORTSTATE);
    if (state != null) {
      Log.d(this, "Renderer state: " + state.getValue());
      return AVTransport.PLAYING.equals(state.getValue());
    } else {
      Log.d(this, "Renderer state: get state failed");
    }
    return false;
  }

  @Override
  public PositionInfo getPositionInfo() throws RendererException {
    Action getInfo = postAction(AVTransport.GETPOSITIONINFO);
    Argument trackUri = getInfo.getArgument(AVTransport.TRACKURI);
    Argument duration = getInfo.getArgument(AVTransport.TRACKDURATION);
    Argument position = getInfo.getArgument(AVTransport.RELTIME);

    checkArgument(trackUri);
    checkArgument(duration);
    checkArgument(position);

    PositionInfo positionInfo = new PositionInfo();
    positionInfo.setDurationStr(duration.getValue());
    positionInfo.setUri(trackUri.getValue());
    positionInfo.setPositionStr(position.getValue());
    return positionInfo;
  }

  private void checkArgument(Argument argument) throws RendererException {
    if (argument == null || Strings.isNullOrEmpty(argument.getValue())) {
      throw new RendererException(RendererException.NO_ARGUMENT);
    }
  }

  private Action postAction(String actionName) throws RendererException {
    return postAction(actionName, null);
  }

  private Action postAction(String actionName, ActionProc proc) throws RendererException {
    if (av == null) {
      throw new RendererException(RendererException.NO_SERVICE);
    }

    Action action = av.getAction(actionName);
    if (action == null) {
      throw new RendererException(RendererException.NO_ACTION);
    }

    action.setArgumentValue(AVTransport.INSTANCEID, "0");

    if (proc != null) proc.call(action);

    if (!action.postControlAction()) {
      throw new RendererException(RendererException.POST_ACTION_FAILED);
    }
    return action;
  }

  private interface ActionProc {
    public void call(Action action);
  }
}
