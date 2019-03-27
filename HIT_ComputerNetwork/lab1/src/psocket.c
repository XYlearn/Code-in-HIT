#include <stdlib.h>
#include <unistd.h>
#include <strings.h>
#include <netdb.h>
#include <errno.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#define PAPI
#include "psocket.h"
#include "utils.h"

#ifdef __cplusplus
extern "C" {
#endif

PAPI PSocket * p_sock_new() {
    PSocket *psock;

    psock = (PSocket *)calloc(sizeof(PSocket), 1);
    return psock;
}

PAPI int p_sock_init(PSocket *psock) {
    int sockfd;

    psock->sockfd = socket(AF_INET, SOCK_STREAM, 0);
    return sockfd;
}

PAPI int p_sock_bind(PSocket *psock, int port) {
    int retval;
    struct sockaddr_in sockaddr;

    sockaddr.sin_family = AF_INET;
    sockaddr.sin_addr.s_addr = INADDR_ANY;
    sockaddr.sin_port = htons(port);
    retval = bind(psock->sockfd, (struct sockaddr *)&sockaddr, sizeof(sockaddr));
    if(!retval)
        memcpy(&(psock->sa), &sockaddr, sizeof(sockaddr));
    return retval;
}

PAPI PSocket * p_sock_accept(PSocket *psock) {
    PSocket *conn;
    int retval;
    socklen_t sock_size;
    struct sockaddr_in c_addr;
    
    retval = accept(psock->sockfd, (struct sockaddr *)&c_addr, &sock_size);
    if (-1 == retval)
        return NULL;
    conn = p_sock_new();
    if (!conn) return NULL;
    conn->sockfd = retval;

    return conn;
}

PAPI int p_sock_listen(PSocket *psock, size_t maxq) {
    int retval;

    retval = listen(psock->sockfd, maxq);
    return retval;
}

PAPI ssize_t p_sock_read(PSocket *psock, char *buf, size_t nbytes) {
    int retval;

    retval = read(psock->sockfd, buf, nbytes);
    return retval;
}

PAPI ssize_t p_sock_read_timeout(PSocket *psock, char *buf, size_t nbytes, struct timeval *timeout) {
    int retval;
    struct fd_set fdset;

    FD_ZERO(&fdset);
    FD_SET(psock->sockfd, &fdset);
    retval = select(psock->sockfd + 1, &fdset, NULL, NULL, timeout);
    if (retval > 0)
        return read(psock->sockfd, buf, nbytes);
    else
        return retval;
}

PAPI ssize_t p_sock_write(PSocket *psock, char *buf, size_t nbytes) {
    int retval;

    retval = write(psock->sockfd, buf, nbytes);
    return retval;
}

PAPI int p_sock_close(PSocket *psock) {
    int retval;

    retval = close(psock->sockfd);
    return retval;
}

PAPI void p_sock_free(PSocket *psock) {
    p_sock_close(psock);
    free(psock);
}

// connect to host on port. port will be ignore if host contain port field
PAPI int p_sock_connect(PSocket *psocket, const char *host, int port) {
    struct sockaddr_in sockaddr;
    char buf[0x100];
    char *sep_ptr;
    int sockfd, retval;

    strncpy(buf, host, sizeof(buf));
    sep_ptr = strchr(buf, ':');
    if (sep_ptr) {
        *sep_ptr = '\0';
        port = atoi(sep_ptr + 1);
    }

    sockaddr.sin_family = AF_INET;
    sockaddr.sin_port = htons(port);
    retval = p_sock_in_addr(buf, &sockaddr.sin_addr.s_addr);
    sockfd = socket(PF_INET, SOCK_STREAM, 0);
    if (-1 == sockfd)
        return -1;

    if (-1 == connect(sockfd, (struct sockaddr *)&sockaddr, sizeof(sockaddr)))
    {
        close(sockfd);
        return -1;
    }
    // construct psocket
    psocket->port = port;
    psocket->sa = sockaddr;
    psocket->sockfd = sockfd;
    return 0;
}

PAPI int p_sock_in_addr(const char *host, in_addr_t *addr) {
    
    p_return_val_if_fail(host, -1);

    struct hostent *hostent;
    struct in_addr in;
    int retval;

    hostent = gethostbyname(host);
    if (!hostent || hostent->h_addr_list[0] == NULL)
        return -1;
    in = *(struct in_addr *)hostent->h_addr_list[0];
    *addr = in.s_addr;

    return 0;
}

#ifdef __cplusplus
}
#endif
