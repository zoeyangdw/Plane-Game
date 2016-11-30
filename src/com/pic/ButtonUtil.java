package com.pic;
/**
 *  这个类用于 菜单按钮
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


public class ButtonUtil {

	private Bitmap mBitButtonBitmap = null;
	
	private int mPosX = 0;
	private int mPosY = 0;
	private int mWidth = 0;
	private int mHeight = 0;
	//图片与按钮相关联
	public ButtonUtil(Bitmap bpt, int posX, int posY){
		mBitButtonBitmap = bpt;
		mPosX = posX;
		mPosY = posY;
		mWidth = mBitButtonBitmap.getWidth();
		mHeight = mBitButtonBitmap.getHeight();
	}
	
	//设置按钮的图片，同时给按钮的大小赋值
	public void setButtonPic(Bitmap bpt){
		if(bpt != null)
		{
			mBitButtonBitmap = bpt;
			
			mWidth = mBitButtonBitmap.getWidth();
			mHeight = mBitButtonBitmap.getHeight();
		}
	}
	//画按钮
	public void drawButton(Canvas canvas, Paint paint){
		if (mBitButtonBitmap != null)
		{
			canvas.drawBitmap(mBitButtonBitmap, mPosX, mPosY, paint);
		}
	}
	//是否触摸到按钮
	public boolean isClick(float x, float y){
		boolean isClick = false;
		
		if (x >= mPosX && x <= mPosX + mWidth
				&& y >= mPosY && y <= mPosY + mHeight){
			isClick = true;
		}
		return isClick;
	}
}
