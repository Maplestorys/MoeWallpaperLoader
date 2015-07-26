package com.maplestory.moewallpaperloader.fragment;

import java.util.ArrayList;
import java.util.List;







import java.util.Map;

import com.maplestory.moewallpaperloader.MainActivity;
import com.maplestory.moewallpaperloader.fragment.ImgPreviewFragment.ItemListAdapter.ViewHolder;
import com.maplestory.moewallpaperloader.utils.HttpUtils;
import com.maplestory.moewallpaperloader.utils.ImageProfile;
import com.maplestory.moewallpaperloader.utils.Item;
import com.maplestory.moewallpaperloader.utils.imageValues;
import com.maplestory.moewallpaperloader.utils.siteAddressGen;
import com.maplestory.moewallpaperloader.MoeWallpaperLoader;
import com.maplestory.moewallpaperloader.UILApplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

















import com.maplestory.moewallpaperloader.R;
import com.maplestory.moewallpaperloader.view.PullDownView;
import com.maplestory.moewallpaperloader.view.PullDownView.OnPullDownListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;



public class ImgPreviewFragment extends Fragment implements OnPullDownListener,OnItemClickListener{

	public ImgPreviewFragment() {
		// TODO Auto-generated constructor stub
	}
	 private ItemListAdapter itemListAdapter;
	 private DisplayImageOptions options;
	 private ImageLoader imageLoader;
	 private View rootView;
	 private int currentPage;
	 /** Handler What加载数据完毕 **/
	 private static final int WHAT_DID_LOAD_DATA = 0;
	 /** Handler What更新数据完毕 **/
	 private static final int WHAT_DID_REFRESH = 1;
	 /** Handler What更多数据完毕 **/
	 private static final int WHAT_DID_MORE = 2;
	 private String[] imageUrls={};
	 private ListView mListView;
	 private ArrayAdapter<String> mAdapter;
	 private PullDownView mPullDownView;
	 private List<String> mStrings = new ArrayList<String>();
	 private List<ImageProfile> imageValueStrings = new ArrayList();
	 private boolean firstLoad = true;
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 View rootView = inflater.inflate(R.layout.pull_down, container, false);
		 mPullDownView = (PullDownView) rootView.findViewById(R.id.pull_down_view);
		return rootView;
	}
	 @Override
	public void onResume() {
	 super.onResume();
	 /**

	  * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView

	  */
	 // 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
	 
	    // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
	    options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_stub)			// 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.ic_empty)	// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.ic_error)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
		.build();			
	    itemListAdapter = new ItemListAdapter();
	   
	 mPullDownView.setOnPullDownListener(this);
	 mListView = mPullDownView.getListView();
	 mListView.setOnItemClickListener(this);
