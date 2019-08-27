#include <climits>
#include <cstdint>
#include <cstdio>

#include <algorithm>
#include <vector>

#include "mergesort.h"

/*--------------------------------------------------------------*
 * MergeSort
 *---------------------------------------------------------------*/

MergeSort::MergeSort(const char *srcf_name, const char *dstf_name,
                     int mblock_count)
    : mblock_count_(mblock_count) {
  if (mblock_count_ < 3) {
    throw MergeSortException();
  }
  srcf_ = fopen(srcf_name, "wb+");
  dstf_ = fopen(dstf_name, "wb+");
  if (!srcf_ || !dstf_) {
    fclose(srcf_);
    fclose(dstf_);
    throw MergeSortException();
  }
  fseek(srcf_, 0, SEEK_END);
  int file_size = ftell(srcf_);
  block_count_ = file_size ? (file_size - 1) / kBlockSize + 1 : 0;
  memory_ = malloc(mblock_count_ * kBlockSize);
  group_count_ = 2;
}

MergeSort::~MergeSort() {
  free(memory_);
  fclose(srcf_);
  fclose(dstf_);
}

void MergeSort::sort() {
  mgroup_block_count_ = (mblock_count_ - 1) / group_count_;
  int sort_block_count = mgroup_block_count_ * group_count_;
  output_buffer_ = (uint8_t *)memory_ + sort_block_count * kBlockSize;
  output_block_count_ = mblock_count_ - sort_block_count;
  merge_sort_(0, block_count_);
}

static int cmp_key(const void *a, const void *b) {
  bkey_t keya = ((Record *)(a))->key;
  bkey_t keyb = ((Record *)(b))->key;
  ;
  return keya > keyb ? 1 : (keya == keyb ? 0 : -1);
}

void MergeSort::internal_sort_(index_t start_idx, index_t end_idx) {
  assert(end_idx - start_idx <= mblock_count_);
  void *block = memory_;
  fseek(srcf_, start_idx * kBlockSize, SEEK_SET);
  int max_record_num = (end_idx - start_idx) * kBlockSize / kRecordSize;
  int nitems = fread(block, kRecordSize, max_record_num, srcf_);
  std::qsort(block, nitems, kRecordSize, cmp_key);
  fseek(srcf_, start_idx * kBlockSize, SEEK_SET);
  fwrite(block, kRecordSize, nitems, srcf_);
}

static bool get_record(std::vector<MergeSortGroup *> &groups, Record *record) {
  bkey_t chosen_key = UINT32_MAX;
  MergeSortGroup *chosen_group;
  bool has_element = false;
  Record *chosen_record;
  for (auto *group : groups) {
    Record *current_record = group->record();
    if (group->has_next()) {
      has_element = true;
    } else {
      continue;
    }
    if (current_record->key < chosen_key) {
      chosen_key = current_record->key;
      chosen_group = group;
      chosen_record = current_record;
    }
  }
  if (has_element) {
    memcpy(record, chosen_record, kRecordSize);
    chosen_group->next();
  }
  return has_element;
}

void MergeSort::merge_sort_n_(index_t start_idx, index_t end_idx) {
  std::vector<MergeSortGroup *> groups;
  int group_block_count = (end_idx - start_idx) / group_count_;
  if ((end_idx - start_idx) % group_count_) ++group_block_count;
  // get groups
  int mem_block_idx = 0;
  for (int i = start_idx; i < end_idx; i += group_block_count) {
    index_t part_end_idx = i + group_block_count;
    part_end_idx = std::min(part_end_idx, end_idx);
    uint8_t *group_mem = (uint8_t *)memory_ + mem_block_idx * kBlockSize;
    int mem_record_count = mgroup_block_count_ * kRecordPerBlock;
    int record_count = group_block_count * kRecordPerBlock;
    groups.push_back(new MergeSortGroup(group_mem, mem_record_count,
                                        record_count, i, srcf_));
    ++mem_block_idx;
  }

  // merge
  Record record;
  int current_record_idx = 0;
  int outbuf_record_limit = output_block_count_ * kRecordPerBlock;
  fseek(dstf_, start_idx, SEEK_SET);
  while (get_record(groups, &record)) {
    memcpy(&((Record *)output_buffer_)[current_record_idx], &record,
           kRecordSize);
    ++current_record_idx;
    if (current_record_idx == outbuf_record_limit) {
      current_record_idx = 0;
      fwrite(output_buffer_, kRecordSize, outbuf_record_limit, dstf_);
    }
  }
  if (current_record_idx != 0) {
    fwrite(output_buffer_, kRecordSize, current_record_idx, dstf_);
  }
  for (MergeSortGroup *group : groups) {
    delete group;
  }
}

void MergeSort::merge_sort_(index_t start_idx, index_t end_idx) {
  if (end_idx - start_idx <= mblock_count_) {
    internal_sort_(start_idx, end_idx);
  } else {
    int way_block_count = (end_idx - start_idx) / group_count_;
    if ((end_idx - start_idx) % group_count_ != 0) {
      ++way_block_count;
    }
    for (int i = start_idx; i < end_idx; i += way_block_count) {
      index_t part_end_idx = i + way_block_count;
      part_end_idx = std::min(part_end_idx, end_idx);
      merge_sort_(i, part_end_idx);
    }
    merge_sort_n_(start_idx, end_idx);
  }
}
