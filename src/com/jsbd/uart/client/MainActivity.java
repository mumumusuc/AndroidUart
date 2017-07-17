package com.jsbd.uart.client;

import com.jsbd.uart.Baudrate;
import com.jsbd.uart.LogBinder;
import com.jsbd.uart.LogBinder.Callback;
import com.jsbd.uart.server.LogService;
import com.jsbd.uart.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements Callback {
	private static final String TAG = "NativeUart_acticity";

	private TextView mLogText;
	private StringBuilder mSB;
	private LogBinder mBinder;
	private EditText mEdit;

	@Override
	protected void onCreate(Bundle arg) {
		super.onCreate(arg);
		this.setContentView(R.layout.main_layout);
		mLogText = (TextView) findViewById(R.id.log_test);
		mEdit = (EditText) findViewById(R.id.input);
		Intent intent = new Intent("com.jsbd.uart.START_LOG");
		intent.putExtra("FROM", getClass().getName());
		bindService(intent, mConnection, BIND_AUTO_CREATE);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinder = (LogBinder) service;
			mBinder.regLog(MainActivity.this);
			mSB = new StringBuilder();
			Log.i(TAG, "onServiceConnected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBinder = null;
			Log.v(TAG, "onServiceDisconnected");
		}
	};

	@Override public void onDestroy(){
		super.onDestroy();
		this.unbindService(mConnection);
	}
	
	@Override
	public void onMessage(String msg) {
		mSB.append(msg);
		mLogText.setText(mSB.toString());
		if(mLogText.getLineCount() > mLogText.getMaxLines()){
			mSB.delete(0, mSB.length());
		}
	}
	
	public void onConnect(View view){
		if(mBinder != null){
			mBinder.open("/dev/ttyMT5", Baudrate.B921600, null, "save1.log");
		}
	}
	
	public void onDisconnect(View view){
		if(mBinder != null){
			mBinder.close();
		}
	}
	
	public void onSend(View view){
		String msg = null;
		if(mBinder != null && (msg = mEdit.getText().toString())!=null){
			mBinder.send(msg);
		}
	}
}

