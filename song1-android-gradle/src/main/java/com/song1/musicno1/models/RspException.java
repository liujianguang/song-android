package com.song1.musicno1.models;

/**
 * User: amongothers
 * Date: 13-6-12
 * Time: 下午9:01
 */
public class RspException extends Exception {

  public static class SyntaxException extends RspException {

    Class _class;
    String rspStr;

    public SyntaxException(Class _class, String str) {
      this._class = _class;
      rspStr = str;
    }

    public String getRspStr() {
      return rspStr;
    }

    @Override
    public String toString() {
      return String.format("SyntaxError when parsing '%s' as %s", rspStr, _class.getSimpleName());
    }
  }

  public static class Not200Exception extends RspException {

    private int httpCode;

    public Not200Exception(int code) {
      httpCode = code;
    }

    public int getHttpCode() {
      return httpCode;
    }

    @Override
    public String toString() {
      return String.format("Not200Exception errorcode: %d", httpCode);
    }
  }
}
