#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "idpriv.h"

void getresugid(uid_t *ruid, uid_t * euid, uid_t *suid, gid_t *rgid, gid_t *egid, gid_t *sgid) {
    if (getresuid(ruid, euid, suid) < 0) {
        perror("getresuid");
        exit(0);
    }
    if (getresgid(rgid, egid, sgid) < 0) {
        perror("getresgid");
        exit(0);
    }
}

void showresugid() {
    uid_t ruid, euid, suid;
    uid_t rgid, egid, sgid;
    getresugid(&ruid, &euid, &suid, &rgid, &egid, &sgid);
    printf("\truid: %d; euid: %d; suid: %d\n\trgid: %d; egid: %d; sgid: %d\n", 
        ruid, euid, suid, rgid, egid, sgid);
}

int main(int argc, char *argv[]) {
    uid_t ruid, euid, suid;
    gid_t rgid, egid, sgid;
    uid_t nuid = 1000;
    gid_t ngid = 1000;
    char *filename = "root_data";
    char *root_cont = "root content";
    char *non_root_cont = "non-root content";
    FILE *fp;

    getresugid(&ruid, &euid, &suid, &rgid, &egid, &sgid);
    if (euid != 0) {
        puts("Please run as root");
        exit(0);
    }

    showresugid();
    printf("[.] Create file \"%s\" with root privilege\n", filename);
    fp = fopen(filename, "w+");
    if (!fp) {
        perror(filename);
        exit(0);
    } else {
        puts("[+] Create success.");
    }

    printf("[.] Write '%s' to %s\n", root_cont, filename);
    if (fprintf(fp, "%s", root_cont) < 0) {
        perror(filename);
        exit(0);
    } else {
        puts("[+] Write success.");
    }
    fclose(fp);
    puts("[+] Close file.");

    // drop priv temp
    printf("[.] Drop priviledge temporarily to %d\n", nuid);
    if (idpriv_drop_temp(nuid, ngid) < 0) {
        perror("idpriv_drop_temp");
        exit(0);
    }
    printf("[+] Drop temp success\n");

    showresugid();
    printf("[.] Open '%s' again (shall not succeed)\n", filename);
    fp = fopen(filename, "w");
    // shall not succeed
    if (NULL == fp) {
        perror(filename);
    } else {
        puts("[+] Open success");
        fclose(fp);
        puts("[+] Close file.");
    }

    // restore priv
    puts("[.] Restore priviledge");
    if (idpriv_restore_temp() < 0) {
        perror("idpriv_restore_temp");
        exit(0);
    }
    printf("[.] Restore success\n");

    showresugid();
    printf("[.] Open '%s' again (shall succeed)\n", filename);
    fp = fopen(filename, "w");
    if (NULL == fp) {
        perror(filename);
        exit(0);
    } else {
        puts("[+] Open success");
    }
    printf("[.] Write '%s' to %s\n", root_cont, filename);
    if (fprintf(fp, "%s", root_cont) < 0) {
        perror(filename);
        exit(0);
    } else {
        puts("[+] Write success.");
        fclose(fp);
        puts("[+] Close file.");
    }

    // drop perm
    printf("[.] Drop priviledge permanently to %d\n", nuid);
    if (idpriv_drop_perm(nuid, ngid) < 0) {
        perror("idpriv_drop_perm");
        exit(0);
    }
    printf("[+] Drop perm success\n");

    showresugid();
    printf("[.] Open '%s' again (shall not succeed)\n", filename);
    fp = fopen(filename, "w");
    // shall not succeed
    if (NULL == fp) {
        perror(filename);
    } else {
        puts("[+] Open success");
        fclose(fp);
        puts("[+] Close file.");
    }

    // restore priv
    puts("[.] Restore priviledge");
    if (idpriv_restore_temp() < 0) {
        perror("idpriv_restore_temp");
        exit(0);
    }
    printf("[.] Restore success\n");
    if (getresuid(&ruid, &euid, &suid) < 0) {
        perror("getresuid");
        exit(0);
    }
    showresugid();
    printf("[.] Open '%s' again (shall not succeed)\n", filename);
    fp = fopen(filename, "w");
    if (NULL == fp) {
        perror(filename);
    } else {
        puts("[+] Open success");
        fclose(fp);
        puts("[+] Close file.");
    }

    return 0;
}
