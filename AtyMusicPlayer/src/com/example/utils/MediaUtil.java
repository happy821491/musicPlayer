package com.example.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.atymusicplayer.Mp3Info;

public class MediaUtil {
	
	static private List<Mp3Info> Mp3Infos=null;
	
	static public List<Mp3Info> getMp3Infos(Context context)
	{
		if(Mp3Infos!=null)
		{
			return Mp3Infos;
		}
		Mp3Infos=new ArrayList<Mp3Info>();
    	Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);    	
    	
    	while(cursor!=null&&cursor.moveToNext())
    	{
    		Mp3Info mp3info=new Mp3Info();
    		long id=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
    		String title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
    		String artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
    		long duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
    		long size=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
    		String url=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
    		int isMusic=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
    		if(isMusic!=0)
    		{
    			mp3info.setArtist(artist);
    			mp3info.setDuration(duration);
    			mp3info.setId(id);
    			mp3info.setSize(size);
    			mp3info.setTitle(title);
    			mp3info.setUrl(url);
    		}
    		Mp3Infos.add(mp3info);
    	}
    	
    	return Mp3Infos;
	}
	static public String formatSongDuration(long duration)
	{
		String miniute=duration/(1000*60)+"";
		String second=(duration%(1000*60))/1000+"";
		if(miniute.length()<2)
		{
			miniute="0"+miniute;
		}
		switch(second.length())
		{
		case 1:
			second="0"+second;
			break;
		case 0:
			second="00";
			break;
		}
		return miniute+":"+second.substring(0, 2);
	}
	
	static private Cursor getCursorFromPath(Context context,String songPath)
	{
		
		Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA+"=?", new String[]{songPath}, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if(cursor.moveToNext())
		{
			return cursor;
		}
		return null;
	}
	
	static public Bitmap getSongAlbumBitmapDrawable(Context context,String songPath)
	{
		Cursor cursor=getCursorFromPath(context,songPath);
		if(cursor==null)
			return null;
		int albumId=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
		String uriAlbums="content://media/external/audio/albums";
		String[] projection=new String[]{"album_art"};
		cursor.close();
		cursor=context.getContentResolver().query(Uri.parse(uriAlbums+File.separator+Integer.toString(albumId)), projection, null, null, null);
		if(cursor.getCount()<=0||cursor.getColumnCount()<=0)
		{
			return null;
		}
		cursor.moveToNext();
		String album_art=cursor.getString(0);
		cursor.close();
		cursor=null;
		
		if(album_art==null)
			return null;
		Bitmap bitMap=BitmapFactory.decodeFile(album_art);
		
		return bitMap;
		
	}

}
