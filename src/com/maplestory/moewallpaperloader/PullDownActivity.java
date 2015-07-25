package com.maplestory.moewallpaperloader;

import java.util.ArrayList;
import java.util.List;

import com.maplestory.moewallpaperloader.view.PullDownView;
import com.maplestory.moewallpaperloader.view.PullDownView.OnPullDownListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PullDownActivity extends Activity implements OnPullDownListener,

OnItemClickListener {



/** Handler What����������� **/

private static final int WHAT_DID_LOAD_DATA = 0;

/** Handler What����������� **/

private static final int WHAT_DID_REFRESH = 1;

/** Handler What����������� **/

private static final int WHAT_DID_MORE = 2;



private ListView mListView;

private ArrayAdapter<String> mAdapter;

private PullDownView mPullDownView;

private List<String> mStrings = new ArrayList<String>();



@Override

protected void onCreate(Bundle savedInstanceState) {

super.onCreate(savedInstanceState);

setContentView(R.layout.pull_down);



/**

 * 1.ʹ��PullDownView 2.����OnPullDownListener 3.��mPullDownView�����ȡListView

 */

mPullDownView = (PullDownView) findViewById(R.id.pull_down_view);



mPullDownView.setOnPullDownListener(this);



mListView = mPullDownView.getListView();

mListView.setOnItemClickListener(this);



mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,

mStrings);

mListView.setAdapter(mAdapter);



// ���ÿ����Զ���ȡ���� �������һ���Զ���ȡ �ĳ�false�������Զ���ȡ����

mPullDownView.enableAutoFetchMore(true, 1);

// ���� ������β��

mPullDownView.setHideFooter();

// ��ʾ�������Զ���ȡ����

mPullDownView.setShowFooter();

/** ���ز��ҽ���ͷ��ˢ��*/

mPullDownView.setHideHeader();

/** ��ʾ���ҿ���ʹ��ͷ��ˢ��*/

mPullDownView.setShowHeader();

/** ֮ǰ ���Ϻܶ���� ���ᵼ��ˢ���¼� �� �����Ĳ˵�ͬʱ���� ���������ԡ������Ѿ���� */

mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {



@Override
public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
	// TODO Auto-generated method stub
	
}

});



// �������� ����ʹ��

loadData();



}



/** ˢ���¼��ӿ� ����Ҫע����ǻ�ȡ������ Ҫ�ر� ˢ�µĽ�����RefreshComplete() **/

@Override

public void onRefresh() {



new Thread(new Runnable() {



@Override

public void run() {

try {

Thread.sleep(2000);

} catch (InterruptedException e) {

e.printStackTrace();

}



/** �ر� ˢ����� ***/

mPullDownView.RefreshComplete();// ������̰߳�ȫ�� �ɿ�Դ����



// ����ط��е�����

Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);

msg.obj = "After refresh " + System.currentTimeMillis();

msg.sendToTarget();

}

}).start();



}



/** ˢ���¼��ӿ� ����Ҫע����ǻ�ȡ������ Ҫ�ر� ����Ľ����� notifyDidMore() **/

@Override

public void onMore() {

new Thread(new Runnable() {



@Override

public void run() {

try {

Thread.sleep(2000);

} catch (InterruptedException e) {

e.printStackTrace();

}



// ��������ȡ������� ������̰߳�ȫ�� �ɿ�Դ����

mPullDownView.notifyDidMore();

Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);

msg.obj = "After more " + System.currentTimeMillis();

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

mAdapter.notifyDataSetChanged();

}

}

Toast.makeText(PullDownActivity.this,

"" + mListView.getAdapter().getCount(),

Toast.LENGTH_LONG).show();

// �������ݼ������;

break;

}

case WHAT_DID_REFRESH: {

String body = (String) msg.obj;

mStrings.add(0, body);

mAdapter.notifyDataSetChanged();

// �������������

break;

}



case WHAT_DID_MORE: {

String body = (String) msg.obj;

mStrings.add(body);

mAdapter.notifyDataSetChanged();



break;

}

}



}



};



@Override

public void onItemClick(AdapterView<?> parent, View view, int position,

long id) {

Toast.makeText(this, "������������� " + position, Toast.LENGTH_SHORT).show();

}



// ģ������

private String[] mStringArray = { "Abbaye de Belloc"/*

 * ,

 * "Abbaye du Mont des Cats"

 * , "Abertam",

 * "Abondance",

 * "Ackawi", "Acorn",

 * "Adelost",

 * "Affidelice au Chablis"

 * , "Afuega'l Pitu",

 * "Airag", "Airedale",

 * "Aisy Cendre",

 * "Allgauer Emmentaler"

 * , "Alverca",

 * "Ambert",

 * "American Cheese"

 */};



private void loadData() {

new Thread(new Runnable() {



@Override

public void run() {

try {

Thread.sleep(0000);

} catch (InterruptedException e) {

e.printStackTrace();

}

List<String> strings = new ArrayList<String>();

for (String body : mStringArray) {

strings.add(body);

}

Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);

msg.obj = strings;

msg.sendToTarget();

}

}).start();

}

}
