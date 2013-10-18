package com.pipi.studo.popincall;

import android.app.Application;
import android.content.Intent;


public class MyApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Intent intent = new Intent(this, ListenPhoneStateService.class);
		startService(intent);
	}
}