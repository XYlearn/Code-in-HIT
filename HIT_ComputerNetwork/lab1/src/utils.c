#define PAPI
#include "utils.h"

#ifdef __cplusplus
extern "C" {
#endif

PAPI int p_log(int level, const char *format, ...) {
    va_list ap;
    int retval;
    char buf[1024];

    va_start(ap, format);
    retval = 0;
    if (level == LDEBUG) {
#ifdef DEBUG
        snprintf(buf, sizeof(buf), "[DEBUG] %s", format);
        retval = vprintf(buf, ap);
#endif
        return retval;
    } else {
        retval = vprintf(format, ap);
    }
    va_end(ap);

    return retval;
}

PAPI void p_expect(int cond, const char *format,  ...) {
    va_list ap;
    va_start(ap, format);
    if (!(cond)) { 
        vprintf(format, ap);
        exit(0);
    }
    va_end(ap);
}

PAPI char *p_replace_first(const char *s, const char *old_s, const char *new_s, int *origin_size) {
    char *sptr;
    int old_s_size, new_s_size;
    int nbytes, idx;
    char *dst;

    old_s_size = strlen(old_s);
    new_s_size = strlen(new_s);
    nbytes = *origin_size + new_s_size - old_s_size;
    
    sptr = strstr(s, old_s);
    if (!sptr) {
        dst = (char *)malloc(*origin_size);
        if (NULL == dst) return NULL;
        memcpy(dst, s, *origin_size);
        return dst;
    }

    dst = (char *)malloc(nbytes);
    if (NULL == dst) return NULL;

    idx = (int)(sptr - s);
    memcpy(dst, s, idx);
    memcpy(dst + idx, new_s, new_s_size);
    memcpy(dst + idx + new_s_size, s + idx + old_s_size, *origin_size - old_s_size - idx);
    *origin_size = nbytes;

    return dst;
}

PAPI int p_write_file(const char *fname, const char *buf, size_t bufsz) {
    FILE *fp;
    int retval;
    
    fp = fopen(fname, "w+");
    if (!fp)
        return -1;
    else {
        retval = fwrite(buf, 1, bufsz, fp);
        fclose(fp);
        return retval;
    }
}

PAPI int p_read_file(const char *fname, char *buf, size_t bufsz) {
    FILE *fp;
    int retval;
    
    fp = fopen(fname, "r");
    if (!fp)
        return -1;
    else {
        retval = fread(buf, 1, bufsz, fp);
        fclose(fp);
        return retval;
    }
}

#ifdef __cplusplus
}
#endif
