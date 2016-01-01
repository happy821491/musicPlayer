package com.example.custom.view;



import com.example.atymusicplayer.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MultiScreenActivity extends Activity  {

	private Button bt_scrollLeft;
	private Button bt_scrollRight;
	private MultiViewGroup mulTiViewGroup  ;
	
	public static int screenWidth  ;  // 屏幕宽度
	public static int scrrenHeight ;  //屏幕高度
	
	private int curscreen = 0;   // 当前位于第几屏幕  ，共3个"屏幕"， 3个LinearLayout
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //获得屏幕分辨率大小
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenWidth = metric.widthPixels ;
		scrrenHeight = metric.heightPixels;		
		System.out.println("screenWidth * scrrenHeight --->" + screenWidth + " * " +scrrenHeight);
		setContentView(R.layout.relative_layout_play_activity);
		
		
 
		
		
	}

	

}