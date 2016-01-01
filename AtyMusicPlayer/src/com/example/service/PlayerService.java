package com.example.service;

import java.io.IOException;
import java.util.List;

import com.example.atymusicplayer.MainActivity;
import com.example.atymusicplayer.Mp3Info;
import com.example.utils.LrcContent;
import com.example.utils.LrcProcess;
import com.example.utils.MediaUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class PlayerService extends Service {

	public final static String ACTION_START_PLAYER_SERVICE="com.example.MUSIC_SERVICE";
	private MediaPlayer mediaPlayer;
	private String songPath;
	private int currentTime;
	private int currentSong;
	private int duration;
	private List<Mp3Info> Mp3Infos;
	private boolean isPrepare=false;
	private BroadCastReceiverPlayService myBroadCastReceiver;
	static private List<LrcContent> listLrc=null;
	private int currentIndex=0;
	private Handler handle=new Handler();
	private UpdateLrcListIndexRunnable runableUpdateLrcListIndex=new UpdateLrcListIndexRunnable();
	private int MyRunnableSum=0;
	private boolean isPregressChangeComplement=true;
	
	
	
	

	//PlayerService发送出去的action名字
	public final static String ACTION_UPDATE_CURRENT_TIME="com.example.service.ACTION_UPDATE_CURRENT_TIME";
    public final static String ACTION_UPDATE_CURRENT_SONG_TITLE="com.example.service.ACTION_UPDATE_CURRENT_SONG_TITLE";
    public final static String ACTION_UPDATE_CURRENT_PLAY_STATUS="com.example.service.ACTION_UPDATE_CURRENT_PLAY_STATUS";
    public final static String ACTION_UPDATE_CURRENT_ISPLAY="com.example.service.UPDATE_CURRENT_ISPLAY";
    public final static String ACTION_UPDATE_CURRENT_LISTVIEW="com.example.service.ACTION_UPDATE_CURRENT_LISTVIEW";
    public final static String ACTION_UPDATE_CURRENT_SINGER_NAME="com.example.service.ACTION_UPDATE_CURRENT_SINGER_NAME";
    public final static String ACTION_UPDATE_CURRENT_SONG_TOTAL_TIME="com.example.service.ACTION_UPDATE_CURRENT_SONG_TOTAL_TIME";
    public final static String ACTION_UPDATE_LRC_LIST_INDEX="com.example.service.ACTION_UPDATE_LRC_LIST_INDEX";
    public final static String ACTION_UPDATE_LRC_LIST="com.example.service.ACTION_UPDATE_LRC_LIST";
    public final static String ACTION_UPDATE_CURRENT_ALBUM_DRAWABLE="com.example.service.ACTION_UPDATE_CURRENT_ALBUM_DRAWABLE";
    
    
    //PlayerService传送出去的intent所携带的数据名字
	public final static String INTENT_EXTRA_CURRENT_TIME="com.example.service.currentTime";
	public final static String INTENT_EXTRA_CURRENT_SONG_TITLE="com.example.service.currentSongTitle";
	public final static String INTENT_EXTRA_CURRENT_PLAY_STATUS="com.example.service.INTENT_EXTRA_CURRENT_PLAY_STATUS";
	public final static String INTENT_EXTRA_CURRENT_ISPLAYING="com.example.service.isplaying";
	public final static String INTEN_EXTRA_SINGER_NAME="com.example.service.singerName";
	public final static String INTENT_EXTRA_CURRENT_SONG_TOTAL_TIME="com.example.service.currentSongDuration";
	public final static String INTENT_EXTRA_CURRENT_LRC_LIST_INDEX="com.example.service.INTENT_EXTRA_CURRENT_LRC_LIST_INDEX";
	public final static String INTENT_EXTRA_CURRENT_ALBUM_DRAWABLE="com.example.service.INTENT_EXTRA_CURRENT_ALBUM_DRAWABLE";
	public final static String INTENT_EXTRA_CURRENT_SONG_PATH="com.example.service.INTENT_EXTRA_CURRENT_SONG_PATH";
	
	
	private int playStatus;                              //播放模式
	public final static int PLAY_STATUS_REPEAT_SUM=3;
	public final static int PLAYSTATUE_SINGLE_CYCLE=0;    //单曲循环
	public final static int PLAYSTATUE_ORDER_PLAY=1;      //顺序播放
	public final static int PLAYSTATUE_REPEAT_ALL=2;      //全部循环
	public final static int PLAYSTATUE_RANDOM_CYCLE=3;   //随机循环

	
	//PlayService接受的intent所携带的数据名字
	public final static int MSG_PAUSE=1;
	public final static int MSG_PRE=2;
	public final static int MSG_CONTINUE_PLAY=3;
	public final static int MSG_NEXT=4;
	public final static int MSG_NEW_PLAY=5;
	public final static int MSG_INITIAL_HOMEACTIVITY=6;
	public final static int MSG_INITIAL_PLAYACTIVITY=7;
	public final static int MSG_START_UPDATE_LRC_INDEX_FOR_PLAYACTIVITY=8;
	public final static int MSG_STOP_UPDATE_LRC_INDEX_FOR_PLAYACTIVITY=9;
	public final static int MSG_CHANGE_SONG_PROGRESS=10;

	
	//PlayService接受的intent所携带的数据名字
	public final static String INTENT_EXTRA_SONG_INDEX="SONGINDEX";
	public final static String INTENT_EXTRA_MSG="MSG";
	public final static String INTENT_EXTRA_CURRENT_SONG_PROGRESS="com.example.service.INTENT_EXTRA_CURRENT_SONG_PROGRESS";



	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer=new MediaPlayer();
		Mp3Infos=MediaUtil.getMp3Infos(this);
		setMediaPlayerListen();
		playStatus=PLAYSTATUE_ORDER_PLAY;
		currentTime=0;
		currentSong=0;
		songPath=Mp3Infos.get(currentSong).getUrl();
		registerBroadCastReceiverPlayService();
		initLrc();


	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		int msg=intent.getIntExtra(INTENT_EXTRA_MSG, MSG_CONTINUE_PLAY);
		switch(msg)
		{
		case MSG_CONTINUE_PLAY:
			resume();
			break;
		case MSG_NEW_PLAY:
			currentSong=intent.getIntExtra(INTENT_EXTRA_SONG_INDEX, currentSong);
			songPath=Mp3Infos.get(currentSong).getUrl();
			currentTime=0;
			play();
			break;
		case MSG_NEXT:
			next();
			break;
		case MSG_PRE:
			pre();
			break;
		case MSG_PAUSE:
			pause();
			break;
		case MSG_INITIAL_HOMEACTIVITY:
			initialHomeActivity();
			break;
		case MSG_INITIAL_PLAYACTIVITY:
			initialPlayActivity();
			break;
		case MSG_START_UPDATE_LRC_INDEX_FOR_PLAYACTIVITY:
			startUpdateCurrentLrcListIndex();
			break;
		case MSG_STOP_UPDATE_LRC_INDEX_FOR_PLAYACTIVITY:
			stopUpdateCurrentLrcListIndex();
			break;
		case MSG_CHANGE_SONG_PROGRESS:
			int newCurrentTime=intent.getIntExtra(INTENT_EXTRA_CURRENT_SONG_PROGRESS, currentTime);
			if(newCurrentTime<=mediaPlayer.getDuration())
			{
				currentTime=newCurrentTime;
				isPregressChangeComplement=false;
				play();
				
			}
			break;
		}


		return super.onStartCommand(intent, flags, startId);
	}

	
	@Override
	public void onDestroy() {
		
		if(mediaPlayer!=null)
		{
			mediaPlayer.stop();
		}
		unRegisterBroadCastReceiverPlayService();
		
		super.onDestroy();
	}
	

	private void pause() {

		if(mediaPlayer!=null&&mediaPlayer.isPlaying())
		{
			currentTime=mediaPlayer.getCurrentPosition();
			mediaPlayer.pause();
		}
	}

	private void pre() {
		if(playStatus==PLAYSTATUE_RANDOM_CYCLE)
		{
			currentSong=(int) (Math.random()*(Mp3Infos.size()-1));
		}
		else
		{
			currentSong--;
			if(currentSong<0)
			{
				currentSong=Mp3Infos.size()-1;
			}
		}
		songPath=Mp3Infos.get(currentSong).getUrl();
		currentTime=0;
		play();

	}

	private void next() {
		if(playStatus==PLAYSTATUE_RANDOM_CYCLE)
		{
			currentSong=(int) (Math.random()*(Mp3Infos.size()-1));
		}
		else
		{
			currentSong++;
			if(currentSong>=Mp3Infos.size())
			{
				currentSong=0;
			}
		}
		songPath=Mp3Infos.get(currentSong).getUrl();
		currentTime=0;
		play();

	}

	private void resume() {

		if(!isPrepare)
		{
			play();
		}
		else
		{
			mediaPlayer.start();
			mediaPlayer.seekTo(currentTime);
			isPregressChangeComplement=true;
			updateTextViewCurrentTime();
			updateTextViewCurrentSongTitle();
			
		}
		
	}

	private void setMediaPlayerListen() {

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				Mp3Info mp3info;
				switch(playStatus)
				{
				case PLAYSTATUE_SINGLE_CYCLE:
					mediaPlayer.start();
					updateTextViewCurrentSongTitle();
					break;
				case PLAYSTATUE_ORDER_PLAY:
					currentSong++;
					currentTime=0;
					if(currentSong<Mp3Infos.size()-1)
					{
						mp3info=Mp3Infos.get(currentSong);
						songPath=mp3info.getUrl();
						play();
					}
					else
					{
						mediaPlayer.seekTo(0);
						currentSong=0;
					}
					break;
				case PLAYSTATUE_REPEAT_ALL:
					currentSong++;
					currentTime=0;
					if(currentSong>=Mp3Infos.size()-1)
					{
						currentSong=0;
					}
					mp3info=Mp3Infos.get(currentSong);
					songPath=mp3info.getUrl();
					play();
					break;
				case PLAYSTATUE_RANDOM_CYCLE:
					currentTime=0;
					currentSong=(int) (Math.random()*(Mp3Infos.size()-1));
					mp3info=Mp3Infos.get(currentSong);
					songPath=mp3info.getUrl();
					play();
					break;
				}


			}
		});

	}

	public void play()
	{

		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(songPath);
			mediaPlayer.setOnPreparedListener(new PreparedListener());
			mediaPlayer.prepare();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		


	}
	


	class PreparedListener implements OnPreparedListener
	{

		@Override
		public void onPrepared(MediaPlayer mp) {

			isPrepare=true;
			mediaPlayer.start();
			if(currentTime>0)
			{
				mediaPlayer.seekTo(currentTime);
				
			}
			isPregressChangeComplement=true;
			updateTextViewCurrentTime();
			updateTextViewCurrentSongTitle();
			updateCurrentSongForListViewInHomeActivity();
			updateIsPlaying();
			updateCurrentSongTotalTime();
			initLrc();
			updateCurrentSingerName();
			updateCurrentDrawable();
		}


	}
	
	
	

	public void updateTextViewCurrentTime()
	{
		if(MyRunnableSum>0)
			return;
		MyRunnable runnable=new MyRunnable();
		MyRunnableSum=1;
		new Thread(runnable).start();
		
		
	}
	class MyRunnable implements Runnable
	{
		boolean i=true;
		void printThreadName()
		{
			if(i)
			{
				Thread current = Thread.currentThread(); 
				System.out.println("MyRunnable所在的线程:  "+current.getName());
				i=false;
			}
		}
		

		@Override
		public void run() {
			printThreadName();
			int time=1;
			while(mediaPlayer!=null&&isPregressChangeComplement==true)
			{
				
				currentTime=mediaPlayer.getCurrentPosition();
				updateTextViewCurrentSongTime();
				System.out.println("广播  "+time++);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			MyRunnableSum=0;
		}
		
	}
	
	public void updateTextViewCurrentSongTime()
	{
		
		Intent intent=new Intent(ACTION_UPDATE_CURRENT_TIME);
		intent.putExtra(INTENT_EXTRA_CURRENT_TIME, currentTime);
		sendBroadcast(intent);
	}
	
	
	
	public void updateTextViewCurrentSongTitle()
	{
		Intent intent=new Intent(ACTION_UPDATE_CURRENT_SONG_TITLE);
		String currentSongTitle=Mp3Infos.get(currentSong).getTitle();
		intent.putExtra(INTENT_EXTRA_CURRENT_SONG_TITLE, currentSongTitle);
		sendBroadcast(intent);
		
	}
	
	public class BroadCastReceiverPlayService extends BroadcastReceiver
	{
		
		public static final String ACTION_CHANGE_PLAY_STATUS="com.example.service.changeplaystatus";
		
		
		public static final String INTENT_EXTRA_PLAY_STATUS_INDEX="com.example.service.STATUSINDEX";

		
		@Override
		public void onReceive(Context context, Intent intent) {
			playStatus=intent.getIntExtra(INTENT_EXTRA_PLAY_STATUS_INDEX, playStatus);
			updateCurrentPlayStatus();
		}
		
		
	}
	
	public void registerBroadCastReceiverPlayService()
	{
		myBroadCastReceiver=new BroadCastReceiverPlayService();
		IntentFilter filter=new IntentFilter();
		filter.addAction(BroadCastReceiverPlayService.ACTION_CHANGE_PLAY_STATUS);
		registerReceiver(myBroadCastReceiver,filter);
	}
	
	public void unRegisterBroadCastReceiverPlayService()
	{
		if(myBroadCastReceiver!=null)
		{
			unregisterReceiver(myBroadCastReceiver);
			
		}
		
	}
	
	public void updateCurrentPlayStatus()
	{
		Intent intent=new Intent(ACTION_UPDATE_CURRENT_PLAY_STATUS);
		intent.putExtra(INTENT_EXTRA_CURRENT_PLAY_STATUS, playStatus);
		sendBroadcast(intent);
		
	}
	
	public void updateIsPlaying()
	{
		Intent intent=new Intent(ACTION_UPDATE_CURRENT_ISPLAY);
		if(mediaPlayer!=null&&mediaPlayer.isPlaying())
		{
			intent.putExtra(INTENT_EXTRA_CURRENT_ISPLAYING, true);
		}
		else
		{
			intent.putExtra(INTENT_EXTRA_CURRENT_ISPLAYING, false);
		}
		sendBroadcast(intent);
		
	}
	
	public void initialHomeActivity()
	{
		updateTextViewCurrentSongTitle();
		updateTextViewCurrentSongTime();
		updateCurrentPlayStatus();
		updateIsPlaying();
		updateCurrentSongForListViewInHomeActivity();
	}
	
	
	public void updateCurrentSongForListViewInHomeActivity()
	{
		Intent intent=new Intent(ACTION_UPDATE_CURRENT_LISTVIEW);
		intent.putExtra(INTENT_EXTRA_SONG_INDEX, currentSong);
		sendBroadcast(intent);
	}
	
	public void initialPlayActivity()
	{
		initialHomeActivity();
		updateCurrentSingerName();
		updateCurrentSongTotalTime();
		updateLrcList();
		updateCurrentDrawable();
	}
	
	public void updateCurrentSingerName()
	{
		Intent intent=new Intent(ACTION_UPDATE_CURRENT_SINGER_NAME);
		intent.putExtra(INTEN_EXTRA_SINGER_NAME, Mp3Infos.get(currentSong).getArtist());
		sendBroadcast(intent);
	}
	
	public void updateCurrentSongTotalTime()
	{
		long totalTIme=Mp3Infos.get(currentSong).getDuration();
		Intent intent=new Intent(ACTION_UPDATE_CURRENT_SONG_TOTAL_TIME);
		intent.putExtra(INTENT_EXTRA_CURRENT_SONG_TOTAL_TIME, totalTIme);
		sendBroadcast(intent);
	}
	
	public void initLrc()
	{
		
		LrcProcess lrcProcess=new LrcProcess();
		boolean isFindLrc=lrcProcess.readLrc(songPath);
		if(isFindLrc)
		{
			listLrc=lrcProcess.getLrcList();
		}
		else
		{
			listLrc=null;
		}
		currentIndex=0;
		updateLrcList();
	}
	
	public void updateLrcList()
	{
		Intent intent=new Intent(ACTION_UPDATE_LRC_LIST);
		sendBroadcast(intent);
	}
	
	public void getLrcListIndex()
	{
		if(listLrc==null||mediaPlayer==null)
		{
			currentIndex=0;
			return;
		}
		if(isPregressChangeComplement==true)
		{
		currentTime=mediaPlayer.getCurrentPosition();
		}
		duration=mediaPlayer.getDuration();
		if(currentTime<duration)
		{
			//currentIndex++;
			if(currentIndex+1<listLrc.size())
			{
				LrcContent currentLrcContent=listLrc.get(currentIndex);
				LrcContent nextLrcContent=listLrc.get(currentIndex+1);
				if(currentTime>=currentLrcContent.getLrcTime()&&currentTime<nextLrcContent.getLrcTime())
				{
					return;
				}
				currentIndex++;
				if(currentIndex==listLrc.size()-1)
				{
					return;
				}
				currentLrcContent=listLrc.get(currentIndex);
				nextLrcContent=listLrc.get(currentIndex+1);
				if(currentTime>=currentLrcContent.getLrcTime()&&currentTime<=nextLrcContent.getLrcTime())
				{
					return;
				}
				
			   
			}
			for(int i=0;i<listLrc.size()-1;i++)
			{
				if(currentTime<=listLrc.get(i).getLrcTime()&&i==0)
				{
					currentIndex=0;
					return;
				}
				if(currentTime>listLrc.get(i).getLrcTime()&&currentTime<=listLrc.get(i+1).getLrcTime())
				{
					currentIndex=i;
					return;
				}
			}
			currentIndex=listLrc.size()-1;
			return;
			
			
		}
		currentIndex=0;
	}
	
	
	public void updateCurrentLrcListIndex()
	{
		getLrcListIndex();
		Intent intent=new Intent(ACTION_UPDATE_LRC_LIST_INDEX);
		intent.putExtra(INTENT_EXTRA_CURRENT_LRC_LIST_INDEX, currentIndex);
		sendBroadcast(intent);
	}
	
	public void startUpdateCurrentLrcListIndex()
	{
		handle.post(runableUpdateLrcListIndex);
	}
	
	public void stopUpdateCurrentLrcListIndex()
	{
		handle.removeCallbacks(runableUpdateLrcListIndex);
	}
	
	static public List<LrcContent> getLrcList()
	{
		return listLrc;
	}
	
	
	class UpdateLrcListIndexRunnable implements Runnable
	{
		@Override
		public void run() {
			updateCurrentLrcListIndex();
			handle.postDelayed(this,100);
		}
		
	}
	
	public void updateCurrentDrawable()
	{
		Intent intent=new Intent(ACTION_UPDATE_CURRENT_ALBUM_DRAWABLE);
		intent.putExtra(INTENT_EXTRA_CURRENT_SONG_PATH, songPath);
		sendBroadcast(intent);
		
	}
	
	
}
