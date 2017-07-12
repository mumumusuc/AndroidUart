#include <fcntl.h>
#include <unistd.h>
#include <Android/log.h>
#include <termios.h>
#include <stdio.h>
#include <sys/stat.h>

#define TAG "NativeUart_C" // ������Զ����LOG�ı�ʶ
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // ����LOGD����
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // ����LOGI����
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // ����LOGW����
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // ����LOGE����
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // ����LOGF����

#define ERROR -1
#define BUFFER_SIZE 2048

#define LOG_FILE "/mnt/sdcard/save.log"

int openUart(const char*,speed_t,int);
void receive(int,void*,size_t);
void send(int,char*,size_t);
void closeUart(int);
void save(const char*);

int createLog(const char*);
int writeLog(int, void*, size_t);
int saveLog(int);
