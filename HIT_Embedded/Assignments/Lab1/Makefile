# uncommand the following definitions for cross-compilation with IOTDev SDK
# KDIR:=/opt/clanton-full/1.4.2/sysroots/i586-poky-linux/usr/src/kernel
# PWD:= $(shell pwd)

#IOT_HOME = /home/yann-hang/Gen2/IoT_downloads/iotdk-ide-linux/devkit-x86/sysroots

#PATH := $(PATH):$(IOT_HOME)/x86_64-pokysdk-linux/usr/bin/i586-poky-linux

#CC = i586-poky-linux-gcc
#ARCH = x86
#CROSS_COMPILE = i586-poky-linux-
#SROOT=$(IOT_HOME)/i586-poky-linux/

APP1 = example1

all:
	echo my new PATH is ${PATH}
	$(CC) -o $(APP1)  lab01.c
#	$(CC) -o $(APP1) --sysroot=$(SROOT) lab01.c 

clean:
	rm -f *.o
	rm -f $(APP) 
