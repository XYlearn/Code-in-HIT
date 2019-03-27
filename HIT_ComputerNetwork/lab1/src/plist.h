#ifndef _PLIST_H_
#define _PLIST_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifdef PAPI
typedef void (*PListFree)(void *);
typedef int (*PListCompare)(const void *a, const void *b);

typedef struct p_list_iter_t {
    void *data;
    struct p_list_iter_t *p, *n;
} PListIter;

typedef struct p_list_t {
    PListIter *head;
    PListIter *tail;
    int length;
    PListFree pfree;
} PList;

#define p_list_foreach(list, it, pos)\
	if (list)\
		for (it = list->head; it && (pos = it->data, 1); it = it->n)

// safe when calling p_list_delete
#define p_list_foreach_safe(list, it, tmp, pos)\
    if (list)\
        for (it = list->head; it && (pos = it->data, tmp = it->n, 1); it = it->n)

PAPI PList *p_list_new();
PAPI PList *p_list_newf(PListFree pfree);
PAPI PListIter *p_list_append(PList *list, void *data);
PAPI PListIter *p_list_prepend(PList *list, void *data);
PAPI void p_list_split_iter(PList *list, PListIter *iter);
PAPI void p_list_delete(PList *list, PListIter *iter);
PAPI void p_list_purge(PList *list);
PAPI void p_list_free(PList *list);

// hash like api
PAPI PListIter *p_list_find(PList *list, const void *data, PListCompare cmp);
PAPI PListIter *p_list_contain(PList *list, const void *data, PListCompare cmp);

#endif // PAPI

#ifdef __cplusplus
}
#endif

#endif
