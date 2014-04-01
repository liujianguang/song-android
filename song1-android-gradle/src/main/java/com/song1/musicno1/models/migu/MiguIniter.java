package com.song1.musicno1.models.migu;

import android.content.Context;
import com.cmsc.cmmusic.common.InitCmmInterface;

import javax.inject.Inject;
import java.util.Hashtable;

/**
 * Created by windless on 3/31/14.
 */
public class MiguIniter {
  protected final Context context;

  @Inject
  public MiguIniter(Context context) {
    this.context = context;
  }

  public boolean isInited() {
    return InitCmmInterface.initCheck(context);
  }

  public boolean init() {
    Hashtable<String, String> cmmEnv = InitCmmInterface.initCmmEnv(context);
    return cmmEnv != null && cmmEnv.containsKey("0");
  }
}
