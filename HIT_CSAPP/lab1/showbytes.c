#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void showbytes(char *cont)
{
	char c;
	int i;
	for(i = 0;c = *(cont+i); ++i)
	{
		if(i % 16 == 0 && i)
		{
			printf("\n");
			for(int j = i-16; j < i; ++j)
				printf("%02x ", cont[j] & 0xff);
			printf("\n");
		}
		switch(c)
		{
			case '\t':
				printf("\\t ");
				break;
			case '\n':
				printf("\\n ");
				break;
			case '\r':
				printf("\\r ");
				break;
			default:
				printf("%c  ", c);
		}
	}
	printf("\n");
	for(int j = i-i%16; j < i; j++)
		printf("%02x ", cont[j] & 0xff);
	printf("\n");	
}

int main(int argc, char *argv[])
{
	if(argc != 2)
	{
		printf("Usage:%s [filename]\n", argv[0]);
		exit(0);
	}
	FILE *fp = NULL;
	if((fp = fopen(argv[1], "rb")) == NULL)
	{
		printf("Can't open the file %s!\n", argv[1]);
		exit(0);
	}
	fseek(fp, 0, SEEK_END);
	size_t length = ftell(fp);
	rewind(fp);
	char *cont = malloc(length);
	fread(cont, sizeof(char), length, fp);
	if(cont == NULL)
	{
		printf("Memory allocate error!\n");
		exit(0);
	}
	showbytes(cont);
	free(cont);

	return 0;
}
