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
