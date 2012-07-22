package com.samxu.battwidget;

import com.samxu.battwidget.R;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class BattWidget extends AppWidgetProvider {

	public static final String MY_PREFS="mSharedPreferences01"; 

	@Override 
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds); 
		context.startService(new Intent(context, UpdateService.class));
	}
	
	public static void updateAppwig(Context context, AppWidgetManager manager) {
		RemoteViews updateViews = keepUpdate(context); 
 
		ComponentName thisWidget = new ComponentName(context, BattWidget.class);
		manager.updateAppWidget(thisWidget, updateViews);
	}
	
	
	public static RemoteViews keepUpdate(Context context) { 
		RemoteViews retRemoteViews = null; 
		retRemoteViews = new RemoteViews(context.getPackageName(),R.layout.main); 

		int power=0;
		SharedPreferences pres = context.getSharedPreferences(MY_PREFS,Context.MODE_PRIVATE);
		if(pres !=null) {
			power = pres.getInt("power", 0);
		}      

		retRemoteViews.setTextViewText(R.id.myTextView1,"Battery\n"+power+"%");
		return retRemoteViews; 
	} 
	
	public static class UpdateService extends Service {
		private MyBroadcastReceiver mReceiver01;

		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override 
		public void onDestroy() {
			unregisterReceiver(mReceiver01); 
			super.onDestroy(); 
		} 
		
		@Override
		public void onStart(Intent intent, int startId) {
			super.onStart(intent, startId);

			Log.e("BATTWIDGET", "service");

			/* 注册Receiver */
			IntentFilter mFilter01; 
			mFilter01 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); 
			mReceiver01 = new MyBroadcastReceiver();
			registerReceiver(mReceiver01, mFilter01);

			keepUpdate(this);
		}

		public class MyBroadcastReceiver extends BroadcastReceiver { 
			@Override 
			public void onReceive(Context context, Intent intent) { 

				if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) { 
					int intLevel = intent.getIntExtra("level", 0);  
					int intScale = intent.getIntExtra("scale", 100); 

					Log.e("BATTWIDGET", "receiver,level=" + intLevel);


					SharedPreferences pres = context.getSharedPreferences(MY_PREFS,Context.MODE_PRIVATE);
					if(pres!=null) {
						SharedPreferences.Editor ed = pres.edit(); 
						ed.putInt("power",(intLevel*100/intScale)); 
						ed.commit();
					}

					AppWidgetManager gm = AppWidgetManager.getInstance(context);
					updateAppwig(context, gm);
				}
			} 
		}
	}
}