//	 mAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,mStrings);
//	 mListView.setAdapter(mAdapter);
	 mListView.setAdapter(itemListAdapter);
	 mPullDownView.enableAutoFetchMore(true, 1);

	 // 隐藏 并禁用尾部
	 mPullDownView.setHideFooter();
	 // 显示并启用自动获取更多
	 mPullDownView.setShowFooter();
	 /** 隐藏并且禁用头部刷新*/
	 mPullDownView.setHideHeader();
	 /** 显示并且可以使用头部刷新*/
	 mPullDownView.setShowHeader();
	 
	 imageLoader = ImageLoader.getInstance();

	 /** 之前 网上很多代码 都会导致刷新事件 跟 上下文菜单同时弹出 这里做测试。。。已经解决 */
	 mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
		 
	 @Override
	 public void onCreateContextMenu(ContextMenu menu, View v,
			 ContextMenuInfo menuInfo) {
	 		}
	 	});
	 // 加载数据 本类使用
	 if(firstLoad){
		 loadData();
		 firstLoad = false;
	 }
	 	
	 }


	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/

	 @Override
	 public void onRefresh() {
		 
		 new Thread(new Runnable() {

			 @Override
			 public void run() {
				 	currentPage = 1;
				 	List<String> strings = new ArrayList<String>();
				 	String htmlString =  HttpUtils.getContent(siteAddressGen.getSiteAddress(currentPage));
					System.out.println(HttpUtils.getMaxPageNumber(htmlString));
					ArrayList<ImageProfile> al = HttpUtils.getNewImageValues(htmlString);
					imageValueStrings.clear();
					imageValueStrings.addAll(al);
					for (int i=0;i<al.size();i++) {
						strings.add(""+al.get(i).getPreview_url());
					}
					currentPage++;
					
				 /** 关闭 刷新完毕 ***/
				 mPullDownView.RefreshComplete();// 这个事线程安全的 可看源代码

				 // 这个地方有点疑问
				 Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
				 msg.obj = strings;
				 msg.sendToTarget();

			 }

		 	}).start();
	 }



	 /** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/

	 		@Override
	 		public void onMore() {
	 			new Thread(new Runnable() {
	 			@Override
	 			public void run() {
	 				
	 				List<String> strings = new ArrayList<String>();
				 	String htmlString =  HttpUtils.getContent(siteAddressGen.getSiteAddress(currentPage));
					System.out.println(HttpUtils.getMaxPageNumber(htmlString));
					ArrayList<ImageProfile> al = HttpUtils.getNewImageValues(htmlString);
					for (int i=0;i<al.size();i++) {
						strings.add(""+al.get(i).getPreview_url());
					}
					imageValueStrings.addAll(al);
					currentPage++;
	 // 告诉它获取更多完毕 这个事线程安全的 可看源代码
		 		mPullDownView.notifyDidMore();
		 		
		 		
		 		Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
		 		msg.obj = strings;
		 		msg.sendToTarget();
	 			}
	 			}).start();
	 		}
	 		

	 private Handler mUIHandler = new Handler() {
		 @Override
		 public void handleMessage(Message msg) {
			 switch (msg.what) {
			 case WHAT_DID_LOAD_DATA: {
				 if (msg.obj != null) {
					 List<String> strings = (List<String>) msg.obj;
					 if (!strings.isEmpty()) {
						 mStrings.addAll(strings);
						 imageUrls = (String[])mStrings.toArray(new String[mStrings.size()]);
					//	 mAdapter.notifyDataSetChanged();
						 itemListAdapter.notifyDataSetChanged();
					 }
				 }
//				 Toast.makeText(getActivity(),"Loading Complete",Toast.LENGTH_LONG).show();
				 // 诉它数据加载完毕;
				 break;
			 }
			 case WHAT_DID_REFRESH: {
				 mStrings.clear();
				 List<String> strings = (List<String>) msg.obj;
				 if (!strings.isEmpty()) {
					 mStrings.addAll(strings);
					 imageUrls = (String[])mStrings.toArray(new String[mStrings.size()]);
				//	 mAdapter.notifyDataSetChanged();
					 itemListAdapter.notifyDataSetChanged();
				 }
//				 Toast.makeText(getActivity(),"Refresh Complete",Toast.LENGTH_LONG).show();
				 // 告诉它更新完毕
				 break;
			 }
			 case WHAT_DID_MORE: {
				 List<String> strings = (List<String>) msg.obj;
				 if (!strings.isEmpty()) {
					 mStrings.addAll(strings);
					 imageUrls = (String[])mStrings.toArray(new String[mStrings.size()]);
				//	 mAdapter.notifyDataSetChanged();
					 itemListAdapter.notifyDataSetChanged();
				 }
//				 Toast.makeText(getActivity(),"Adding Complete",Toast.LENGTH_LONG).show();
				 break;
			 }
			 }
		 }
		};



		@Override

		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			//position指的是第几个 ，从1开始计算
			
			
			Toast.makeText(getActivity(), ""+position, Toast.LENGTH_SHORT).show();
	//		System.out.println(imageValueStrings.get(position-1).getJpeg_url());
			ViewHolder holder = (ViewHolder) view.getTag();
	//		System.out.println(holder.text.getText().toString());
			Bundle bundle = new Bundle();
			bundle.putSerializable("imageObject", imageValueStrings.get(position-1));
			Intent intent = new Intent();
			intent.setClass(getActivity(), MainActivity.class);
			intent.putExtras(bundle);
			getActivity().startActivity(intent);

	 }







	 private void loadData() {

	 new Thread(new Runnable() {



	 @Override

	 public void run() {
		 	currentPage = 1;
		 	List<String> strings = new ArrayList<String>();
		 	String htmlString =  HttpUtils.getContent(siteAddressGen.getSiteAddress(currentPage));
			System.out.println(HttpUtils.getMaxPageNumber(htmlString));
			ArrayList<ImageProfile> al = HttpUtils.getNewImageValues(htmlString);
			for (int i=0;i<al.size();i++) {
				strings.add(""+al.get(i).getPreview_url());
			}
			imageValueStrings.addAll(al);
			currentPage++;
	 Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);

	 msg.obj = strings;

	 msg.sendToTarget();

	 }

	 }).start();

	 }
	 class ItemListAdapter extends BaseAdapter {
		    @Override
		    public int getCount() {
		      // TODO Auto-generated method stub
		      return imageUrls.length;
		    }

		    @Override
		    public Object getItem(int position) {
		      // TODO Auto-generated method stub
		      return imageUrls[position];
		    }
		    
		    public String getCurrentString(View convertView) {
		    	ViewHolder holder = (ViewHolder)convertView.getTag();
		    	return holder.text.getText().toString();
		    }
		    
		    public Drawable getCurrentBackground(View convertView){
		    ViewHolder holder = (ViewHolder)convertView.getTag();
		    	return holder.image.getBackground();
		    }
		    
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		      // TODO Auto-generated method stub
		      ViewHolder holder = null;
		      if (convertView == null) {
		        convertView = getActivity().getLayoutInflater().inflate(R.layout.item_list, parent, false);
		        holder = new ViewHolder();
		        holder.text = (TextView) convertView.findViewById(R.id.text);
		        holder.image = (ImageView) convertView.findViewById(R.id.image);
		        convertView.setTag(holder);
		      } else {
		        holder = (ViewHolder) convertView.getTag();
		      }
		      holder.text.setText("" + imageValueStrings.get(position).getId());
		      imageLoader.displayImage(imageUrls[position], holder.image, options);
		      return convertView;
		    }

		    @Override
		    public long getItemId(int position) {
		      // TODO Auto-generated method stub
		      return position;
		    }

		    class ViewHolder {
		      public ImageView image;
		      public TextView text;
		    }
		  }
	
	
}
