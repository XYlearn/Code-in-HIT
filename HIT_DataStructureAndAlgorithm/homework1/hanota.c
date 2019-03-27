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
���Ӷȷ�����
  ʱ�临�Ӷȣ�
    �ݹ鷽�̣�
    T(n) = | G
           | 3G
           | 2T(n-1) + C
    ����T(n) = O(2^n)
  �ռ临�Ӷȣ�
    ���㺯������ջ�����Ŀռ临�Ӷ�,���㷽����ʱ�临�Ӷ���ͬ
    S(n) = O(2^n)
*/

