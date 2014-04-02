package com.song1.musicno1.models.cmmusic;

/*
结果码	结果描述
-1      未知错误
000000	返回结果成功。
999000	【OPEN】调用安全模块鉴权失败
999001	【OPEN】订购关系不存在或者接口权限未开通
999002	【OPEN】您请求的音源不存在
999003	【OPEN】安全参数为空
999004	【OPEN】系统繁忙
999005	【OPEN】业务参数为空
999006	【OPEN】您请求的歌曲受限
999007	【OPEN】请求参数不合法
999008	【OPEN】应用已经暂停
999009	【OPEN】应用已经下线
999010	【OPEN】校验请求IP地址不合法
999011	【OPEN】IMSI和手机号对应关系不存在
999012	【OPEN】获取APP相关信息失败
999013	【OPEN】校验应用公钥不合法
999014	【OPEN】校验应用包名不合法
999015	【OPEN】请求地址中接口版本号不合法
200001	必要参数为空必选参数为空
200002	必要参数为空必选参数格式错误
300001	鉴权失败
300002	系统异常
300003	查询不到试听歌曲
167231	业务请求失败
302099	用户下载成功，但设置失败(订购彩铃会出现这个错误码，表示订购彩铃成功，但是设置为默认彩铃失败。)
*/

public class CMException extends Exception {
  protected int code;

  public CMException(String code, String msg) {
    super(msg);
    try {
      this.code = Integer.parseInt(code);
    } catch (Exception e) {
      this.code = -1;
    }
  }

  public int code() {
    return code;
  }
}
