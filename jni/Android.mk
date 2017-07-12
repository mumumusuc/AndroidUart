LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := AndroidUart
LOCAL_SRC_FILES := AndroidUart.cpp \
				   com_jsbd_uart_NativeUart.cpp

LOCAL_LDLIBS :=-llog

include $(BUILD_SHARED_LIBRARY)
