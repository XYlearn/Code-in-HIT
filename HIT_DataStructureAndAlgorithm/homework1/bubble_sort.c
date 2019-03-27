#include <stdio.h>

typedef int TYPE;

void swap(TYPE *a, TYPE *b)
{
	TYPE temp = *a;
	*a = *b;
	*b = temp;
}

void bubble_sort(TYPE *arr, TYPE *end)
{
	if(arr == end)
		return;
	TYPE *p, *pmin;
	for(p = pmin = arr; p < end; ++p)
	{
		if(*p < *pmin)
			pmin = p;
	}
	swap(arr, pmin);
	bubble_sort(arr + 1, end);
}

void main()
{
	TYPE a[] = {3, 4, 2, 5, 7, 3};
	TYPE *begin = a;
	TYPE *p = begin;
	TYPE *end = a + sizeof(a)/sizeof(TYPE);
	
	bubble_sort(begin, end);
	for(;p < end; ++p)
	{
		printf("%d ", *p);
	}
}

/*
  复杂度分析：
    时间复杂度：
      列方程：
      T(n) = | n          n = 1
             | n + T(n-1) n > 1
           = O(n^2)
    空间复杂度：
      S(n) = C + kn = O(n)
      而迭代实现的冒泡算法的时间复杂度为O(n^2)而空间复杂度只有O(1)
*/
