// =============================================================================
// 
// This example program is to show the operation of led matrix connected as follows:
//   Led Matrix SPI
//     MOSI = IO11
//     CS   = IO12
//     SCK  = IO13
// 
//  
// =============================================================================

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <errno.h>
#include <fcntl.h>
#include <poll.h>
#include <pthread.h>
#include <sys/ioctl.h>
#include <signal.h>
#include <linux/spi/spidev.h>

#include "gpio.h"

#define DEVICE_FILE "/dev/spidev1.0"
#define SPI_CS 15
#define PERIOD_MAX 200000000
#define BITS_PER_WORD 16
#define MSB_FIRST 0


int spi_fd, exit_flag;
unsigned long period_ns = PERIOD_MAX;
uint8_t mode = SPI_MODE_0;
uint8_t bits = BITS_PER_WORD;
uint32_t speed = 5000000;
uint32_t lsb = MSB_FIRST;


uint64_t frames[8] = {
	0x0000000018000000,
	0x000000183c000000,
	0x0000183c7e660000,
	0x00183c7effff6600,
	0x183c7effffffff66,
	0x00183c7effff6600,
	0x0000183c7e660000,
	0x000000183c000000
};

// return the TSC value  -- demo only, not used in this example
unsigned long long rdtscl() {
	
	unsigned int lo, hi;
	__asm__ __volatile__ ("rdtsc" : "=a"(lo), "=d"(hi));                        
	return ( (unsigned long long)lo)|( ((unsigned long long)hi)<<32 );  
}

// send the 16-bit packet to the led matrix over spi connect
void send_packet (uint8_t addr, uint8_t data) 
{
	int ret;
	uint16_t pkt = (addr << 8) + data; // bitshift may cause problems for uint8
	uint16_t rx;
	
	struct spi_ioc_transfer tr = {
		.tx_buf   = (unsigned long) &pkt,
		.rx_buf	  = (unsigned long) &rx,
		.len      = sizeof (uint16_t),
		.speed_hz = speed,
		.bits_per_word = bits
	};
	
	gpio_set_val(SPI_CS, LOW);

	ret = ioctl(spi_fd, SPI_IOC_MESSAGE(1), &tr);
	if (ret < 1) error("Could not send spi message");

	gpio_set_val(SPI_CS, HIGH);
}

// send sequence of packets over spi to set frame on led matrix

void send_pattern(uint64_t frame) 
{
	uint8_t i;
	for (i = 0; i < 8; i++) send_packet((i+1), frame >> (i*8));
}

// Setup IO and initialize led matrix over spi
void spi_init() 
{
	// Setup IO11 for SPI1 MOSI
	gpio_init_set(24, LOW);
	gpio_init_set(44, HIGH);
	gpio_export(72);
	gpio_set_val(72, LOW);

	// Setup IO13 for SPI1 SCK
	gpio_init_set(30, LOW);
	gpio_init_set(46, HIGH);
  
	// Setup IO12 as SPI CS
	gpio_init_set(15, LOW);
	gpio_init_set(42, LOW);

	spi_fd = open(DEVICE_FILE,  O_RDWR | O_SYNC);
	
	if (spi_fd < 0) error("Could not open spi device");
	
	if (ioctl(spi_fd, SPI_IOC_WR_MODE, &mode) < 0) 
		error("Could not set up spi mode");
	if (ioctl(spi_fd, SPI_IOC_WR_BITS_PER_WORD, &bits) < 0) 
		error("Could not set bits per word");
	if (ioctl(spi_fd, SPI_IOC_WR_MAX_SPEED_HZ, &speed) < 0) 
		error("Could not set spi speed");
	if (ioctl(spi_fd, SPI_IOC_WR_LSB_FIRST, &lsb) < 0)
		error("Could not set msb first");
	
	send_packet(0x0B, 0x07); // scan all
	send_packet(0x09, 0x00); // decode off
	send_packet(0x0C, 0x01); // shutdown mode
	send_packet(0x0F, 0x00); // test mode off
	send_packet(0x0A, 0x0F); // full intensity
	
	int i;
	for (i = 0; i < 8; i++) send_packet((uint8_t) i+1, 0x00);
}



// pthread function to periodically update frame, uses period set in main
void* frame_changer(void *arg) 
{
	struct timespec next;
	clock_gettime(CLOCK_MONOTONIC, &next);
   
	int i = 0;
	while (!exit_flag) 
	{
		send_pattern(frames[i]);
		i++; 
		if (i == 8) i=0;
		next.tv_sec  = (next.tv_sec) + (next.tv_nsec + period_ns) / 1000000000L;
		next.tv_nsec = (next.tv_nsec + period_ns) % 1000000000L;
		clock_nanosleep(CLOCK_MONOTONIC, TIMER_ABSTIME, &next, 0);
	}
	return NULL;
}

void signal_handler(int signal)
{

	exit_flag = 1;
	
	return;
}

int main() 
{
	pthread_attr_t attr;
	struct sched_param param;
	pthread_t fc_thread; // to run frame changer thread

	spi_init();  // setup connection to matrix
	exit_flag=0;
	
	/* Set up the structure to specify the signal handling. */
	struct sigaction sig_one;

	
	sig_one.sa_handler = signal_handler;
    sig_one.sa_flags = 0;
	if (sigaction(SIGINT, &sig_one, NULL) < 0) {
		error("sigaction error");
		return 1;
	}
  	
	// start the frame changer
	pthread_attr_init(&attr);
	param.sched_priority = 99;
	pthread_attr_setschedparam(&attr,&param);
	pthread_attr_setinheritsched(&attr,PTHREAD_EXPLICIT_SCHED);
	//pthread_attr_setschedpolicy(&attr,SCHED_FIFO);
	
	if (pthread_create(&fc_thread,  &attr, &frame_changer, NULL) != 0)
		error("Could not start thread");

	pthread_join(fc_thread, NULL);
	
	int i;
	for (i = 0; i < 8; i++) send_packet((uint8_t) i+1, 0x00);

	close(spi_fd);
	
	return 0;
}

