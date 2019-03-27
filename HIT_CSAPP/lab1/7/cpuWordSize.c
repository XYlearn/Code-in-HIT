#include <stdio.h>
#include <limits.h>

unsigned cpuWordSize()
{
	return __WORDSIZE;
}
void main()
{
	printf("WordSize:%ubit\n", cpuWordSize());
}
