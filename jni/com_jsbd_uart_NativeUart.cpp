#include "com_jsbd_uart_NativeUart.h"
#include "AndroidUart.h"

JNIEXPORT
void JNICALL Java_com_jsbd_uart_NativeUart_helloWorld(JNIEnv* env, jclass clz) {
	LOGI("hello native world");
}

JNIEXPORT
jobject JNICALL Java_com_jsbd_uart_NativeUart_open(JNIEnv* env, jclass clz,
		jstring port, jint baudrate, jint flag) {
	const char* _PORT = NULL;
	_PORT = env->GetStringUTFChars(port, 0);
	int _FLAG = flag;
	int _SPEED = baudrate;

	int fd = openUart(_PORT, _SPEED, _FLAG);

	jclass cFileDescriptor = env->FindClass("java/io/FileDescriptor");
	jmethodID iFileDescriptor = env->GetMethodID(cFileDescriptor, "<init>",
			"()V");
	jfieldID descriptorID = env->GetFieldID(cFileDescriptor, "descriptor", "I");
	jobject mFileDescriptor = env->NewObject(cFileDescriptor, iFileDescriptor);
	env->SetIntField(mFileDescriptor, descriptorID, (jint) fd);

	env->ReleaseStringUTFChars(port, _PORT);

	return mFileDescriptor;
}

JNIEXPORT
void JNICALL Java_com_jsbd_uart_NativeUart_close(JNIEnv* env, jclass clz, jobject fd) {
	jclass _FD = env->GetObjectClass(fd);
	jfieldID mFdID = env->GetFieldID(_FD, "mFd","Ljava/io/FileDescriptor;");
	jclass FileDescriptorClass = env->FindClass("java/io/FileDescriptor");
	jfieldID descriptorID = env->GetFieldID(FileDescriptorClass,"descriptor", "I");

	jobject mFd = env->GetObjectField(fd, mFdID);
	jint descriptor = env->GetIntField(mFd, descriptorID);

	LOGD("close(fd = %d)", descriptor);
	closeUart(descriptor);
}
