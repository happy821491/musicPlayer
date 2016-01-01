package com.example.atymusicplayer;

import java.util.Timer;
import java.util.TimerTask;

import com.example.atymusicplayer.MainActivity.BroadCastReceiverForHomeActivity;
import com.example.custom.view.LrcView;
import com.example.service.PlayerService;
import com.example.utils.ImageUtil;
import com.example.utils.MediaUtil;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends Activity {
	
//	public static int screenWidth  ;  // 屏幕宽度
//	public static int scrrenHeight ;  //屏幕高度
	
	private int playStatus;                              //播放模式
	boolean isPause=true;
	
	private int maxVolume;
	private AudioManager am;

	SongSeekBarProgressChange mSongSeekBarProgressChange=new SongSeekBarProgressChange();
	Animation animationComeUpVolumeControler;
	Animation animationDisappearVolumeControler;
	
	MyHandleForPlayActivity mHandle=new MyHandleForPlayActivity();
	MyDispearVolumeControlRunnable mDispearVolumeControlRunnable=new MyDispearVolumeControlRunnable();
	
	Button btnPlay;
	Button btnPre;
	Button btnNext;
	Button btnRepeatSong;
	Button btnShuffleMusic;
	Button btnVolume;
	Button btnMenu;
	
	TextView tvSongTitle;
	TextView tvSingerName;
	TextView tvSongCurrentTime;
	TextView tvSongTotalTime;
	
	LrcView lrcView;
	
	SeekBar seekBarProgress;
	
	ImageView ivAlbum;
	ImageView ivAlbumReflection;
	
	
	RelativeLayout rlControlVolume;
	SeekBar seekBarControlVolume;

	BroadCastReceiverForPlayActivity mBroadCastReceiver;
	
	ViewOnClickListenerForPlayActivity viewOnClickListenerForPlayActivity=new ViewOnClickListenerForPlayActivity();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relative_layout_play_activity);
		getView();
		registerReceiverForHomeActivity();
		startUpdateLrcListIndex();
		setSongSeekBarOnProgressChangeListener();
		setBtnOnClickListener();
		am=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		seekBarControlVolume.setMax(maxVolume);
		setMyAnimation();
		
	}
	
	

	@Override
		protected void onDestroy() {
			super.onDestroy();
			stopUpdateLrcListIndex();
		}
	
	@Override
		protected void onResume() {
			super.onResume();
			initialPlayActivity();
			
		}
	
	void getView()
	{
		btnPlay=(Button) findViewById(R.id.play_song_ForPlayActivity);
		btnPre=(Button) findViewById(R.id.previous_song_ForPlayActivity);
		btnNext=(Button) findViewById(R.id.next_song_ForPlayActivity);
		btnRepeatSong=(Button) findViewById(R.id.repeat_song_ForPlayActivity);
		btnShuffleMusic=(Button) findViewById(R.id.shuffle_music_ForPlayActivity);
		btnVolume=(Button) findViewById(R.id.button_playVoice_ForPlayActivity);
		btnMenu=(Button) findViewById(R.id.menu_ForPlayActivity);
		
		
		tvSongTitle=(TextView) findViewById(R.id.textView_songTitle_ForPlayActivity);
		tvSingerName=(TextView) findViewById(R.id.textView_Singer_ForPlayActivity);
		tvSongCurrentTime=(TextView) findViewById(R.id.textView_currentTimeForPlayActivity);
		tvSongTotalTime=(TextView) findViewById(R.id.textView_totalTime_ForPlayActivity);
		
		seekBarProgress=(SeekBar) findViewById(R.id.seekBar);
		
		lrcView=(LrcView) findViewById(R.id.lrcView);
		
		ivAlbum=(ImageView) findViewById(R.id.imageView_Album_In_PlayActivity);
		ivAlbumReflection=(ImageView) findViewById(R.id.imageView_Album_Reflection_In_PlayActivity);
		
		
		rlControlVolume=(RelativeLayout) findViewById(R.id.relative_layout_control_volume);
		seekBarControlVolume=(SeekBar) findViewById(R.id.seekBar_ControlVolume);
		
	}
	public void registerReceiverForHomeActivity()
	{
		mBroadCastReceiver=new BroadCastReceiverForPlayActivity();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_TIME);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_SONG_TITLE);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_PLAY_STATUS);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_ISPLAY);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_LISTVIEW); 
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_SINGER_NAME);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_SONG_TOTAL_TIME);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_LRC_LIST_INDEX);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_LRC_LIST);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_ALBUM_DRAWABLE);
		

		
		registerReceiver(mBroadCastReceiver, intentFilter);

	}
	
	public void onClick_button(View v)
	{
		Toast.makeText(this, "ffsadfasdf", Toast.LENGTH_SHORT).show();
	}
	
	
	public class BroadCastReceiverForPlayActivity extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_TIME))
			{
				int currentTime=intent.getIntExtra(PlayerService.INTENT_EXTRA_CURRENT_TIME, 0);
				String currentTimeFormat=MediaUtil.formatSongDuration(currentTime);
				tvSongCurrentTime.setText(currentTimeFormat+"");
				seekBarProgress.setProgress(currentTime);
			}
			else if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_SONG_TITLE))
			{
				String currentSongTitle=intent.getStringExtra(PlayerService.INTENT_EXTRA_CURRENT_SONG_TITLE);
				tvSongTitle.setText(currentSongTitle);
			}
			else if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_PLAY_STATUS))
			{
				playStatus=intent.getIntExtra(PlayerService.INTENT_EXTRA_CURRENT_PLAY_STATUS, playStatus);
				changeButtonPlayStatus();
			}
			else if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_ISPLAY))
			{
				boolean isPlaying=intent.getBooleanExtra(PlayerService.INTENT_EXTRA_CURRENT_ISPLAYING, false);
				isPause=!isPlaying;
				if(isPlaying)
				{
					btnPlay.setBackgroundResource(R.drawable.pause_up_down);
				}
				else
				{
					btnPlay.setBackgroundResource(R.drawable.play_up_down);
				}


			}
			else if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_SINGER_NAME))
			{
				String singerName=intent.getStringExtra(PlayerService.INTEN_EXTRA_SINGER_NAME);
				if(singerName!=null)
				{
					tvSingerName.setText(singerName);
				}
				else
				{
					tvSingerName.setText(" ");
				}
			}
			else if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_SONG_TOTAL_TIME))
			{
				long totalTIme=intent.getLongExtra(PlayerService.INTENT_EXTRA_CURRENT_SONG_TOTAL_TIME, 0);
				String totalTime=MediaUtil.formatSongDuration(totalTIme);
				tvSongTotalTime.setText(totalTime);
				seekBarProgress.setMax((int)totalTIme);
			}
			else if(action.equals(PlayerService.ACTION_UPDATE_LRC_LIST_INDEX))
			{
				int currentLrcListIndex=intent.getIntExtra(PlayerService.INTENT_EXTRA_CURRENT_LRC_LIST_INDEX, 0);
				lrcView.setIndex(currentLrcListIndex);
				lrcView.invalidate();
			}
			
			else if(action.equals(PlayerService.ACTION_UPDATE_LRC_LIST))
			{
				lrcView.setLrcList(PlayerService.getLrcList());
			}	
			else if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_ALBUM_DRAWABLE))
			{
				String songPath=intent.getStringExtra(PlayerService.INTENT_EXTRA_CURRENT_SONG_PATH);
				Bitmap bitMap=MediaUtil.getSongAlbumBitmapDrawable(PlayerActivity.this, songPath);
				if(bitMap==null)
				{
					bitMap=getDefaultAlbumBitmap();
				}
				ivAlbum.startAnimation(animationComeUpVolumeControler);
				ivAlbum.setImageBitmap(bitMap);
				ivAlbumReflection.startAnimation(animationComeUpVolumeControler);
				ivAlbumReflection.setImageBitmap(ImageUtil.createReflectionBitmapForSingle(bitMap));
			}

		}

		
		
	}
	

	private void changeButtonPlayStatus() {
		switch(playStatus)
		{
		case PlayerService.PLAYSTATUE_ORDER_PLAY:
			btnRepeatSong.setBackgroundResource(R.drawable.repeat_up_down_order);
			btnShuffleMusic.setBackgroundResource(R.drawable.shuffle_up_down_no_shuffle);
			break;

		case PlayerService.PLAYSTATUE_RANDOM_CYCLE:
			btnRepeatSong.setBackgroundResource(R.drawable.repeat_up_down_order);
			btnShuffleMusic.setBackgroundResource(R.drawable.shuffle_up_down);
			break;

		case PlayerService.PLAYSTATUE_REPEAT_ALL:
			btnRepeatSong.setBackgroundResource(R.drawable.repeat_up_down_all);
			btnShuffleMusic.setBackgroundResource(R.drawable.shuffle_up_down_no_shuffle);
			break;

		case PlayerService.PLAYSTATUE_SINGLE_CYCLE:
			btnRepeatSong.setBackgroundResource(R.drawable.repeat_up_down_single);
			btnShuffleMusic.setBackgroundResource(R.drawable.shuffle_up_down_no_shuffle);
			break;
			
		}
	}
	
	public void initialPlayActivity()
	{
		Intent intent=new Intent(PlayerService.ACTION_START_PLAYER_SERVICE);
		intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_INITIAL_PLAYACTIVITY);
		startService(intent);
	}
	
	public void setLrcListForLrcView()
	{
		lrcView.setLrcList(PlayerService.getLrcList());
	}
	
	
	public void startUpdateLrcListIndex()
	{
		Intent intent=new Intent(PlayerService.ACTION_START_PLAYER_SERVICE);
		intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_START_UPDATE_LRC_INDEX_FOR_PLAYACTIVITY);
	    startService(intent);
	}
	
	public void stopUpdateLrcListIndex()
	{
		Intent intent=new Intent(PlayerService.ACTION_START_PLAYER_SERVICE);
		intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_STOP_UPDATE_LRC_INDEX_FOR_PLAYACTIVITY);
	    startService(intent);	
	}
	
	public Bitmap getDefaultAlbumBitmap()
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(getResources()
				.openRawResource(R.drawable.defaultalbum), null, opts);
	}
	class SongSeekBarProgressChange implements OnSeekBarChangeListener
	{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(!fromUser)
				return;
			switch(seekBar.getId())
			{
			case R.id.seekBar:

				updateSeekBarProgressChange(progress);

				break;
			case R.id.seekBar_ControlVolume:
				mHandle.removeCallbacks(mDispearVolumeControlRunnable);
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				
				
				mHandle.postDelayed(mDispearVolumeControlRunnable, 3000);
				break;
			}


		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	}
	
	private void updateSeekBarProgressChange(int progress)
	{
		Intent intent=new Intent(PlayerService.ACTION_START_PLAYER_SERVICE);
		intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_CHANGE_SONG_PROGRESS);
		intent.putExtra(PlayerService.INTENT_EXTRA_CURRENT_SONG_PROGRESS, progress);
		startService(intent);
	}
	
	private void setSongSeekBarOnProgressChangeListener()
	{
		seekBarProgress.setOnSeekBarChangeListener(mSongSeekBarProgressChange);
		seekBarControlVolume.setOnSeekBarChangeListener(mSongSeekBarProgressChange);
	}
	
	private void setBtnOnClickListener()
	{
		btnPlay.setOnClickListener(viewOnClickListenerForPlayActivity);
	    btnPre.setOnClickListener(viewOnClickListenerForPlayActivity);
		btnNext.setOnClickListener(viewOnClickListenerForPlayActivity);
		btnRepeatSong.setOnClickListener(viewOnClickListenerForPlayActivity);
		btnShuffleMusic.setOnClickListener(viewOnClickListenerForPlayActivity);
		btnVolume.setOnClickListener(viewOnClickListenerForPlayActivity);
		btnMenu.setOnClickListener(viewOnClickListenerForPlayActivity);
	}
	
	class ViewOnClickListenerForPlayActivity implements OnClickListener
	{

		@Override
		public void onClick(View v) {

			Intent intent=new Intent();
			switch(v.getId())
			{
			case R.id.previous_song_ForPlayActivity:
				intent.setAction(PlayerService.ACTION_START_PLAYER_SERVICE);
				intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_PRE);
				btnPlay.setBackgroundResource(R.drawable.pause_up_down);
				isPause=false;
				startService(intent);
				break;
			case R.id.next_song_ForPlayActivity:
				intent.setAction(PlayerService.ACTION_START_PLAYER_SERVICE);
				intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_NEXT);
				btnPlay.setBackgroundResource(R.drawable.pause_up_down);
				isPause=false;
				startService(intent);
				break;
			case R.id.play_song_ForPlayActivity:
				intent.setAction(PlayerService.ACTION_START_PLAYER_SERVICE);
				if(isPause)
				{
					intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_CONTINUE_PLAY);
					btnPlay.setBackgroundResource(R.drawable.pause_up_down);
				}
				else
				{
					intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_PAUSE);
					btnPlay.setBackgroundResource(R.drawable.play_up_down);
				}
				isPause=!isPause;
				startService(intent);
				break;
			case R.id.repeat_song_ForPlayActivity:
				if(playStatus!=PlayerService.PLAYSTATUE_RANDOM_CYCLE)
				{
					playStatus=(playStatus+1)%PlayerService.PLAY_STATUS_REPEAT_SUM;
				}
				else
				{
					playStatus=PlayerService.PLAYSTATUE_ORDER_PLAY+1;
				}

				intent.setAction(PlayerService.BroadCastReceiverPlayService.ACTION_CHANGE_PLAY_STATUS);
				intent.putExtra(PlayerService.BroadCastReceiverPlayService.INTENT_EXTRA_PLAY_STATUS_INDEX, playStatus);
				sendBroadcast(intent);
				break;
			case R.id.shuffle_music_ForPlayActivity:
				if(playStatus!=PlayerService.PLAYSTATUE_RANDOM_CYCLE)
				{
					playStatus=PlayerService.PLAYSTATUE_RANDOM_CYCLE;
				}
				else
				{
					playStatus=PlayerService.PLAYSTATUE_ORDER_PLAY;
				}
				intent.setAction(PlayerService.BroadCastReceiverPlayService.ACTION_CHANGE_PLAY_STATUS);
				intent.putExtra(PlayerService.BroadCastReceiverPlayService.INTENT_EXTRA_PLAY_STATUS_INDEX, playStatus);
				sendBroadcast(intent);
				break;
				
			case R.id.button_playVoice_ForPlayActivity:
				
				mHandle.removeCallbacks(mDispearVolumeControlRunnable);
				int currentVolume=am.getStreamVolume(AudioManager.STREAM_MUSIC);
				seekBarControlVolume.setProgress(currentVolume);
				rlControlVolume.startAnimation(animationComeUpVolumeControler);
				rlControlVolume.setVisibility(View.VISIBLE);
				
				mHandle.postDelayed(mDispearVolumeControlRunnable, 3000);
				break;
			
			}



		}

	}
	
	class MyHandleForPlayActivity extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			rlControlVolume.setVisibility(View.GONE);
			
		}
	}
	
	
	
	class MyDispearVolumeControlRunnable implements Runnable
	{

		@Override
		public void run() {
			rlControlVolume.startAnimation(animationDisappearVolumeControler);
			rlControlVolume.setVisibility(View.GONE);
		}
		
	}
	
	private void setMyAnimation() {
		animationComeUpVolumeControler=AnimationUtils.loadAnimation(this, R.anim.animation_for_relative_layout_control_volume);
		animationDisappearVolumeControler=AnimationUtils.loadAnimation(this, R.anim.animation_for_relative_layout_control_volume_disappear);
	}
	
}
