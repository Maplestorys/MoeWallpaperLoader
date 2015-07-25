package com.maplestory.moewallpaperloader.utils;

import java.util.List;

import com.maplestory.moewallpaperloader.utils.MultiThreadDownload;
import com.maplestory.moewallpaperloader.utils.MultiThreadDownloadListenner;
import com.maplestory.moewallpaperloader.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ItemAdapter extends BaseAdapter
{
	List<Item> list;
	Context context;
	Handler hd;
	public ItemAdapter(List<Item> list,Context context,Handler hd)
	{
		// TODO Auto-generated constructor stub
		this.list = list;
		this.context = context;
		this.hd = hd;
	}
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams", "SdCardPath" })
	@Override
	public View getView(int position, View contentView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		final Item item = list.get(position);
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.ite,null);
		ImageView i = (ImageView) layout.findViewById(R.id.b);
		TextView tv = (TextView) layout.findViewById(R.id.tv1);
		final ProgressBar p = (ProgressBar) layout.findViewById(R.id.p1);
		// ���������֤ˢ�º�Ķ���ĵ�ַ
		System.out.println(p);
		i.setImageDrawable(item.getBackGround());
		tv.setText(item.getName());
		tv.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Toast.makeText(context,"Begin Download",Toast.LENGTH_LONG).show();
				final MultiThreadDownload tastk0 = new MultiThreadDownload(item.getUrl(), "/sdcard/WallpapersDownloader/"+item.getName()+".jpg", 3);
				tastk0.setDownThreadListenner(new MultiThreadDownloadListenner()
				{
					@Override
					public void onSuccess()
					{
						// Do not like this.
						//						list.remove(item);
						Message m = hd.obtainMessage();
						m.obj = item;
						///< ���﷢hanlerȥɾ����Ŀ������������ֱ��ɾ�����������ͬʱɾ���Ļ�������ģ���ʹ��Handler����˳��ģ������˱���
						hd.sendMessage(m); 
					}

					@Override
					public void onProgress(long bytes, float percent)
					{
						p.setProgress((int) (100*percent));
						///< ʵʱ�������ݿ��������Ϣ��Ŀ�ľ�����ʾ���ȣ������Ż�Ϊ���θ���һ�Ρ�
					}
				});
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						tastk0.start();
					}
				}).start();
			}
		});
		return layout;
	}

	public void remove(Item item)
	{
		list.remove(item);
	}

}
