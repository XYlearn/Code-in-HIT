/*
 * This file is to define the BPlusTree class
 * It will include following class
 * Arena: manage the memory and block map
 * BPlusNode: node of BPlusTree
 * BPlusTree: BPlusTree
 *
 */
#ifndef _BPlusTree_H_
#define _BPlusTree_H_

#include <cassert>
#include <cstdint>
#include <cstdio>

#include <exception>
#include <unordered_map>
#include <vector>

using index_t = uint32_t;
using bkey_t = uint32_t;
using val_t = uint8_t[12];
using valsize_t = uint32_t;
using nodetype_t = bool;

#define TERM true
#define NON_TERM false

#define kValueSize 12
#define kInvalidIndex ((index_t)-1)
#define kBlockSize 4096
#define kBlockCount (256 * 1024 * 1024 / kBlockSize)  // 2MB
#define kMetaBlockNum 1
#define kMaxEntries \
  ((kBlockSize - sizeof(BPlusNode)) / (sizeof(bkey_t) + kValueSize))
// max key number of non-term node is kMaxOrder - 1
#define kMaxOrder \
  ((kBlockSize - sizeof(BPlusNode)) / (sizeof(bkey_t) + sizeof(index_t)))
// #define kMaxEntries 6
// #define kMaxOrder 6

#define insert_pos(idx) \
  (((((int)(idx)) < 0) ? (-((int)(idx)) - 1) : ((int)(idx + 1))))
#define key_pos(idx) ((int)(insert_pos(idx)) - 1)
#define key_found(idx) (((idx) >= 0))

enum RemoveStrategy {
  kBorrowFromLeft,
  kBorrowFromRight,
  kMergeToLeft,
  kMergeFromRight
};

struct IndexMeta;
class Arena;
struct BPlusNode;
class BPlusTree;

// store metadata of databse
struct IndexMeta {
  uint64_t data_block_count;  // number of blocks in the index file
  uint64_t meta_block_count;  // number of blocks occupied by meta
  index_t root;               // root index
  index_t next_block;         // next block index of meta
  std::vector<index_t> free_blocks;
};

// exception thrown by Arena
class ArenaException : public std::exception {};

// Arena to manage memory blocks
class Arena {
 public:
  Arena(FILE *fp, int mem_block_count, bool empty);
  virtual ~Arena();
  /* index is index in disk */
  BPlusNode *get_node(index_t disk_idx);
  BPlusNode *get_root();
  void set_root(index_t root_idx);
  BPlusNode *new_node(nodetype_t type);
  BPlusNode *new_root(nodetype_t type);
  void remove_node(BPlusNode *node);
  void flush_node(BPlusNode *node);
  BPlusNode *get_arena_node(index_t arena_idx);

 protected:
  /* index is index in arena memory */

  void _read_from_disk(void *dst, index_t disk_idx);
  void _write_to_disk(void *src, index_t disk_idx);

  void _map_meta();
  void flush_meta();
  void _unmap_meta();
  index_t _alloc_arena_idx();
  index_t _alloc_disk_idx();

 public:
  bool _in_range(void *block) {
    return (block >= arena_) && (block < arena_ + mem_bcount_ * kBlockSize);
  }

 protected:
  int mem_bcount_;
  index_t next_;  // the next memory block to use
  uint8_t *arena_;
  IndexMeta *meta_;
  FILE *fp_;
  bool *used_;  // whether an memory index is used.

  // cache of <disc block index, memory block index>
  std::unordered_map<index_t, index_t> cache_;
};

class IOException : public std::exception {};

// Node of B+Tree
struct BPlusNode {
  index_t self;
  index_t parent;
  index_t prev;
  index_t next;
  uint16_t child_num;  // number of children
  nodetype_t type;
  bool locked;  // this flag is used by arena. if it's set, it can't be clear
                // out of memory

 public:
  static BPlusNode *from_block(void *block) {
    return reinterpret_cast<BPlusNode *>(block);
  }

  bool is_term() { return type; }

  uint8_t *get_data_area() { return (uint8_t *)&self + sizeof(BPlusNode); }

  bkey_t *get_key_at(index_t idx) {
    assert(is_term() ? idx <= kMaxEntries : idx <= kMaxOrder - 1);
    return (bkey_t *)get_data_area() + idx;
  }

  val_t *get_val_at(index_t idx) {
    assert(is_term() && idx <= kMaxEntries);
    return (val_t *)(get_data_area() + kMaxEntries * sizeof(bkey_t)) + idx;
  }

  index_t *get_child_at(index_t idx) {
    assert(!is_term() && idx <= kMaxOrder);
    return (index_t *)(get_data_area() + (kMaxOrder - 1) * sizeof(bkey_t)) +
           idx;
  }

  void set_key_at(index_t idx, bkey_t key) { *(get_key_at(idx)) = key; }

  void set_val_at(index_t idx, val_t val) {
    memcpy(*(get_val_at(idx)), val, sizeof(val_t));
  }

  void set_child_at(index_t idx, index_t child) {
    *(get_child_at(idx)) = child;
  }

