package com.example.custom.view;

import java.util.List;

import com.example.utils.LrcContent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class LrcView extends View {

	private float width;
	private float height;
	private Paint currentPaint;
	private Paint notCurrentPaint;
	private float textHeight=40;
	private float currentTextSize=40;
	private float noCurrentTextSize=24;
	private int index=0;
	private List<LrcContent> lrcList;
	
	public void setLrcList(List<LrcContent> list)
	{
		this.lrcList=list;
	}
	
	public void init()
	{
		setFocusable(true);
		currentPaint=new Paint();
		currentPaint.setAntiAlias(true);
		currentPaint.setTextAlign(Paint.Align.CENTER);
		
		notCurrentPaint=new Paint();
		notCurrentPaint.setAntiAlias(true);
		notCurrentPaint.setTextAlign(Paint.Align.CENTER);
		
	}
	
	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		System.out.println("LrcView.onDraw()");
		if(canvas==null)
		{
			return;
		}
		currentPaint.setColor(Color.YELLOW);
		notCurrentPaint.setColor(Color.WHITE);
		currentPaint.setTextSize(currentTextSize);
		notCurrentPaint.setTextSize(noCurrentTextSize);
		currentPaint.setTypeface(Typeface.SERIF);
		notCurrentPaint.setTypeface(Typeface.DEFAULT);
		
		
		if(lrcList==null||lrcList.size()<=0)
		{
			canvas.drawText("...木有歌词文件，赶紧去下载吧....", width/2, height/2, currentPaint);
			
		}
		else
		{
			float tempX=width/2;
			float tempY=height/2;
			canvas.drawText(lrcList.get(index).getLrcStr(), tempX, tempY, currentPaint);
			
			
			for(int i=index-1;i>=0;i--)
			{
				tempY-=textHeight;
				canvas.drawText(lrcList.get(i).getLrcStr(), tempX,tempY, notCurrentPaint);
			}
			tempY=height/2;
			for(int i=index+1;i<lrcList.size();i++)
			{
				tempY+=textHeight;
				canvas.drawText(lrcList.get(i).getLrcStr(), tempX,tempY, notCurrentPaint);
			}
			
		}
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.width=w;
		this.height=h;
		System.out.println("onSizeChanged");
		
	}
	
	public void setIndex(int i)
	{
		this.index=i;
	}

}
