package com.example.atymusicplayer;

import java.util.List;

import com.example.utils.MediaUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	int currentSong=0;
	List<Mp3Info> Mp3Infos;
	Context myContext;
	
	public ListViewAdapter(Context c,List<Mp3Info> l) {
		myContext=c;
		Mp3Infos=l;
		
		
	}
	@Override
	public int getCount() {
		return Mp3Infos.size();
	}

	@Override
	public Mp3Info getItem(int position) {
		return Mp3Infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView==null)
		{
			convertView=LayoutInflater.from(myContext).inflate(R.layout.relativelayout_listview_cell, null);
		}
		ImageView portrait=(ImageView) convertView.findViewById(R.id.imageView_portrait);
		TextView textViewTitle=(TextView) convertView.findViewById(R.id.textView_title);
		TextView textViewSingerName=(TextView) convertView.findViewById(R.id.textView_singerName);
		TextView textViewDuration=(TextView) convertView.findViewById(R.id.textView_duration);
		Mp3Info mp3info=getItem(position);
		String title=mp3info.getTitle();
		String singerName=mp3info.getArtist();
		//String duration=String.valueOf(mp3info.getDuration());
		long duration=mp3info.getDuration();
		String songDuration=MediaUtil.formatSongDuration(duration);
		textViewTitle.setText(title);
		textViewDuration.setText(songDuration);
		textViewSingerName.setText(singerName);
		if(position==currentSong)
		{
			textViewTitle.setTextColor(myContext.getResources().getColor(R.color.DeepSkyBlue));
			textViewSingerName.setTextColor(myContext.getResources().getColor(R.color.DeepSkyBlue));
			textViewDuration.setTextColor(myContext.getResources().getColor(R.color.DeepSkyBlue));
		}
		else
		{
			textViewTitle.setTextColor(myContext.getResources().getColor(R.color.White));
			textViewSingerName.setTextColor(myContext.getResources().getColor(R.color.White));
			textViewDuration.setTextColor(myContext.getResources().getColor(R.color.White));
		}
		
		return convertView;
	}
	public void setCurrentSong(int currentSong)
	{
		this.currentSong=currentSong;
	}

}
