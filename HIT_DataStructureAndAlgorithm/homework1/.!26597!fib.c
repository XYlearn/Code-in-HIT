#include <stdio.h>
#include <stdlib.h>

unsigned int fib(unsigned int n)
{
	if(n <= 1)
		return n;
	return fib(n-1) + fib(n-2);
}

int main(int argc, char *argv[])
{
	if(argc != 2)
	{
		printf("Usage: fib [num]");
		return 0;
	}
	int num = atoi(argv[1]);
	if(num < 0)
		return 0;
	printf("fib(%d)=%d\n", num, fib(num));
	return 0;
}

/*
