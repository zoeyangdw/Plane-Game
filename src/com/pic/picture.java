package com.pic;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.Bundle;

public class picture extends Activity 
{
	public PictureView PV;
	/** Called when the activity is first created. */
	MyBroadcastReceiver receiver;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);
       Intent intent = getIntent();
       String user = intent.getStringExtra("one");
       PV=new PictureView(this,user);//定义对象并实例化，调用了PictureView类的构造函数
       setContentView(PV);//调用View类的onDraw函数    
    }
	public void CloseActivity()
	{
		finish();//这个方法，会自动调用注销方法onDestroy()，完成对程序的关闭
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		PictureView.FreeMedia();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(PV.myReceiver,intentFilter);		
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(PV.myReceiver);
	}
	
}