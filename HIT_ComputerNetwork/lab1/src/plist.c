#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#define PAPI
#include "plist.h"
#include "utils.h"

#ifdef __cplusplus
extern "C" {
#endif

// allocate list
PAPI PList *p_list_new() {
    return (PList *) calloc(1, sizeof(PList));
}

// allocate list with free function
PAPI PList *p_list_newf(PListFree pfree) {
    PList *l = p_list_new();
    if (l) l->pfree = pfree;
    return l;
}

// append data element to list
PAPI PListIter *p_list_append(PList *list, void *data) {
	
	p_return_val_if_fail(list && data, NULL);
    
	PListIter *item = NULL;

	item = P_NEW(PListIter);
	if (!item) {
		return item;
	}
	if (list->tail) {
		list->tail->n = item;
	}
	item->data = data;
	item->p = list->tail;
	item->n = NULL;
	list->tail = item;
	if (!list->head) {
		list->head = item;
	}
	list->length++;
	return item;
}

PAPI PListIter *p_list_prepend(PList *list, void *data) {
	p_return_val_if_fail(list && data, NULL);

	PListIter *item;

	item = P_NEW(PListIter);
	if (!item)
		return NULL;
	if (list->head)
		list->head->p = item;
	item->data = data;
	item->n = list->head;
	item->p = NULL;
	list->head = item;
	if (!list->tail)
		list->tail = item;
	list->length++;
	return item;
}

PAPI void p_list_split_iter(PList *list, PListIter *iter) {
	p_return_void_if_fail(list && iter);

	if (list->head == iter)
		list->head = iter->n;
	if (list->tail == iter)
		list->tail = iter->p;
	if (iter->p)
		iter->p->n = iter->n;
	if (iter->n)
		iter->n->p = iter->p;
	--list->length;
}

PAPI void p_list_delete(PList *list, PListIter *iter) {

	p_return_void_if_fail(list && iter);

	p_list_split_iter(list, iter);
	if (list->pfree && iter->data)
		list->pfree(iter->data);
	iter->data = NULL;
	P_FREE(iter);
}

PAPI void p_list_purge(PList *list) {

	p_return_void_if_fail(list);

	PListIter *it, *next;

	it = list->head;
	while (it) {
		next = it->n;
		p_list_delete(list, it);
		it = next;
	}
	list->head = list->tail = NULL;
}

PAPI void p_list_free(PList *list) {
    if (list) {
		p_list_free(list);
		P_FREE(list);
	}
}

PAPI PListIter *p_list_find(PList *list, const void *p, PListCompare cmp) {
	p_return_val_if_fail(list && cmp, NULL);

	PListIter *it;
	void *q;

	p_list_foreach (list, it, q) {
		if (!cmp(p, q))
			return it;
	}
	return NULL;
}


PAPI PListIter *p_list_contain(PList *list, const void *p, PListCompare cmp) {
	p_return_val_if_fail(list && cmp, NULL);

	PListIter *it;
	void *q;

	p_list_foreach (list, it, q) {
		if (p == q)
			return it;
	}
	return NULL;
}

#ifdef __cplusplus
}
#endif
