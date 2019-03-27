#include <stdint.h>
#include <string.h>
#include <stdlib.h>

#include <winsock.h>
/*
* THIS FILE IS FOR IP FORWARD TEST
*/
#include "sysInclude.h"

// get fields value
#define ipv4_version_val(buf) (*(uint8_t *)(buf) >> 4)
#define ipv4_ihl_val(buf) (*(uint8_t *)(buf) & 0xf)
#define ipv4_ttl_val(buf) (*((uint8_t *)(buf) + 8))
#define ipv4_checksum_val(buf) (*(uint16_t *)((uint8_t *)(buf) + 10))
#define ipv4_src_val(buf) (*((uint32_t *)(buf) + 3))
#define ipv4_dst_val(buf) (*((uint32_t *)(buf) + 4))

// get field ptr
#define ipv4_version_ihl_ptr(buf) ((uint8_t *)(buf))
#define ipv4_tos_ptr(buf) ((uint8_t *)(buf) + 1)
#define ipv4_total_len_ptr(buf) ((uint16_t *)(buf) + 1)
#define ipv4_iden_ptr(buf) ((uint16_t *)(buf) + 2)
#define ipv4_frag_flag_off_ptr(buf) ((uint16_t *)(buf) + 3)
#define ipv4_ttl_ptr(buf) ((uint8_t *)(buf) + 8)
#define ipv4_protocol_ptr(buf) ((uint8_t *)(buf) + 9)
#define ipv4_checksum_ptr(buf) ((uint16_t *)(buf) + 5)
#define ipv4_src_ptr(buf) (((uint32_t *)(buf)) + 3)
#define ipv4_dst_ptr(buf) (((uint32_t *)(buf)) + 4)

#define MAX_HEADER_SIZE 60

// system support
extern void fwd_LocalRcv(char *pBuffer, int length);
extern void fwd_SendtoLower(char *pBuffer, int length, unsigned int nexthop);
extern void fwd_DiscardPkt(char *pBuffer, int type);
extern unsigned int getIpv4Address();

// implemented by students

// route table
// trie of binary number
typedef struct route_table_t {
    struct route_table_t *l;
    struct route_table_t *r;
    stud_route_msg *route;
} RouteTable;

static RouteTable *route_table;


static int ipv4_should_recv(uint32_t dst_ip);
static uint16_t ipv4_calc_checksum(char *buf, int hlen);
static stud_route_msg *route_table_search(unsigned int ip);
static char *trans_repack(char* buf, int length);

void stud_Route_Init() {
	route_table = (RouteTable *)calloc(sizeof(RouteTable), 1);
}

void stud_route_add(stud_route_msg *proute) {
	RouteTable *rt;
    unsigned int dest, masklen, bit;
    stud_route_msg *new_route;
    
    rt = route_table;
    dest = ntohl(proute->dest);
    masklen = ntohl(proute->masklen);
    bit = 0x80000000;
    for (int i = 0; i < masklen; i++, bit >>= 1) {
        if (bit & dest) {
            if (!rt->r)
                rt->r = (RouteTable *)calloc(sizeof(RouteTable), 1);
            rt = rt->r;
        } else {
            if (!rt->l)
                rt->l = (RouteTable *)calloc(sizeof(RouteTable), 1);
            rt = rt->l;
        }
    }
    if (rt->route)
        free(rt->route);
    new_route = (stud_route_msg *)malloc(sizeof(stud_route_msg));
    memcpy(new_route, proute, sizeof(stud_route_msg));
    rt->route = new_route;
}

int stud_fwd_deal(char *buf, int length) {
    int err_type;
    uint32_t local_ip, dest_ip;
    stud_route_msg *route;
    char *new_packet;

    // should receive
    if (ipv4_should_recv(ntohl(ipv4_dst_val(buf)))) {
        fwd_LocalRcv(buf, length);
        return 0;
    }

    // check ttl error
    if (ipv4_ttl_val(new_packet) - 1 == 0) {
        fwd_DiscardPkt(buf, STUD_FORWARD_TEST_TTLERROR);
        return 1;
    }
    
    // query route table
    dest_ip = ntohl(ipv4_dst_val(buf));
    route = route_table_search(dest_ip);
    if (!route) {
        fwd_DiscardPkt(buf, STUD_FORWARD_TEST_NOROUTE);
        return 1;
    }

    // transport
    new_packet = trans_repack(buf, length);
    fwd_SendtoLower(new_packet, length, ntohl(route->nexthop));
    free(new_packet);
    return 0;
}

static stud_route_msg *route_table_search(unsigned int ip) {
    unsigned int bit;
    RouteTable *rt;
    stud_route_msg *route;
    
    rt = route_table;
    bit = 0x80000000;
    route = NULL;
    while (rt) {
        if (rt->route)
            route = rt->route;
        if (bit & ip)
            rt = rt->r;
        else
            rt = rt->l;
        bit >>= 1;
    }
    return route;
}

/**
 * Modified from last lab code
 */ 

// calculate checksum for two byte aligned data
static uint16_t ipv4_calc_checksum(char *buf, int hlen) {
    char header[MAX_HEADER_SIZE];
    uint16_t *checksum_ptr, tmp;
    uint8_t *ptr;
    uint32_t sum;

    memcpy(header, buf, hlen);
    checksum_ptr = ipv4_checksum_ptr(header);
    *checksum_ptr = 0;
    ptr = (uint8_t *)header;
    sum = 0;
    while (hlen) {
        tmp = *ptr;
        tmp <<= 8;
        ptr++;
        tmp |= *ptr;
        ptr++;
        sum += tmp;
        hlen -= 2;
    }
    sum = (sum >> 16) + (uint16_t)sum;
    if (sum >> 16)
        sum = (sum >> 16) + (uint16_t)sum;
    tmp = sum;
    return ~tmp;
}

// need to receive: return 1; else: return 0;
static int ipv4_should_recv(uint32_t dst_ip) {
    uint32_t local_ip, val;

    local_ip = getIpv4Address();
    if (local_ip == dst_ip || dst_ip == 0 || dst_ip == 0x7f000001)
        return 1;
    while (dst_ip & 1) {
        dst_ip >>= 1;
        local_ip >>= 1;
    }
    return dst_ip == local_ip ? 1 : 0;       
}

static char *trans_repack(char* buf, int length) {
    char *packet;

    length = *ipv4_total_len_ptr(buf);
    packet = (char *) malloc(length);
    memcpy(packet, buf, length);
    *(ipv4_ttl_ptr(packet)) = ipv4_ttl_val(buf) - 1;
    *(ipv4_checksum_ptr(packet)) = 0;

    // calc checksum
    *(ipv4_checksum_ptr(packet)) = htons(ipv4_calc_checksum(packet, 20));

    return packet;
}

