package com.song1.musicno1.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.helpers.ViewHelper;

import java.util.List;

/**
 * Created by leovo on 2014/4/29.
 */
public class ButtonTool extends LinearLayout {

  Context mContext;
  float toolHeight = 50;

  List<Button> buttonList = Lists.newArrayList();

  public ButtonTool(Context context) {
    super(context);
    mContext = context;
    init();
  }

  public ButtonTool(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ButtonTool(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private void init() {
    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewHelper.dp2pixels(mContext, toolHeight));
    setLayoutParams(layoutParams);
    setBackgroundColor(getResources().getColor(R.color.title_bg_color));
    setGravity(Gravity.CENTER);
  }
  LinearLayout.LayoutParams layoutParams = new LayoutParams(0,LayoutParams.MATCH_PARENT,1);


  public Button newButton(int id,String text,OnClickListener clickListener){
    if (buttonList.size() != 0){
      addView(createSplit());
    }
    Button button = new Button(mContext);
    button.setId(id);
    button.setText(text);
    button.setOnClickListener(clickListener);
    button.setLayoutParams(layoutParams);
    addView(button);
    buttonList.add(button);
    return button;
  }
  public Button newButton(int id,int resId,OnClickListener clickListener){
    String text = getResources().getString(resId);
    return newButton(id,text,clickListener);
  }

  private View createSplit(){
    View view = new View(mContext);
    view.setLayoutParams(new ViewGroup.LayoutParams(1,ViewHelper.dp2pixels(mContext,toolHeight - 30)));
    view.setBackgroundColor(getResources().getColor(R.color.content_bg_color));
    return view;
  }
}
