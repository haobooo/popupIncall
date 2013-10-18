package com.pipi.studo.popincall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;


public class ListenPhoneStateService extends Service {
	private static final String TAG = "ListenPhoneStateService";
	private static final boolean DEBUG = true;
	
	private WindowManager mWm;
	private WindowManager.LayoutParams wParams;
	private View mPopView;
	private float mOffsetX;
	private float mOffsetY;
	private boolean mDraged = false;
	
	private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
        public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING |
					state == TelephonyManager.CALL_STATE_OFFHOOK) {
				// Popup the Window.
				PopupWindow(ListenPhoneStateService.this.getResources().getString(R.string.hello_world));
				
			} else if (state == TelephonyManager.CALL_STATE_IDLE) {
				// Close the Popup window.
				RemovePopWindow();
			}
		}
	};
	
	private OnTouchListener popTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int action = event.getAction();
			switch(action) {
			case MotionEvent.ACTION_DOWN:
				mOffsetX = event.getX();
				mOffsetY = event.getY();
				
				if(DEBUG) Log.d(TAG, "MotionEvent.ACTION_DOWN");
				if(DEBUG) Log.d(TAG, "Left=" + mPopView.getLeft());
				if(DEBUG) Log.d(TAG, "Top=" + mPopView.getTop());
				if(DEBUG) Log.d(TAG, "Width=" + mPopView.getWidth());
				if(DEBUG) Log.d(TAG, "Height=" + mPopView.getHeight());
				if(DEBUG) Log.d(TAG, "mOffsetX=" + mOffsetX);
				if(DEBUG) Log.d(TAG, "mOffsetY=" + mOffsetY);
				
				mDraged = true;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mDraged) {
					int x = (int) (event.getRawX() - mOffsetX);
					int y = (int) (event.getRawY() - mOffsetY);
					if(DEBUG) Log.d(TAG, "MotionEvent.ACTION_MOVE");
//					if(DEBUG) Log.d(TAG, "event.getRawX()=" + event.getRawX());
//					if(DEBUG) Log.d(TAG, "event.getRawY()=" + event.getRawY());
					if(DEBUG) Log.d(TAG, "x="+x);
					if(DEBUG) Log.d(TAG, "y="+y);
					
					MovePopupWindow(x, y);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mDraged) {
					int x = (int) (event.getRawX() - mOffsetX);
					int y = (int) (event.getRawY() - mOffsetY);
					
					MovePopupWindow(x, y);
					
					mDraged = false;
					mOffsetX = 0;
					mOffsetY = 0;
				}
				
				break;
			}
			return false;
		}
		
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("popupIncall", "ListenPhoneStateService onCreate");
		
		TelephonyManager telephonyManager =
                (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        
        mWm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		TelephonyManager telephonyManager =
    			(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}
	
	private void PopupWindow(String message) {
		TextView textView = new TextView(this);
		textView.setText(message);
		//textView.setGravity(Gravity.CENTER);
		textView.setAlpha(0.8f);
		
		mPopView = textView;
		
		wParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
										WindowManager.LayoutParams.WRAP_CONTENT,
										WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
										WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
											| WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING,
										PixelFormat.RGBX_8888);
		wParams.token = new Binder();
		wParams.gravity = Gravity.LEFT | Gravity.TOP;
		wParams.x = 100;
		wParams.y = 200;
		//lp.gravity = Gravity.CENTER;
		
		mWm.addView(mPopView, wParams);
		
		mPopView.setOnTouchListener(popTouchListener);
	}
	
	private void RemovePopWindow() {
		if (mPopView != null) {
			WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(mPopView);
			
			mPopView = null;
		}
	}
	
	private void MovePopupWindow(int x, int y) {
		if (mPopView != null) {
			WindowManager.LayoutParams lp = (LayoutParams) mPopView.getLayoutParams();
			lp.x = x;
			lp.y = y;
			
			mWm.updateViewLayout(mPopView, lp);
		}
	}
}