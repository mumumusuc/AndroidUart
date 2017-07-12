package com.jsbd.uart;

import android.os.Binder;

public abstract class LogBinder extends Binder {
	public abstract void regLog(Callback callback);

	public interface Callback {
		void onMessage(String msg);
	}
}
