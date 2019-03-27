#ifndef _PROXY_H_
#define _PROXY_H_

#include <stdarg.h>
#include <arpa/inet.h>

#include "psocket.h"
#include "plist.h"

#define HTTPHOST 80

#define MAX_BUF_SIZE 65507
#define MAX_URL_SIZE 1024
#define MAX_HOST_SIZE 1024
#define MAX_COOKIE_SIZE 1024 * 10
#define MAX_CONNECTION_SIZE 0x10
#define MAX_METHOD_SIZE 10

#define METHOD_GET "GET"
#define METHOD_POST "POST"

#define FIELD_HOST "Host: "
#define FIELD_COOKIE "Cookie: "
#define FIELD_CONNECTION "Connection: "

#ifdef __cplusplus
extern "C" {
#endif

#ifdef PAPI

typedef struct p_server_t {
    PSocket server;
    PList *black_list;
    PList *redirect_list;
    int maxq;
} ProxyServer;

typedef struct p_http_header_t {
    char method[MAX_METHOD_SIZE];
    char url[MAX_URL_SIZE];
    char host[MAX_HOST_SIZE];
    char cookie[MAX_COOKIE_SIZE];
    char connection[MAX_CONNECTION_SIZE];
} HttpHeader;

typedef struct p_connection_t {
    ProxyServer *ps;
    PSocket *psock;
    pthread_mutex_t *mutex;
} PConnection;

typedef struct p_redirect_t {
    char *url;
    char *red_url;
} PRedirect;

// parse http packet to HttpHeader
PAPI int parse_http_header(char *buf, HttpHeader *header);

PAPI int ps_init(ProxyServer *ps, int maxq);
PAPI int ps_start(ProxyServer *ps, int port);
PAPI void ps_serve(ProxyServer *ps, PSocket *client);
PAPI int ps_connect(ProxyServer *ps, const char *host, PSocket *sock);

// handle black list
PAPI PList *ps_black_list_load(ProxyServer *ps, const char *fname);
PAPI PListIter *ps_black_list_contain(ProxyServer *ps, const char *host);

// handle redirection
PAPI PList *ps_redirect_list_load(ProxyServer *ps, const char *fname);
PAPI PListIter *ps_redirect_contain(ProxyServer *ps, const char *url);

// cache
PAPI int p_getcache(const char *hash, char *buf, size_t bufsz, int id);
PAPI int p_storecache(const char *hash, const char *buf, size_t bufsz, int id);

#endif // PAPI

#ifdef __cplusplus
}
#endif

#endif
