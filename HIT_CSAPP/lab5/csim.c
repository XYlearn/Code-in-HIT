#include <getopt.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <assert.h>
#include <math.h>
#include <limits.h>
#include <string.h>
#include <errno.h>
#include <limits.h>
#include "cachelab.h"

//#define DEBUG_ON 
#define ADDRESS_LENGTH 64

/* Type: Memory address */
typedef unsigned long long int mem_addr_t;

/* Type: Cache line
   LRU is a counter used to implement LRU replacement policy  */
typedef struct cache_line {
    char valid;
    mem_addr_t tag;
    unsigned long long int lru;
} cache_line_t;

typedef cache_line_t* cache_set_t;
typedef cache_set_t* cache_t;

/* Globals set by command line args */
int verbosity = 0; /* print trace if set */
int s = 0; /* set index bits */
int b = 0; /* block offset bits */
int E = 0; /* associativity */
char* trace_file = NULL;

/* Derived from command line args */
int S; /* number of sets */
int B; /* block size (bytes) */
int t; /* number of tag bits */

/* Counters used to record cache statistics */
int miss_count = 0;
int hit_count = 0;
int eviction_count = 0;
unsigned long long int lru_counter = 1;

/* The cache we are simulating */
cache_t cache;  
mem_addr_t set_index_mask;
mem_addr_t tag_mask;	/*tag mask*/

/* 
 * initCache - Allocate memory, write 0's for valid and tag and LRU
 * also computes the set_index_mask
 */
void initCache()
{
	// malloc sets
	cache = (cache_t)calloc(sizeof(cache_set_t), S);
	for (int i = 0; i < S; ++i)
	{
		// malloc cache_line
		cache[i] = (cache_set_t)calloc(sizeof(cache_line_t), E);
		for(int j = 0; j < E; ++j)
		{
			cache[i][j].valid = 0;
			cache[i][j].lru = 0;
		}
	}

	int bits = b + s;

	/* calculate tag_mask */
	tag_mask = 0; tag_mask--;
	for(int i = 0; i < bits; ++i)
		tag_mask <<= 1;

	/* calculate set_index_mask */
	set_index_mask = 0; set_index_mask--;
	for (int i = 0; i < t; ++i)
		set_index_mask >>= 1;
	bits = 1;
	for (int j = 0; j < b; ++j)
		bits <<= 1;
	bits--;
	set_index_mask ^= bits;

}


/* 
 * freeCache - free allocated memory
 */
void freeCache()
{
	for (int i = 0; i < S; ++i)
		free(cache[i]);
	free(cache);
}


typedef enum HIT_STATUS
{
	HIT, MISS, EVICTED
} HIT_STATUS;

/* 
 * accessData - Access data at memory address addr.
 *   If it is already in cache, increast hit_count
 *   If it is not in cache, bring it in cache, increase miss count.
 *   Also increase eviction_count if a line is evicted.
 */
HIT_STATUS accessData(mem_addr_t addr)
{
	int set_index = (addr & set_index_mask) >> b;
	cache_set_t set = cache[set_index];

	int max_lru = 1;
	int max_lru_index = 0;
	int invalid_index = -1;

	// increase lru of each line
	for(int i = 0; i != E; ++i)
	{
		++set[i].lru;
	}
	for(int i = 0; i != E; ++i)
	{

		if(!set[i].valid)
		{
			invalid_index = i;
			break;
		}
	}

	/* search each line */
	for(int i = 0; i != E; ++i)
	{
		/* if hit */
		if(set[i].valid && set[i].tag == (addr & tag_mask))
		{
			++hit_count;
			set[i].lru = 0;
			return HIT;
		}

		/* get max lru*/
		if(set[i].lru > max_lru)
		{
			max_lru = set[i].lru;
			max_lru_index = i;
		}
	}
	/* if cold miss */
	if(invalid_index != -1)
	{
		++miss_count;
		set[invalid_index].valid = 1;
		set[invalid_index].tag = addr & tag_mask;
		set[invalid_index].lru = 0;
		return MISS;
	}
	/* if 'hot' miss */
	else
	{
		++miss_count;
		++eviction_count;
		set[max_lru_index].tag = addr & tag_mask;
		set[max_lru_index].lru = 0;
		return EVICTED;
	}
}


