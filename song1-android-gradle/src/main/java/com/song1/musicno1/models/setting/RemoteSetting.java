package com.song1.musicno1.models.setting;

import com.google.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * User: windless
 * Date: 13-5-14
 * Time: 上午11:21
 */
public class RemoteSetting {
  private final int PORT = 8500;
  private final String address;

  public RemoteSetting(String address) {
    this.address = address;
  }

  public int getDefaultMode() {
    SettingCommand command = new SettingCommand(SettingCommand.GET_DEFAULT_MODE);
    CommandResult result = execute(command);
    if (result.isFailed()) {
      return -1;
    } else {
      return result.getInt(0);
    }
  }

  private CommandResult execute(SettingCommand command) {
    CommandResult result = new CommandResult();

    OutputStream output = null;
    InputStream input = null;

    try {
      Socket socket = new Socket(address, PORT);
      socket.setSoTimeout(1000);
      output = socket.getOutputStream();
      output.write(command.toBytes());
      output.flush();

      input = socket.getInputStream();

      byte[] readBuffer = new byte[256];
      int readCount = 0;
      for (int retryCount = 0; retryCount < 5 && readCount <= 0; readCount++) {
        readCount = input.read(readBuffer, 0, 256);
      }

      if (readCount == 0) {
        result.fail("Execute command %d failed: read buffer failed", command.getCode());
      } else {
        result.setBytes(readBuffer);
      }

    } catch (IOException e) {
      result.fail("Execute command %d failed: %s", command.getCode(), e);
    } finally {
      if (output != null) try {
        output.close();
      } catch (IOException e) {
      }
      if (input != null) try {
        input.close();
      } catch (IOException e) {
      }
    }
    return result;
  }

  public boolean setDefaultMode(boolean isAp) {
    SettingCommand command = new SettingCommand(SettingCommand.SET_DEFAULT_MODE);
    command.addParam(isAp);
    return !execute(command).isFailed();
  }

  public int getCurrentMode() {
    SettingCommand command = new SettingCommand(SettingCommand.GET_MODE);
    CommandResult result = execute(command);
    if (result.isFailed()) {
      return -1;
    } else {
      return result.getInt(0);
    }
  }

  public boolean setCurrentMode(boolean isAp) {
    SettingCommand command = new SettingCommand(SettingCommand.SET_MODE);
    command.addParam(isAp);
    return !execute(command).isFailed();
  }

  public boolean setSsidAndPass(String ssid, String pass) {
    SettingCommand command = new SettingCommand(SettingCommand.SET_SSID_AND_PASS);
    try {
      command.addParam(ssid);
      command.addParam(pass);
    } catch (UnsupportedEncodingException e) {
//      LogHelper.e("Set ssid and pass failed: %s", e);
      System.out.println(String.format("Set ssid and pass failed: %s", e));
      return false;
    }
    return !execute(command).isFailed();
  }

  // Name should not contain '-'
  public boolean setDeviceName(String name) {
    if (name.contains("-"))
      throw new IllegalArgumentException("非法的设备名，请勿包含 '-' 符号");
    SettingCommand command = new SettingCommand(SettingCommand.SET_NAME);
    try {
      command.addParam(name);
    } catch (UnsupportedEncodingException e) {
//      LogHelper.e("Set device name failed: %s", e);
      System.out.println(String.format("Set device name failed: %s", e));
      return false;
    }
    return !execute(command).isFailed();
  }

  public Optional<String> getDeviceName() {
    SettingCommand command = new SettingCommand(SettingCommand.GET_NAME);
    CommandResult result = execute(command);
    if (result.isFailed()) {
      return Optional.absent();
    } else {
      return result.getString(0);
    }
  }

  public Optional<String> getVersion() {
    SettingCommand command = new SettingCommand(SettingCommand.GET_VERSION);
    CommandResult result = execute(command);
    if (result.isFailed()) {
      return Optional.absent();
    } else {
      return result.getString(0);
    }
  }

  public boolean upgrade(String packagePath) {
    SettingCommand command = new SettingCommand(SettingCommand.UPGRADE);
    return !execute(command).isFailed();
  }

  public Optional<DevicePreference> all_preferences() {
    SettingCommand command = new SettingCommand(SettingCommand.GET_ALL);
    CommandResult result = execute(command);
    if (result.isFailed()) {
      return Optional.absent();
    } else {
      return DevicePreference.from(result);
    }
  }
}
