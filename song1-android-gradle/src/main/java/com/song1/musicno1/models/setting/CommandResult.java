package com.song1.musicno1.models.setting;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: windless
 * Date: 13-6-25
 * Time: AM11:40
 */
public class CommandResult {
  private boolean isFailed = false;
  private short             wCmdSeq;
  private short             wErrorCode;
  private short             wParamNum;
  private ArrayList<String> results;

  public CommandResult() {
  }

  public boolean isFailed() {
    return isFailed;
  }

  public int getInt(int i) {
    return 0;
  }

  public void setBytes(byte[] bytes) {
    wCmdSeq = getShortFromByte(bytes, 4);
    wErrorCode = getShortFromByte(bytes, 6);
    wParamNum = getShortFromByte(bytes, 8);

    if (wErrorCode != 100) {
      fail("error code: %d", wErrorCode);
      return;
    }

    int param_index = 10;
    results = Lists.newArrayList();

    for (short i = 0; i < wParamNum; i++) {
      short param_len = getShortFromByte(bytes, param_index);
      param_index += 2;

      byte[] value_bytes = Arrays.copyOfRange(bytes, param_index, param_index + param_len);
      String value = new String(value_bytes);
      results.add(value);

      param_index += param_len;
    }
  }

  public void fail(String msg, Object... params) {
//    LogHelper.e(msg, params);
    isFailed = true;
  }

  public Optional<String> getString(int i) {
    String s = Iterables.get(results, i, null);
    return Optional.fromNullable(s);
  }

  private short getShortFromByte(byte[] ret, int startIndex) {
    byte[] buf = new byte[2];
    buf[0] = ret[startIndex];
    buf[1] = ret[startIndex + 1];

    return (short) (buf[1] & 0xff | (buf[0] & 0xff) << 8);
  }

  public int size() {
    return Iterables.size(results);
  }
}
