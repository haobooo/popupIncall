package com.pipi.studo.popincall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("popupIncall", "BootCompleteReceiver onReceive");
		Intent service = new Intent(context, ListenPhoneStateService.class);
		context.startService(service);
	}
	
}