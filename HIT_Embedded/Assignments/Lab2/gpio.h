// =============================================================================
//
// GPIO utillity functions
// =============================================================================

 
#ifndef __GPIO_H
#define __GPIO_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>

#define SYSFS_GPIO_DIR "/sys/class/gpio"
#define MAX_BUF 64

#define HIGH 1
#define LOW  0

#define INPUT  1
#define OUTPUT 0

void error(const char *msg) 
{
	if (errno != 0) perror(msg);
	else printf("%s\n", msg);
	//abort();
}

// =============================================================================
// GPIO Methods
// =============================================================================

void gpio_export(unsigned int gpio) 
{
	int fd, len;
	char buf[MAX_BUF];
 
	fd = open(SYSFS_GPIO_DIR "/export", O_WRONLY);
	if (fd < 0) error("Could not open /export");
 
	len = snprintf(buf, sizeof(buf), "%d", gpio);
	write(fd, buf, len);
	close(fd);
}

void gpio_unexport(unsigned int gpio) 
{
	int fd, len;
	char buf[MAX_BUF];
 
	fd = open(SYSFS_GPIO_DIR "/export", O_WRONLY);
	if (fd < 0) error("Could not open /unexport");
 
	len = snprintf(buf, sizeof(buf), "%d", gpio);
	write(fd, buf, len);
	close(fd);
}

void gpio_set_val(unsigned int gpio, unsigned int value) 
{
	int fd;
	char buf[MAX_BUF];
 
	snprintf(buf, sizeof(buf), SYSFS_GPIO_DIR "/gpio%d/value", gpio);
 
	fd = open(buf, O_WRONLY);
	if (fd < 0) {
		printf("%s\n", buf);
		error("Can't open /gpio*/value");
	}
 
	if (value) write(fd, "1", 1);
	else       write(fd, "0", 1);
 
	close(fd);
}

void gpio_get_value(unsigned int gpio, unsigned int *value) 
{
	int fd;
	char buf[MAX_BUF];
	char ch;

	snprintf(buf, sizeof(buf), SYSFS_GPIO_DIR "/gpio%d/value", gpio);
 
	fd = open(buf, O_RDONLY);
	if (fd < 0) error("Can't open /gpio*/value");
 
	read(fd, &ch, 1);

	if (ch == '0') *value = LOW;
	else           *value = HIGH;
 
	close(fd);
}

void gpio_set_dir(unsigned int gpio, unsigned int dir) 
{
	int fd;
	char buf[MAX_BUF];
	snprintf(buf, sizeof(buf), SYSFS_GPIO_DIR  "/gpio%d/direction", gpio);
	fd = open(buf, O_WRONLY);
	
	//if (fd < 0) error("Can't open /gpio*/direction");
	if (dir) write(fd, "in", 2);
	else     write(fd, "out", 3);
 
	close(fd);
}

void gpio_set_edge(unsigned int gpio, char *edge) 
{
	int fd;
	char buf[MAX_BUF];

	snprintf(buf, sizeof(buf), SYSFS_GPIO_DIR "/gpio%d/edge", gpio);
 
	fd = open(buf, O_WRONLY);
	if (fd < 0) error("Can't open /gpio*/direction");
 
	write(fd, edge, strlen(edge) + 1); 
	close(fd);
}

void gpio_init_set(unsigned int gpio, unsigned int value) {
  gpio_export(gpio);
  gpio_set_dir(gpio, OUTPUT);
  gpio_set_val(gpio, value);
}

#endif
