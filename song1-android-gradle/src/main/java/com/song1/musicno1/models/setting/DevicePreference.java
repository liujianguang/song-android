package com.song1.musicno1.models.setting;

import android.content.Context;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * User: windless
 * Date: 13-10-8
 * Time: PM3:13
 */
public class DevicePreference {
  private String mode;
  private String name;
  private String version;
  private String ssid;

  public static Optional<DevicePreference> from(CommandResult result) {
    if (is_ok(result)) {
      DevicePreference preference = new DevicePreference();
      preference.mode(result.getString(1).or("").trim());
      preference.name(result.getString(3).or("").trim());
      preference.version(result.getString(4).or("").trim());
      preference.ssid(result.getString(5).or("").trim());
      return Optional.of(preference);
    } else {
      return Optional.absent();
    }
  }

  private void ssid(String ssid) {
    this.ssid = ssid;
  }

  private void version(String version) {
    this.version = version;
  }

  private void name(String name) {
    this.name = name;
  }

  private void mode(String mode) {
    this.mode = mode;
  }

  private static boolean is_ok(CommandResult result) {
    return result.size() == 6;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("name", name)
        .add("mode", mode)
        .add("version", version)
        .add("ssid", ssid)
        .toString();
  }

  public String name() {
    return name;
  }

  public String version() {
    return version;
  }

  public String mode_str(Context context) {
    return mode_str(context, this.mode);
  }

  public String mode() {
    return this.mode;
  }

  public String mode_str(Context context, String mode) {
    if ("0".equals(mode)) {
//      return context.getString(R.string.direct_connect);
    } else {
//      return context.getString(R.string.network);
    }
    return "";
  }

  public String ssid() {
    return ssid;
  }
}
