package com.maplestory.moewallpaperloader;


import com.example.notifications.tools.BaseTools;
import com.maplestory.moewallpaperloader.fragment.TabFragment;
import com.maplestory.moewallpaperloader.fragment.ImgPreviewFragment;
import com.maplestory.moewallpaperloader.fragment.WallpaperSelectFragment;
import com.maplestory.moewallpaperloader.utils.Item;
import com.maplestory.moewallpaperloader.utils.ItemAdapter;
import com.maplestory.moewallpaperloader.view.ChangeColorIconWithTextView;










import com.maplestory.moewallpaperloader.view.TitleView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;  
import android.support.v4.app.FragmentActivity;  
import android.support.v4.app.FragmentPagerAdapter;  
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;  
import android.support.v4.view.ViewPager.OnPageChangeListener;  
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MoeWallpaperLoader extends FragmentActivity implements OnPageChangeListener,OnClickListener {
	private  WallpaperManager wallpaperManager ;
	private ViewPager mViewPager;  
    private List<Fragment> mTabs = new ArrayList<Fragment>();  
    private FragmentStatePagerAdapter mAdapter;  
    private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<ChangeColorIconWithTextView>();  
    private ItemAdapter itemAdapter;
    private Handler hd,fileHandler;
    private String IMAGE_PATH="/sdcard/WallpapersDownloader";
	public NotificationManager mNotificationManager;
	private TitleView titleView;
	private final static String TAG = "CustomActivity";
	
	/** ��ť����ʾ�Զ���֪ͨ */
	private Button btn_show_custom;
	/** ��ť����ʾ�Զ������ť��֪ͨ */
	private Button btn_show_custom_button;
	/** Notification ��ID */
	int notifyId = 101;
	/** NotificationCompat ������*/
	NotificationCompat.Builder mBuilder;
	/** �Ƿ��ڲ���*/
	public boolean isPlay = false;
	/** ֪ͨ����ť�㲥 */
	public ButtonBroadcastReceiver bReceiver;
	/** ֪ͨ����ť����¼���Ӧ��ACTION */
	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
	private void initService() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	private List<String> images = new ArrayList();
	private List<String> imagePaths = new ArrayList();;
	private int totalImage ;
	private int currentImage = 0;

    private void initFile(){
		 File f = new File(IMAGE_PATH);  
         File[] files = f.listFiles();
         if(files != null){
             int count = files.length;// �ļ�����  
             for (int i = 0; i < count; i++) {  
                 File file = files[i];  
                 if(getExtensionName(file.getName()).equals("jpg")){
                	 String path = "file:///mnt/sdcard/WallpapersDownloader" + "/" + file.getName();
                	 String paths = "/sdcard/WallpapersDownloader/"+file.getName();
                	 images.add(path);
                	 imagePaths.add(paths);
                 }
             }  
             totalImage = imagePaths.size();
             
         }  
    }
    
    public static String getExtensionName(String filename) {    
        if ((filename != null) && (filename.length() > 0)) {    
            int dot = filename.lastIndexOf('.');    
            if ((dot >-1) && (dot < (filename.length() - 1))) {    
                return filename.substring(dot + 1);    
            }    
        }    
        return filename;    
    }    
    
    private void initStorageFolder(){
    	File file = new File(IMAGE_PATH);
    	if(!file.exists()){
    		file.mkdir();
    	}
    }
    
    @Override  
    protected void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        	    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_moe_wallpaper_loader);  
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);  
        titleView = (TitleView) findViewById(R.id.title_view);
        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        initDatas();  
        initStorageFolder();
        initService();
        initFile();
        initButtonReceiver();
        showButtonNotify();

        mViewPager.setAdapter(mAdapter);  
        mViewPager.setOnPageChangeListener(this);  
    }  
  
    private void initDatas()  
    {  
  
        Fragment imgPreview = new ImgPreviewFragment();
        mTabs.add(imgPreview);
        Fragment WallpaperSelectFragment = new WallpaperSelectFragment();
        fileHandler = (Handler)((WallpaperSelectFragment) WallpaperSelectFragment).getHandler();
        mTabs.add(WallpaperSelectFragment);
        mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager())  
        {  
  
            @Override  
            public int getCount()  
            {  
            	//System.out.println(mTabs.size());
                return mTabs.size();  
            }  
  
            @Override  
            public Fragment getItem(int arg0)  
            {  
                return mTabs.get(arg0);  
            }  
        };  
  
        initTabIndicator();  
  
    }  
  
    @Override  
    public boolean onCreateOptionsMenu(Menu menu)  
    {  
     //   getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  
  
    private void initTabIndicator()  
    {  
        ChangeColorIconWithTextView one = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_one);  
        ChangeColorIconWithTextView two = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_two);  

  
        mTabIndicator.add(one);  
        mTabIndicator.add(two);  

  
        one.setOnClickListener(this);  
        two.setOnClickListener(this);  

  
        one.setIconAlpha(1.0f);  
    }  
  
    @Override  
    public void onPageSelected(int arg0)  
    {  
    }  
  
    @Override  
    public void onPageScrolled(int position, float positionOffset,  
            int positionOffsetPixels)  
    {  
        // Log.e("TAG", "position = " + position + " , positionOffset = "  
        // + positionOffset);  
    	initFile();
    	System.out.println("..................."+position);
        if (positionOffset > 0)  
        {  
            ChangeColorIconWithTextView left = mTabIndicator.get(position);  
            ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);  
  
            left.setIconAlpha(1 - positionOffset);  
            right.setIconAlpha(positionOffset);  

        }  
  
    }  
  
    @Override  
    public void onPageScrollStateChanged(int state)  
    {  
  
    }  
  
    @Override  
    public void onClick(View v)  
    {  
  
        resetOtherTabs();  
  
        switch (v.getId())  
        {  
        
        case R.id.id_indicator_one:  
            mTabIndicator.get(0).setIconAlpha(1.0f);  
            mViewPager.setCurrentItem(0, false);  
            break;  
        case R.id.id_indicator_two:  
            mTabIndicator.get(1).setIconAlpha(1.0f);  
            mViewPager.setCurrentItem(1, false);  
            break;  
  
        }  
  
    }  
  
    /** 
     * ����������Tab 
     */  
    private void resetOtherTabs()  
    {  
        for (int i = 0; i < mTabIndicator.size(); i++)  
        {  
            mTabIndicator.get(i).setIconAlpha(0);  
        }  
    }  
  
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mNotificationManager.cancelAll();
    	
    }
    
	public void showButtonNotify(){
		NotificationCompat.Builder mBuilder = new Builder(this);
		System.out.println("===============================");
		RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_button);
		Drawable dw = wallpaperManager.getDrawable();
		BitmapDrawable bd = (BitmapDrawable) dw;
		Bitmap bitmap = bd.getBitmap();
		mRemoteViews.setImageViewBitmap(R.id.custom_song_icon, bitmap);
		//API3.0 ���ϵ�ʱ����ʾ��ť��������ʧ
		
		mRemoteViews.setTextViewText(R.id.tv_custom_song_singer, "��ֽ�������� ");
		
		//����汾�ŵ��ڣ�3��0������ô����ʾ��ť
		
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
			//
			if(isPlay){
				mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_pause);
				mRemoteViews.setTextViewText(R.id.tv_custom_song_name, "������");
			}else{
				mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_play);
				mRemoteViews.setTextViewText(R.id.tv_custom_song_name, "��ͣ��");
			}
		

		//������¼�����
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		/* ��һ�Ű�ť */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
		//������˹㲥������INTENT�ı�����getBroadcast����
		PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
		/* ��ʼ/��ͣ  ��ť */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
		PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);
		/* ��һ�� ��ť  */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
		PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);
		

		
		
		mBuilder.setContent(mRemoteViews)
				.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis())// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ
				.setTicker("MoeWallpaperLoader")
				.setPriority(Notification.PRIORITY_DEFAULT)// ���ø�֪ͨ���ȼ�
				.setOngoing(true)
				.setSmallIcon(R.drawable.pic_image);
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		//�ᱨ�������ҽ��˼·
//		notify.contentView = mRemoteViews;
//		notify.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		mNotificationManager.notify(200, notify);
		System.out.println("=========================================...");
	}
    
	/** ����ť��֪ͨ������㲥���� */
	public void initButtonReceiver(){
		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}
	
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	/** ��һ�� ��ť��� ID */
	public final static int BUTTON_PREV_ID = 1;
	/** ����/��ͣ ��ť��� ID */
	public final static int BUTTON_PALY_ID = 2;
	/** ��һ�� ��ť��� ID */
	public final static int BUTTON_NEXT_ID = 3;
	/**
	 *	 �㲥������ť���ʱ�� 
	 */
	public class ButtonBroadcastReceiver extends BroadcastReceiver{

		private int nextImage;
		private int previousImage;
		public ButtonBroadcastReceiver() {
			// TODO Auto-generated constructor stub
	
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			
			nextImage = (currentImage+1)%totalImage;
			previousImage = Math.abs((currentImage-1)%totalImage);
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(ACTION_BUTTON)){
				//ͨ�����ݹ�����ID�жϰ�ť������Ի���ͨ��getResultCode()�����Ӧ����¼�
				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case BUTTON_PREV_ID:
					Log.d(TAG , "��һ��");
					Bitmap nextWallpaper = BitmapFactory.decodeFile(imagePaths.get(previousImage)); 
					currentImage = previousImage;
					try {
						wallpaperManager.setBitmap(nextWallpaper);
//						Toast.makeText(getApplicationContext(), "���óɹ�", Toast.LENGTH_SHORT).show(); 
						} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						nextWallpaper = null;
					}
					showButtonNotify();
					break;
				case BUTTON_PALY_ID:
					String play_status = "";
					isPlay = !isPlay;
					if(isPlay){
						play_status = "��ʼ����";
						Intent intents = new Intent();
						intents.setClass(getApplicationContext(), MoeWallpaperChangeSerivice.class);
						startService(intents);
						System.out.println("��ʼ���� ");
					}else{
						play_status = "����ͣ";
						Intent intent1 = new Intent();
						intent1.setClass(getApplicationContext(), MoeWallpaperChangeSerivice.class);
						stopService(intent1);
						System.out.println("ֹͣ���� ");
					}
					showButtonNotify();
					break;
				case BUTTON_NEXT_ID:
					Log.d(TAG , "��һ��");
 
					nextWallpaper = BitmapFactory.decodeFile(imagePaths.get(nextImage)); 
					currentImage = nextImage;
					try {
						wallpaperManager.setBitmap(nextWallpaper);
	//					Toast.makeText(getApplicationContext(), "���óɹ�", Toast.LENGTH_SHORT).show(); 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						nextWallpaper = null;
					}  
					showButtonNotify();
					break;
				default:
					break;
				}
			}
		}
	}
	
	
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
		return pendingIntent;
	}
	



	

	
	
	
}
