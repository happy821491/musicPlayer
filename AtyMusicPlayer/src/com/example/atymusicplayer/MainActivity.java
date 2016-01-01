package com.example.atymusicplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Text;

import com.example.custom.view.MultiScreenActivity;
import com.example.service.PlayerService;
import com.example.utils.MediaUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnItemClickListener {




	private int playStatus;                              //播放模式
	boolean isPause=true;
	ViewOnClickListener myOnClickListener;

	ListView listViewSong;
	//SimpleAdapter listAdapter;
	//List<HashMap<String, String>> listMp3;
	List<Mp3Info> Mp3Infos;
	ListViewAdapter myAdapter;


	Button btnPlay;
	Button btnPre;
	Button btnNext;
	Button btnRepeatSong;
	Button btnShuffleMusic;

	TextView tvSongTitle;
	TextView tvSongCurrentTime;
	ImageView ivSongPic;

	RelativeLayout relativeLayoutSingleSong;
	BroadCastReceiverForHomeActivity myReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listViewSong=(ListView) findViewById(R.id.listView_song);
		setListAdapter();
		findView();
		myOnClickListener=new ViewOnClickListener();
		setMyOnClickListener();
		registerReceiverForHomeActivity();
		//initialHomeActivity();
		listViewSong.setOnItemClickListener(this);
		


	}
	
	void f()
	{
		Intent intent=new Intent(this, PlayerActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initialHomeActivity();



	}

	@Override
	protected void onDestroy() {
		unRegisterReceiverForHomeActivity();

		super.onDestroy();
	}


	public void registerReceiverForHomeActivity()
	{
		myReceiver=new BroadCastReceiverForHomeActivity();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_TIME);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_SONG_TITLE);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_PLAY_STATUS);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_ISPLAY);
		intentFilter.addAction(PlayerService.ACTION_UPDATE_CURRENT_LISTVIEW); 
		


		registerReceiver(myReceiver, intentFilter);

	}

	public void unRegisterReceiverForHomeActivity()
	{
		if(myReceiver!=null)
		{
			unregisterReceiver(myReceiver);
		}

	}

	public void findView()
	{
		btnPlay=(Button) findViewById(R.id.play_song);
		btnNext=(Button) findViewById(R.id.next_song);
		btnPre=(Button) findViewById(R.id.previous_song);
		btnRepeatSong=(Button) findViewById(R.id.repeat_song);
		btnShuffleMusic=(Button) findViewById(R.id.shuffle_music);
		tvSongTitle=(TextView) findViewById(R.id.textView_one);
		tvSongCurrentTime=(TextView) findViewById(R.id.textView_two);
		ivSongPic=(ImageView) findViewById(R.id.imageView_album);
		relativeLayoutSingleSong=(RelativeLayout) findViewById(R.id.relativeLayout_singleSong);
	}


	public void setMyOnClickListener()
	{
		btnPlay.setOnClickListener(myOnClickListener);
		btnNext.setOnClickListener(myOnClickListener);
		btnPre.setOnClickListener(myOnClickListener);
		btnRepeatSong.setOnClickListener(myOnClickListener);
		btnShuffleMusic.setOnClickListener(myOnClickListener);
		relativeLayoutSingleSong.setOnClickListener(myOnClickListener);


	}

	public void setListAdapter()
	{
		/*List<Mp3Info> listMp3Infos=getMp3Infos();
    	listMp3 =new ArrayList<HashMap<String,String>>();
    	for(Iterator iterator=listMp3Infos.iterator();iterator.hasNext();)
    	{
    		Mp3Info mp3info=(Mp3Info) iterator.next();
    		HashMap<String, String> map=new HashMap<String, String>();
    		map.put("title",mp3info.getTitle());
    		map.put("Artist",mp3info.getArtist());
    		map.put("duration", String.valueOf(mp3info.getDuration()));
    		map.put("size",String.valueOf(mp3info.getSize()));
    		map.put("url", mp3info.getUrl());
    		listMp3.add(map);
     	}
    	listAdapter=new SimpleAdapter(this, listMp3, R.layout.relativelayout_listview_cell, new String[]{"title","Artist","duration"},new int[]{R.id.textView_title,R.id.textView_singerName,R.id.textView_duration});
    	listViewSong.setAdapter(listAdapter);*/

		Mp3Infos=MediaUtil.getMp3Infos(this);
		myAdapter=new ListViewAdapter(this, Mp3Infos);
		listViewSong.setAdapter(myAdapter);

	}


	class ViewOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {

			Intent intent=new Intent();
			switch(v.getId())
			{
			case R.id.previous_song:
				intent.setAction(PlayerService.ACTION_START_PLAYER_SERVICE);
				intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_PRE);
				btnPlay.setBackgroundResource(R.drawable.pause_up_down);
				isPause=false;
				startService(intent);
				break;
			case R.id.next_song:
				intent.setAction(PlayerService.ACTION_START_PLAYER_SERVICE);
				intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_NEXT);
				btnPlay.setBackgroundResource(R.drawable.pause_up_down);
				isPause=false;
				startService(intent);
				break;
			case R.id.play_song:
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
			case R.id.repeat_song:
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
			case R.id.shuffle_music:
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
			case R.id.relativeLayout_singleSong:
				//Toast.makeText(MainActivity.this, "asdfasf", Toast.LENGTH_SHORT).show();
				intent.setClass(MainActivity.this, PlayerActivity.class);
				startActivity(intent);
				break;
			}



		}

	}


	public class BroadCastReceiverForHomeActivity extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {

			Thread current = Thread.currentThread(); 
			System.out.println("BroadCastReceiverForHomeActivity所在的线程:  "+current.getName());
			String action=intent.getAction();
			if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_TIME))
			{
				int currentTime=intent.getIntExtra(PlayerService.INTENT_EXTRA_CURRENT_TIME, 0);
				String currentTimeFormat=MediaUtil.formatSongDuration(currentTime);
				tvSongCurrentTime.setText(currentTimeFormat+"");
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
			else if(action.equals(PlayerService.ACTION_UPDATE_CURRENT_LISTVIEW))
			{
				int currentSong=intent.getIntExtra(PlayerService.INTENT_EXTRA_SONG_INDEX, 0);
				myAdapter.setCurrentSong(currentSong);
				myAdapter.notifyDataSetChanged();

			}

		}

	}
	public void sendBroadCastToChangePlayStaus(int status)
	{
		Intent intent=new Intent(PlayerService.BroadCastReceiverPlayService.ACTION_CHANGE_PLAY_STATUS);
		intent.putExtra(PlayerService.BroadCastReceiverPlayService.INTENT_EXTRA_PLAY_STATUS_INDEX, status);
		sendBroadcast(intent);
	}

	public void initialHomeActivity()
	{
		Intent intent=new Intent(PlayerService.ACTION_START_PLAYER_SERVICE);
		intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_INITIAL_HOMEACTIVITY);
		startService(intent);
	}
	public void changeButtonPlayStatus()
	{
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Intent intent=new Intent(PlayerService.ACTION_START_PLAYER_SERVICE);
		intent.putExtra(PlayerService.INTENT_EXTRA_MSG, PlayerService.MSG_NEW_PLAY);
		intent.putExtra(PlayerService.INTENT_EXTRA_SONG_INDEX, position);
		startService(intent);

	}



}
