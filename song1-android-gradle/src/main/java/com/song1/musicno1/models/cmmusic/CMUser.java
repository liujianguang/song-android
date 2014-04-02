package com.song1.musicno1.models.cmmusic;

import android.content.Context;
import android.net.ConnectivityManager;
import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.InitCmmInterface;
import com.cmsc.cmmusic.common.UserManagerInterface;
import com.cmsc.cmmusic.common.data.GetUserInfoRsp;
import com.cmsc.cmmusic.common.data.Result;
import com.cmsc.cmmusic.common.data.UserInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Hashtable;

//处理有关用户的信息
@Singleton
public class CMUser {
  private final Context context;

  private boolean isInit = false;

  @Inject
  public CMUser(Context context) {
    this.context = context;
  }

  //获取用户信息 ,目前只能获得用户等级信息
  public UserInfo getUserInfo() throws CMException {
    GetUserInfoRsp p = UserManagerInterface.getUserInfo(context);
    if (p == null) {
      throw new CMException("-1", "未知错误");
    }
    if (p.getUserInfo() == null) {
      throw new CMException(p.getResCode(), p.getResMsg());
    }
    return p.getUserInfo();
  }

  //网络开通咪咕特级会员
  public void openMemberByNet(Boolean prioritySendSms, CMMusicCallback<Result> callback) {

    //TO DO

  }

  //短信开通咪咕特级会员
  public void openMemberBySms() {

    //TO DO

  }

  //通过token获取手机号码
//  public String getPhoneNumber() {
//    return new SIMCardInfo(context).getNativePhoneNumber();
//  }

  //单卡手机设备的初始化检查
  public Boolean initCheck() {
    Boolean isInit = false;
    isInit = InitCmmInterface.initCheck(context);
    setIsInit(isInit);
    return isInit;
  }

  //有SIM卡的设备初始化（适合双卡手机等等)
  public boolean initSIMMachine() throws CMException {
    boolean isInited = InitCmmInterface.initCheck(context);
    if (isInited) {
      setIsInit(true);
      return true;
    }
    Hashtable<String, String> initHashTable = InitCmmInterface.initCmmEnv(context);
    if (initHashTable == null) {
      return false;
    }
    if (initHashTable.containsKey("0")) {
      setIsInit(true);
      return true;
    } else {
      return false;
    }
  }

  private void setIsInit(boolean isInit) {
    this.isInit = isInit;
  }

  //无SIM卡的PAD类的初始化
  public String initNotSIMMachine(CMMusicCallback<Result> callback) {
    String message = "";

    // TODO

    return message;
  }


  //检查是否有网络连接
  public Boolean isConnectTo3G() {
    ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
  }

  //检查是否有网络连接
  public Boolean isConnectToWifi() {
    ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
  }

  //检查是否为中国移动业务
//    public Boolean isSIMCM() {
//        return new SIMCardInfo(context).getProviders() == SIMCardInfo.Provider.P_CMCC;
//    }

  public boolean isInit() {
    return isInit;
  }

}
