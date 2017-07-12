#include <fcntl.h>
#include <unistd.h>
#include <Android/log.h>
#include <termios.h>
#include <stdio.h>
#include <sys/stat.h>

#define TAG "NativeUart_C" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

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
