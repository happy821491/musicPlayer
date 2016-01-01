package com.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;

public class LrcProcess {
	private List<LrcContent> mList=null;
	private LrcContent currentLrcContent=null;
	
	public List<LrcContent> getLrcList()
	{
		return mList;
	}
	
	public LrcProcess()
	{
		mList=new ArrayList<LrcContent>();
	}
	
	public boolean readLrc(String songPath)
	{
		if(!songPath.endsWith(".mp3"))
			return false;
		File file=new File(songPath.replace(".mp3", ".lrc"));
		
		try {
			FileInputStream fis=new FileInputStream(file);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			String s="";
			currentLrcContent=new LrcContent();
			while((s=br.readLine())!=null)
			{
				s=s.replace("[", "");
				s=s.replace("]", "@");
				String splitLrcData[]=s.split("@");
				if(splitLrcData.length>1)
				{
					int lrcTime=timeLrc(splitLrcData[0]);
					currentLrcContent.setLrcTime(lrcTime);
					currentLrcContent.setLrcStr(splitLrcData[1]);
					mList.add(currentLrcContent);
					currentLrcContent=new LrcContent();
				}
				
			}
			br.close();
			isr.close();
			fis.close();
			
		} catch (Exception e) {

			
			e.printStackTrace();
			return false;
		} 
		
		return true;
	}

	private int timeLrc(String timeStr) {
		
		timeStr=timeStr.replace(":", ".");
		timeStr=timeStr.replace(".", "@");
		String timeData[]=timeStr.split("@");
		
		if(timeData.length>=3)
		{
			int minute=Integer.parseInt(timeData[0]);
			int second=Integer.parseInt(timeData[1]);
			int milliSecond=Integer.parseInt(timeData[2]);
			
			int currentTime=(minute*60+second)*1000+milliSecond*10;
			return currentTime;
		}
		
		return 0;
	}

}
