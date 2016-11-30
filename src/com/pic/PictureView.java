package com.pic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import com.pic.SpriteCmd.MainState;


public class PictureView extends View implements Runnable,SensorEventListener
{
	private Context context;
	
	private  int scrWidth;//屏幕的宽度
	private  int scrHeight;//屏幕的高度
	private  int MainTime = 0; //主计时器
	private  int Power = 0;
	private  int UserScore;
	
	private  Bitmap imgBackground = null;//菜单的背景图片
	//菜单上的按钮
	private ButtonUtil ButtonMenu = null;//主菜单
	private ButtonUtil ButtonStart = null;//开始按钮
	private ButtonUtil ButtonHelp = null;//帮助按钮
	private ButtonUtil ButtonRank = null;//高分榜
	private ButtonUtil ButtonDeveloper = null;//开发者
	
	private  Bitmap imgMenu = null;
	private  Bitmap imgStart = null;//开始按钮对应的图
	private  Bitmap imgHelp = null;//帮助按钮对应的图片
	private  Bitmap imgRank = null;
	private  Bitmap imgDeveloper = null;
	//选关的按钮
	private ButtonUtil ButtonFirst = null;//选关界面第一关对应按钮
	private ButtonUtil ButtonSecond = null;//选关界面第二关对应按钮
	private ButtonUtil ButtonThird = null;
	private ButtonUtil ButtonFourth = null;
	private Bitmap imgFirst = null;//第一关对应图片
	private Bitmap imgSecond = null;//第二关对应图片
	private Bitmap imgThird = null;
	private Bitmap imgFourth = null;
	//帮助的内容图片（用图片实现的）
	private Bitmap imgHelpDisplay = null;//帮助图片
	private Bitmap imgDeveloperDisplay = null;
	//返回按钮
	private ButtonUtil ButtonReturn = null;//返回按钮
	private Bitmap imgReturn = null;//返回按钮对应图片
	//声音按钮
	private ButtonUtil ButtonSound = null;//声音开关按钮
	private Bitmap imgSoundOn = null;//开声音对应图片
	private Bitmap imgSoundOff = null;//关声音对应图片
	private Boolean is_SoundOn = true;//声音是否开
	private Boolean is_LeverUp = false;//是否过关
	private Boolean is_Dead = false;
	
	private static MediaPlayer mediaPlayer = null;//声音播放对象
	private static MediaPlayer mediaPlayer_start = null;
	
	private Bitmap imgPower = null;

	
	public MyBroadcastReceiver myReceiver = new MyBroadcastReceiver();
	
	private int DeadEnemyCnt = 0;//敌人死亡计数器     当死亡敌人个数达到某个数时   过关
	private int LeverNum;//关数计数器
	private int LeverCnt = 0;//过关界面的计数器
	
	public String ClientName;
	
	private int Grade = 0;
	
	private Paint textpaint = new Paint();
	
	public enum GameState//游戏状态
	{
		GAMESTATE_MENU,			//菜单
		GAMESTATE_LEVELGUIDE,	//关数提示，显示第一关，第二关 。。
		GAMESTATE_HELP,         //帮助
		GAMESTATE_GAME,			//游戏
		GAMESTATE_DEVELOPER,
		GAMESTATE_RANK,
	}
	public  GameState GameState; // 游戏状态
	
	public static final int STAR_NUM = 30;
	public SpriteCmd rgSpriteCmd = new SpriteCmd();////主角SpriteCmd类对象实例化
	public SpriteCmd rgCmdStar[] = new SpriteCmd[30];//星星对象
	public SpriteCmd rgBulletCmd[] = new SpriteCmd[20];//主角子弹对象
	public SpriteCmd rgEnemyBulletCmd[];
	public SpriteCmd rgEnemy1Cmd = new SpriteCmd();//敌人1的SpriteCmd类对象
	public SpriteCmd rgEnemy2Cmd = new SpriteCmd();//敌人2对象
	public SpriteCmd rgEnemy3Cmd = new SpriteCmd();//敌人3对象
	public SpriteCmd rgBombCmds[] = new SpriteCmd[5];//敌人死亡时候爆炸的对象
	public SpriteCmd rgBackground1 = new SpriteCmd();//背景对象1
	public SpriteCmd rgBackground2 = new SpriteCmd();//背景对象2
	public SpriteCmd rgLerver = new SpriteCmd();//“恭喜过关”对象
	public SpriteCmd rgBloodBg = new SpriteCmd();//“血量框”对象
	public SpriteCmd rgBlood = new SpriteCmd();//“血量”对象
	
	//游戏里的图片
	public Bitmap imgPlane;//主角飞机对应图片
	public Bitmap imgStar;//星星对应图片
	public Bitmap imgBullet;//主角子弹图片
	public Bitmap imgEnemyBullet;//敌机子弹图片
	public Bitmap imgEnemy1;//敌人1对应图片
	public Bitmap imgEnemy2;//敌人2对象图片
	public Bitmap imgBomb;	//炸弹对应图片
	public Bitmap imgPassLerver;//过关图片
	public Bitmap imgBloodBg;//血量框图片
	public Bitmap imgBlood;//血量图片
	public Bitmap imgDeadLerver;
	public Bitmap imgRankBackground = null;
	
	public Bitmap battery;
	
	public boolean IsRightKeyRealess = true;//右键是否释放
	public boolean IsLeftKeyRealess = true;//左键是否释放
	public boolean IsUpKeyRealess = true;//上键是否释放
	public boolean IsDownKeyRealess = true;//下键是否释放
	
	private SensorManager sm;//传感器管理器
	private Sensor sensor;//重力传感器对象
	public float x_sensor = 0;//重力传感器x方向的位置偏移
	public float y_sensor = 0;//重力传感器y方向的位置偏移
	public float z_sensor = 0;//重力传感器z方向的位置偏移
	
	private int bulletnum;
	
	private UserItem User;
	private List<UserItem>userItems=new ArrayList<PictureView.UserItem>();
	
	public boolean  Is_exit = false;//是否退出游戏
	
	
	public PictureView(Context context,String s)//pictureView构造函数
	{
	      super(context);//调用父类的构造方法
	      MainTime = 0; // 主计时清零
	      this.context = context;
	      this.User = new UserItem();
	      this.User.name=s;
	      File file = new File(context.getFilesDir(),"Rank.txt");
	      if(file.exists())
	    	  readRank(context);
	      sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		  sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 得到一个重力传感器实例
		  sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);//向系统注册这个重力传感器
	      new Thread(this).start(); //启动线程    通过start()方法找到run()方法
      
