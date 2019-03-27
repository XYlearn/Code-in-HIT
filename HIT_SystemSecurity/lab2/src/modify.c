#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <pwd.h>

char *get_user_name(pid_t uid) {
    struct passwd *pw = getpwuid(uid);
    if (!pw) {
        puts("No such uid");
        exit(0);
    }
    return pw->pw_name;
}

char *seek_to_user(char *buf, uid_t uid) {
    char *username = get_user_name(uid);
    size_t username_size = strlen(username);
    char *tmp_buf = malloc(username_size + 0x10);
    tmp_buf[0] = '\0';
    strncat(tmp_buf, username, username_size);
    strncat(tmp_buf, "\n\t", 2);
    char * item = strstr(buf, tmp_buf);
    if (!item) {
        puts("Not found");
        exit(0);
    }
    free(tmp_buf);
    return item;
}

char *read_aaa(size_t *size) {
    size_t buf_size;
    char *buf;
    FILE *fp;
    
    fp = fopen("./aaa", "r");
    if (NULL == fp) {
        perror("aaa");
        exit(0);
    }
    fseek(fp, 0, SEEK_END);
    buf_size = ftell(fp);
    rewind(fp);

    buf = malloc(buf_size);
    if (!buf) {
        perror("malloc");
        exit(0);
    }
    *size = fread(buf, 1, buf_size, fp);
    return buf;
}

size_t write_aaa(char *buf, size_t size) {
    FILE *fp;

    fp = fopen("./aaa", "w+");
    if (NULL == fp) {
        perror("aaa");
        exit(0);
    }
    return fwrite(buf, 1, size, fp);
}

int has_permission(uid_t uid) {
    uid_t ruid = getuid();
    return !ruid || ruid == uid;
}

char *get_user_cont_start(uid_t uid, char *user_item) {
    char *username = get_user_name(getuid());
    return user_item + strlen(username) + 2;
}

char *get_user_cont_end(uid_t uid, char *user_item) {
    return strchr(get_user_cont_start(uid, user_item), '\n') + 1;
}

int read_content(char *buf, size_t nbytes) {
    size_t size = 0;
    char c;
    for (size = 0; size < nbytes - 1; size++) {
        read(0, &c, 1);
        if (c == '\n') {
            break;
        }
        else
            buf[size] = c;
    }
    buf[size] = 0;
    return strlen(buf);
}

// content mustn't contains '\n'
char * modify(char *buf, uid_t uid, char *content, size_t nbytes, size_t *buf_size) {
    if(!has_permission(uid)) {
        puts("Permission Denied");
        exit(0);
    }
    char *item = seek_to_user(buf, uid);
    char *cont_start = get_user_cont_start(uid, item);
    char *cont_end = get_user_cont_end(uid, item);
    size_t prev_size = (size_t)(cont_start - buf);
    size_t orig_cont_size = (size_t)(cont_end - cont_start);
    size_t post_size = *buf_size - prev_size - orig_cont_size;
    
    char *new_buf = malloc(*buf_size + nbytes - orig_cont_size + 1);
    memcpy(new_buf, buf, prev_size);
    memcpy(new_buf + prev_size, content, nbytes);
    memcpy(new_buf + prev_size + nbytes, "\n", 1);
    memcpy(new_buf + prev_size + nbytes + 1, cont_end, post_size);
    free(buf);
    *buf_size = *buf_size + nbytes - orig_cont_size + 1;
    return new_buf;
}


int show(char *buf, uid_t uid) {
    if(!has_permission(uid)) {
        puts("Permission Denied");
        exit(0);
    }
    char *item = seek_to_user(buf, uid);
    char *cont_start = get_user_cont_start(uid, item);
    char *cont_end = get_user_cont_end(uid, item);
    size_t cont_size = (size_t)(cont_end - cont_start);
    return write(1, cont_start, cont_size);
}

int main(int argc, char *argv[]) {
    size_t size;
    char *content = read_aaa(&size);
    char user_content[0x200];
    
    if (argc != 3) {
        printf("Usage: %s [read|write] [uid]\n", argv[0]);
        exit(0);
    }

    uid_t uid = atoi(argv[2]);
    if (!strcmp("write", argv[1])) {
        size_t user_content_size = read_content(user_content, sizeof(user_content) - 1);
        if (user_content_size == -1) {
            puts("Read Error");
            exit(0);
        }
        content = modify(content, uid, user_content, user_content_size, &size);
        write_aaa(content, size);
    } else if (!strcmp("read", argv[1])) {
        show(content, uid);
    } else {
        printf("Usage: %s [read|write] [uid]\n", argv[0]);
    }
}
