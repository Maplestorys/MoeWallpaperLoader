package com.maplestory.moewallpaperloader.fragment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.maplestory.moewallpaperloader.R;
import com.maplestory.moewallpaperloader.utils.Item;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
public class WallpaperSelectFragment extends Fragment{

	public WallpaperSelectFragment() {
		// TODO Auto-generated constructor stub

	}
	
	public Handler getHandler(){
		return this.fileHandler;
	}
	private ImageAdapter ia;
	private static Handler fileHandler ;
	private ImageLoader imageLoader;
	private String[] imageUrls;					// ͼƬUrl
	private List<String> images;
	private List<String> imagePaths;
	private DisplayImageOptions options;		// ��ʾͼƬ������
	private GridView listView;
	private String IMAGE_PATH="/sdcard/WallpapersDownloader";
	 @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.ac_image_grid, container, false);
		 images = new ArrayList();
		 imagePaths = new ArrayList();
		 options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)	 //����ͼƬ�Ľ�������
			.build();
		initFile();
		listView = (GridView) rootView.findViewById(R.id.gridview);
		final ImageAdapter ia = new ImageAdapter();
		((GridView) listView).setAdapter(ia);			// �������
		fileHandler = new Handler()
		{
			public void handleMessage(android.os.Message msg) {
			
			initFile();	
			ia.notifyDataSetChanged();
			};
		};
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				System.out.println("on long click");
				new AlertDialog.Builder(getActivity()) 
				.setTitle("ȷ��ɾ��ͼƬ")
				.setMessage("�Ƿ�ɾ����ͼƬ")
				.setPositiveButton("��", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0,int arg1) {
						File deleteImage = new File(imagePaths.get(position));
						deleteImage.delete();  
						fileHandler.sendEmptyMessage(0);
						Toast.makeText(getActivity(), "ɾ���ɹ�", Toast.LENGTH_SHORT).show();  
				}
				})
				.setNegativeButton("��", null)
				.show();
				return true;
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());  
				Drawable wallpaperDrawable = wallpaperManager.getDrawable();
				final Bitmap previousWallpaper = ((BitmapDrawable) wallpaperDrawable).getBitmap();
				Bitmap nextWallpaper = BitmapFactory.decodeFile(imagePaths.get(position)); 

				try {
					wallpaperManager.setBitmap(nextWallpaper);
					Toast.makeText(getActivity(), "���óɹ�", Toast.LENGTH_SHORT).show(); 
					
					new AlertDialog.Builder(getActivity()) 
					.setTitle("ȷ��")
					.setMessage("����ԭ�б�ֽ")
					.setPositiveButton("��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0,int arg1) {
							// TODO Auto-generated method stub
							try {
								wallpaperManager.setBitmap(previousWallpaper);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  
							Toast.makeText(getActivity(), "����ԭ�б�ֽ", Toast.LENGTH_SHORT).show();  
					}
					})
					.setNegativeButton("��", null)

					.show();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   

				
				
				
				
				
			}
		});
		 return rootView;
	 }

		public class ImageAdapter extends BaseAdapter {
			@Override
			public int getCount() {
				return imageUrls.length;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final ImageView imageView;
				if (convertView == null) {
					imageView = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
				} else {
					imageView = (ImageView) convertView;
				}
				imageLoader = ImageLoader.getInstance();
				// ��ͼƬ��ʾ�������ӵ�ִ�гأ�ͼƬ������ʾ��ImageView���ֵ���ImageView
				imageLoader.displayImage(imageUrls[position], imageView, options);

				return imageView;
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
	    
	    
	    public static String getFileNameNoEx(String filename) {    
	        if ((filename != null) && (filename.length() > 0)) {    
	            int dot = filename.lastIndexOf('.');    
	            if ((dot >-1) && (dot < (filename.length()))) {    
	                return filename.substring(0, dot);    
	            }    
	        }    
	        return filename;    
	    }    
		
	    
	    private void initFile(){
	    	 images.clear();
	    	 imagePaths.clear();
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
	             
	             imageUrls = (String[])images.toArray(new String[images.size()]);  
	         }  
	    }

}
