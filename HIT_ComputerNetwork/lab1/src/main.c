#include <stdlib.h>
#include <unistd.h>
#include <sys/fcntl.h>
#include <sys/stat.h>

#define PAPI
#include "proxy.h"
#include "utils.h"

int main(int argc, char *argv[]) {
    ProxyServer ps;
    PList *black_list;
    PList *redirect_list;
    int port;
    char cache[] = ".cache";

    if (argc == 1) {
        port = 1337;
    } else {
        port = atoi(argv[1]);
    }

    if (-1 == access(cache, R_OK)) {
        if (-1 == mkdir(cache, 0770))
            p_log(LRELEASE, "[-] Create cache fail\n");
        else
            p_log(LRELEASE, "[+] Create cache success\n");
    }

    black_list = ps_black_list_load(&ps, "black_list");
    if (!black_list)
        p_log(LRELEASE, "[+] Loaded 0 blackers from black list\n");
    else
        p_log(LRELEASE, "[+] Loaded %d blackers from black list\n", black_list->length);
    redirect_list = ps_redirect_list_load(&ps, "redirect");
    if (!redirect_list)
        p_log(LRELEASE, "[+] Loaded 0 redirects from redirect\n");
    else
        p_log(LRELEASE, "[+] Loaded %d redirects from redirect_list\n", redirect_list->length);

    ps_init(&ps, 16);
    ps_start(&ps, port);

    return 0;
}
