#include <stdio.h>
#define true 1
#define false 0
#define bool int

bool isLittleEndian()
{
	short n = 0x1100;
	/*expand to sizeof(short)*/
	int t = sizeof(n) >> 2;
	for(int i = 0; i < t-1; ++i)
		n |= n << (2 * sizeof(char));
	if(*(char *)&n == 0x00)
		return true;
	else
		return false;
}

void main()
{
	if(isLittleEndian())
		printf("LittleEndian\n");
	else
		printf("BigEndian\n");
}

