#ifndef _PSOCKET_H_
#define _PSOCKET_H_

#include <stdio.h>
#include <netinet/in.h>

#ifdef __cplusplus
extern "C" {
#endif

#ifdef PAPI

typedef struct p_socket_t {
    int sockfd;
    int is_ssl;
    int port;
    struct sockaddr_in sa;
} PSocket;

PAPI PSocket * p_sock_new();
PAPI int p_sock_init(PSocket *psock);
PAPI int p_sock_bind(PSocket *psock, int port);
PAPI PSocket * p_sock_accept(PSocket *psock);
PAPI int p_sock_listen(PSocket *psock, size_t maxq);
PAPI ssize_t p_sock_read(PSocket *psock, char *buf, size_t nbytes);
PAPI ssize_t p_sock_read_timeout(PSocket *psock, char *buf, size_t nbytes, struct timeval *timeout);
PAPI ssize_t p_sock_write(PSocket *psock, char *buf, size_t nbytes);
PAPI int p_sock_close(PSocket *psock);
PAPI void p_sock_free(PSocket *psock);
PAPI int p_sock_connect(PSocket *psocket, const char *host, int port);
PAPI int p_sock_in_addr(const char *host, in_addr_t *addr);

#endif //PAPI

#ifdef __cplusplus
}
#endif

#endif
