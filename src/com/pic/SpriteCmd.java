/*
 * ���Ǽ����־��鹲ͬ���ԣ������Լ�����Ϸ�޸ģ�
 */

package com.pic;

public class SpriteCmd
{
	public int x;// �����xλ��
	public int y;// �����yλ��

	public int unWidth;// ���ƵĿ��
	public int unHeight;//���Ƶĸ߶�

	public int unSpriteIndex;// ������ͼƬ�ϵ�����ֵ
	public int unLayer;// ������Ƿ���ʾ��Ϊ0ʱ��ʾ��Ϊ255ʱ����ʾ
	public int WalkCount;//��������
	public int ProtectCount;
    public enum MainState//�ɻ�״̬
	{
    	    enSTAND,//վ
			enWALKRIGHT,//����
			enWALKLEFT,	//����
			enWALKUP,	//����
			enWALKDOWN,//����
			enHURT,  //����״̬
    }
    public MainState CurrentState;//���ǵĵ�ǰ״̬
    
    public Boolean IsActive;//
    public Boolean IsRight;//
    public Boolean IsHurt;//
    public Boolean IsLeftStop;//�����Ƿ�ֹͣ�����ƶ�
    public Boolean IsRigtStop;//�����Ƿ�ֹͣ�����ƶ�
    public Boolean IsTopStop;//�����Ƿ�ֹͣ�����ƶ�
    public Boolean IsDownStop;//�����Ƿ�ֹͣ�����ƶ�
}