/*
 * replayTrace - replays the given trace file against the cache 
 */
void replayTrace(char* trace_fn)
{
    char buf[1000];
    mem_addr_t addr=0;
    unsigned int len=0;
    FILE* trace_fp = fopen(trace_fn, "r");

    if(!trace_fp){
        fprintf(stderr, "%s: %s\n", trace_fn, strerror(errno));
        exit(1);
    }

    HIT_STATUS status;

    while( fgets(buf, 1000, trace_fp) != NULL) {
        if(buf[1]=='S' || buf[1]=='L' || buf[1]=='M') {
            sscanf(buf+3, "%llx,%u", &addr, &len);

            status = accessData(addr);

            /* If the instruction is R/W then access again */
            if(buf[1]=='M')
                status = accessData(addr);

            if(verbosity)
            {
                printf("%c %llx,%u ", buf[1], addr, len);
                switch(status)
                {
                case HIT:
                	printf("hit ");
                	break;
                case EVICTED:
                	printf("miss evicted ");
                	break;
                case MISS:
                	printf("miss ");
                }
                printf("\n");
            }
        }
    }

    fclose(trace_fp);
}

/*
 * printUsage - Print usage info
 */
void printUsage(char* argv[])
{
    printf("Usage: %s [-hv] -s <num> -E <num> -b <num> -t <file>\n", argv[0]);
    printf("Options:\n");
    printf("  -h         Print this help message.\n");
    printf("  -v         Optional verbose flag.\n");
    printf("  -s <num>   Number of set index bits.\n");
    printf("  -E <num>   Number of lines per set.\n");
    printf("  -b <num>   Number of block offset bits.\n");
    printf("  -t <file>  Trace file.\n");
    printf("\nExamples:\n");
    printf("  linux>  %s -s 4 -E 1 -b 4 -t traces/yi.trace\n", argv[0]);
    printf("  linux>  %s -v -s 8 -E 2 -b 4 -t traces/yi.trace\n", argv[0]);
    exit(0);
}

/*
 * main - Main routine 
 */
int main(int argc, char* argv[])
{
    char c;

    while( (c=getopt(argc,argv,"s:E:b:t:vh")) != -1){
        switch(c){
        case 's':
            s = atoi(optarg);
            break;
        case 'E':
            E = atoi(optarg);
            break;
        case 'b':
            b = atoi(optarg);
            break;
        case 't':
            trace_file = optarg;
            break;
        case 'v':
            verbosity = 1;
            break;
        case 'h':
            printUsage(argv);
            exit(0);
        default:
            printUsage(argv);
            exit(1);
        }
    }

    /* Make sure that all required command line args were specified */
    if (s == 0 || E == 0 || b == 0 || trace_file == NULL) {
        printf("%s: Missing required command line argument\n", argv[0]);
        printUsage(argv);
        exit(1);
    }

    /* Compute S, E and B and t from command line args */
    S = 1;
    for(int i = 0; i < s; ++i)
    	S <<= 1;
    B = 1;
    for(int i = 0; i < b; ++i)
    	B <<= 1;
    t = ADDRESS_LENGTH - (b + s);
 
    /* Initialize cache */
    initCache();

#ifdef DEBUG_ON
    printf("DEBUG: S:%u E:%u B:%u trace:%s\n", S, E, B, trace_file);
    printf("DEBUG: set_index_mask: %llx\n", set_index_mask);
    printf("DEBUG: tag_mask: %llx\n", tag_mask);
#endif
 
    replayTrace(trace_file);

    /* Free allocated memory */
    freeCache();

    /* Output the hit and miss statistics for the autograder */
    printSummary(hit_count, miss_count, eviction_count);
    return 0;
}
