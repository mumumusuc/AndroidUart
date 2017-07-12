#include "AndroidUart.h"


int openUart(const char* port, speed_t speed, int flag) {
	int fd;
	fd = open(port, O_RDWR|flag);
	if (fd == -1) {
		LOGE("error on open %s", port);
		return ERROR;
	}
	LOGI("open %s success,baudrate = %d", port,speed);

	struct termios cfg;
	LOGD("Configuring serial port");

	if (tcgetattr(fd, &cfg)) {
		LOGE("tcgetattr() failed");
		close(fd);
		return ERROR;
	}

	cfmakeraw(&cfg);
	cfsetispeed(&cfg, speed);
	cfsetospeed(&cfg, speed);
	//�޸Ŀ���ģʽ����֤���򲻻�ռ�ô���
	cfg.c_cflag |= CLOCAL;
	//�޸Ŀ���ģʽ��ʹ���ܹ��Ӵ����ж�ȡ��������
	cfg.c_cflag |= CREAD;

	cfg.c_cflag &= ~CRTSCTS;
	cfg.c_cflag |= CRTSCTS;
	cfg.c_cflag |= IXON | IXOFF | IXANY;
	//�޸����ģʽ��ԭʼ�������
	cfg.c_oflag &= ~OPOST;
	cfg.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
	//���õȴ�ʱ�����С�����ַ�
//	cfg.c_cc[VTIME] = 1; /* ��ȡһ���ַ��ȴ�1*(1/10)s */
	cfg.c_cc[VMIN] = 1; /* ��ȡ�ַ������ٸ���Ϊ1 */

	//�����������������������ݣ����ǲ��ٶ�ȡ ˢ���յ������ݵ��ǲ���
	tcflush(fd, TCIFLUSH);

	if (tcsetattr(fd, TCSANOW, &cfg) != 0) {
		LOGE("tcsetattr() failed");
		return ERROR;
	}
	return fd;
}

void receive(int fd, void* buff, size_t size) {
	int len = 0;
	int file = createLog(LOG_FILE);
	while (1) {
		len = read(fd, buff, size);
		if (len > 0) {
			//buff[len] = '\0';
			LOGD("%s", buff);
			writeLog(file,buff,len);
		} else {
			LOGW("cannot receive data\n");
		}
		memset(buff, 0, size);
		usleep(100 * 1000);
	}
	LOGW("receive end\n");
	saveLog(file);
}

void send(int fd, char* str, size_t len) {
	if (fd <= 0 || str == NULL) {
		return;
	}
	LOGD("send data is %s,size=%d", str,len);
	write(fd, str, len);
}

void closeUart(int fd) {
	if (fd > 0) {
		close(fd);
	}
}

int createLog(const char* file) {
	int fd;
	fd = open(file,O_RDWR|O_APPEND|O_CREAT,S_IRUSR|S_IWUSR);
	return fd;
}

int writeLog(int file, void* str,size_t size){
	if(file && str){
		return write(file,str,size);
	}
	return ERROR;
}

int saveLog(int file){
	if(file){
		return close(file);
	}
	return ERROR;
}
