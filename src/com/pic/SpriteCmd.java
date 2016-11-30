/*
 * 主角及各种精灵共同属性（根据自己的游戏修改）
 */

package com.pic;

public class SpriteCmd
{
	public int x;// 精灵的x位置
	public int y;// 精灵的y位置

	public int unWidth;// 绘制的宽度
	public int unHeight;//绘制的高度

	public int unSpriteIndex;// 精灵在图片上的索引值
	public int unLayer;// 精灵的是否显示，为0时显示，为255时不显示
	public int WalkCount;//计数参量
	public int ProtectCount;
    public enum MainState//飞机状态
	{
    	    enSTAND,//站
			enWALKRIGHT,//右移
			enWALKLEFT,	//左移
			enWALKUP,	//上移
			enWALKDOWN,//下移
			enHURT,  //受伤状态
    }
    public MainState CurrentState;//主角的当前状态
    
    public Boolean IsActive;//
    public Boolean IsRight;//
    public Boolean IsHurt;//
    public Boolean IsLeftStop;//主角是否停止向左移动
    public Boolean IsRigtStop;//主角是否停止向右移动
    public Boolean IsTopStop;//主角是否停止向上移动
    public Boolean IsDownStop;//主角是否停止向下移动
}