  bool full() {
    if (type == TERM) {
      return child_num == kMaxEntries;
    } else {
      return child_num == kMaxOrder;
    }
  }

  bool half() {
    if (type == TERM) {
      return child_num == kMaxEntries / 2;
    } else {
      return child_num == kMaxOrder / 2;
    }
  }
};

// B+Tree
class BPlusTree {
 public:
  BPlusTree(const char *path, bool empty = false);
  virtual ~BPlusTree();

  bool insert(bkey_t key, val_t val);
  bool update(bkey_t key, val_t val);
  bool remove(bkey_t key);
  bool search(bkey_t key, val_t res);

 public:
  static void print_node(BPlusNode *node, int mode = 0);
  static void print_tree(BPlusTree *tree, int mode = 0);
  static void load_raw_data(const char *data_path);

 protected:
  int _binary_search(BPlusNode *node, bkey_t key);
  BPlusNode *_search_node_idx(bkey_t key, int *idx_ptr);
  bool _insert_term(BPlusNode *node, bkey_t key, val_t val);
  bool _insert_non_term(BPlusNode *node, BPlusNode *lch, BPlusNode *rch,
                        bkey_t key);
  void _direct_insert_term(BPlusNode *node, index_t pos, bkey_t key, val_t val);
  void _direct_insert_non_term(BPlusNode *node, index_t pos, bkey_t key,
                               BPlusNode *lch, BPlusNode *rch);
  bkey_t _split_insert_term(BPlusNode *node, BPlusNode *&lnode,
                            BPlusNode *&rnode, index_t pos, bkey_t key,
                            val_t val);
  bkey_t _split_insert_non_term(BPlusNode *node, BPlusNode *&lnode,
                                BPlusNode *&rnode, BPlusNode *lch,
                                BPlusNode *rch, index_t pos, bkey_t key);
  bkey_t _split_insert_term_left(BPlusNode *node, BPlusNode *lnode,
                                 BPlusNode *rnode, index_t pos, bkey_t key,
                                 val_t val);
  bkey_t _split_insert_term_right(BPlusNode *node, BPlusNode *lnode,
                                  BPlusNode *rnode, index_t pos, bkey_t key,
                                  val_t val);
  bkey_t _split_insert_non_term_left(BPlusNode *node, BPlusNode *&lnode,
                                     BPlusNode *&rnode, BPlusNode *lch,
                                     BPlusNode *rch, index_t pos, bkey_t key);
  bkey_t _split_insert_non_term_right(BPlusNode *node, BPlusNode *&lnode,
                                      BPlusNode *&rnode, BPlusNode *lch,
                                      BPlusNode *rch, index_t pos, bkey_t key);
  bkey_t _split_insert_non_term_mid(BPlusNode *node, BPlusNode *&lnode,
                                    BPlusNode *&rnode, BPlusNode *lch,
                                    BPlusNode *rch, index_t pos, bkey_t key);
  void _insert_parent(BPlusNode *lch, BPlusNode *rch, bkey_t key);
  void _bind_parent_child(BPlusNode *parent, BPlusNode *child, index_t pos);
  void _bind_sibling(BPlusNode *lnode, BPlusNode *rnode);
  void _unlink_node(BPlusNode *node);

  bool _remove_term(BPlusNode *node, bkey_t key);
  void _remove_non_term(BPlusNode *node, index_t pos);
  void _direct_remove_term(BPlusNode *node, index_t pos);
  bkey_t _direct_remove_non_term(BPlusNode *node, index_t pos);
  void _borrow_from_term_left(BPlusNode *node, BPlusNode *lnode);
  void _borrow_from_term_right(BPlusNode *node, BPlusNode *rnode);
  void _borrow_from_non_term_left(BPlusNode *node, BPlusNode *lnode,
                                  bkey_t overflow_key);
  void _borrow_from_non_term_right(BPlusNode *node, BPlusNode *rnode,
                                   bkey_t overflow_key);
  void _merge_to_term_left(BPlusNode *node, BPlusNode *lnode);
  void _merge_from_term_right(BPlusNode *node, BPlusNode *rnode);
  void _merge_to_non_term_left(BPlusNode *node, BPlusNode *lnode);
  void _merge_from_non_term_right(BPlusNode *node, BPlusNode *rnode);
  RemoveStrategy _choose_remove_strategy(BPlusNode *node, BPlusNode *&target);
  void _find_parent_key_idx(BPlusNode *node, bkey_t needle, BPlusNode *&parent,
                            index_t &parent_key_idx);

//  private:
//   void memmove(void *dst, void *src, size_t nbytes) {
//     if (nbytes) {
//       assert(arena_->_in_range((uint8_t *)dst + nbytes));
//     }
//     ::memmove(dst, src, nbytes);
//   }

//   void *memcpy(void *dst, void *src, size_t nbytes) {
//     if (nbytes && arena_->_in_range(dst)) {
//       assert(arena_->_in_range((uint8_t *)dst + nbytes));
//     }
//     return ::memcpy(dst, src, nbytes);
//   }

 protected:
  FILE *_create_empty(const char *path);

 protected:
  Arena *arena_;
  FILE *fp_;
};

#endif  // !_BTREE_H_