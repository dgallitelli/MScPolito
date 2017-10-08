#include <stdio.h>
#include <stdlib.h>

extern unsigned int countweeks ( char* date );

void main()
{
    char my_string[8];
	unsigned int result;
	scanf("%s", my_string);
	result = countweeks(&my_string);
	printf("Number of weeks since 01/01/2000 : %d", result);
}