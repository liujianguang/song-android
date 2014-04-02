package com.song1.musicno1.models;

/**
 * Created with IntelliJ IDEA.
 * User: SV
 * Date: 13-6-17
 * Time: 下午1:26
 * To change this template use File | Settings | File Templates.
 */
/*
结果码	结果描述
-1      未知错误
000000	返回结果成功。
555001	HTTP调用失败
555002	HTTP返回错误代码
666001  解析错误
*/

public class BeetleException extends Exception{
  protected int mCode;
  protected String mMsg;

  public BeetleException(String code, String msg)
  {
    try {
      mCode = Integer.parseInt(code);
    }catch (Exception e)
    {
      mCode = -1;
    }
    mMsg = msg;
  }

  public BeetleException(int code, String msg)
  {
    mCode = code;
    mMsg = msg;
  }

  public int getCode()
  {
    return mCode;
  }

  public String getMsg()
  {
    return mMsg;
  }
}
