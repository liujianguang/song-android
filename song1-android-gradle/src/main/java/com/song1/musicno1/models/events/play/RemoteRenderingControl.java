package com.song1.musicno1.models.events.play;

import com.song1.musicno1.models.play.RendererException;
import com.song1.musicno1.models.play.RenderingControl;
import com.song1.musicno1.models.play.Volume;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;

/**
 * Created by windless on 14-4-9.
 */
public class RemoteRenderingControl implements RenderingControl {
  protected final Service renderingControl;

  public RemoteRenderingControl(Device device) {
    renderingControl = device.getService(org.cybergarage.upnp.std.av.renderer.RenderingControl.SERVICE_TYPE);
  }

  @Override
  public Volume getVolume() throws RendererException {
    if (renderingControl == null) throw new RendererException("");

    Action action = renderingControl.getAction(org.cybergarage.upnp.std.av.renderer.RenderingControl.GETVOLUME);
    if (action == null) throw new RendererException("");

    action.setArgumentValue(org.cybergarage.upnp.std.av.renderer.RenderingControl.INSTANCEID, "0");
    action.setArgumentValue(org.cybergarage.upnp.std.av.renderer.RenderingControl.CHANNEL, "Master");

    if (!action.postControlAction()) throw new RendererException("");

    Argument argument = action.getArgument(org.cybergarage.upnp.std.av.renderer.RenderingControl.CURRENTVOLUME);
    if (argument == null) throw new RendererException("");

    return new Volume(argument.getIntegerValue(), 100);
  }

  @Override
  public void setVolume(int volume) throws RendererException {
    if (renderingControl == null) throw new RendererException("");

    Action action = renderingControl.getAction(org.cybergarage.upnp.std.av.renderer.RenderingControl.SETVOLUME);
    if (action == null) throw new RendererException("");

    action.setArgumentValue(org.cybergarage.upnp.std.av.renderer.RenderingControl.INSTANCEID, "0");
    action.setArgumentValue(org.cybergarage.upnp.std.av.renderer.RenderingControl.CHANNEL, "Master");
    action.setArgumentValue(org.cybergarage.upnp.std.av.renderer.RenderingControl.DESIREDVOLUME, volume);

    if (!action.postControlAction()) throw new RendererException("");
  }

  @Override
  public void volumeUp() throws RendererException {
    Volume volume = getVolume();
    int setVolume = volume.getCurrent() + 3;
    if (setVolume > volume.getMax()) {
      setVolume = volume.getMax();
    }
    setVolume(setVolume);
  }

  @Override
  public void volumeDown() throws RendererException {
    Volume volume = getVolume();
    int setVolume = volume.getCurrent() - 3;
    if (setVolume < 0) {
      setVolume = 0;
    }
    setVolume(setVolume);
  }
}
