#include <stdio.h>

#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <limits.h>

static int jail_create_dir(char *dir);

int create_jail(char *jail_path) {
    char dirbuf[PATH_MAX];
    // create dirs 
    jail_create_dir(jail_path);
    
    // ln

    return 0;
}

int main(int argc, char *argv[]) {

    return 0;
}

static int jail_create_dir(char *dir) {
    if (access(dir, F_OK) < 0) {
        if (mkdir(dir, S_IRWXU) < 0) {
            perror(dir);
            return -1;
        } else {
            printf("[+] Create folder '%s'\n", dir);
        }
    }
    return 0;
}
