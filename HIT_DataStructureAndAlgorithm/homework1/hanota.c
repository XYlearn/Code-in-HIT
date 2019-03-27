#include <stdio.h>
#include <stdlib.h>

int count;

void move(char from, char to)
{
	printf("move from %c to %c\n", from, to);
	++count;
}

/*move disk from a to b through c*/
void hanota(int num, char a, char b, char c)
{
	if(num == 1)
	{
		move(a, b);
		return;
	}
	else if(num == 2)
	{
		move(a, c);
		move(a, b);
		move(c, b);
		return;
	}
	hanota(num-1, a, c, b);
	move(a, b);
	hanota(num-1, c, b, a);
}

int main(int argc, char* argv[])
{
	if(argc != 2)
	{
		printf("Usage: hanota [num]\n");
		return 0;
	}
	hanota(atoi(argv[1]), 'a', 'b', 'c');
	printf("move %d times\n", count);
	return 0;
}
/*
复杂度分析：
  时间复杂度：
    递归方程：
    T(n) = | G
           | 3G
           | 2T(n-1) + C
    求解得T(n) = O(2^n)
  空间复杂度：
    计算函数调用栈产生的空间复杂度,计算方法与时间复杂度相同
    S(n) = O(2^n)
*/

