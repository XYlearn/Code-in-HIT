#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <netdb.h>
#include <assert.h>
#include <limits.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>

#define PAPI
#include "proxy.h"
#include "utils.h"
#include "md5.h"

#ifdef __cplusplus
extern "C" {
#endif

static inline int cmp_in_addr(const void *a, const void *b) {
    return *(in_addr_t *)a - *(in_addr_t *)b;
}

static inline void free_redirection(void * p) {
    free(((PRedirect *)p)->url);
    free(((PRedirect *)p)->red_url);
    free(p);
}

static inline int cmp_redirect(const void *a, const void *b) {
    return strcmp(((PRedirect *)a)->url, ((PRedirect *)b)->url);
}

/*
 * parse Http Header from buf
 * return 0 if success, return -1 if message has invalid format
 */
PAPI int parse_http_header(char *buf, HttpHeader *header) {
#define strtok_chk(str, sep) \
    delim[0] = sep; \
    sptr = strtok((str), (delim)); \
    if (!sptr) {return -1;}

    char *method;
    ssize_t nbytes;
	char *sptr, *tmp_ptr;
    int host_found;
    int delim_pos;
    char delim[] = " ";

    memset(header, 0, sizeof(HttpHeader));

    // parse first line
    strtok_chk(buf, ' '); // method
    if (!strncmp(METHOD_GET, sptr, sizeof(METHOD_GET)))
        method = METHOD_GET;
    else if(!strncmp(METHOD_POST, sptr, sizeof(METHOD_POST)))
        method = METHOD_POST;
    else
        return -1;
    strncpy(header->method, method, MAX_METHOD_SIZE);
    strtok_chk(NULL, ' '); // URL
    strncpy(header->url, sptr, MAX_URL_SIZE);

    strtok_chk(sptr + strlen(sptr) + 1, '\r');  // protocol version

    // parse header fields
    host_found = 0;
    while (1) {
        *delim = '\r';
        sptr = strtok(NULL, delim);
        if (!sptr) break; // reach end of header
        sptr++;

        if (NULL == strchr(sptr, ':')) // invalid format
            break;

        // handle fields.
        if (!strncmp(FIELD_HOST, sptr, sizeof(FIELD_HOST) - 1)) {
            host_found = 1;
            strncpy(header->host, sptr + sizeof(FIELD_HOST) - 1, MAX_HOST_SIZE);
        } else if (!strncmp(FIELD_COOKIE, sptr, sizeof(FIELD_COOKIE) - 1)) {
            strncpy(header->cookie, sptr + sizeof(FIELD_COOKIE) - 1, MAX_COOKIE_SIZE);
        } else if (!strncmp(FIELD_CONNECTION, sptr, sizeof(FIELD_CONNECTION) - 1)) {
            strncpy(header->connection, sptr + sizeof(FIELD_CONNECTION) - 1, MAX_CONNECTION_SIZE);
        }
    }
    if (!host_found) {
        p_log(LDEBUG, "No host found\n");
        return -1;
    }
    return 0;
}

PAPI int ps_init(ProxyServer *ps, int maxq) {
    assert(ps);
    assert(maxq > 0);

    int sock;
    int retval;
    struct sockaddr_in addr;

    sock = socket(AF_INET, SOCK_STREAM, 0);
    ps->server.sockfd = sock;
    ps->maxq = maxq;
    return 0;
}

static void *thread_func(void *arg) {
    PConnection conn = *(PConnection *)arg;
    arg = NULL;
    pthread_mutex_unlock(conn.mutex);
    ps_serve(conn.ps, conn.psock);
    p_sock_free(conn.psock);
    return NULL;
}

PAPI int ps_start(ProxyServer *ps, int port) {
    assert(ps);
    assert(port > 1024);

    int retval;
    char *host;
    PSocket *client;
    PConnection conn;
    pthread_t thread;
    pthread_mutex_t mutex;

    ps->server.port = port;
    retval = p_sock_bind(&ps->server, port);
    p_expect(!retval, "[-] Fail to bind port %d", port);
    
    retval = p_sock_listen(&ps->server, ps->maxq);
    p_expect(!retval, "[-] Fail to listen");

    pthread_mutex_init(&mutex, NULL);
    while (1) {
        client = p_sock_accept(&ps->server);
        host = inet_ntoa(ps->server.sa.sin_addr);
        // check black list
        if (ps->black_list && ps_black_list_contain(ps, host)) {
            p_log(LRELEASE, "[+] Reject from %s\n", host);
            continue;
        }
        p_log(LRELEASE, "[+] Accept from %s\n", host);
        // TODO: use thread pool to manage
        pthread_mutex_lock(&mutex); // lock for overwritten, will be unlock in sub-thread
        conn.ps = ps;
        conn.psock = client;
        conn.mutex = &mutex;
        retval = pthread_create(&thread, NULL, thread_func, &conn);
        if (retval)
            perror("pthread_create");
    }
    pthread_mutex_destroy(&mutex);

    return 0;
}

// real proxy serve for each connection
PAPI void ps_serve(ProxyServer *ps, PSocket *client) {
    assert(ps);
    assert(client);

    int nbytes, retval, keep_alive, cnt;
    char buf[MAX_BUF_SIZE];
    char hashval[33];
    char *temp, *temp_a;
    HttpHeader header;
    PSocket psock;
    struct timeval timeout;
    PListIter *item;
    PRedirect *redirect;

    // set timeout
    timeout.tv_sec = 3;
    timeout.tv_usec = 0;

    while(1) {
        nbytes = p_sock_read_timeout(client, buf, MAX_BUF_SIZE, &timeout);
        if (nbytes <= 0)
            goto serve_end;

        p_log(LDEBUG, "Receive %d bytes from %s\n", nbytes, inet_ntoa(client->sa.sin_addr));

        // replace proxy mark
        temp = p_replace_first(buf, "Proxy-Connection: ", "Connection: ", &nbytes);

        // parse http header
        temp_a = (char *)malloc(nbytes + 1);
        memcpy(temp_a, temp, nbytes);
        temp_a[nbytes] = '\0';
        retval = parse_http_header(temp_a, &header);
        if (retval < 0) {
            p_log(LDEBUG, "Header parse error\n");
            goto serve_end;
        }
        free(temp_a); temp_a = NULL;
        
        p_log(LDEBUG, "Parse header:\n\tmethod: %s\n\turl: %s\n\thost: %s\n\tcookie: %s\n\tconnection: %s\n", 
            header.method, header.url, header.host, header.cookie, header.connection);

        // set keep-alive
        if (strstr(header.connection, "keep-alive"))
            keep_alive = 1;
        else
            keep_alive = 0;

        // redirect
        if ((item = ps_redirect_contain(ps, header.host))) {
            redirect = (PRedirect *)item->data;
            p_log(LRELEASE, "Redirect from %s to %s\n", header.host, redirect->red_url);
            nbytes = snprintf(buf, sizeof(buf), "HTTP/1.1 302 Temporary Redirect\r\nLocation: http://%s\r\n", redirect->red_url);
            if (0 <= p_sock_write(client, buf, nbytes))
                goto serve_end;
            free(temp); temp = NULL;
            if (keep_alive)
                continue;
            else
                break;
        }

        // access control
        // check if the dest host in black list
        if (ps_black_list_contain(ps, header.host)) {
            p_log(LRELEASE, "Reject to %s\n", header.host);
            goto serve_end;
        }

        // query cache
        md5((uint8_t *)&header, sizeof(header), (uint8_t *)hashval);
        if (!strcmp(header.method, METHOD_GET) || !strcmp(header.method, METHOD_POST)) {
            cnt = 0;
            retval = p_getcache(hashval, buf, sizeof(buf), cnt++);
            if (retval > 0) {
                p_log(LDEBUG, "Cache hit %s\n", hashval);
                do {
                    p_sock_write(client, buf, retval);
                    retval = p_getcache(hashval, buf, sizeof(buf), cnt++);
                } while (retval > 0);
                continue;
            }
        }

        // establish connection
        retval = ps_connect(ps, header.host, &psock);
        if (retval < 0) {
            p_log(LDEBUG, "Fail to connect to %s\n", header.host);
            goto serve_end;
        }

        // send to server
        retval = p_sock_write(&psock, temp, nbytes);
        free(temp); temp = NULL;
        if (retval < 0) {
            goto serve_end;
        }
        // p_log(LDEBUG, "Send '%s'\n\tto %s\n", temp, header.host);
        p_log(LDEBUG, "Send %d bytes to %s\n", retval, header.host);
        cnt = 0;
        while(1) {
            // get response
            retval = p_sock_read_timeout(&psock, buf, MAX_BUF_SIZE, &timeout);
            if (retval <= 0) break;
            p_sock_write(client, buf, retval);
            p_log(LDEBUG, "Feed %d bytes to %s\n", retval, inet_ntoa(client->sa.sin_addr));

            // store cache
            if (p_storecache(hashval, buf, retval, cnt++) > 0)
                p_log(LDEBUG, "Cache store %s/%d\n", hashval, cnt - 1);
        }
        // check keep alive
        if (keep_alive)
            goto serve_end;
    }
serve_end:
    if (temp) free(temp);
    if (temp_a) free(temp_a);
    p_log(LRELEASE, "[+] Disconnected from %s\n", inet_ntoa(client->sa.sin_addr));
    p_sock_close(&psock);
    p_sock_close(client);
    pthread_exit(NULL);
}

PAPI int ps_connect(ProxyServer *ps, const char *host, PSocket *psock) {
    assert(psock);
    return p_sock_connect(psock, host, HTTPHOST);
}

// load black list from fname
PList *ps_black_list_load(ProxyServer *ps, const char *fname) {
    assert(ps);

    FILE *fp;
    PList *black_list;
    char buf[MAX_BUF_SIZE];
    int retval;
    char *sptr, delim[] = "\n";
    void *data;

    black_list = NULL;
    fp = fopen(fname, "r");
    if(!fp) 
        goto black_list_load_end;
    // TODO read time by time
    retval = fread(buf, 1, MAX_BUF_SIZE - 1, fp);
    fclose(fp);
    if (retval <= 0)
        goto black_list_load_end;
    buf[retval] = '\0';
    
    black_list = p_list_newf(free);

    // parse lines
    for (sptr = strtok(buf, delim); sptr; sptr = strtok(NULL, delim)) {
        data = P_NEW(in_addr_t);
        if (!data || -1 == p_sock_in_addr(sptr, data))
            continue;
        p_list_append(black_list, data);
    }
black_list_load_end:
    ps->black_list = black_list;
    return black_list;
}

// check if host is banned
PAPI PListIter *ps_black_list_contain(ProxyServer *ps, const char *host) {
    assert(ps);
    p_return_val_if_fail(host, 0);

    in_addr_t addr;
    int retval;

    retval = p_sock_in_addr(host, &addr);
    if (-1 == retval)
        return 0;
    return p_list_find(ps->black_list, &addr, cmp_in_addr);
}

PAPI PList *ps_redirect_list_load(ProxyServer *ps, const char *fname) {
    assert(ps);

    FILE *fp;
    PList *redirect_list;
    char buf[MAX_BUF_SIZE];
    char delim[] = "\n";
    int retval;
    char *sptr, *pt;
    PRedirect *redirect;
    void *data;

    redirect_list = NULL;
    fp = fopen(fname, "r");
    if(!fp) 
        goto redirect_list_load_end;
    // TODO read time by time
    retval = fread(buf, 1, MAX_BUF_SIZE - 1, fp);
    fclose(fp);
    if (retval <= 0)
        goto redirect_list_load_end;
    buf[retval] = '\0';
    
    redirect_list = p_list_newf(free_redirection);

    // parse lines
    for (sptr = strtok(buf, delim); sptr; sptr = strtok(NULL, delim)) {
        pt = strchr(buf, ' ');
        if (!pt)
            continue;
        *pt = '\0';
        redirect = P_NEW(PRedirect);
        if (!redirect)
            continue;
        redirect->url = strdup(sptr);
        redirect->red_url = strdup(pt + 1);
        if (!redirect->url || !redirect->red_url) {
            free_redirection(redirect);
            continue;
        }
        p_list_append(redirect_list, redirect);
    }
redirect_list_load_end:
    ps->redirect_list = redirect_list;
    return redirect_list;
}

PAPI PListIter *ps_redirect_contain(ProxyServer *ps, const char *url) {
    assert(ps);
    p_return_val_if_fail(url, 0);

    PRedirect redirect;
    redirect.url = (char *)url;

    return p_list_find(ps->redirect_list, &redirect, cmp_redirect);
}

PAPI int p_getcache(const char *hash, char *buf, size_t bufsz, int id) {
    char pathname[PATH_MAX];
    size_t url_len;

    snprintf(pathname, sizeof(pathname), ".cache/%s/%d", hash, id);
    if (!access(pathname, R_OK)) {
        return p_read_file(pathname, buf, bufsz);
    } else {
        return -1;
    }
}

PAPI int p_storecache(const char *hash, const char *buf, size_t bufsz, int id) {
    char pathname[PATH_MAX];

    snprintf(pathname, sizeof(pathname), ".cache/%s", hash);
    if (access(pathname, R_OK))
        if (-1 == mkdir(pathname, 0770))
            return -1;
    snprintf(pathname, sizeof(pathname), ".cache/%s/%d", hash, id);
    return p_write_file(pathname, buf, bufsz);
}

#ifdef __cplusplus
}
#endif
