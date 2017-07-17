package com.jsbd.uart;

import com.jsbd.uart.UartHandler.Option;

import android.os.Binder;

public abstract class LogBinder extends Binder {
	public abstract void regLog(Callback callback);
	
	public abstract void open(String port,int br,Option opt,String save);
	
	public abstract void close();

	public interface Callback {
		void onMessage(String msg);
	}
}
