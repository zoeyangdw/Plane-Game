package com.pic;
/**
 *  ��������� �˵���ť
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
	//ͼƬ�밴ť�����
	public ButtonUtil(Bitmap bpt, int posX, int posY){
		mBitButtonBitmap = bpt;
		mPosX = posX;
		mPosY = posY;
		mWidth = mBitButtonBitmap.getWidth();
		mHeight = mBitButtonBitmap.getHeight();
	}
	
	//���ð�ť��ͼƬ��ͬʱ����ť�Ĵ�С��ֵ
	public void setButtonPic(Bitmap bpt){
		if(bpt != null)
		{
			mBitButtonBitmap = bpt;
			
			mWidth = mBitButtonBitmap.getWidth();
			mHeight = mBitButtonBitmap.getHeight();
		}
	}
	//����ť
	public void drawButton(Canvas canvas, Paint paint){
		if (mBitButtonBitmap != null)
		{
			canvas.drawBitmap(mBitButtonBitmap, mPosX, mPosY, paint);
		}
	}
	//�Ƿ�������ť
	public boolean isClick(float x, float y){
		boolean isClick = false;
		
		if (x >= mPosX && x <= mPosX + mWidth
				&& y >= mPosY && y <= mPosY + mHeight){
			isClick = true;
		}
		return isClick;
	}
}
