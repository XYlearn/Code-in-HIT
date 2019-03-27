#include <unistd.h>
#include <stdio.h>
#include <poll.h>
#include <fcntl.h>

inline int gpio_open_export() {
	int export_fd = open("/sys/class/gpio/export");
	if (-1 == export_fd)
		perror("/sys/class/gpio/export");
	return export_fd;
}

inline int gpio_open_unexport() {
	int export_fd = open("/sys/class/gpio/unexport");
	if (-1 == export_fd)
		perror("/sys/class/gpio/unexport");
	return export_fd;
}

inline int gpio_open_value(int id) {
	char path[0x100];
	snprintf(path, 0x100, "/sys/class/gpio/gpio%d/value", id);
	int fd = open(path, O_WRONLY)
	if (-1 == fd)
		perror(path);
	return fd;
}

inline int gpio_open_direction(int id) {
	char path[0x100];
	snprintf(path, 0x100, "/sys/class/gpio/gpio%d/direction", id);
	int fd = open(path, O_WRONLY)
	if (-1 == fd)
		perror(path);
	return fd;
}

inline void gpio_write(int fd, const char * value) {
	if (-1 == write(fd, dir, strlen(dir)))
		perror(path);
	return fd;
}

inline io_setup() {
	int fd_export = gpio_open_export();
	gpio_write(fd_export, "12");
	gpio_write(fd_export, "28");
	gpio_write(fd_export, "45");
	gpio_write(fd_export, "1");
	gpio_write(fd_export, "20");
	gpio_write(fd_export, "68");
	gpio_write(fd_export, "38");
	gpio_write(fd_export, "40");

	int fd12 = 
}

int main() {

}
