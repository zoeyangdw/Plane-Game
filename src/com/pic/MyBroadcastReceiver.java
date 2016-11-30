package com.pic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class MyBroadcastReceiver extends BroadcastReceiver{
	public  int curPower;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED))
		{
			int level = intent.getIntExtra("level", 0);
			int scale = intent.getIntExtra("scale", 100);
			curPower = (level * 100/scale)/25;		
		}		
	}
}
