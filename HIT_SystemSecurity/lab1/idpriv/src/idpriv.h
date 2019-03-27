#ifndef _IDPRIV_H_
#define _IDPRIV_H_

#include <unistd.h>

#ifdef __cplusplus
extern "C" {
#endif

// drop privilege permanently
int idpriv_drop_perm(uid_t nuid, gid_t ngid);

// drop privilege temporarily
int idpriv_drop_temp(uid_t nuid, gid_t ngid);

// restore temporarily dropped privilege
int idpriv_restore_temp(void);

#ifdef __cplusplus
}
#endif

#endif
