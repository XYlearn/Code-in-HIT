#include <unistd.h>

#include "idpriv.h"

#ifdef __cplusplus
extern "C" {
#endif

// drop privilege permanently
int idpriv_drop_perm(uid_t nuid, gid_t ngid) {
    uid_t ruid, euid, suid;
    gid_t rgid, egid, sgid;

    if (setresgid(ngid, ngid, ngid) < 0)
        return -1;
    if (getresgid(&rgid, &egid, &sgid) < 0)
        return -1;
    if (rgid != ngid || egid != ngid || sgid != ngid)
        return -1;
        
    if (setresuid(nuid, nuid, nuid) < 0)
        return -1;
    if (getresuid(&ruid, &euid, &suid) < 0)
        return -1;
    if (ruid != nuid || euid != nuid || suid != nuid)
        return -1;
    return 0;
}

// drop privilege temporarily
int idpriv_drop_temp(uid_t nuid, gid_t ngid) {
    if (setresgid(-1, ngid, getegid()) < 0)
        return -1;
    if (getegid() != ngid)
        return -1;

    if (setresuid(-1, nuid, geteuid()) < 0)
        return -1;
    if (geteuid() != nuid)
        return -1;
    
    return 0;
}

// restore temporarily dropped privilege
int idpriv_restore_temp() {
    uid_t ruid, euid, suid;
    uid_t rgid, egid, sgid;

    if (getresuid(&ruid, &euid, &suid) < 0)
        return -1;
    if (setresuid(-1, suid, -1) < 0)
        return -1;
    if (geteuid() != suid)
        return -1;
    
    if (getresgid(&rgid, &egid, &sgid) < 0)
        return -1;
    if (setresgid(-1, sgid, -1) < 0)
        return -1;
    if (getegid() != sgid)
        return -1;

    return 0;
}

#ifdef __cplusplus
}
#endif
