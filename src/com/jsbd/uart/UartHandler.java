package com.jsbd.uart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import android.os.Handler;
import android.util.Log;

public class UartHandler {

	private static final String TAG = "NativeUart_UartHandler";
	
	public static enum Option{
		BYTE,STRING
	};

	private Handler mHandler;
	private NativeUart mUart;
	private Option mOption = Option.STRING;

	private UartHandler() {	}
	
	public static UartHandler openUart(String port,int baudrate) throws IOException{
		UartHandler instance = new UartHandler();
		instance.mUart = NativeUart.openUart(port, baudrate&0x0FFFFFFFF);
		instance.mReceiveThread.start();
		return instance;
	}
	
	public void closeUart() throws IOException{
		if(mUart != null){
			mUart.closeUart();
		}
	}

	public void receive(Handler handler) {
		mHandler = handler;
	}
	
	public void receive(Handler handler,Option option) {
		mHandler = handler;
		if(option != null){
			mOption = option;
		}
	}

	public void send(final String msg) {
		Runnable send = new Runnable() {
			@Override
			public void run() {
				if (msg != null && mUart != null) {
					try {
						mUart.getOutputStream().write(msg.getBytes());
						mUart.getOutputStream().flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		if (mHandler != null) {
			mHandler.post(send);
		} else {
			/* this MAY cause ANR */
			send.run();
		}
	}
	
	private Thread mReceiveThread = new Thread() {
		@Override
		public void run() {
			StringBuilder builder = new StringBuilder();
			InputStream is = null;
			try {
				is = mUart.getInputStream();
				if (is != null) {
					final byte[] data = new byte[2048];
					int read = -1;
					try {
						while (!Thread.interrupted() && (read = is.read(data)) > -1) {
							for (int i = 0; i < read; i++) {
								if(mOption == Option.BYTE){
									builder.append(Integer.toHexString(data[i]&0xFF));
									builder.append(" ");
								}
								else{
									builder.append((char)data[i]);
								}
							}
							String msg = builder.toString();
							mHandler.obtainMessage(read, msg).sendToTarget();
							builder.delete(0, builder.length());
							Arrays.fill(data, (byte)0);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	
}