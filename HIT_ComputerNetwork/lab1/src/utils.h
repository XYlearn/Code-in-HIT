#ifndef _UTILS_H_
#define _UTILS_H_

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>

#ifdef P_NEW
#undef P_NEW
#endif

#define DEBUG

#define P_NEW(t) (t*)calloc(1, sizeof(t))
#define P_FREE(m) free(m)

#define MIN(x, y) ((x) < (y) ? (x): (y))
#define MAX(x, y) ((x) > (y) ? (x): (y))

#define LDEBUG 0
#define LRELEASE 1

#define p_return_val_if_fail(cond, val) \
    if (!(cond)) \
        return (val)
#define p_return_void_if_fail(cond) \
    if (!(cond)) \
        return

#ifdef __cplusplus
extern "C" {
#endif

#ifdef PAPI

PAPI int p_log(int level, const char *format, ...);

PAPI void p_expect(int cond, const char *format,  ...);

PAPI char *p_replace_first(const char *s, const char *old_s, const char *new_s, int *origin_size);

PAPI int p_write_file(const char *fname, const char *buf, size_t bufsz);
PAPI int p_read_file(const char *fname, char *buf, size_t bufsz);

#endif  // PAPI

#ifdef __cplusplus
}
#endif

#endif
