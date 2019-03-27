#include <stdint.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include <winsock.h>

#include "sysInclude.h"

extern void ip_DiscardPkt(char * buf ,int type);
extern void ip_SendtoLower(char *buf ,int length);
extern void ip_SendtoUp(char *buf, int length);
extern unsigned int getIpv4Address();

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

static uint16_t ipv4_calc_checksum(char *buf, int hlen);
static int ipv4_check_head(char *buf, int *type);
static int ipv4_should_recv(uint32_t dst_ip);

/**
 * receive ipv4 packet and upsend to transport layer
 * 
 * @param buf: pointer to ipv4 head
 * @param size: length of buf
 * @return: 0: success
 *          1: fail
 */
int stud_ip_recv(char * buf, unsigned short size) {
    int err_type;
    uint32_t local_ip;

    // check header field
    if (ipv4_check_head(buf, &err_type)) {
        ip_DiscardPkt(buf, err_type);
        return 1;
    }
    
    // check ip address
    if (!ipv4_should_recv(ntohl(ipv4_dst_val(buf)))) {
        ip_DiscardPkt(buf, STUD_IP_TEST_DESTINATION_ERROR);
        return 1;
    }
    
    ip_SendtoUp(buf, size);
    return 0;
}

/**
 * wrap data from transport layer and down send to data link layer
 * 
 * @param buf: pointer to data of ipv4 packet
 * @param size: length of buf
 * @param src: source ipv4 addr
 * @param dst: dest ipv4 addr
 * @param proto: protocol number of transport layer
 * @param ttl: time to live
 * 
 * @return 0: success
 *         1: fail
 */ 
int stud_ip_Upsend(char* buf, unsigned short size, unsigned int src, unsigned int dst, char proto, char ttl) {
    uint16_t total_len;
    char *packet;

    total_len = size + 20;
    packet = (char *) calloc(total_len, 1);
    *(ipv4_version_ihl_ptr(packet)) = 0x45;
    *(ipv4_total_len_ptr(packet)) = htons(total_len);
    *(ipv4_iden_ptr(packet)) = 0;
    *(ipv4_frag_flag_off_ptr(packet)) = 0;
    *(ipv4_ttl_ptr(packet)) = ttl;
    *(ipv4_protocol_ptr(packet)) = proto;
    *(ipv4_src_ptr(packet)) = htonl(src);
    *(ipv4_dst_ptr(packet)) = htonl(dst);

    // calc checksum
    *(ipv4_checksum_ptr(packet)) = htons(ipv4_calc_checksum(packet, 20));
    memcpy(packet + 20, buf, size);

    ip_SendtoLower(packet, total_len);
    // free(packet);

    return 0;
}

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

// check header, if success return 0, if fail return -1 and set type
static int ipv4_check_head(char *buf, int *type) {
    uint8_t ver, ihl, hlen, ttl, chksum, exp_chksum;
    
    // check version
    ver = ipv4_version_val(buf);
    if (ver != 4) {
        printf("[DEBUG] version %d != 4\n", ver);
        *type = STUD_IP_TEST_VERSION_ERROR;
        return -1;
    }

    // check header length
    ihl = ipv4_ihl_val(buf);
    if (ihl > 15 || ihl < 5) {
        printf("[DEBUG] ihl %d not in [5, 15]\n", ihl);
        *type = STUD_IP_TEST_HEADLEN_ERROR;
        return -1;
    }

    // check ttl
    ttl = ipv4_ttl_val(buf);
    if (ttl == 0) {
        printf("[DEBUG] ttl %d == 0\n", ttl);
        *type = STUD_IP_TEST_TTL_ERROR;
        return -1;
    }

    // check checksum
    hlen = ihl * 4;
    exp_chksum = htons(ipv4_calc_checksum(buf, hlen));
    chksum = ipv4_checksum_val(buf);
    if (exp_chksum != chksum) {
        printf("[DEBUG] checksum %d != %d\n", chksum, exp_chksum);
        *type = STUD_IP_TEST_CHECKSUM_ERROR;
        return -1;
    }

    return 0;
}

// need to receive: return 1; else: return 0;
static int ipv4_should_recv(uint32_t dst_ip) {
    uint32_t local_ip, val;

    local_ip = getIpv4Address();
    if (local_ip == dst_ip)
        return 1;
    while (dst_ip & 1) {
        dst_ip >>= 1;
        local_ip >>= 1;
    }
    return dst_ip == local_ip ? 1 : 0;
        
}
