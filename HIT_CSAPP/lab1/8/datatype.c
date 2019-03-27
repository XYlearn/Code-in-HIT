#include <stdio.h>
#include <string.h>

int i = 250;
short sh = 520;
long l = 520;
float f = 3.14;
double df = 9.87;
char c = 'X';
char s[12] = "xhy";
int arr[7] = {1, 2, 3, 4, 5 ,6 ,7};
int *p = arr;
struct Struct
{
	char sex;
	int age;
}infos;


union Union
{
	char sex;
	int age;
}infou;

enum Weekday
{
	Mon, Tue, Wed, Thu, Fri, Sat, Sun
}day = Mon;

void showbytes(char *var, size_t len)
{
	for(size_t i = 0 ; i < len ; i++)
		printf("%02x", *((char *)var + i));
}

void main()
{
	infos.age = 18;
	infos.sex = 'm';
	infou.age = 18;
	infou.sex = 'm';
	printf("\n");
	printf("varname\t value\t address\t hexbytes\n");
	printf("i\t %d\t %p\t ", i, &i);
	showbytes((char *)&i,sizeof(int));
	printf("\n");

	printf("sh\t %d\t %p\t ", sh, &sh);
	showbytes((char *)&sh, sizeof(short));
	printf("\n");

	printf("l\t %ld\t %p\t ", l, &l);
	showbytes((char *)&l, sizeof(long));
	printf("\n");

	printf("f\t %f\t %p\t ", f, &f);
	showbytes((char *)&f, sizeof(float));
	printf("\n");

	printf("df\t %lf\t %p\t ", df, &df);
	showbytes((char *)&df, sizeof(double));
	printf("\n");

	printf("c\t %c\t %p\t ", c, &c);
	showbytes((char *)&c, sizeof(char));
	printf("\n");

	printf("s\t %s\t %p\t ", s, s);
	showbytes((char *)s, sizeof(s));
	printf("\n");

	printf("p\t %p\t %p\t ", p, &p);
	showbytes((char *)&p, sizeof(int *));
	printf("\n");

	printf("arr\t ");
	for(int j = 0; j < sizeof(arr)/sizeof(int); ++j)
		printf("%d ", arr[j]);
	printf("\t %p\t ", arr);
	showbytes((char *)arr, sizeof(arr));
	printf("\n");

	printf("infos\t %c %d\t %p\t ", infos.sex, infos.age, &infos);
	showbytes((char *)&infos, sizeof(infos));
	printf("\n");

	printf("infou\t %c %d\t %p\t ", infou.sex, infou.age, &infou);
	showbytes((char *)&infou, sizeof(infou));
	printf("\n");

	printf("day\t %d\t %p\t ", day, &day);
	showbytes((char *)&day, sizeof(day));
	printf("\n");
	
	printf("main\t %p\t %p\t ", main, main);
	showbytes((char *)main, sizeof(main));
	printf("\n");
}
