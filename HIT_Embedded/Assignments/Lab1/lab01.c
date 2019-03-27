#include <stdio.h>
#include <unistd.h>

#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
// #include <pthread.h>
#include <poll.h>

/*
 * On board LED blink C example
 *
 * Demonstrate how to blink the on board LED, writing a digital value to an
 * output pin
 *
 * - digital out: a LED on shield pin 1
 *
 * Additional linker flags: none
 */

void IOSetup(void)
{
	int FdExport, Fd12, Fd28, Fd45;

	FdExport = open("/sys/class/gpio/export", O_WRONLY);
		if (FdExport < 0)
		{
			printf("\n gpio export open failed");
		}

		if(0< write(FdExport,"12",2))
			printf("error FdExport 12");
		if(0< write(FdExport,"28",2))
			printf("error FdExport 28");
		if(0< write(FdExport,"45",2))
			printf("error FdExport 45");

		close(FdExport);

	    /* Initialize all GPIOs */
		Fd12 = open("/sys/class/gpio/gpio12/direction", O_WRONLY);
		if (Fd12 < 0)
		{
			printf("\n gpio12 direction open failed");
		}
		//IO00
		Fd28 = open("/sys/class/gpio/gpio28/direction", O_WRONLY);
		if (Fd28 < 0)
		{
			printf("\n gpio28 direction open failed");
		}

		Fd45 = open("/sys/class/gpio/gpio45/direction", O_WRONLY);
		if (Fd45 < 0)
		{
			printf("\n gpio45 direction open failed");
		}


		if(0< write(Fd12,"out",3))
			printf("error Fd12");

		if(0< write(Fd28,"out",3))
			printf("error Fd28");

		if(0< write(Fd45,"out",3))
			printf("error Fd45");

		Fd28 = open("/sys/class/gpio/gpio28/value", O_WRONLY);
		if (Fd28 < 0)
				{
					printf("\n gpio28 value open failed");
				}
		Fd45 = open("/sys/class/gpio/gpio45/value", O_WRONLY);
		if (Fd45 < 0)
				{
					printf("\n gpio45 value open failed");
				}

		if(0< write(Fd28,"0",1))
					printf("error Fd28 value");

		if(0< write(Fd45,"0",1))
					printf("error Fd45 value");
}

int main()
{
	// select onboard LED pin based on the platform type
	// create a GPIO object from MRAA using it
	int Led;

	IOSetup();

	Led = open("/sys/class/gpio/gpio12/value", O_WRONLY);

	// loop forever toggling the LED every second
	for (;;) {
		write(Led,"1",1);
		sleep(1);
		write(Led, "0", 1);
		sleep(1);
	}

	return 0;
}
