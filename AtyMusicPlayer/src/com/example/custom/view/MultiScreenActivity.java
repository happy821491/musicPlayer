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
	
	public static int screenWidth  ;  // ��Ļ���
	public static int scrrenHeight ;  //��Ļ�߶�
	
	private int curscreen = 0;   // ��ǰλ�ڵڼ���Ļ  ����3��"��Ļ"�� 3��LinearLayout
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //�����Ļ�ֱ��ʴ�С
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenWidth = metric.widthPixels ;
		scrrenHeight = metric.heightPixels;		
		System.out.println("screenWidth * scrrenHeight --->" + screenWidth + " * " +scrrenHeight);
		setContentView(R.layout.relative_layout_play_activity);
		
		
 
		
		
	}

	

}