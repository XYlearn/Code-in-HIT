#ifndef _MERGE_SORT_H_
#define _MERGE_SORT_H_

#include <algorithm>
#include <vector>

#include <cstdint>
#include <cstdio>

#include "common.h"

#define kRecordSize sizeof(Record)
#define kRecordPerBlock (kBlockSize / kRecordSize)

class MergeSort;
struct MergeSortGroup;
struct Record;
struct MemPartition;

class MergeSortException : public std::exception {};

// Record must divide kBlockSize
struct Record {
  bkey_t key;
  val_t val;
} __attribute__((packed));

class MergeSort {
 public:
  MergeSort(const char *srcf, const char *dstf, int mblock_count);
  virtual ~MergeSort();
  bool set_group_count(int n) {
    if (n >= 2) {
      group_count_ = n;
      return true;
    }
    return false;
  }
  int get_group_count() { return group_count_; }
  void sort();

 protected:
  void internal_sort_(index_t start_idx, index_t end_idx);
  void merge_sort_n_(index_t start_idx, index_t end_idx);
  void merge_sort_(index_t start_idx, index_t end_idx);

 protected:
  int block_count_;    // disk block count
  long mblock_count_;  // memory block count
  int group_count_;
  int mgroup_block_count_;
  void *memory_;
  void *output_buffer_;
  int output_block_count_;
  FILE *srcf_, *dstf_;
};

struct MergeSortGroup {
  void *mem;
  int mem_record_count;
  int record_count;
  FILE *fp;
  index_t start_idx;
  int current_record_idx;

  MergeSortGroup(void *mem, int mem_record_count, int record_count,
                 index_t start_idx, FILE *fp)
      : mem(mem),
        mem_record_count(mem_record_count),
        record_count(record_count),
        fp(fp),
        start_idx(start_idx),
        current_record_idx(0) {
    prepare();
  }

  Record *record() {
    return &((Record *)mem)[current_record_idx % mem_record_count];
  }

  bool next() {
    ++current_record_idx;
    if (current_record_idx >= record_count) {
      return false;
    }
    prepare();
    return true;
  }

  bool has_next() { return current_record_idx < record_count; }

  void prepare() {
    if (current_record_idx % mem_record_count == 0) {
      index_t disk_idx =
          start_idx + (current_record_idx * kRecordSize / kBlockSize);
      fseek(fp, disk_idx, SEEK_SET);
      int read_record_count = fread(mem, kRecordSize, mem_record_count, fp);
      if (mem_record_count != read_record_count) {
        record_count -= mem_record_count - read_record_count;
      }
    }
  }
};

#endif  // !_MERGE_SORT_H_