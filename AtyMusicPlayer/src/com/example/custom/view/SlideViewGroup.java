package com.example.custom.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SlideViewGroup extends ViewGroup {

	private int mCurrentScreen=1;
	
	private Scroller mScroller;
	// �ٶ�׷��������Ҫ��Ϊ��ͨ����ǰ�����ٶ��жϵ�ǰ�����Ƿ�Ϊfling
	private VelocityTracker mVelocityTracker;
	// ��¼����ʱ�ϴ���ָ������λ��
	private float mLastMotionX;
	private float mLastMotionY;
	// Touch״ֵ̬ 0����ֹ 1������
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	// ��¼��ǰtouch�¼�״̬--������TOUCH_STATE_SCROLLING������ֹ��TOUCH_STATE_REST Ĭ�ϣ�
	private int mTouchState = TOUCH_STATE_REST;
	// ��¼touch�¼��б���Ϊ�ǻ����¼�ǰ�����ɻ�������
	private int mTouchSlop;
	// ��ָ�׶���������ٶ�px/s ÿ���������
	private int mMaximumVelocity;
	
	
	
	public SlideViewGroup(Context context) {
		super(context);
		intial();
		// TODO Auto-generated constructor stub
	}
	
	
	public SlideViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		
		// TODO Auto-generated constructor stub
	}
	

	public SlideViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		intial();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int childCount=getChildCount();
		final int width=MeasureSpec.getSize(widthMeasureSpec);
		for(int i=0;i<childCount;i++)
		{
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurrentScreen * width, 0);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int childLeft=0;
		int childCount=getChildCount();
		int width;
		int height;
		View child;
		for(int i=0;i<childCount;i++)
		{
			child=getChildAt(i);
			width=child.getMeasuredWidth();
			height=child.getMeasuredHeight();
			child.layout(childLeft, 0, childLeft+width, height);
			childLeft+=width;
		}
		
	}
	
	private void intial()
	{
		mScroller=new Scroller(getContext());
		ViewConfiguration viewCf=ViewConfiguration.get(getContext());
		mTouchSlop = viewCf.getScaledTouchSlop();
		//mMaximumVelocity = viewCf.getScaledMaximumFlingVelocity();
		mMaximumVelocity=600;
		//f();
		System.out.println("childCount  "+getChildCount());
	}
	
	
	void f()
	{
		LinearLayout l1=new LinearLayout(getContext());
		l1.setBackgroundColor(Color.BLUE);
		LinearLayout l2=new LinearLayout(getContext());
		l2.setBackgroundColor(Color.RED);
		LinearLayout l3=new LinearLayout(getContext());
		l3.setBackgroundColor(Color.GREEN);
		addView(l1);
		addView(l2);
		addView(l3);
	}
	
	
	@Override
	public void computeScroll() {
		if(mScroller.computeScrollOffset())
		{
			scrollTo(mScroller.getCurrX(), 0);
			postInvalidate();
		}
	}
	
	
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		
//		return false;
//	}
//	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		
		float x;
		float y;
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			
			mLastMotionX=event.getX();
			mLastMotionY=event.getY();
			if(!mScroller.isFinished())
			{
				mScroller.abortAnimation();
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			x=event.getX();
			y=event.getY();
		//	if(Math.abs(y-mLastMotionY)/Math.abs(x-mLastMotionX)<1)
			{
				float width=getWidth()/4f;
				
				{
					
					int deltaX=(int) (mLastMotionX-x);
					scrollBy(deltaX, 0);
					mLastMotionX=event.getX();
				}
			}
			
			
			break;
		case MotionEvent.ACTION_UP:
			//final VelocityTracker velocityTracker = mVelocityTracker  ;
			mVelocityTracker.computeCurrentVelocity(1000);
			
			float velocityX= mVelocityTracker.getXVelocity();
			if(velocityX>mMaximumVelocity&&mCurrentScreen>0)
			{
				snapToScreen(mCurrentScreen-1);
			}
			else if(velocityX<-mMaximumVelocity&&mCurrentScreen<getChildCount()-1)
			{
				
				snapToScreen(mCurrentScreen+1);
			}
			else
			{
				snapToDestination();
			}
			
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker=null;
			}
			break;
		}
		
		return true;
	}
	
	private void snapToScreen(int whichScreen)
	{
		mCurrentScreen=whichScreen;
		int targetScrollX=whichScreen*getWidth();
		if(getScrollX()!=targetScrollX)
		{
			int delta=targetScrollX-getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,Math.abs(delta)*2);
			invalidate();
		}
	}
	private void snapToDestination()
	{
		mCurrentScreen=(getScrollX()+getWidth()/2)/getWidth();
		if(mCurrentScreen>=getChildCount())
		{
			mCurrentScreen=getChildCount()-1;
		}
		else if(mCurrentScreen<0)
		{
			mCurrentScreen=0;
		}
		snapToScreen(mCurrentScreen);
	}

}
