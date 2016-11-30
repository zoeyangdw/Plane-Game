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
	
	private  int scrWidth;//��Ļ�Ŀ��
	private  int scrHeight;//��Ļ�ĸ߶�
	private  int MainTime = 0; //����ʱ��
	private  int Power = 0;
	private  int UserScore;
	
	private  Bitmap imgBackground = null;//�˵��ı���ͼƬ
	//�˵��ϵİ�ť
	private ButtonUtil ButtonMenu = null;//���˵�
	private ButtonUtil ButtonStart = null;//��ʼ��ť
	private ButtonUtil ButtonHelp = null;//������ť
	private ButtonUtil ButtonRank = null;//�߷ְ�
	private ButtonUtil ButtonDeveloper = null;//������
	
	private  Bitmap imgMenu = null;
	private  Bitmap imgStart = null;//��ʼ��ť��Ӧ��ͼ
	private  Bitmap imgHelp = null;//������ť��Ӧ��ͼƬ
	private  Bitmap imgRank = null;
	private  Bitmap imgDeveloper = null;
	//ѡ�صİ�ť
	private ButtonUtil ButtonFirst = null;//ѡ�ؽ����һ�ض�Ӧ��ť
	private ButtonUtil ButtonSecond = null;//ѡ�ؽ���ڶ��ض�Ӧ��ť
	private ButtonUtil ButtonThird = null;
	private ButtonUtil ButtonFourth = null;
	private Bitmap imgFirst = null;//��һ�ض�ӦͼƬ
	private Bitmap imgSecond = null;//�ڶ��ض�ӦͼƬ
	private Bitmap imgThird = null;
	private Bitmap imgFourth = null;
	//����������ͼƬ����ͼƬʵ�ֵģ�
	private Bitmap imgHelpDisplay = null;//����ͼƬ
	private Bitmap imgDeveloperDisplay = null;
	//���ذ�ť
	private ButtonUtil ButtonReturn = null;//���ذ�ť
	private Bitmap imgReturn = null;//���ذ�ť��ӦͼƬ
	//������ť
	private ButtonUtil ButtonSound = null;//�������ذ�ť
	private Bitmap imgSoundOn = null;//��������ӦͼƬ
	private Bitmap imgSoundOff = null;//��������ӦͼƬ
	private Boolean is_SoundOn = true;//�����Ƿ�
	private Boolean is_LeverUp = false;//�Ƿ����
	private Boolean is_Dead = false;
	
	private static MediaPlayer mediaPlayer = null;//�������Ŷ���
	private static MediaPlayer mediaPlayer_start = null;
	
	private Bitmap imgPower = null;

	
	public MyBroadcastReceiver myReceiver = new MyBroadcastReceiver();
	
	private int DeadEnemyCnt = 0;//��������������     ���������˸����ﵽĳ����ʱ   ����
	private int LeverNum;//����������
	private int LeverCnt = 0;//���ؽ���ļ�����
	
	public String ClientName;
	
	private int Grade = 0;
	
	private Paint textpaint = new Paint();
	
	public enum GameState//��Ϸ״̬
	{
		GAMESTATE_MENU,			//�˵�
		GAMESTATE_LEVELGUIDE,	//������ʾ����ʾ��һ�أ��ڶ��� ����
		GAMESTATE_HELP,         //����
		GAMESTATE_GAME,			//��Ϸ
		GAMESTATE_DEVELOPER,
		GAMESTATE_RANK,
	}
	public  GameState GameState; // ��Ϸ״̬
	
	public static final int STAR_NUM = 30;
	public SpriteCmd rgSpriteCmd = new SpriteCmd();////����SpriteCmd�����ʵ����
	public SpriteCmd rgCmdStar[] = new SpriteCmd[30];//���Ƕ���
	public SpriteCmd rgBulletCmd[] = new SpriteCmd[20];//�����ӵ�����
	public SpriteCmd rgEnemyBulletCmd[];
	public SpriteCmd rgEnemy1Cmd = new SpriteCmd();//����1��SpriteCmd�����
	public SpriteCmd rgEnemy2Cmd = new SpriteCmd();//����2����
	public SpriteCmd rgEnemy3Cmd = new SpriteCmd();//����3����
	public SpriteCmd rgBombCmds[] = new SpriteCmd[5];//��������ʱ��ը�Ķ���
	public SpriteCmd rgBackground1 = new SpriteCmd();//��������1
	public SpriteCmd rgBackground2 = new SpriteCmd();//��������2
	public SpriteCmd rgLerver = new SpriteCmd();//����ϲ���ء�����
	public SpriteCmd rgBloodBg = new SpriteCmd();//��Ѫ���򡱶���
	public SpriteCmd rgBlood = new SpriteCmd();//��Ѫ��������
	
	//��Ϸ���ͼƬ
	public Bitmap imgPlane;//���Ƿɻ���ӦͼƬ
	public Bitmap imgStar;//���Ƕ�ӦͼƬ
	public Bitmap imgBullet;//�����ӵ�ͼƬ
	public Bitmap imgEnemyBullet;//�л��ӵ�ͼƬ
	public Bitmap imgEnemy1;//����1��ӦͼƬ
	public Bitmap imgEnemy2;//����2����ͼƬ
	public Bitmap imgBomb;	//ը����ӦͼƬ
	public Bitmap imgPassLerver;//����ͼƬ
	public Bitmap imgBloodBg;//Ѫ����ͼƬ
	public Bitmap imgBlood;//Ѫ��ͼƬ
	public Bitmap imgDeadLerver;
	public Bitmap imgRankBackground = null;
	
	public Bitmap battery;
	
	public boolean IsRightKeyRealess = true;//�Ҽ��Ƿ��ͷ�
	public boolean IsLeftKeyRealess = true;//����Ƿ��ͷ�
	public boolean IsUpKeyRealess = true;//�ϼ��Ƿ��ͷ�
	public boolean IsDownKeyRealess = true;//�¼��Ƿ��ͷ�
	
	private SensorManager sm;//������������
	private Sensor sensor;//��������������
	public float x_sensor = 0;//����������x�����λ��ƫ��
	public float y_sensor = 0;//����������y�����λ��ƫ��
	public float z_sensor = 0;//����������z�����λ��ƫ��
	
	private int bulletnum;
	
	private UserItem User;
	private List<UserItem>userItems=new ArrayList<PictureView.UserItem>();
	
	public boolean  Is_exit = false;//�Ƿ��˳���Ϸ
	
	
	public PictureView(Context context,String s)//pictureView���캯��
	{
	      super(context);//���ø���Ĺ��췽��
	      MainTime = 0; // ����ʱ����
	      this.context = context;
	      this.User = new UserItem();
	      this.User.name=s;
	      File file = new File(context.getFilesDir(),"Rank.txt");
	      if(file.exists())
	    	  readRank(context);
	      sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		  sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// �õ�һ������������ʵ��
		  sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);//��ϵͳע���������������
	      new Thread(this).start(); //�����߳�    ͨ��start()�����ҵ�run()����
      
	      Is_exit = false;
	}
	
	public void TurnToMenu()//����˵�
	{
		//����˵��ı���ͼƬ
		imgBackground =((BitmapDrawable)getResources().getDrawable(R.drawable.bg1)).getBitmap();
		//���롰��ʼ��Ϸ����ťͼƬ
		if (imgStart == null)
		{
			imgStart = ((BitmapDrawable)getResources().getDrawable(R.drawable.start)).getBitmap();//����ʼ��Ϸ����ͼƬ
		}
		//���롰��������ťͼƬ
		if (imgHelp == null)
		{
			imgHelp = ((BitmapDrawable)getResources().getDrawable(R.drawable.help)).getBitmap();//����Ϸ��������ͼƬ
		}
		//���롰���ء���ťͼƬ
		if (imgReturn == null)
		{
			imgReturn = ((BitmapDrawable)getResources().getDrawable(R.drawable.back)).getBitmap();//�����ء���ͼƬ
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
		//ʵ�����ԡ���ʼ��Ϸ��ͼƬ�İ�ť��ȷ���˸ð�ť��ͼƬ�ͻ�����Ļ�ϵ�λ��
		ButtonStart = new ButtonUtil(imgStart,(scrWidth - imgStart.getWidth())/2,280);
		//ʵ�����ԡ�������ͼƬ�İ�ť��ȷ���˸ð�ť��ͼƬ�ͻ�����Ļ�ϵ�λ��
		ButtonHelp = new ButtonUtil(imgHelp,(scrWidth - imgHelp.getWidth())/2,360);//�ѡ���Ϸ��������ͼƬ�밴ť��
		//ʵ�����ԡ����ء�ͼƬ�İ�ť��ȷ���˸ð�ť��ͼƬ�ͻ�����Ļ�ϵ�λ��
		ButtonMenu = new ButtonUtil(imgMenu,(scrWidth - imgMenu.getWidth())/2,200);
		ButtonRank = new ButtonUtil(imgRank,(scrWidth - imgRank.getWidth())/2,440);
		ButtonDeveloper = new ButtonUtil(imgDeveloper,(scrWidth - imgDeveloper.getWidth())/2,520);
		ButtonReturn = new ButtonUtil(imgReturn,scrWidth - imgReturn.getWidth() - 10,scrHeight - 50);//�ѡ����ء���ͼƬ�밴ť��
		//������Ϸ��״̬Ϊ�˵�״̬
		GameState = GameState.GAMESTATE_MENU; //״̬ת���ɲ˵�״̬				

	}
	
	public void TurnToLeverGuide()//����ѡ��
	{
		ReleaseImage(imgBackground);
		imgBackground =((BitmapDrawable)getResources().getDrawable(R.drawable.bg2)).getBitmap();//����ͼƬ
		
		if (imgFirst == null)
		{
			imgFirst = ((BitmapDrawable)getResources().getDrawable(R.drawable.firstlevel)).getBitmap();//����1�ء���ͼƬ
		}
		if (imgSecond == null)
		{
			imgSecond = ((BitmapDrawable)getResources().getDrawable(R.drawable.secondlevel)).getBitmap();//����2�ء���ͼƬ
		}
		if (imgThird == null)
		{
			imgThird = ((BitmapDrawable)getResources().getDrawable(R.drawable.thirdlevel)).getBitmap();//����3�ء���ͼƬ
		}
		if (imgFourth == null)
		{
			imgFourth = ((BitmapDrawable)getResources().getDrawable(R.drawable.fourthlevel)).getBitmap();//����4�ء���ͼƬ
		}
		//ʵ�����ԡ���һ�ء�ͼƬ�İ�ť��ȷ���˸ð�ť��ͼƬ�ͻ�����Ļ�ϵ�λ��
		ButtonFirst = new ButtonUtil(imgFirst,(scrWidth - imgFirst.getWidth())/2,100);
		//ʵ�����ԡ��ڶ��ء�ͼƬ�İ�ť��ȷ���˸ð�ť��ͼƬ�ͻ�����Ļ�ϵ�λ��
		ButtonSecond = new ButtonUtil(imgSecond,(scrWidth - imgSecond.getWidth())/2,200);
		ButtonThird = new ButtonUtil(imgThird,(scrWidth - imgThird.getWidth())/2,300);
		ButtonFourth = new ButtonUtil(imgFourth,(scrWidth - imgThird.getWidth())/2,400);
		
		mediaPlayer_start = MediaPlayer.create(context, R.raw.stars);
	    mediaPlayer_start.start();
	    mediaPlayer_start.setLooping(true);
	    
		GameState = GameState.GAMESTATE_LEVELGUIDE;
		
		Grade = 0;
	}
	
	public void TurnToHelp()//�������
	{
		//�����������ͼƬ
		if (imgHelpDisplay == null)
		{
			imgHelpDisplay = ((BitmapDrawable)getResources().getDrawable(R.drawable.helpdisplay)).getBitmap();
		}	
		//����Ϸ״̬Ϊ����״̬
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
	
	public void TurnToGame()//������Ϸ
	{   
		//������Ϸ���ȫ��ͼƬ��Դ
		LoadResource();		
		InitLever();//
		
		GameState = GameState.GAMESTATE_GAME;	
		
		//////////Ϊ����ͼ������SpriteCmd�����������Ϊ����ͼ�趨����λ��ͬʱ������////		
		rgBackground1.x = 0;
		rgBackground1.y = -scrHeight;
		rgBackground1.unLayer = 0;
		rgBackground1.unWidth = scrWidth;  //����ͼ�Ŀ����Ϊȫ��
		rgBackground1.unHeight = scrHeight;//����ͼ�Ŀ����Ϊȫ��
		
		rgBackground2.x = 0;
		rgBackground2.y = 0;
		rgBackground2.unLayer = 0;
		rgBackground2.unWidth = scrWidth;//����ͼ�Ŀ����Ϊȫ��
		rgBackground2.unHeight = scrHeight;	//����ͼ�Ŀ����Ϊȫ��
	/////////////////////////////////////////////////////////////	
		//�������صİ�ť
		imgSoundOn =((BitmapDrawable)getResources().getDrawable(R.drawable.bgsoundon)).getBitmap();
		imgSoundOff =((BitmapDrawable)getResources().getDrawable(R.drawable.bgsoundoff)).getBitmap();
		ButtonSound = new ButtonUtil(imgSoundOn,10,scrHeight - 20);
		
		//����ϲ���ء�����
		rgLerver.unWidth = 200;
		rgLerver.unHeight = 100;
		rgLerver.unLayer = 255;
		//Ѫ��
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
		//���������롢����
		mediaPlayer = MediaPlayer.create(context, R.raw.game);
	    mediaPlayer.start();
	    mediaPlayer.setLooping(true);
	    UserScore = 0;
		is_LeverUp = false;//�Ƿ����
		is_Dead = false;
	}
	
	void InitLever()
	{
		int i;
		switch (LeverNum) {
		case 1:   //��һ��
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
					
			//���˳�ʼ��
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
		case 2:  //�ڶ���
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
			
			//���˳�ʼ��
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
			
			//��ʼ���ӵ�
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
					
			//���˳�ʼ��
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
			
			//��ʼ���ӵ�
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
					
			//���˳�ʼ��
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
			
			//��ʼ���ӵ�
			rgEnemyBulletCmd = new SpriteCmd[30];
			bulletnum = 30;
			break;
		default:
			break;
		}
		
		////���ǵĳ�ʼ��///////////////////////////////
		for (i = 0; i < STAR_NUM; i += 1)
		{
			rgCmdStar[i] = new SpriteCmd();//ÿ�����Ƕ�ʵ����SpriteCmd�����
		}
		for (i = 0; i < STAR_NUM; i += 2) //ż�������ǵ�ͼƬ����ֵΪ0
		{
			rgCmdStar[i].unSpriteIndex = 0;
		}
		for (i = 1; i < STAR_NUM; i += 2) //���������ǵ�ͼƬ����ֵΪ1
		{
			rgCmdStar[i].unSpriteIndex = 1;
		}
		
		for (i = 0; i < STAR_NUM; i ++)
		{
			rgCmdStar[i].unWidth = 3; //ÿ������ͼƬ�Ŀ�Ⱥ͸߶�
			rgCmdStar[i].unHeight = 3;
			rgCmdStar[i].x = scrWidth/STAR_NUM*i + 10;//ÿ������ͼƬ��������Ļ�ϵ�λ��
			rgCmdStar[i].y = scrHeight/STAR_NUM*i;
		}	
		
		
		//////////////ÿ���ӵ�ʵ����SpriteCmd����󣬳�ʼ��////////
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
      
		//////////////ÿ��ը��ʵ����SpriteCmd����󣬳�ʼ��////////
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
		//�ͷ�ѡ�ؽ���ı���ͼƬ
		ReleaseImage(imgBackground);
		//������Ϸ�ı���ͼƬ
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
		
		//���빧ϲ���ص�ͼƬ
		imgPassLerver = ((BitmapDrawable)getResources().getDrawable(R.drawable.level_up)).getBitmap();
		//����Ѫ���ı���ͼƬ
		imgBloodBg = ((BitmapDrawable)getResources().getDrawable(R.drawable.bloodbg)).getBitmap();
		//����Ѫ��ͼƬ
		imgBlood = ((BitmapDrawable)getResources().getDrawable(R.drawable.blood)).getBitmap();
		//��������ͼƬ
		imgStar = ((BitmapDrawable)getResources().getDrawable(R.drawable.star)).getBitmap();
		//����ɻ�ͼƬ
		imgPlane = ((BitmapDrawable)getResources().getDrawable(R.drawable.plane)).getBitmap();//����
		//�����ӵ�ͼƬ
		imgBullet = ((BitmapDrawable)getResources().getDrawable(R.drawable.bullet)).getBitmap();
		//
		imgEnemyBullet = ((BitmapDrawable)getResources().getDrawable(R.drawable.enemybullet)).getBitmap();

		//���뱬ըͼƬ
		imgBomb = ((BitmapDrawable)getResources().getDrawable(R.drawable.zhadan)).getBitmap();
		
		imgDeadLerver = ((BitmapDrawable)getResources().getDrawable(R.drawable.gameover)).getBitmap();
		
	}
	
    public void run()
	{		
		long frameElapse, frameTick;
		try
        {
            frameTick = System.currentTimeMillis(); // ����ִ����ʼʱ��
            if(!Is_exit)
            {
            	  while (true)
                  {
                      frameElapse = System.currentTimeMillis() - frameTick; // ��һ֡���õ�ʱ��

                      if (frameElapse < 80) // �������80����
                      {
                          Thread.sleep((80 - frameElapse)); // �߳����ߣ���֤��һ֡��ʱ��Ϊ80����
                      }
      				  postInvalidate(); // ˢ����Ļ     				   
                      frameTick = System.currentTimeMillis(); // ����ִ�н���ʱ��
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
        scrWidth = w; // ��ȡ��Ļ���
        scrHeight = h; // ��ȡ��Ļ�߶�
		super.onSizeChanged(w, h, oldw, oldh);
		TurnToMenu();//����˵�״̬
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
///////������Ļ    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
	public void onDraw(Canvas canvas)
	{
		Power = myReceiver.curPower;
		super.onDraw(canvas);
		MainTime ++; // ����ʱʱ������
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
			case GAMESTATE_MENU: //���˵�
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
		//������ͼ
		DisplayImage(canvas, imgBackground,imgBackground.getWidth(), imgBackground.getHeight());
		//����ť����ʼ��Ϸ��
		ButtonStart.drawButton(canvas, null);
		//����ť����Ϸ������
		ButtonHelp.drawButton(canvas, null);
		//����ť�����ء�
		ButtonReturn.drawButton(canvas, null);
		ButtonMenu.drawButton(canvas,null);
		ButtonRank.drawButton(canvas, null);
		ButtonDeveloper.drawButton(canvas,null);
	}
	
	public void DrawLeverGuide(Canvas canvas)
	{
		//������ͼ
		DisplayImage(canvas, imgBackground,imgBackground.getWidth(), imgBackground.getHeight());
		//����ť����һ�ء�
		ButtonFirst.drawButton(canvas, null);
		//����ť���ڶ��ء�
		ButtonSecond.drawButton(canvas, null);
		//����ť�����ء�
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
		//����paint��drawTextֱ������Ļ�ϻ���string
		
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
		//�����ͼ��߽�
		if (rgSpriteCmd.x < 0) {
			rgSpriteCmd.IsLeftStop = true;
			rgSpriteCmd.x = 0;
		}
		else {
			rgSpriteCmd.IsLeftStop = false;
		}
		
		//�����ͼ�ұ߽�
		if (rgSpriteCmd.x + rgSpriteCmd.unWidth > scrWidth) {
			rgSpriteCmd.IsRigtStop = true;
			rgSpriteCmd.x = scrWidth - rgSpriteCmd.unWidth;
		}
		else {
			rgSpriteCmd.IsRigtStop = false;
		}
		//�����ͼ�ϱ߽�
		if (rgSpriteCmd.y < 0) {
			rgSpriteCmd.IsTopStop = true;
			rgSpriteCmd.y = 0;
		}
		else {
			rgSpriteCmd.IsTopStop = false;
		}
		
		//�����ͼ�±߽�
		if (rgSpriteCmd.y + rgSpriteCmd.unHeight > scrHeight) {
			rgSpriteCmd.IsDownStop = true;
			rgSpriteCmd.y = scrHeight - rgSpriteCmd.unHeight;
		}
		else {
			rgSpriteCmd.IsDownStop = false;
		}
		
		////������л�����ײ�жϼ�����///////////////////////////////
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
				
		/////�����ӵ���л�1����ײ////////////////////////////////
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
		
		////////�����ӵ���л�2����ײ//////////////////////////////
		for(i=0;i<20;i++)
		{
			if (CheckHitEnemy(rgBulletCmd[i], rgEnemy2Cmd)) {
				//���ӵ���л�����ײʱ
				rgBulletCmd[i].unLayer = 255;//��ײ���ӵ���ʧ
				rgEnemy2Cmd.unLayer = 255;//��ײ�ĵл�����ʧ
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
		
		/////�����ӵ���л�3����ײ/////////////////////////////////
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
		
		//�л��ӵ���������ײ
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

		/////ը���Ĵ���////////////////////////////////////
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
		
		////////////������/////////////////////////
		DrawSprites(canvas,imgBackground,rgBackground1);//�ڶ���������Ҫ����ͼƬ������������Ҫ������Ļ�ϵ�λ�õ���Ϣ
		DrawSprites(canvas,imgBackground,rgBackground2);
		rgBackground1.y += 10;
		rgBackground2.y += 10;
		
		/////////////��Ѫ��/////////////////
		DrawSprites(canvas,imgBloodBg,rgBloodBg);
		DrawSprites(canvas,imgBlood,rgBlood);
		
		////////////������//////////////////
		
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
		
		///////////���÷�///////
		textpaint.setColor(Color.WHITE);
		textpaint.setTextSize(20);
		String score = Integer.toString(UserScore);
		String defen = "�÷�";
		canvas.drawText(defen, 350, 60, textpaint);
		canvas.drawText(score, 410, 60, textpaint);


		///////�����ж�//////////////////////////
		if (DeadEnemyCnt > 30 && LeverNum != 4) {
			is_LeverUp = true;
		}
		
		////���ش���    �������л��������ӵ�ȫ����ʧ   ��������ͣ��     ��ʼ����ϲ���� �Լ�������
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
			DrawSprites(canvas,imgPassLerver,rgLerver);//��������ϲ���ء�
			if(LeverCnt == 40)//����������40ʱ��ȥ��һ��
			{
				LeverNum +=1;
				LeverCnt = 0;
				Grade = Grade + DeadEnemyCnt + rgBlood.unWidth;
				TurnToGame();
			}
		}
		
		//ʧ�ܴ���
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
			DrawSprites(canvas,imgDeadLerver,rgLerver);//��������Ϸʧ�ܡ�		
			if(LeverCnt == 40)//����������40ʱ�ص��˵�
			{
				TurnToMenu();
			}			
		}
		
		//////ÿ֡�����ǵ�λ�ò�������////////////////////////////
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
		
		///////�������ء��͡���������ť//////////
		ButtonReturn.drawButton(canvas, null);
		ButtonSound.drawButton(canvas, null);
		DisplayImage(canvas,battery,450,20,30,15,0,0);
	   
		/////�����ӵ����³�ʼ��///////////////////////
		for(i = 0;i<20;i++)
		{
			if (rgBulletCmd[i].unLayer == 255 && MainTime%2 == 0 && !is_LeverUp) {
				rgBulletCmd[i].unLayer = 0;
				rgBulletCmd[i].x = rgSpriteCmd.x + 10;
				rgBulletCmd[i].y = rgSpriteCmd.y - 10;
				break;
			}
		}
		
		//�л��ӵ���ʼ��
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
		
		/////�����ӵ����˶��Լ��߳���Ļ��Ĵ���
		for(i = 0;i<20;i++)
		{
			if (rgBulletCmd[i].unLayer != 255) {
				rgBulletCmd[i].y -= 35;
				if (rgBulletCmd[i].y < -10) {
					rgBulletCmd[i].unLayer = 255;
				}
			}
		}
		
		//�л��ӵ����˶��Լ��߳���Ļ
		for (i = 0;i < bulletnum;i++)
		{
			if (rgEnemyBulletCmd[i].unLayer != 255) {
				rgEnemyBulletCmd[i].y += 105;
				if (rgEnemyBulletCmd[i].y > 650) {
					rgEnemyBulletCmd[i].unLayer = 255;
				}
			}
		}
		
		///���ӵ�
		for(i=0;i<20;i++)
		{
			DrawSprites(canvas,imgBullet,rgBulletCmd[i]);
		}
		for(i=0;i<bulletnum;i++)
		{
			DrawSprites(canvas,imgEnemyBullet,rgEnemyBulletCmd[i]);
		}
	
		///////����1��ʼ�� �Լ��˶������л�1//////////////////
		if(rgEnemy1Cmd.unLayer == 255 && !is_LeverUp)
		{
			rgEnemy1Cmd.unLayer = 0;
			rgEnemy1Cmd.x = (scrWidth - rgEnemy1Cmd.unWidth)/2; //��Ļx�����м�
			rgEnemy1Cmd.y = -scrHeight/5 ;
			
		}
		else {
			rgEnemy1Cmd.y +=10;
			if (rgEnemy1Cmd.y > scrHeight) {
				rgEnemy1Cmd.unLayer = 255;
			}
		}
		DrawSprites(canvas,imgEnemy1,rgEnemy1Cmd);//���л�1����imgEnemy1��rgEnemy1Cmd���ɵл�1��imgEnemy1����ͼ����rgEnemy1Cmd�������λ�õ�

	///////����2��ʼ�� �Լ��˶������л�2//////////////////
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
		DrawSprites(canvas,imgEnemy2,rgEnemy2Cmd);//���л�2����imgEnemy2��rgEnemy2Cmd���ɵл�2
		
	///////����3��ʼ�� �Լ��˶������л�3//////////////////
		if (rgEnemy3Cmd.unLayer == 255 && !is_LeverUp) {
			rgEnemy3Cmd.unLayer = 0;
			rgEnemy3Cmd.x = scrWidth - rgEnemy3Cmd.unWidth;//��Ļx�������ұ�
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
		DrawSprites(canvas,imgEnemy1,rgEnemy3Cmd);//���л�3����imgEnemy1��rgEnemy3Cmd���ɵл�3���л�1�͵л�3ʹ����ͬ��ͼƬ����ͬ�ĳ�ʼλ�ú��ƶ�
		
		/////��ը��///////////////
		for(i=0;i<5;i++)
		{
			DrawSprites(canvas,imgBomb,rgBombCmds[i]);
		}	
	
		////������///////////////////
		DrawSprites(canvas,imgPlane,rgSpriteCmd);
		
		//��������״̬�µĴ���   �����仯�Լ��ٶȱ仯
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
   
	///////��ͼƬ����ȫ��///////////////////////////////////////////
	public  void DisplayImage(Canvas canvas, Bitmap bitmap,int width, int height)
	{
    	if(bitmap!=null)
    	{
    		Rect scrRect = new Rect(); //������������Ļ�ϵľ��η�Χ
    	    scrRect.left = 0; //�����������ϽǶ�������Ļ�ϵ�x����
    	    scrRect.top = 0; //�����������ϽǶ�������Ļ�ϵ�y����
    	    scrRect.right = scrWidth;//width; //�����������½Ƕ�������Ļ�ϵ�x����
    	    scrRect.bottom = scrHeight;//height; //�����������½Ƕ�������Ļ�ϵ�y����
    	    Rect imgRect = new Rect(); //����������ͼƬ�ϵľ��η�Χ
    	    imgRect.left = 0; //�����������ϽǶ������ͼƬ���Ͻǵ�x����
    	    imgRect.top = 0; //�����������ϽǶ������ͼƬ���Ͻǵ�y����
    	    imgRect.right = 0 + width; //�����������½Ƕ������ͼƬ���Ͻǵ�x����
    	    imgRect.bottom = 0 + height; //�����������½Ƕ������ͼƬ���Ͻǵ�y����
    	    canvas.drawBitmap(bitmap, imgRect, scrRect, null); //����ͼƬ
    	}	    
	}
	///////////////////////////////////////////////////////////////////////////
	
  ////////�����飨�������Ļ�����λ�ã�//////////////////////
	public   void DrawSprites(Canvas canvas, Bitmap bitmap, SpriteCmd spriteCmd)
	{
		if (spriteCmd.unLayer != 255) {
			DisplayImage(canvas, 
					bitmap, //Ҫ����ͼƬ
					spriteCmd.x, //ͼƬ���Ͻǻ�����Ļ�ϵ�xλ��
					spriteCmd.y, //ͼƬ���Ͻǻ�����Ļ�ϵ�yλ��
					spriteCmd.unWidth, //ͼƬ�Ŀ��
					spriteCmd.unHeight, //ͼƬ�ĸ߶�
					0, //Ҫ����ͼƬ������ͼƬ�е�x����λ��
					spriteCmd.unSpriteIndex * spriteCmd.unHeight); //Ҫ����ͼƬ������ͼƬ�е�y����λ��
		}
	}
	//��ͼƬ
	//��������Ϊ����Canvas����λͼ��������Ļ�ϵ�x���꣬����Ļ�ϵ�y���꣬
	//���ƵĿ�ȣ����Ƶĸ߶ȣ���ͼƬ�ϵ�x���꣬��ͼƬ�ϵ�y����
	
	public  void DisplayImage(Canvas canvas, Bitmap bitmap, int scrX, int scrY, int width, int height, int imgX, int imgY)
	{
		if(bitmap!=null)
		{
			Rect scrRect = new Rect(); //������������Ļ�ϵľ��η�Χ
			scrRect.left = scrX; //�����������ϽǶ�������Ļ�ϵ�x����
			scrRect.top = scrY; //�����������ϽǶ�������Ļ�ϵ�y����
			scrRect.right = scrX + width; //�����������½Ƕ�������Ļ�ϵ�x����
			scrRect.bottom = scrY + height; //�����������½Ƕ�������Ļ�ϵ�y����

			Rect imgRect = new Rect(); //����������ͼƬ�ϵľ��η�Χ
			imgRect.left = imgX; //�����������ϽǶ������ͼƬ���Ͻǵ�x����
			imgRect.top = imgY; //�����������ϽǶ������ͼƬ���Ͻǵ�y����
			imgRect.right = imgX + width; //�����������½Ƕ������ͼƬ���Ͻǵ�x����
			imgRect.bottom = imgY + height; //�����������½Ƕ������ͼƬ���Ͻǵ�y����

			canvas.drawBitmap(bitmap, imgRect, scrRect, null); //����ͼƬ
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//�����¼�
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN: // ����������Ļ
			IsRightKeyRealess = false;
			IsLeftKeyRealess = false;
			IsUpKeyRealess = false;
			IsDownKeyRealess = false;
			switch (GameState)
			{
			case GAMESTATE_MENU:
				onTouchEventInMenu(event); // �˵�����Ĵ����¼�����
				break;
			case GAMESTATE_LEVELGUIDE:
				onTouchEventLeverGuide(event);// ѡ�ؽ���Ĵ����¼�����
				break;
			case GAMESTATE_HELP:
				onTouchEventHelp(event);// ��������Ĵ����¼�����
				break;
			case GAMESTATE_GAME:
				onTouchEventInGame(event);// ��Ϸ����Ĵ����¼�����
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
		case MotionEvent.ACTION_UP:// ����̧���뿪��Ļ
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
	public void onTouchEventInMenu(MotionEvent event)// �˵�����Ĵ����¼�����
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
	
	public void onTouchEventLeverGuide(MotionEvent event)// ѡ�ؽ���Ĵ����¼�����
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
	
	public void onTouchEventHelp(MotionEvent event)// ��������Ĵ����¼�����
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
	
	public void onTouchEventInGame(MotionEvent event)// ��Ϸ����Ĵ����¼�����
	{
		float x, y;
		x = event.getX();
		y = event.getY();
		
	//////�����ؼ�///////////////////////////////	
		if (ButtonReturn.isClick(x, y)) {
			TurnToMenu();
			mediaPlayer.pause();
		}
	///////////////////////////////////////////	
		
		///////////////��������///////////////////////////
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
		
		///////////////���ݴ������λ���ж����Ƿɻ����˶�״̬////////////////////
		if ((x- rgSpriteCmd.x - rgSpriteCmd.unWidth/2) > (y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2)
			&& (x- rgSpriteCmd.x - rgSpriteCmd.unWidth/2) > -(y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2)
			&& !rgSpriteCmd.IsRigtStop && rgSpriteCmd.CurrentState != MainState.enHURT &&!ButtonSound.isClick(x, y)){
			rgSpriteCmd.CurrentState = MainState.enWALKRIGHT;//������
		}
		else if ((x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2 < (y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2)) 
				&& (x- rgSpriteCmd.x - rgSpriteCmd.unWidth/2) < -(y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2)
				&& !rgSpriteCmd.IsLeftStop && rgSpriteCmd.CurrentState != MainState.enHURT &&!ButtonSound.isClick(x, y)){
			rgSpriteCmd.CurrentState = MainState.enWALKLEFT;//������
		}
		else if ((y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2) <  (x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2) 
			    && (y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2) < - (x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2)
			    && !rgSpriteCmd.IsTopStop && rgSpriteCmd.CurrentState != MainState.enHURT &&!ButtonSound.isClick(x, y)){
			rgSpriteCmd.CurrentState = MainState.enWALKUP;//������
		}
		else if ((y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2) >= (x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2) 
				&& (y - rgSpriteCmd.y - rgSpriteCmd.unHeight / 2) >= -(x - rgSpriteCmd.x - rgSpriteCmd.unWidth / 2)
				&& !rgSpriteCmd.IsDownStop && rgSpriteCmd.CurrentState != MainState.enHURT &&!ButtonSound.isClick(x, y)){
			rgSpriteCmd.CurrentState = MainState.enWALKDOWN;//������
		}
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	//�����������ײ�ж�
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
//////////////////////��������������صĽӿ�SensorEventListener�ķ���/////////////////////////////
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		x_sensor = event.values[0]; // �ֻ����򷭹�
		// x>0 ˵����ǰ�ֻ��� x<0�ҷ�
		y_sensor = event.values[1]; // �ֻ����򷭹�
		// y>0 ˵����ǰ�ֻ��·� y<0�Ϸ�
		z_sensor = event.values[2]; // ��Ļ�ĳ���
		// z>0 �ֻ���Ļ���� z<0 �ֻ���Ļ����		
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
////////////�˳���Ϸʱ�ͷ�������Դ///////////////////////////////////////////////////////////////////////////////////////
	public void CloseGame()
	{
		FreeAppData(); // �ͷ�Ӧ��������Դ
		((picture)this.getContext()).CloseActivity(); // �ر�Ӧ��	
	}
	
	public void saveRank(Context context)//д�߷ְ�
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
	
	public void readRank(Context context)//���߷ְ�
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
		//�ͷ�ͼƬ��Դ
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
			mediaPlayer.stop(); // ֹͣ��������
			mediaPlayer.release(); // �ͷ�mediaPlayer��Դ
			mediaPlayer = null; // �����ÿ�
		}		
		// �ͷ�������Դ
		if (mediaPlayer_start != null)
		{
			mediaPlayer_start.stop(); // ֹͣ��������
			mediaPlayer_start.release(); // �ͷ�mediaPlayer��Դ
			mediaPlayer_start = null; // �����ÿ�
		}
	}
	
	public static void FreeMedia()
	{
		if (mediaPlayer != null)
		{
			mediaPlayer.stop(); // ֹͣ��������
			mediaPlayer.release(); // �ͷ�mediaPlayer��Դ
			mediaPlayer = null; // �����ÿ�
		}
		if (mediaPlayer_start != null)
		{
			mediaPlayer_start.stop(); // ֹͣ��������
			mediaPlayer_start.release(); // �ͷ�mediaPlayer��Դ
			mediaPlayer_start = null; // �����ÿ�
		}
	}
	
	public static Bitmap ReleaseImage(Bitmap bitmap)
	{
		if (bitmap != null && !bitmap.isRecycled())
        {
			//bitmap.recycle(); // ���ڻ��ո�bitmap��ռ�õ��ڴ�
			bitmap = null; // ��bitmap�ÿ�
        }		
		return null; // ���ؿգ���ԭͼƬ������Ϊ��
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