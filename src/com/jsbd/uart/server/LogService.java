package com.jsbd.uart.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.jsbd.uart.Baudrate;
import com.jsbd.uart.LogBinder;
import com.jsbd.uart.LogBinder.Callback;
import com.jsbd.uart.UartHandler;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class LogService extends Service {
	private static final String TAG = "LogService";

	private final String PORT_NAME = "/dev/ttyMT0";
	private final String SAVE_NAME = Environment.getExternalStorageDirectory().getPath() + "/save.log";
	private UartHandler mUartHandler;
	private File mSaveFile;
	private FileOutputStream mOut;
	private Callback mCallback = null;

	private LogBinder mBinder = new LogBinder(){
		@Override
		public void regLog(Callback callback) {
			mCallback = callback;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			mUartHandler = UartHandler.openUart(PORT_NAME, Baudrate.B921600);
		} catch (IOException e) {
		}
		if (mUartHandler != null) {
			mSaveFile = new File(SAVE_NAME);
			if (!mSaveFile.exists()) {
				try {
					mSaveFile.createNewFile();
				} catch (IOException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
			}
			try {
				mOut = new FileOutputStream(mSaveFile);
			} catch (FileNotFoundException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			// mUartHandler.send("#logcat");
			mUartHandler.receive(mHandler);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mUartHandler != null) {
			try {
				mUartHandler.closeUart();
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
		if(mOut != null){
			try {
				mOut.close();
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	}

	private final Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			String _msg = (String)msg.obj;
			Log.d(TAG, _msg);
			if(mCallback != null){
				mCallback.onMessage(_msg);
			}
			try {
				mOut.write(_msg.getBytes());
				mOut.flush();
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	};
}
