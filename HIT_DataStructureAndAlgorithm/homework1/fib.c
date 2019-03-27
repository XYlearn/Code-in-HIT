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
  复杂度分析：
    时间复杂度：
      方程：
      T(n) = | G                   n = 0,1
             | T(n-1) + T(n-2) + C n > 1
      求解得 T(n) = O(2^n)
    空间复杂度:
      分析方法与时间复杂度相同
      S(n) = O(2^n)
*/