	      Is_exit = false;
	}
	
	public void TurnToMenu()//进入菜单
	{
		//载入菜单的背景图片
		imgBackground =((BitmapDrawable)getResources().getDrawable(R.drawable.bg1)).getBitmap();
		//载入“开始游戏”按钮图片
		if (imgStart == null)
		{
			imgStart = ((BitmapDrawable)getResources().getDrawable(R.drawable.start)).getBitmap();//“开始游戏”的图片
		}
		//载入“帮助”按钮图片
		if (imgHelp == null)
		{
			imgHelp = ((BitmapDrawable)getResources().getDrawable(R.drawable.help)).getBitmap();//“游戏帮助”的图片
		}
		//载入“返回”按钮图片
		if (imgReturn == null)
		{
			imgReturn = ((BitmapDrawable)getResources().getDrawable(R.drawable.back)).getBitmap();//“返回”的图片
		}
		if (imgMenu == null)
		{
			imgMenu = ((BitmapDrawable)getResources().getDrawable(R.drawable.menu_button)).getBitmap();
		}
		if (imgRank == null)
		{
			imgRank = ((BitmapDrawable)getResources().getDrawable(R.drawable.rank)).getBitmap();
		}
		if (imgDeveloper == null)
		{
			imgDeveloper = ((BitmapDrawable)getResources().getDrawable(R.drawable.developer)).getBitmap();
		}
		if (imgRankBackground == null)
		{
			imgRankBackground = ((BitmapDrawable)getResources().getDrawable(R.drawable.ranking_bg)).getBitmap();
		}
		//实例化以“开始游戏”图片的按钮，确定了该按钮的图片和画在屏幕上的位置
		ButtonStart = new ButtonUtil(imgStart,(scrWidth - imgStart.getWidth())/2,280);
		//实例化以“帮助”图片的按钮，确定了该按钮的图片和画在屏幕上的位置
		ButtonHelp = new ButtonUtil(imgHelp,(scrWidth - imgHelp.getWidth())/2,360);//把“游戏帮助”的图片与按钮绑定
		//实例化以“返回”图片的按钮，确定了该按钮的图片和画在屏幕上的位置
		ButtonMenu = new ButtonUtil(imgMenu,(scrWidth - imgMenu.getWidth())/2,200);
		ButtonRank = new ButtonUtil(imgRank,(scrWidth - imgRank.getWidth())/2,440);
		ButtonDeveloper = new ButtonUtil(imgDeveloper,(scrWidth - imgDeveloper.getWidth())/2,520);
		ButtonReturn = new ButtonUtil(imgReturn,scrWidth - imgReturn.getWidth() - 10,scrHeight - 50);//把“返回”的图片与按钮绑定
		//设置游戏的状态为菜单状态
		GameState = GameState.GAMESTATE_MENU; //状态转换成菜单状态				

	}
	
	public void TurnToLeverGuide()//进入选关
	{
		ReleaseImage(imgBackground);
		imgBackground =((BitmapDrawable)getResources().getDrawable(R.drawable.bg2)).getBitmap();//背景图片
		
		if (imgFirst == null)
		{
			imgFirst = ((BitmapDrawable)getResources().getDrawable(R.drawable.firstlevel)).getBitmap();//“第1关”的图片
		}
		if (imgSecond == null)
		{
			imgSecond = ((BitmapDrawable)getResources().getDrawable(R.drawable.secondlevel)).getBitmap();//“第2关”的图片
		}
		if (imgThird == null)
		{
			imgThird = ((BitmapDrawable)getResources().getDrawable(R.drawable.thirdlevel)).getBitmap();//“第3关”的图片
		}
		if (imgFourth == null)
		{
			imgFourth = ((BitmapDrawable)getResources().getDrawable(R.drawable.fourthlevel)).getBitmap();//“第4关”的图片
		}
		//实例化以“第一关”图片的按钮，确定了该按钮的图片和画在屏幕上的位置
		ButtonFirst = new ButtonUtil(imgFirst,(scrWidth - imgFirst.getWidth())/2,100);
		//实例化以“第二关”图片的按钮，确定了该按钮的图片和画在屏幕上的位置
		ButtonSecond = new ButtonUtil(imgSecond,(scrWidth - imgSecond.getWidth())/2,200);
		ButtonThird = new ButtonUtil(imgThird,(scrWidth - imgThird.getWidth())/2,300);
		ButtonFourth = new ButtonUtil(imgFourth,(scrWidth - imgThird.getWidth())/2,400);
		
		mediaPlayer_start = MediaPlayer.create(context, R.raw.stars);
	    mediaPlayer_start.start();
	    mediaPlayer_start.setLooping(true);
	    
		GameState = GameState.GAMESTATE_LEVELGUIDE;
		
		Grade = 0;
	}
	
	public void TurnToHelp()//进入帮助
	{
		//载入帮助内容图片
		if (imgHelpDisplay == null)
		{
			imgHelpDisplay = ((BitmapDrawable)getResources().getDrawable(R.drawable.helpdisplay)).getBitmap();
		}	
		//置游戏状态为帮助状态
		GameState = GameState.GAMESTATE_HELP;
	}
	
	public void TurnToDeveloper()
	{
		if (imgDeveloperDisplay == null)
		{
			imgDeveloperDisplay = ((BitmapDrawable)getResources().getDrawable(R.drawable.developerdisplay)).getBitmap();
		}
		GameState = GameState.GAMESTATE_DEVELOPER;
	}
	
	public void TurnToRank()
	{
		GameState = GameState.GAMESTATE_RANK;
		saveRank(getContext());
	}
	
	public void TurnToGame()//进入游戏
	{   
		//载入游戏里的全部图片资源
		LoadResource();		
		InitLever();//
		
		GameState = GameState.GAMESTATE_GAME;	
		
		//////////为背景图定义了SpriteCmd类的两个对象，为背景图设定两个位置同时画出来////		
		rgBackground1.x = 0;
		rgBackground1.y = -scrHeight;
		rgBackground1.unLayer = 0;
		rgBackground1.unWidth = scrWidth;  //背景图的宽度设为全屏
		rgBackground1.unHeight = scrHeight;//背景图的宽度设为全屏
		
		rgBackground2.x = 0;
		rgBackground2.y = 0;
		rgBackground2.unLayer = 0;
		rgBackground2.unWidth = scrWidth;//背景图的宽度设为全屏
		rgBackground2.unHeight = scrHeight;	//背景图的宽度设为全屏
	/////////////////////////////////////////////////////////////	
		//声音开关的按钮
		imgSoundOn =((BitmapDrawable)getResources().getDrawable(R.drawable.bgsoundon)).getBitmap();
		imgSoundOff =((BitmapDrawable)getResources().getDrawable(R.drawable.bgsoundoff)).getBitmap();
		ButtonSound = new ButtonUtil(imgSoundOn,10,scrHeight - 20);
		
		//“恭喜过关”对象
		rgLerver.unWidth = 200;
		rgLerver.unHeight = 100;
		rgLerver.unLayer = 255;
		//血量
		rgBloodBg.unWidth = 100;
		rgBloodBg.unHeight = 32;
		rgBloodBg.unLayer = 0;
		rgBloodBg.x = 0;
		rgBloodBg.y = 0;
		
		rgBlood.unWidth = 96;
		rgBlood.unHeight = 14;
		rgBlood.unLayer = 0;
		rgBlood.x = rgBloodBg.x;
		rgBlood.y = rgBloodBg.y + 10;
		//声音的载入、播放
		mediaPlayer = MediaPlayer.create(context, R.raw.game);
	    mediaPlayer.start();
	    mediaPlayer.setLooping(true);
	    UserScore = 0;
		is_LeverUp = false;//是否过关
		is_Dead = false;
	}
	
	void InitLever()
	{
		int i;
		switch (LeverNum) {
		case 1:   //第一关
			rgSpriteCmd.unWidth = 36;
			rgSpriteCmd.unHeight = 34;
			rgSpriteCmd.x = (scrWidth - rgSpriteCmd.unWidth)/2;
			rgSpriteCmd.y = scrHeight - rgSpriteCmd.unHeight;
			rgSpriteCmd.unLayer = 0;
			rgSpriteCmd.unSpriteIndex = 0;
			rgSpriteCmd.CurrentState = MainState.enSTAND;
			rgSpriteCmd.WalkCount = 0;
			rgSpriteCmd.IsHurt = false;
			rgSpriteCmd.IsLeftStop = false;
			rgSpriteCmd.IsRight = false;
			rgSpriteCmd.IsTopStop = false;
			rgSpriteCmd.IsDownStop = false;
					
			//敌人初始化
			rgEnemy1Cmd.unWidth = 55;
			rgEnemy1Cmd.unHeight = 30;
			rgEnemy1Cmd.unLayer = 255;

			rgEnemy2Cmd.unWidth = 55;
			rgEnemy2Cmd.unHeight = 30;
			rgEnemy2Cmd.unLayer = 255;
			
			rgEnemy3Cmd.unWidth = 55;
			rgEnemy3Cmd.unHeight = 30;
			rgEnemy3Cmd.unLayer = 255;
			rgEnemy3Cmd.IsRight = false;
			bulletnum = 0;
			break;
		case 2:  //第二关
			rgSpriteCmd.unWidth = 36;
			rgSpriteCmd.unHeight = 34;
			rgSpriteCmd.x = (scrWidth - rgSpriteCmd.unWidth)/4;
			rgSpriteCmd.y = scrHeight - rgSpriteCmd.unHeight;
			rgSpriteCmd.unLayer = 0;
			rgSpriteCmd.unSpriteIndex = 0;
			rgSpriteCmd.CurrentState = MainState.enSTAND;
			rgSpriteCmd.WalkCount = 0;
			rgSpriteCmd.IsHurt = false;
			
			rgSpriteCmd.IsLeftStop = false;
			rgSpriteCmd.IsRight = false;
			rgSpriteCmd.IsTopStop = false;
			rgSpriteCmd.IsDownStop = false;
			
			//敌人初始化
			rgEnemy1Cmd.unWidth = 55;
			rgEnemy1Cmd.unHeight = 30;
			rgEnemy1Cmd.unLayer = 255;
			
			rgEnemy2Cmd.unWidth = 55;
			rgEnemy2Cmd.unHeight = 30;
			rgEnemy2Cmd.unLayer = 255;
			
			rgEnemy3Cmd.unWidth = 55;
			rgEnemy3Cmd.unHeight = 30;
			rgEnemy3Cmd.unLayer = 255;
			rgEnemy3Cmd.IsRight = false;
			
			//初始化子弹
			rgEnemyBulletCmd = new SpriteCmd[10];
			bulletnum = 10;
			break;
		case 3:
			rgSpriteCmd.unWidth = 36;
			rgSpriteCmd.unHeight = 34;
			rgSpriteCmd.x = (scrWidth - rgSpriteCmd.unWidth)/2;
			rgSpriteCmd.y = scrHeight - rgSpriteCmd.unHeight;
			rgSpriteCmd.unLayer = 0;
			rgSpriteCmd.unSpriteIndex = 0;
			rgSpriteCmd.CurrentState = MainState.enSTAND;
			rgSpriteCmd.WalkCount = 0;
			rgSpriteCmd.IsHurt = false;
			rgSpriteCmd.IsLeftStop = false;
			rgSpriteCmd.IsRight = false;
			rgSpriteCmd.IsTopStop = false;
			rgSpriteCmd.IsDownStop = false;
					
			//敌人初始化
			rgEnemy1Cmd.unWidth = 55;
			rgEnemy1Cmd.unHeight = 30;
			rgEnemy1Cmd.unLayer = 255;

			rgEnemy2Cmd.unWidth = 55;
			rgEnemy2Cmd.unHeight = 30;
			rgEnemy2Cmd.unLayer = 255;
			
			rgEnemy3Cmd.unWidth = 55;
			rgEnemy3Cmd.unHeight = 30;
			rgEnemy3Cmd.unLayer = 255;
			rgEnemy3Cmd.IsRight = false;
			
			//初始化子弹
			rgEnemyBulletCmd = new SpriteCmd[20];
			bulletnum = 20;
			break;
		case 4:
			rgSpriteCmd.unWidth = 36;
			rgSpriteCmd.unHeight = 34;
			rgSpriteCmd.x = (scrWidth - rgSpriteCmd.unWidth)/2;
			rgSpriteCmd.y = scrHeight - rgSpriteCmd.unHeight;
			rgSpriteCmd.unLayer = 0;
			rgSpriteCmd.unSpriteIndex = 0;
			rgSpriteCmd.CurrentState = MainState.enSTAND;
			rgSpriteCmd.WalkCount = 0;
			rgSpriteCmd.IsHurt = false;
			rgSpriteCmd.IsLeftStop = false;
			rgSpriteCmd.IsRight = false;
			rgSpriteCmd.IsTopStop = false;
			rgSpriteCmd.IsDownStop = false;
					
			//敌人初始化
			rgEnemy1Cmd.unWidth = 55;
			rgEnemy1Cmd.unHeight = 30;
			rgEnemy1Cmd.unLayer = 255;

			rgEnemy2Cmd.unWidth = 55;
			rgEnemy2Cmd.unHeight = 30;
			rgEnemy2Cmd.unLayer = 255;
			
			rgEnemy3Cmd.unWidth = 55;
			rgEnemy3Cmd.unHeight = 30;
			rgEnemy3Cmd.unLayer = 255;
			rgEnemy3Cmd.IsRight = false;
			
			//初始化子弹
			rgEnemyBulletCmd = new SpriteCmd[30];
			bulletnum = 30;
			break;
		default:
			break;
		}
		
		////星星的初始化///////////////////////////////
		for (i = 0; i < STAR_NUM; i += 1)
		{
			rgCmdStar[i] = new SpriteCmd();//每颗星星都实例化SpriteCmd类对象
		}
		for (i = 0; i < STAR_NUM; i += 2) //偶数个星星的图片索引值为0
		{
			rgCmdStar[i].unSpriteIndex = 0;
		}
		for (i = 1; i < STAR_NUM; i += 2) //基数个星星的图片索引值为1
		{
			rgCmdStar[i].unSpriteIndex = 1;
		}
		
		for (i = 0; i < STAR_NUM; i ++)
		{
			rgCmdStar[i].unWidth = 3; //每颗星星图片的宽度和高度
			rgCmdStar[i].unHeight = 3;
			rgCmdStar[i].x = scrWidth/STAR_NUM*i + 10;//每颗星星图片绘制在屏幕上的位置
			rgCmdStar[i].y = scrHeight/STAR_NUM*i;
		}	
		
		
		//////////////每颗子弹实例化SpriteCmd类对象，初始化////////
		for(i=0;i<20;i++)
		{
			rgBulletCmd[i] = new SpriteCmd();
		}for(i=0;i<bulletnum;i++)
		{
			rgEnemyBulletCmd[i] = new SpriteCmd();
		}
		for(i=0;i<20;i++)
		{
			rgBulletCmd[i].unWidth = 16;
			rgBulletCmd[i].unHeight = 14;
			rgBulletCmd[i].unLayer = 255;
		}
		for(i=0;i<bulletnum;i++)
		{
			rgEnemyBulletCmd[i].unWidth = 9;
			rgEnemyBulletCmd[i].unHeight = 9;
			rgEnemyBulletCmd[i].unLayer = 255;
		}
      
		//////////////每个炸弹实例化SpriteCmd类对象，初始化////////
		for(i=0;i<5;i++)
		{
			rgBombCmds[i] = new SpriteCmd();
		}
		
		for(i=0;i<5;i++)
		{
			rgBombCmds[i].unWidth = 40;
			rgBombCmds[i].unHeight = 40;
			rgBombCmds[i].unLayer = 255;
		}
		DeadEnemyCnt = 0;
		LeverCnt = 0;
		
	}
	
	void LoadResource()
	{
		//释放选关界面的背景图片
		ReleaseImage(imgBackground);
		//载入游戏的背景图片
		switch (LeverNum) {
		case 1:
			imgBackground =((BitmapDrawable)getResources().getDrawable(R.drawable.background1)).getBitmap();
			imgEnemy1 = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemy1_1)).getBitmap();
			imgEnemy2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemy1_2)).getBitmap();
			
			break;
		case 2:
			imgBackground =((BitmapDrawable)getResources().getDrawable(R.drawable.background2)).getBitmap();
			imgEnemy1 = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemy2_1)).getBitmap();
			imgEnemy2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemy2_2)).getBitmap();
			break;
		case 3:
			imgBackground =((BitmapDrawable)getResources().getDrawable(R.drawable.background3)).getBitmap();
			imgEnemy1 = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemy3_1)).getBitmap();
			imgEnemy2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemy3_2)).getBitmap();
			break;
		case 4:
			imgBackground =((BitmapDrawable)getResources().getDrawable(R.drawable.background4)).getBitmap();
			imgEnemy1 = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemy4_1)).getBitmap();
			imgEnemy2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemy4_2)).getBitmap();
			break;
		default:
			break;
		}
		
		//载入恭喜过关的图片
		imgPassLerver = ((BitmapDrawable)getResources().getDrawable(R.drawable.level_up)).getBitmap();
		//载入血量的背景图片
		imgBloodBg = ((BitmapDrawable)getResources().getDrawable(R.drawable.bloodbg)).getBitmap();
		//载入血量图片
		imgBlood = ((BitmapDrawable)getResources().getDrawable(R.drawable.blood)).getBitmap();
		//载入星星图片
		imgStar = ((BitmapDrawable)getResources().getDrawable(R.drawable.star)).getBitmap();
		//载入飞机图片
		imgPlane = ((BitmapDrawable)getResources().getDrawable(R.drawable.plane)).getBitmap();//主机
		//载入子弹图片
		imgBullet = ((BitmapDrawable)getResources().getDrawable(R.drawable.bullet)).getBitmap();
		//
		imgEnemyBullet = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemybullet)).getBitmap();

		//载入爆炸图片
		imgBomb = ((BitmapDrawable)getResources().getDrawable(R.drawable.zhadan)).getBitmap();
		
		imgDeadLerver = ((BitmapDrawable)getResources().getDrawable(R.drawable.gameover)).getBitmap();
		
	}
	
    public void run()
	{		
		long frameElapse, frameTick;
		try
        {
            frameTick = System.currentTimeMillis(); // 本次执行起始时间
            if(!Is_exit)
            {
            	  while (true)
                  {
                      frameElapse = System.currentTimeMillis() - frameTick; // 画一帧所用的时间

                      if (frameElapse < 80) // 如果不足80毫秒
                      {
                          Thread.sleep((80 - frameElapse)); // 线程休眠，保证画一帧的时间为80毫秒
                      }
      				  postInvalidate(); // 刷新屏幕     				   
                      frameTick = System.currentTimeMillis(); // 本次执行结束时间
                  }
            }
            else
            {
            	return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}
    

	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        scrWidth = w; // 获取屏幕宽度
        scrHeight = h; // 获取屏幕高度
		super.onSizeChanged(w, h, oldw, oldh);
		TurnToMenu();//进入菜单状态
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
///////绘制屏幕    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
	public void onDraw(Canvas canvas)
	{
		Power = myReceiver.curPower;
		super.onDraw(canvas);
		MainTime ++; // 主计时时间增加
		if (MainTime == 100000000)
		{
			MainTime = 0;
		}		
		if (rgBackground1.y >= 0) {
			rgBackground1.y = -scrHeight;
		}
		if (rgBackground2.y >= scrHeight) {
			rgBackground2.y = 0;
		}
		switch (GameState)
		{
			case GAMESTATE_MENU: //画菜单
				DrawMenu(canvas);
				break;
			case GAMESTATE_LEVELGUIDE:
				DrawLeverGuide(canvas);
				break;
			case GAMESTATE_HELP:
				DrawHelp(canvas);
				break;
			case GAMESTATE_GAME:
				DrawGame(canvas);
				break;
			case GAMESTATE_DEVELOPER:
				DrawDeveloper(canvas);
				break;
			case GAMESTATE_RANK:
				DrawRank(canvas);
				break;
		    default:
				break;
		}
	}
	
	public void DrawMenu(Canvas canvas)
	{
		//画背景图
		DisplayImage(canvas, imgBackground,imgBackground.getWidth(), imgBackground.getHeight());
		//画按钮“开始游戏”
		ButtonStart.drawButton(canvas, null);
		//画按钮“游戏帮助”
		ButtonHelp.drawButton(canvas, null);
		//画按钮“返回”
		ButtonReturn.drawButton(canvas, null);
		ButtonMenu.drawButton(canvas,null);
		ButtonRank.drawButton(canvas, null);
		ButtonDeveloper.drawButton(canvas,null);
	}
	
	public void DrawLeverGuide(Canvas canvas)
	{
		//画背景图
		DisplayImage(canvas, imgBackground,imgBackground.getWidth(), imgBackground.getHeight());
		//画按钮“第一关”
		ButtonFirst.drawButton(canvas, null);
		//画按钮“第二关”
		ButtonSecond.drawButton(canvas, null);
		//画按钮“返回”
		ButtonThird.drawButton(canvas, null);
		ButtonFourth.drawButton(canvas, null);
		ButtonReturn.drawButton(canvas, null);
	}
	
	public void DrawHelp(Canvas canvas)
	{
		DisplayImage(canvas,imgHelpDisplay,imgHelpDisplay.getWidth(),imgHelpDisplay.getHeight());
		ButtonReturn.drawButton(canvas, null);
	}
	
	public void DrawDeveloper(Canvas canvas)
	{
		DisplayImage(canvas,imgDeveloperDisplay,imgDeveloperDisplay.getWidth(),imgDeveloperDisplay.getHeight());
		ButtonReturn.drawButton(canvas, null);
	}
	
	public void DrawRank(Canvas canvas)
	{
		DisplayImage(canvas, imgRankBackground,imgRankBackground.getWidth(), imgRankBackground.getHeight());
		ButtonReturn.drawButton(canvas, null);
		Paint p = new Paint();
		Paint p1 = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(26);
		p1.setColor(Color.BLACK);
		p1.setTextSize(26);
		//利用paint和drawText直接在屏幕上绘制string
		
		for(int i=0;i<userItems.size()&&i<5;i++)
		{
			UserItem item=userItems.get(i);
			canvas.drawText(item.name, 140,240+i*95, p1);
			canvas.drawText(item.score+"", 300,240+i*95, p);
		}
		
	}
	
	public void DrawGame(Canvas canvas)
	{
		int i,j;
		//到达地图左边界
		if (rgSpriteCmd.x < 0) {
			rgSpriteCmd.IsLeftStop = true;
			rgSpriteCmd.x = 0;
		}
		else {
			rgSpriteCmd.IsLeftStop = false;
		}
		
		//到达地图右边界
		if (rgSpriteCmd.x + rgSpriteCmd.unWidth > scrWidth) {
			rgSpriteCmd.IsRigtStop = true;
			rgSpriteCmd.x = scrWidth - rgSpriteCmd.unWidth;
		}
		else {
			rgSpriteCmd.IsRigtStop = false;
		}
		//到达地图上边界
		if (rgSpriteCmd.y < 0) {
			rgSpriteCmd.IsTopStop = true;
			rgSpriteCmd.y = 0;
		}
		else {
			rgSpriteCmd.IsTopStop = false;
		}
		
		//到达地图下边界
		if (rgSpriteCmd.y + rgSpriteCmd.unHeight > scrHeight) {
			rgSpriteCmd.IsDownStop = true;
			rgSpriteCmd.y = scrHeight - rgSpriteCmd.unHeight;
		}
		else {
			rgSpriteCmd.IsDownStop = false;
		}
		
		////主机与敌机的碰撞判断及处理///////////////////////////////
		if ((CheckHitEnemy(rgSpriteCmd, rgEnemy1Cmd) || CheckHitEnemy(rgSpriteCmd, rgEnemy2Cmd)
			|| CheckHitEnemy(rgSpriteCmd, rgEnemy3Cmd)) && !rgSpriteCmd.IsHurt) {
			rgSpriteCmd.CurrentState = MainState.enHURT;
			rgSpriteCmd.ProtectCount = 18;
			rgSpriteCmd.IsHurt = true;
			rgBlood.unWidth -= 19;
			if (rgBlood.unWidth < 0) {
				is_Dead = true;
			}
		}
				
		/////主机子弹与敌机1的碰撞////////////////////////////////
		for(i=0;i<20;i++)
		{
			if (CheckHitEnemy(rgBulletCmd[i], rgEnemy1Cmd)) {
				rgBulletCmd[i].unLayer = 255;
				rgEnemy1Cmd.unLayer = 255;
				for (j = 0; j < 5; j++) {
					if(rgBombCmds[j].unLayer == 255)
					{
						rgBombCmds[j].unLayer = 0;
						rgBombCmds[j].x = rgEnemy1Cmd.x + 15;
						rgBombCmds[j].y = rgEnemy1Cmd.y + 15;
						rgBombCmds[j].WalkCount = 0;
						rgBombCmds[j].unSpriteIndex = 0;
						break;
					}
				}
				DeadEnemyCnt++;
				UserScore = UserScore + 67;
			}
		}
		
		////////主机子弹与敌机2的碰撞//////////////////////////////
		for(i=0;i<20;i++)
		{
			if (CheckHitEnemy(rgBulletCmd[i], rgEnemy2Cmd)) {
				//当子弹与敌机２碰撞时
				rgBulletCmd[i].unLayer = 255;//碰撞的子弹消失
				rgEnemy2Cmd.unLayer = 255;//碰撞的敌机２消失
				for (j = 0; j < 5; j++) {
					if(rgBombCmds[j].unLayer == 255)
					{
						rgBombCmds[j].unLayer = 0;
						rgBombCmds[j].x = rgEnemy2Cmd.x + 15;
						rgBombCmds[j].y = rgEnemy2Cmd.y + 15;
						rgBombCmds[j].WalkCount = 0;
						rgBombCmds[j].unSpriteIndex = 0;
						break;
					}
				}
				DeadEnemyCnt++;
				UserScore = UserScore + 67;
			}
		}
		
		/////主机子弹与敌机3的碰撞/////////////////////////////////
		for(i=0;i<20;i++)
		{
			if (CheckHitEnemy(rgBulletCmd[i], rgEnemy3Cmd)) {
				rgBulletCmd[i].unLayer = 255;
				rgEnemy3Cmd.unLayer = 255;
				for (j = 0; j < 5; j++) {
					if(rgBombCmds[j].unLayer == 255)
					{
						rgBombCmds[j].unLayer = 0;
						rgBombCmds[j].x = rgEnemy3Cmd.x + 15;
						rgBombCmds[j].y = rgEnemy3Cmd.y + 15;
						rgBombCmds[j].WalkCount = 0;
						rgBombCmds[j].unSpriteIndex = 0;
						break;
					}
				}
				DeadEnemyCnt++;
				UserScore = UserScore + 67;
			}
		}
		
		//敌机子弹与主机碰撞
		for(i=0;i<bulletnum;i++)
		{
			if (CheckHitEnemy(rgEnemyBulletCmd[i], rgSpriteCmd)) {
				rgSpriteCmd.CurrentState = MainState.enHURT;
				rgSpriteCmd.ProtectCount = 18;
				rgSpriteCmd.IsHurt = true;
				rgBlood.unWidth -= 9;
				if (rgBlood.unWidth < 0) {
					is_Dead = true;
					}
				rgEnemyBulletCmd[i].unLayer = 255;
			}
			}

		/////炸弹的处理////////////////////////////////////
		for(i=0;i<5;i++)
		{
			if (rgBombCmds[i].unLayer != 255) {
				if (rgBombCmds[i].WalkCount < 5) {
					rgBombCmds[i].unSpriteIndex = rgBombCmds[i].WalkCount;
				}
				else {
					rgBombCmds[i].unLayer = 255;
				}
				rgBombCmds[i].WalkCount++;
			}
		}
		
		////////////画背景/////////////////////////
		DrawSprites(canvas,imgBackground,rgBackground1);//第二个参数是要画的图片，第三个参数要画在屏幕上的位置等信息
		DrawSprites(canvas,imgBackground,rgBackground2);
		rgBackground1.y += 10;
		rgBackground2.y += 10;
		
		/////////////画血量/////////////////
		DrawSprites(canvas,imgBloodBg,rgBloodBg);
		DrawSprites(canvas,imgBlood,rgBlood);
		
		////////////画电量//////////////////
		
		switch(Power)
		  {
		  case 0:
			imgPower = ((BitmapDrawable)getResources().getDrawable(R.drawable.battery_power1)).getBitmap();
			break;
		  case 1:
			imgPower = ((BitmapDrawable)getResources().getDrawable(R.drawable.battery_power2)).getBitmap();
			break;
		  case 2:
			imgPower = ((BitmapDrawable)getResources().getDrawable(R.drawable.battery_power3)).getBitmap();
			break;
		  case 3:
			imgPower = ((BitmapDrawable)getResources().getDrawable(R.drawable.battery_power4)).getBitmap();
			break;
		  case 4:
			imgPower = ((BitmapDrawable)getResources().getDrawable(R.drawable.battery_power5)).getBitmap();
			break;
		  }
		DisplayImage(canvas,imgPower,420,20,30,15,0,0);
		
		///////////画得分///////
		textpaint.setColor(Color.WHITE);
		textpaint.setTextSize(20);
		String score = Integer.toString(UserScore);
		String defen = "得分";
		canvas.drawText(defen, 350, 60, textpaint);
		canvas.drawText(score, 410, 60, textpaint);


		///////过关判断//////////////////////////
		if (DeadEnemyCnt > 30 && LeverNum != 4) {
			is_LeverUp = true;
		}
		
		////过关处理    主机、敌机、主机子弹全部消失   背景音乐停播     初始化恭喜过关 以及画出来
		if (is_LeverUp) {
			LeverCnt++;
			rgSpriteCmd.unLayer = 255;
			rgEnemy1Cmd.unLayer = 255;
			rgEnemy2Cmd.unLayer = 255;
			rgEnemy3Cmd.unLayer = 255;
			for (i = 0; i < 20; i++) {
				rgBulletCmd[i].unLayer = 255;
			}
			for (i = 0;i < bulletnum;i++)
			{
				rgEnemyBulletCmd[i].unLayer = 255;
			}
			mediaPlayer.pause();
			rgLerver.unLayer = 0;
			rgLerver.x = (scrWidth - rgLerver.unWidth)/2;
			rgLerver.y = (scrHeight - rgLerver.unHeight)/2 + MainTime%2*3;
			DrawSprites(canvas,imgPassLerver,rgLerver);//画出“恭喜过关”
			if(LeverCnt == 40)//当计数器到40时进去下一关
			{
				LeverNum +=1;
				LeverCnt = 0;
				Grade = Grade + DeadEnemyCnt + rgBlood.unWidth;
				TurnToGame();
			}
		}
		
		//失败处理
		if (is_Dead) {
			if (LeverCnt == 0)
			{
				Grade = Grade + DeadEnemyCnt + rgBlood.unWidth;
			}
			LeverCnt++;
			rgSpriteCmd.unLayer = 255;
			rgEnemy1Cmd.unLayer = 255;
			rgEnemy2Cmd.unLayer = 255;
			rgEnemy3Cmd.unLayer = 255;
			for (i = 0; i < 20; i++) {
				rgBulletCmd[i].unLayer = 255;
			}
			for (i = 0;i < bulletnum;i++)
			{
				rgEnemyBulletCmd[i].unLayer = 255;
			}
			mediaPlayer.pause();
			rgLerver.unLayer = 0;
			rgLerver.x = (scrWidth - rgLerver.unWidth)/2;
			rgLerver.y = (scrHeight - rgLerver.unHeight)/2 + MainTime%2*3;
			DrawSprites(canvas,imgDeadLerver,rgLerver);//画出“游戏失败”		
			if(LeverCnt == 40)//当计数器到40时回到菜单
			{
				TurnToMenu();
			}			
		}
		
		//////每帧各星星的位置并画出来////////////////////////////
		for (i = 0; i < STAR_NUM; i ++)
		{
			if (rgCmdStar[i].y + rgCmdStar[i].unHeight > scrHeight)
			{	
				if (MainTime %2 == 0) {
					rgCmdStar[i].x = scrWidth/STAR_NUM*i + 10;
				}
				else {
					rgCmdStar[i].x = scrWidth/STAR_NUM*(STAR_NUM - i) + 10;
				}
				
				rgCmdStar[i].y = 0;
			}
			else
			{
				rgCmdStar[i].y += 5;
			}
			DrawSprites(canvas,imgStar,rgCmdStar[i]);
		}
		
		///////画“返回”和“声音”按钮//////////
		ButtonReturn.drawButton(canvas, null);
		ButtonSound.drawButton(canvas, null);
		DisplayImage(canvas,battery,450,20,30,15,0,0);
	   
		/////主机子弹重新初始化///////////////////////
		for(i = 0;i<20;i++)
		{
			if (rgBulletCmd[i].unLayer == 255 && MainTime%2 == 0 && !is_LeverUp) {
				rgBulletCmd[i].unLayer = 0;
				rgBulletCmd[i].x = rgSpriteCmd.x + 10;
				rgBulletCmd[i].y = rgSpriteCmd.y - 10;
				break;
			}
		}
		
		//敌机子弹初始化
		for(i = 0;i<bulletnum;i++)
		{
			if (i < 10)
			{
				if (rgEnemy1Cmd.unLayer == 0 && rgEnemyBulletCmd[i].unLayer == 255 && MainTime%2 == 0 && !is_LeverUp)
				{
					rgEnemyBulletCmd[i].unLayer = 0;
					rgEnemyBulletCmd[i].x = rgEnemy1Cmd.x + 5;
					rgEnemyBulletCmd[i].y = rgEnemy1Cmd.y + 10;
					i = 9;
				}
			}
			else if(i < 20)
			{
				if (rgEnemy2Cmd.unLayer == 0 && rgEnemyBulletCmd[i].unLayer == 255 && MainTime%2 == 0 && !is_LeverUp)
				{
					rgEnemyBulletCmd[i].unLayer = 0;
					rgEnemyBulletCmd[i].x = rgEnemy2Cmd.x + 5;
					rgEnemyBulletCmd[i].y = rgEnemy2Cmd.y + 10;
					i = 19;
				}
			}
			else
			{
				if (rgEnemy3Cmd.unLayer == 0 && rgEnemyBulletCmd[i].unLayer == 255 && MainTime%2 == 0 && !is_LeverUp)
				{
					rgEnemyBulletCmd[i].unLayer = 0;
					rgEnemyBulletCmd[i].x = rgEnemy3Cmd.x + 5;
					rgEnemyBulletCmd[i].y = rgEnemy3Cmd.y + 10;
					break;
				}
			}
		}
		
		/////主机子弹的运动以及走出屏幕后的处理
		for(i = 0;i<20;i++)
		{
			if (rgBulletCmd[i].unLayer != 255) {
				rgBulletCmd[i].y -= 35;
				if (rgBulletCmd[i].y < -10) {
					rgBulletCmd[i].unLayer = 255;
				}
			}
		}
		
		//敌机子弹的运动以及走出屏幕
		for (i = 0;i < bulletnum;i++)
		{
			if (rgEnemyBulletCmd[i].unLayer != 255) {
				rgEnemyBulletCmd[i].y += 105;
				if (rgEnemyBulletCmd[i].y > 650) {
					rgEnemyBulletCmd[i].unLayer = 255;
				}
			}
		}
		
		///画子弹
		for(i=0;i<20;i++)
		{
			DrawSprites(canvas,imgBullet,rgBulletCmd[i]);
		}
		for(i=0;i<bulletnum;i++)
		{
			DrawSprites(canvas,imgEnemyBullet,rgEnemyBulletCmd[i]);
		}
	
		///////敌人1初始化 以及运动，画敌机1//////////////////
		if(rgEnemy1Cmd.unLayer == 255 && !is_LeverUp)
		{
			rgEnemy1Cmd.unLayer = 0;
			rgEnemy1Cmd.x = (scrWidth - rgEnemy1Cmd.unWidth)/2; //屏幕x方向中间
			rgEnemy1Cmd.y = -scrHeight/5 ;
			
		}
		else {
			rgEnemy1Cmd.y +=10;
			if (rgEnemy1Cmd.y > scrHeight) {
				rgEnemy1Cmd.unLayer = 255;
			}
		}
		DrawSprites(canvas,imgEnemy1,rgEnemy1Cmd);//画敌机1；由imgEnemy1和rgEnemy1Cmd构成敌机1，imgEnemy1代表图条，rgEnemy1Cmd代表绘制位置等

	///////敌人2初始化 以及运动，画敌机2//////////////////
		if (rgEnemy2Cmd.unLayer == 255 && !is_LeverUp) {
			rgEnemy2Cmd.unLayer = 0;
			rgEnemy2Cmd.x = 0;
			rgEnemy2Cmd.y = 0;
		}
		else {
			rgEnemy2Cmd.x +=6;
			rgEnemy2Cmd.y +=10;
			if (rgEnemy2Cmd.y > scrHeight) {
				rgEnemy2Cmd.unLayer = 255;
			}
		}
		DrawSprites(canvas,imgEnemy2,rgEnemy2Cmd);//画敌机2；由imgEnemy2和rgEnemy2Cmd构成敌机2
		
	///////敌人3初始化 以及运动，画敌机3//////////////////
		if (rgEnemy3Cmd.unLayer == 255 && !is_LeverUp) {
			rgEnemy3Cmd.unLayer = 0;
			rgEnemy3Cmd.x = scrWidth - rgEnemy3Cmd.unWidth;//屏幕x方向最右边
			rgEnemy3Cmd.y = scrHeight/3;
		}
		else {
			if (rgEnemy3Cmd.IsRight) {
				rgEnemy3Cmd.x +=8;
			}
			else {
				rgEnemy3Cmd.x -=8;
			}
			if (rgEnemy3Cmd.x < 0) {
				rgEnemy3Cmd.IsRight = true;
			}
			if (rgEnemy3Cmd.x > scrWidth - rgEnemy3Cmd.unWidth) {
				rgEnemy3Cmd.IsRight = false;
			}
		}
		DrawSprites(canvas,imgEnemy1,rgEnemy3Cmd);//画敌机3；由imgEnemy1和rgEnemy3Cmd构成敌机3，敌机1和敌机3使用相同的图片，不同的初始位置和移动
		
		/////画炸弹///////////////
		for(i=0;i<5;i++)
		{
			DrawSprites(canvas,imgBomb,rgBombCmds[i]);
		}	
	
		////画主机///////////////////
		DrawSprites(canvas,imgPlane,rgSpriteCmd);
		
		//主机各个状态下的处理   索引变化以及速度变化
		if (rgSpriteCmd.CurrentState == MainState.enSTAND) {
			rgSpriteCmd.unSpriteIndex = MainTime/2%2;
		}
		else if (rgSpriteCmd.CurrentState == MainState.enWALKRIGHT) {
			rgSpriteCmd.WalkCount++;
			rgSpriteCmd.unSpriteIndex = 3;
			if (rgSpriteCmd.WalkCount < 5) {
				rgSpriteCmd.x += 8;
			}
			else {
				if (IsRightKeyRealess) {
					rgSpriteCmd.CurrentState = MainState.enSTAND;
				}
				else {
					rgSpriteCmd.CurrentState = MainState.enWALKRIGHT;
				}
				rgSpriteCmd.WalkCount = 0;
			}
		}
		else if (rgSpriteCmd.CurrentState == MainState.enWALKLEFT) {
			rgSpriteCmd.WalkCount++;
			if (rgSpriteCmd.WalkCount < 5) {
					rgSpriteCmd.x -= 8;
					rgSpriteCmd.unSpriteIndex = 2;
			}
			else {
				if (IsLeftKeyRealess) {
					rgSpriteCmd.CurrentState = MainState.enSTAND;
				}
				else {
					rgSpriteCmd.CurrentState = MainState.enWALKLEFT;
				}
				rgSpriteCmd.WalkCount = 0;
			}
		}
		else if (rgSpriteCmd.CurrentState == MainState.enWALKUP) {
			if (IsUpKeyRealess && y_sensor > -1 && y_sensor < 1) {
				rgSpriteCmd.CurrentState = MainState.enSTAND;
			}
			else {
				rgSpriteCmd.unSpriteIndex = 0;
				rgSpriteCmd.y -= 10;
			}
		}
		else if (rgSpriteCmd.CurrentState == MainState.enWALKDOWN) {
			if (IsDownKeyRealess && y_sensor > -1 && y_sensor < 1) {
				rgSpriteCmd.CurrentState = MainState.enSTAND;
			}
			else {
				rgSpriteCmd.unSpriteIndex = 0;
				rgSpriteCmd.y += 10;
			}
		}
		else if (rgSpriteCmd.CurrentState == MainState.enHURT) {
			rgSpriteCmd.unSpriteIndex = MainTime%2 - 1; 
			rgSpriteCmd.ProtectCount--;
			if (rgSpriteCmd.ProtectCount == 0) {
				rgSpriteCmd.CurrentState = MainState.enSTAND;
				rgSpriteCmd.IsHurt = false;
			}
		}
	}
   
	///////把图片画成全屏///////////////////////////////////////////
	public  void DisplayImage(Canvas canvas, Bitmap bitmap,int width, int height)
	{
    	if(bitmap!=null)
    	{
    		Rect scrRect = new Rect(); //绘制区域在屏幕上的矩形范围
    	    scrRect.left = 0; //绘制区域左上角顶点在屏幕上的x坐标
    	    scrRect.top = 0; //绘制区域左上角顶点在屏幕上的y坐标
    	    scrRect.right = scrWidth;//width; //绘制区域右下角顶点在屏幕上的x坐标
    	    scrRect.bottom = scrHeight;//height; //绘制区域右下角顶点在屏幕上的y坐标
    	    Rect imgRect = new Rect(); //绘制区域在图片上的矩形范围
    	    imgRect.left = 0; //绘制区域左上角顶点距离图片左上角的x坐标
    	    imgRect.top = 0; //绘制区域左上角顶点距离图片左上角的y坐标
    	    imgRect.right = 0 + width; //绘制区域右下角顶点距离图片左上角的x坐标
    	    imgRect.bottom = 0 + height; //绘制区域右下角顶点距离图片左上角的y坐标
    	    canvas.drawBitmap(bitmap, imgRect, scrRect, null); //绘制图片
    	}	    
	}
	///////////////////////////////////////////////////////////////////////////
	
  ////////画精灵（相对于屏幕的相对位置）//////////////////////
	public   void DrawSprites(Canvas canvas, Bitmap bitmap, SpriteCmd spriteCmd)
	{
		if (spriteCmd.unLayer != 255) {
			DisplayImage(canvas, 
					bitmap, //要画的图片
					spriteCmd.x, //图片左上角画在屏幕上的x位置
					spriteCmd.y, //图片左上角画在屏幕上的y位置
					spriteCmd.unWidth, //图片的宽度
					spriteCmd.unHeight, //图片的高度
					0, //要画的图片在整个图片中的x方向位置
					spriteCmd.unSpriteIndex * spriteCmd.unHeight); //要画的图片在整个图片中的y方向位置
		}
	}
	//画图片
	//参数依次为画布Canvas对象，位图对象，在屏幕上的x坐标，在屏幕上的y坐标，
	//绘制的宽度，绘制的高度，在图片上的x坐标，在图片上的y坐标
	
	public  void DisplayImage(Canvas canvas, Bitmap bitmap, int scrX, int scrY, int width, int height, int imgX, int imgY)
	{
		if(bitmap!=null)
		{
			Rect scrRect = new Rect(); //绘制区域在屏幕上的矩形范围
			scrRect.left = scrX; //绘制区域左上角顶点在屏幕上的x坐标
			scrRect.top = scrY; //绘制区域左上角顶点在屏幕上的y坐标
			scrRect.right = scrX + width; //绘制区域右下角顶点在屏幕上的x坐标
			scrRect.bottom = scrY + height; //绘制区域右下角顶点在屏幕上的y坐标

			Rect imgRect = new Rect(); //绘制区域在图片上的矩形范围
			imgRect.left = imgX; //绘制区域左上角顶点距离图片左上角的x坐标
			imgRect.top = imgY; //绘制区域左上角顶点距离图片左上角的y坐标
			imgRect.right = imgX + width; //绘制区域右下角顶点距离图片左上角的x坐标
			imgRect.bottom = imgY + height; //绘制区域右下角顶点距离图片左上角的y坐标

			canvas.drawBitmap(bitmap, imgRect, scrRect, null); //绘制图片
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//触屏事件
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN: // 当触摸到屏幕
			IsRightKeyRealess = false;
			IsLeftKeyRealess = false;
			IsUpKeyRealess = false;
			IsDownKeyRealess = false;
			switch (GameState)
			{
			case GAMESTATE_MENU:
				onTouchEventInMenu(event); // 菜单界面的触屏事件处理
				break;
			case GAMESTATE_LEVELGUIDE:
				onTouchEventLeverGuide(event);// 选关界面的触屏事件处理
				break;
			case GAMESTATE_HELP:
				onTouchEventHelp(event);// 帮助界面的触屏事件处理
				break;
			case GAMESTATE_GAME:
				onTouchEventInGame(event);// 游戏界面的触屏事件处理
				break;
			case GAMESTATE_DEVELOPER:
				onTouchEventDeveloper(event);
				break;
			case GAMESTATE_RANK:
				onTouchEventRank(event);
				break;
			default:
				break;
			}
			return true;
		case MotionEvent.ACTION_UP:// 当手抬起，离开屏幕
			IsRightKeyRealess = true;
			IsLeftKeyRealess = true;
			IsUpKeyRealess = true;
			IsDownKeyRealess = true;
			return true;
		default:
			break;
		}	
		return super.onTouchEvent(event);
	}
	public void onTouchEventInMenu(MotionEvent event)// 菜单界面的触屏事件处理
	{
		float x, y;
		x = event.getX();
		y = event.getY();
		if (ButtonStart.isClick(x, y)) {
			TurnToLeverGuide();
		}
		else if (ButtonHelp.isClick(x, y)) {
			TurnToHelp();
		}
		else if (ButtonReturn.isClick(x, y)) {
			Is_exit = true;
			CloseGame();
		}
		else if (ButtonDeveloper.isClick(x, y)){
			TurnToDeveloper();
		}
		else if (ButtonRank.isClick(x, y)){
			TurnToRank();
		}
	}
	
	public void onTouchEventLeverGuide(MotionEvent event)// 选关界面的触屏事件处理
	{
		float x, y;
		x = event.getX();
		y = event.getY();
		if (ButtonReturn.isClick(x, y)) {
			mediaPlayer_start.pause();
			TurnToMenu();
		}
		else if (ButtonFirst.isClick(x, y)) {
			LeverNum = 1;
			mediaPlayer_start.pause();
			TurnToGame();
			
		}
		else if (ButtonSecond.isClick(x, y)) {
			LeverNum = 2;
			mediaPlayer_start.pause();
			TurnToGame();
		}
		else if (ButtonThird.isClick(x, y)) {
			LeverNum = 3;
			mediaPlayer_start.pause();
			TurnToGame();
		}
		else if (ButtonFourth.isClick(x, y)) {
			LeverNum = 4;
			mediaPlayer_start.pause();
			TurnToGame();
		}
	}
	
	public void onTouchEventHelp(MotionEvent event)// 帮助界面的触屏事件处理
	{
		float x, y;
		x = event.getX();
		y = event.getY();
		if (ButtonReturn.isClick(x, y)) {
			TurnToMenu();
		}
	}
	
	public void onTouchEventDeveloper(MotionEvent event)
	{
		float x,y;
		x = event.getX();
		y = event.getY();
		if (ButtonReturn.isClick(x, y)) {
			TurnToMenu();
		}
	}
	
	public void onTouchEventRank(MotionEvent event)
	{
		float x,y;
		x = event.getX();
		y = event.getY();
		if (ButtonReturn.isClick(x, y)) {
			TurnToMenu();
		}
	}
	
	public void onTouchEventInGame(MotionEvent event)// 游戏界面的触屏事件处理
	{
		float x, y;
		x = event.getX();
		y = event.getY();
		
	//////处理返回键///////////////////////////////	
		if (ButtonReturn.isClick(x, y)) {
			TurnToMenu();
			mediaPlayer.pause();
		}
	///////////////////////////////////////////	
		
		///////////////声音处理///////////////////////////
		else if(ButtonSound.isClick(x, y)){
			if (is_SoundOn) {
				ButtonSound.setButtonPic(imgSoundOff);
				is_SoundOn = false;
				mediaPlayer.pause();
			}
			else {
				ButtonSound.setButtonPic(imgSoundOn);
				is_SoundOn = true;
			    mediaPlayer.start();
			    mediaPlayer.setLooping(true);
			}
		}
		//////////////////////////////////////////////////
		
		///////////////根据触摸点的位置判断主角飞机的运动状态////////////////////
		if ((x- rgSpriteCmd.x - rgSpriteCmd.unWidth/2) > (y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2)
			&& (x- rgSpriteCmd.x - rgSpriteCmd.unWidth/2) > -(y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2)
			&& !rgSpriteCmd.IsRigtStop && rgSpriteCmd.CurrentState != MainState.enHURT &&!ButtonSound.isClick(x, y)){
			rgSpriteCmd.CurrentState = MainState.enWALKRIGHT;//向右走
		}
		else if ((x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2 < (y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2)) 
				&& (x- rgSpriteCmd.x - rgSpriteCmd.unWidth/2) < -(y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2)
				&& !rgSpriteCmd.IsLeftStop && rgSpriteCmd.CurrentState != MainState.enHURT &&!ButtonSound.isClick(x, y)){
			rgSpriteCmd.CurrentState = MainState.enWALKLEFT;//向左走
		}
		else if ((y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2) <  (x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2) 
			    && (y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2) < - (x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2)
			    && !rgSpriteCmd.IsTopStop && rgSpriteCmd.CurrentState != MainState.enHURT &&!ButtonSound.isClick(x, y)){
			rgSpriteCmd.CurrentState = MainState.enWALKUP;//向上走
		}
		else if ((y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2) >= (x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2) 
				&& (y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2) >= -(x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2)
				&& !rgSpriteCmd.IsDownStop && rgSpriteCmd.CurrentState != MainState.enHURT &&!ButtonSound.isClick(x, y)){
			rgSpriteCmd.CurrentState = MainState.enWALKDOWN;//向下走
		}
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	//两个精灵的碰撞判断
	public Boolean  CheckHitEnemy(SpriteCmd Cmd,SpriteCmd SpCmd) {
		if (Cmd.unLayer!=255 && SpCmd.unLayer != 255) {
			if (Cmd.x + Cmd.unWidth > SpCmd.x && Cmd.x < SpCmd.x + SpCmd.unWidth &&
				    Cmd.y + Cmd.unHeight > SpCmd.y && Cmd.y < SpCmd.y + SpCmd.unHeight) {
					return true;
			}
		} 
		return false;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//////////////////////与重力传感器相关的接口SensorEventListener的方法/////////////////////////////
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		x_sensor = event.values[0]; // 手机横向翻滚
		// x>0 说明当前手机左翻 x<0右翻
		y_sensor = event.values[1]; // 手机纵向翻滚
		// y>0 说明当前手机下翻 y<0上翻
		z_sensor = event.values[2]; // 屏幕的朝向
		// z>0 手机屏幕朝上 z<0 手机屏幕朝下		
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
////////////退出游戏时释放所有资源///////////////////////////////////////////////////////////////////////////////////////
	public void CloseGame()
	{
		FreeAppData(); // 释放应用所有资源
		((picture)this.getContext()).CloseActivity(); // 关闭应用	
	}
	
	public void saveRank(Context context)//写高分榜
	{
		File file=new File(context.getFilesDir(),"Rank.txt");
		try
		{
			FileOutputStream f=new FileOutputStream(file);
			JSONArray array=new JSONArray();
			User.score=Math.max(UserScore, User.score);
			for(UserItem item:userItems)
			{
				try {
					JSONObject obj=new JSONObject();
					obj.put("name", item.name);
					obj.put("score", item.score);
					array.put(obj);
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			f.write(array.toString().getBytes());
			f.close();
		}
		catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        }   
	}
	
	public void readRank(Context context)//读高分榜
	{
		String tmp=null;
		File file=new File(context.getFilesDir(),"Rank.txt");
		try
		{
			FileInputStream f=new FileInputStream(file);
			BufferedReader b=new BufferedReader(new InputStreamReader(f));
			tmp=b.readLine();
			b.close();
			f.close();
		}
		catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        }  
        JSONArray array=null;
        try {
        	array=new JSONArray(tmp);
        	
		} catch (Exception e) {
			array=new JSONArray();
		}
        List<UserItem>items=new ArrayList<PictureView.UserItem>();
        for(int i=0;i<array.length();i++)
        {
        	try {
				UserItem  item=new UserItem();
				item.name=array.getJSONObject(i).getString("name");
				item.score=array.getJSONObject(i).getInt("score");
				items.add(item);
				if(item.name.equals(User.name))
				{
					User.score=Math.max(item.score, User.score);
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
        }
        userItems.clear();
        userItems.addAll(items);
        userItems.remove(User);

        userItems.add(User);
       Collections.sort(userItems, new Comparator<UserItem>() {
    	   @Override
    	public int compare(UserItem object1, UserItem object2) {
    		if(object1.score<object2.score)
    			return 1;
    		else 
    			if(object1.score>object2.score)
        			return -1;
    		return 0;
    	}
       	});      
	}
	

	public void FreeAppData()
	{
		//释放图片资源
		ReleaseImage(imgPlane);
		ReleaseImage(imgStar);
		ReleaseImage(imgStar);
		ReleaseImage(imgEnemy1);
		ReleaseImage(imgEnemy2);
		ReleaseImage(imgBomb);
		ReleaseImage(imgPassLerver);
		ReleaseImage(imgBloodBg);
		ReleaseImage(imgBlood);
		ReleaseImage(imgBackground);
		ReleaseImage(imgStart);
		ReleaseImage(imgHelp);
		ReleaseImage(imgFirst);
		ReleaseImage(imgSecond);
		ReleaseImage(imgHelpDisplay);
		ReleaseImage(imgReturn);
		ReleaseImage(imgSoundOn);
		ReleaseImage(imgSoundOff);
			
		if (mediaPlayer != null)
		{
			mediaPlayer.stop(); // 停止播放声音
			mediaPlayer.release(); // 释放mediaPlayer资源
			mediaPlayer = null; // 对象置空
		}		
		// 释放声音资源
		if (mediaPlayer_start != null)
		{
			mediaPlayer_start.stop(); // 停止播放声音
			mediaPlayer_start.release(); // 释放mediaPlayer资源
			mediaPlayer_start = null; // 对象置空
		}
	}
	
	public static void FreeMedia()
	{
		if (mediaPlayer != null)
		{
			mediaPlayer.stop(); // 停止播放声音
			mediaPlayer.release(); // 释放mediaPlayer资源
			mediaPlayer = null; // 对象置空
		}
		if (mediaPlayer_start != null)
		{
			mediaPlayer_start.stop(); // 停止播放声音
			mediaPlayer_start.release(); // 释放mediaPlayer资源
			mediaPlayer_start = null; // 对象置空
		}
	}
	
	public static Bitmap ReleaseImage(Bitmap bitmap)
	{
		if (bitmap != null && !bitmap.isRecycled())
        {
			//bitmap.recycle(); // 用于回收该bitmap所占用的内存
			bitmap = null; // 将bitmap置空
        }		
		return null; // 返回空，将原图片对象置为空
	}		
	class UserItem
	{
		String name;
		int score;
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return name;
		}
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof UserItem))
				return false;
			return ((UserItem)o).name.equals(name);
		}
	}
} 