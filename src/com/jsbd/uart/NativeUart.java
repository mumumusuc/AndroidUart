package com.jsbd.uart;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class NativeUart {
	private static final String TAG = "NativeUart";

	static{
		System.loadLibrary("AndroidUart");
	}
	
	private FileDescriptor mFd;  
	private FileInputStream mFileInputStream;  
	private FileOutputStream mFileOutputStream;
	
	private NativeUart(){};
	
	public static NativeUart openUart(String port, int baudrate) throws IOException{
		NativeUart uart = new NativeUart();
		uart.mFd = uart.open(port, baudrate, 0);
		if (uart.mFd == null) {  
	        Log.e(TAG, "native open returns null");  
	        throw new IOException();  
	    }  
		uart.mFileInputStream = new FileInputStream(uart.mFd);  
		uart.mFileOutputStream = new FileOutputStream(uart.mFd);
	    return uart;
	}
	
	public void closeUart() throws IOException{
		if(mFd != null){
			close(mFd);
			mFileOutputStream.close();
			mFileInputStream.close();
		}
	}
	
	public InputStream getInputStream(){
		return mFileInputStream;
	}
	
	public OutputStream getOutputStream(){
		return mFileOutputStream;
	}
	
	public static final native void helloWorld();
	private final native FileDescriptor open(String port, int baudrate, int flags);  
	private final native void close(FileDescriptor fd); 
}
