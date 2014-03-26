package com.song1.musicno1.models.setting;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * User: windless
 * Date: 13-6-25
 * Time: AM11:04
 */
public class SettingCommand {
  private static short sCmdSeq = 0;

  public static final short GET_DEFAULT_MODE  = 1;
  public static final short SET_DEFAULT_MODE  = 2;
  public static final short GET_MODE          = 3;
  public static final short SET_MODE          = 4;
  public static final short SET_SSID_AND_PASS = 5;
  public static final short GET_NAME          = 6;
  public static final short SET_NAME          = 7;
  public static final short GET_VERSION       = 8;
  public static final short UPGRADE           = 9;
  public static final short GET_SSID          = 12;
  public static final short GET_ALL           = 13;

  private final short cmdCode;
  private final short cmdSeq;
  private List<byte[]> params = new ArrayList<byte[]>();

  private static final int HEAD_BUF_LENGTH = 4;
  private static final int NUMBER_LENGTH   = 2;

  public SettingCommand(short cmdCode) {
    sCmdSeq++;
    this.cmdSeq = sCmdSeq;
    this.cmdCode = cmdCode;
  }

  public void addParam(String param) throws UnsupportedEncodingException {
    byte[] bytes = param.getBytes("UTF-8");
    params.add(bytes);
  }

  public void addParam(boolean param) {
    byte b = (byte) (param ? '0' : '1');
    params.add(new byte[]{ b });
  }

  public byte[] toBytes() {
    short cmdLength = NUMBER_LENGTH * 3;
    for (byte[] bytes : params) {
      cmdLength += 2 + bytes.length;
    }
    ByteBuffer byteBuffer = ByteBuffer.allocate(HEAD_BUF_LENGTH + cmdLength);
    // head_buf
    byteBuffer.put((byte) '1'); // head_buf cVersion
    byteBuffer.putShort(cmdLength); // head_buf wCmdLen
    byteBuffer.put(new byte[1]); // head_buf ext

    byteBuffer.putShort(cmdSeq); // wCmdSeq
    byteBuffer.putShort(cmdCode); // wCmdNum
    byteBuffer.putShort((short) params.size()); // wParamNum
    for (byte[] bytes : params) {
      byteBuffer.putShort((short) bytes.length); // wParamLen
      byteBuffer.put(bytes); // strParamValue
    }
    return byteBuffer.array();
  }

  public short getCode() {
    return cmdCode;
  }
}
