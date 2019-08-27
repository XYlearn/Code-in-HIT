#include "bplustree.h"

#include <algorithm>
#include <cstdio>
#include <cstdlib>
#include <deque>

/*---------------------------------------------------------*
 * Arena
 *---------------------------------------------------------*/

// throw ArenaException when the path doesn't exist or can be written.
Arena::Arena(FILE *fp, int mem_block_count, bool empty) {
  arena_ = (uint8_t *)calloc(mem_block_count, kBlockSize);
  used_ = (bool *)calloc(mem_block_count, 1);
  next_ = 0;
  fp_ = fp;
  mem_bcount_ = mem_block_count;
  if (!empty) {
    _map_meta();
  } else {
    meta_ = new IndexMeta;
    meta_->meta_block_count = 1;
    meta_->data_block_count = 0;
    meta_->root = kInvalidIndex;
    meta_->next_block = kInvalidIndex;
  }
}

Arena::~Arena() {
  // writeback
  for (const auto &item : cache_) {
    void *block = arena_ + kBlockSize * item.second;
    fseek(fp_, kBlockSize * item.first, SEEK_SET);
    fwrite(block, kBlockSize, 1, fp_);
  }
  flush_meta();
  _unmap_meta();

  fclose(fp_);
  free(arena_);
  free(used_);
}

BPlusNode *Arena::get_node(index_t disk_idx) {
  BPlusNode *node = nullptr;
  index_t arena_idx;
  const auto ite = cache_.find(disk_idx);
  if (ite == cache_.end()) {
    // can't find in cache: read from disk
    arena_idx = _alloc_arena_idx();
    node = get_arena_node(arena_idx);
    if (used_[arena_idx]) {
      // clear from cache adn flush
      cache_.erase(node->self);
      flush_node(node);
    }
    _read_from_disk(node, disk_idx);
    cache_[disk_idx] = arena_idx;
  } else {
    arena_idx = ite->second;
    node = get_arena_node(arena_idx);
  }
  used_[arena_idx] = true;
  assert(node->self == disk_idx);
  return node;
}

inline BPlusNode *Arena::get_root() {
  if (meta_->root == kInvalidIndex) {
    return nullptr;
  }
  BPlusNode *node = get_node(meta_->root);
  return node;
}

inline void Arena::set_root(index_t root_idx) {
  meta_->root = root_idx;
  BPlusNode *node = get_node(root_idx);
  node->parent = kInvalidIndex;
  assert(node->prev == kInvalidIndex && node->next == kInvalidIndex);
}

BPlusNode *Arena::new_node(nodetype_t type) {
  index_t arena_idx = _alloc_arena_idx();
  BPlusNode *arena_node = get_arena_node(arena_idx);
  if (used_[arena_idx]) {
    // clear from cache adn flush
    cache_.erase(arena_node->self);
    flush_node(arena_node);
  } else {
    used_[arena_idx] = true;
  }
  // put in index
  index_t disk_idx = _alloc_disk_idx();
  cache_[disk_idx] = arena_idx;
  // initialize
  arena_node->self = disk_idx;
  arena_node->parent = kInvalidIndex;
  arena_node->prev = kInvalidIndex;
  arena_node->next = kInvalidIndex;
  arena_node->child_num = 0;
  arena_node->locked = false;
  arena_node->type = type;
  assert(_in_range(arena_node));
  return arena_node;
}

inline BPlusNode *Arena::new_root(nodetype_t type) {
  BPlusNode *node = new_node(type);
  meta_->root = node->self;
  return node;
}

void Arena::remove_node(BPlusNode *node) {
  assert(_in_range(node));
  if (node->parent == kInvalidIndex) {
    // remove root
    meta_->root = kInvalidIndex;
  }
  index_t disk_idx = node->self;
  auto ite = cache_.find(disk_idx);
  if (ite != cache_.end()) {
    index_t arena_idx = ite->second;
    used_[arena_idx] = false;
    cache_.erase(ite);
    meta_->free_blocks.push_back(disk_idx);
  }
  // --meta_->data_block_count;
}

inline void Arena::flush_node(BPlusNode *node) {
  assert(_in_range(node));
  node->locked = false;
  index_t disk_idx = node->self;
  _write_to_disk(node, disk_idx);
}

inline BPlusNode *Arena::get_arena_node(index_t arena_idx) {
  assert(arena_idx < mem_bcount_);
  return reinterpret_cast<BPlusNode *>(arena_ + arena_idx * kBlockSize);
}

inline void Arena::_read_from_disk(void *dst, index_t disk_idx) {
  assert(_in_range(dst));
  fseek(fp_, disk_idx * kBlockSize, SEEK_SET);
  fread(dst, kBlockSize, 1, fp_);
}

inline void Arena::_write_to_disk(void *src, index_t disk_idx) {
  assert(_in_range(src));
  fseek(fp_, disk_idx * kBlockSize, SEEK_SET);
  fwrite(src, kBlockSize, 1, fp_);
}

void Arena::_map_meta() {
  uint8_t *meta_block = (uint8_t *)malloc(kBlockSize);
  assert(meta_block);
  fseek(fp_, 0, SEEK_SET);
  size_t nbytes = fread(meta_block, kBlockSize, 1, fp_);
  if (!nbytes) {
    throw ArenaException();
  }
  uint8_t *ptr = meta_block;
  meta_ = new IndexMeta;
  meta_->data_block_count = *(uint64_t *)(ptr);
  ptr += sizeof(uint64_t);
  meta_->meta_block_count = *(uint64_t *)(ptr);
  ptr += sizeof(uint64_t);
  uint64_t free_block_num = *(uint64_t *)(ptr);
  ptr += sizeof(uint64_t);
  meta_->root = *(index_t *)(ptr);
  ptr += sizeof(index_t);
  index_t next_block = *(index_t *)(ptr);
  meta_->next_block = next_block;
  ptr += sizeof(index_t);
  meta_->free_blocks.reserve(free_block_num);
  for (int i = 0; i < free_block_num; ++i) {
    if (ptr + sizeof(index_t) > meta_block + kBlockSize) {
      fseek(fp_, next_block * kBlockSize, SEEK_SET);
      nbytes = fread(meta_block, kBlockSize, 1, fp_);
      if (nbytes == 0) {
        throw ArenaException();
      }
      ptr = meta_block;
      next_block = *(index_t *)(ptr);
      if (next_block == kInvalidIndex) {
        throw ArenaException();
      }
      ptr += sizeof(index_t);
    }
    index_t free_idx = *(index_t *)(ptr);
    meta_->free_blocks.push_back(free_idx);
    ptr += sizeof(index_t);
  }
  free(meta_block);
}

void Arena::flush_meta() {
  uint8_t *meta_block = (uint8_t *)malloc(kBlockSize);
  assert(meta_block);
  fseek(fp_, 0, SEEK_SET);
  uint8_t *ptr = meta_block;
  *(uint64_t *)(ptr) = meta_->data_block_count;
  ptr += sizeof(uint64_t);
  *(uint64_t *)(ptr) = meta_->meta_block_count;
  ptr += sizeof(uint64_t);
  assert(meta_->free_blocks.size() >= 0);
  *(uint64_t *)(ptr) = meta_->free_blocks.size();
  ptr += sizeof(uint64_t);
  *(index_t *)(ptr) = meta_->root;
  ptr += sizeof(index_t);
  index_t next_block = meta_->next_block;
  *(index_t *)(ptr) = next_block;
  ptr += sizeof(index_t);
  index_t current_block = 0;
  for (int i = 0; i < meta_->free_blocks.size(); ++i) {
    if (ptr + sizeof(index_t) > meta_block + kBlockSize) {
      fseek(fp_, current_block * kBlockSize, SEEK_SET);
      fwrite(meta_block, kBlockSize, 1, fp_);
      ptr = meta_block;
      current_block = next_block;
      if (current_block == kInvalidIndex) {
        current_block = _alloc_disk_idx();
      }
      *(index_t *)(ptr) = current_block;
      ptr += sizeof(index_t);
    }
    index_t free_idx = *(index_t *)(ptr);
    meta_->free_blocks.push_back(free_idx);
    ptr += sizeof(index_t);
  }
  fseek(fp_, current_block * kBlockSize, SEEK_SET);
  fwrite(meta_block, kBlockSize, 1, fp_);
  free(meta_block);
}

inline void Arena::_unmap_meta() {
  delete meta_;
  meta_ = nullptr;
}

inline index_t Arena::_alloc_arena_idx() {
  while (used_[next_] && get_arena_node(next_)->locked) {
    next_ = (next_ + 1) % mem_bcount_;
  }
  index_t ret = next_;
  next_ = (next_ + 1) % mem_bcount_;
  used_[next_] = true;
  return ret;
}

inline index_t Arena::_alloc_disk_idx() {
  index_t ret;
  if (meta_->free_blocks.size()) {
    ret = meta_->free_blocks.back();
    meta_->free_blocks.pop_back();
  } else {
    ret = meta_->meta_block_count + meta_->data_block_count++;
  }
  return ret;
}

/*---------------------------------------------------------*
 * BPlusNode
 *---------------------------------------------------------*/

/*---------------------------------------------------------*
 * BPlusTree
 *---------------------------------------------------------*/

BPlusTree::BPlusTree(const char *path, bool empty) {
  FILE *fp;
  if (empty) {
    fp = _create_empty(path);
  } else {
    fp = fopen(path, "rb+");
  }
  assert(fp);
  arena_ = new Arena(fp, kBlockCount, empty);
  fp_ = fp;  // save for close
}

BPlusTree::~BPlusTree() {
  delete arena_;
  fclose(fp_);
}

bool BPlusTree::search(bkey_t key, val_t res) {
  int idx;
  BPlusNode *node = _search_node_idx(key, &idx);
  if (idx != kInvalidIndex && idx >= 0) {
    if (res) {
      assert(node);
      memcpy(res, node->get_val_at(idx), kValueSize);
    }
    return true;
  }
  return false;
}

bool BPlusTree::insert(bkey_t key, val_t val) {
  BPlusNode *node = arena_->get_root();
  // the btree has no root, and so is empty
  if (!node) {
    node = arena_->new_root(TERM);
  }
  while (true) {
    if (node->is_term()) {
      node->locked = true;
      bool res = _insert_term(node, key, val);
      node->locked = false;
      return res;
    }
    int idx = _binary_search(node, key);
    idx = insert_pos(idx);
    node = arena_->get_node(*(node->get_child_at(idx)));
  }
}

bool BPlusTree::remove(bkey_t key) {
  BPlusNode *node = arena_->get_root();
  if (!node) {
    return false;
  }
  while (true) {
    if (node->is_term()) {
      node->locked = true;
      bool res = _remove_term(node, key);
      node->locked = false;
      return res;
    }
    int idx = _binary_search(node, key);
    idx = insert_pos(idx);
    // I dont know why
    assert(idx < node->child_num);
    node = arena_->get_node(*(node->get_child_at(idx)));
  }
}

// search index within a node
// if node is leaf and key is not found, return kInvalidIndex
int BPlusTree::_binary_search(BPlusNode *node, bkey_t key) {
  bkey_t *key_start = node->get_key_at(0);
  int length = node->is_term() ? node->child_num : node->child_num - 1;
  int low = -1, high = length;
  while (low < high - 1) {
    int mid = low + (high - low) / 2;
    if (key > key_start[mid]) {
      low = mid;
    } else {
      high = mid;
    }
  }
  // for non_term node, check if high is in last ptr
  // for term node, check if key is not found
  if (high >= length || key_start[high] != key) {
    // negtive number means insert position |high|
    // if retval < 0 then insert position is -retval-1
    return -high - 1;
  }
  return high;
}

BPlusNode *BPlusTree::_search_node_idx(bkey_t key, int *idx_ptr) {
  BPlusNode *node = arena_->get_root();
  while (node) {
    int idx = _binary_search(node, key);
    if (node->is_term()) {
      *idx_ptr = idx;
      if (idx < 0) {
        return nullptr;
      }
      return node;
    }
    idx = insert_pos(idx);
    assert(idx <= node->child_num);
    node = arena_->get_node(*(node->get_child_at(idx)));
  }
  *idx_ptr = kInvalidIndex;
  return nullptr;
}

void BPlusTree::_direct_insert_term(BPlusNode *node, index_t index, bkey_t key,
                                    val_t val) {
  memmove(node->get_key_at(index + 1), node->get_key_at(index),
          (node->child_num - index) * sizeof(bkey_t));
  memmove(node->get_val_at(index + 1), node->get_val_at(index),
          (node->child_num - index) * sizeof(val_t));
  *(node->get_key_at(index)) = key;
  ++node->child_num;
  memmove(node->get_val_at(index), val, kValueSize);
}

void BPlusTree::_direct_insert_non_term(BPlusNode *node, index_t index,
                                        bkey_t key, BPlusNode *lch,
                                        BPlusNode *rch) {
  int cpy_count = std::max((int)(node->child_num - index - 1), 0);
  memmove(node->get_key_at(index + 1), node->get_key_at(index),
          cpy_count * sizeof(bkey_t));
  memmove(node->get_child_at(index + 2), node->get_child_at(index + 1),
          cpy_count * sizeof(index_t));
  node->set_key_at(index, key);
  _bind_parent_child(node, lch, index);
  _bind_parent_child(node, rch, index + 1);
  ++node->child_num;
}

bool BPlusTree::_insert_term(BPlusNode *node, bkey_t key, val_t val) {
  int idx = _binary_search(node, key);
  // key exists
  if (key_found(idx)) {
    return false;
  }
  idx = insert_pos(idx);
  if (node->full()) {
    BPlusNode *lnode;
    BPlusNode *rnode;
    bkey_t parent_key = _split_insert_term(node, lnode, rnode, idx, key, val);
    lnode->locked = true;
    rnode->locked = true;
    _insert_parent(lnode, rnode, parent_key);
    lnode->locked = false;
    rnode->locked = false;
  } else {
    _direct_insert_term(node, idx, key, val);
  }
  return true;
}

bool BPlusTree::_insert_non_term(BPlusNode *node, BPlusNode *lch,
                                 BPlusNode *rch, bkey_t key) {
  int idx = _binary_search(node, key);
  // idx is the key index of splitted child
  idx = insert_pos(idx);
  if (node->full()) {
    BPlusNode *lnode, *rnode;
    bkey_t parent_key =
        _split_insert_non_term(node, lnode, rnode, lch, rch, idx, key);
    lnode->locked = true;
    rnode->locked = true;
    _insert_parent(lnode, rnode, parent_key);
    lnode->locked = false;
    rnode->locked = false;
  } else {
    _direct_insert_non_term(node, idx, key, lch, rch);
  }
  return true;
}

// split node and insert key val at right position
bkey_t BPlusTree::_split_insert_term(BPlusNode *node, BPlusNode *&lnode,
                                     BPlusNode *&rnode, index_t pos, bkey_t key,
                                     val_t val) {
  lnode = node;
  lnode->locked = true;
  rnode = arena_->new_node(TERM);
  lnode->locked = false;
  index_t mid = (kMaxEntries + 1) / 2;
  rnode->locked = true;
  bkey_t res;
  if (pos < mid) {
    res = _split_insert_term_left(node, lnode, rnode, pos, key, val);
  } else {
    res = _split_insert_term_right(node, lnode, rnode, pos, key, val);
  }
  rnode->locked = false;
  return res;
}

bkey_t BPlusTree::_split_insert_term_left(BPlusNode *node, BPlusNode *lnode,
                                          BPlusNode *rnode, index_t pos,
                                          bkey_t key, val_t val) {
  index_t mid = (kMaxEntries + 1) / 2;
  lnode->child_num = mid;
  rnode->child_num = kMaxEntries - mid;
  memmove(rnode->get_key_at(0), node->get_key_at(mid),
          rnode->child_num * sizeof(bkey_t));
  memmove(rnode->get_val_at(0), node->get_val_at(mid),
          rnode->child_num * sizeof(val_t));
  _direct_insert_term(lnode, pos, key, val);
  return *(rnode->get_key_at(0));
}

bkey_t BPlusTree::_split_insert_term_right(BPlusNode *node, BPlusNode *lnode,
                                           BPlusNode *rnode, index_t pos,
                                           bkey_t key, val_t val) {
  index_t mid = (kMaxEntries + 1) / 2;
  lnode->child_num = mid;
  rnode->child_num = kMaxEntries - mid;
  memmove(rnode->get_key_at(0), node->get_key_at(mid),
          rnode->child_num * sizeof(bkey_t));
  memmove(rnode->get_val_at(0), node->get_val_at(mid),
          rnode->child_num * sizeof(val_t));
  _direct_insert_term(rnode, pos - mid, key, val);
  return *(rnode->get_key_at(0));
}

bkey_t BPlusTree::_split_insert_non_term(BPlusNode *node, BPlusNode *&lnode,
                                         BPlusNode *&rnode, BPlusNode *lch,
                                         BPlusNode *rch, index_t pos,
                                         bkey_t key) {
  lnode = node;
  rnode = arena_->new_node(NON_TERM);
  index_t mid = (kMaxOrder + 1) / 2;
  rnode->locked = true;
  bkey_t res;
  if (pos < mid) {
    res = _split_insert_non_term_left(node, lnode, rnode, lch, rch, pos, key);
  } else if (pos > mid) {
    res = _split_insert_non_term_right(node, lnode, rnode, lch, rch, pos, key);
  } else {
    res = _split_insert_non_term_mid(node, lnode, rnode, lch, rch, pos, key);
  }
  assert(*(lnode->get_child_at(0)));
  assert(*(rnode->get_child_at(0)));
  // set rnode's children
  for (int i = 0; i < rnode->child_num; ++i) {
    arena_->get_node(*(rnode->get_child_at(i)))->parent = rnode->self;
  }
  rnode->locked = false;
  return res;
}

bkey_t BPlusTree::_split_insert_non_term_left(BPlusNode *node,
                                              BPlusNode *&lnode,
                                              BPlusNode *&rnode, BPlusNode *lch,
                                              BPlusNode *rch, index_t pos,
                                              bkey_t key) {
  index_t mid = (kMaxOrder + 1) / 2;
  index_t pivot = mid - 1;
  bkey_t split_key = *(node->get_key_at(pivot));
  rnode->child_num = kMaxOrder - pivot - 1;
  lnode->child_num = pivot + 1;
  memcpy(rnode->get_key_at(0), node->get_key_at(pivot + 1),
         sizeof(bkey_t) * (rnode->child_num - 1));
  memcpy(rnode->get_child_at(0), node->get_child_at(pivot + 1),
         sizeof(index_t) * (rnode->child_num));
  _direct_insert_non_term(lnode, pos, key, lch, rch);
  assert(lnode->child_num >= kMaxOrder / 2);
  assert(rnode->child_num >= kMaxOrder / 2);
  return split_key;
}

bkey_t BPlusTree::_split_insert_non_term_right(BPlusNode *node,
                                               BPlusNode *&lnode,
                                               BPlusNode *&rnode,
                                               BPlusNode *lch, BPlusNode *rch,
                                               index_t pos, bkey_t key) {
  index_t mid = (kMaxOrder + 1) / 2;
  index_t pivot = mid;
  bkey_t split_key = *(node->get_key_at(pivot));

  index_t rnode_pos = pos - pivot - 1;
  // non_term node has 1 less key than child
  memcpy(rnode->get_key_at(0), node->get_key_at(pivot + 1),
         (kMaxOrder - pivot - 2) * sizeof(bkey_t));
  memcpy(rnode->get_child_at(0), node->get_child_at(pivot + 1),
         (kMaxOrder - pivot - 1) * sizeof(index_t));
  lnode->child_num = pivot + 1;
  rnode->child_num = kMaxOrder - pivot - 1;
  _direct_insert_non_term(rnode, rnode_pos, key, lch, rch);
  assert(lnode->child_num >= kMaxOrder / 2);
  assert(rnode->child_num >= kMaxOrder / 2);
  return split_key;
}

bkey_t BPlusTree::_split_insert_non_term_mid(BPlusNode *node, BPlusNode *&lnode,
                                             BPlusNode *&rnode, BPlusNode *lch,
                                             BPlusNode *rch, index_t pos,
                                             bkey_t key) {
  index_t mid = (kMaxOrder + 1) / 2;
  index_t pivot = mid;
  bkey_t split_key = key;

  // non_term node has 1 less key than child
  memcpy(rnode->get_key_at(0), node->get_key_at(pivot),
         (kMaxOrder - pivot - 1) * sizeof(bkey_t));
  memcpy(rnode->get_child_at(1), node->get_child_at(pivot + 1),
         (kMaxOrder - pivot - 1) * sizeof(index_t));
  rnode->set_child_at(0, rch->self);
  lnode->child_num = pivot + 1;
  rnode->child_num = kMaxOrder - pivot;
  assert(lnode->child_num >= kMaxOrder / 2);
  assert(rnode->child_num >= kMaxOrder / 2);
  return split_key;
}

void BPlusTree::_insert_parent(BPlusNode *lch, BPlusNode *rch, bkey_t key) {
  if (lch->parent == kInvalidIndex) {
    // add root
    BPlusNode *parent = arena_->new_root(NON_TERM);
    parent->child_num = 2;
    parent->set_key_at(0, key);
    parent->set_child_at(0, lch->self);
    parent->set_child_at(1, rch->self);
    lch->parent = parent->self;
    rch->parent = parent->self;
  } else {
    BPlusNode *parent = arena_->get_node(lch->parent);
    parent->locked = true;
    _insert_non_term(parent, lch, rch, key);
    parent->locked = false;
  }
  _bind_sibling(lch, rch);
}

void BPlusTree::_bind_parent_child(BPlusNode *parent, BPlusNode *child,
                                   index_t pos) {
  child->parent = parent->self;
  parent->set_child_at(pos, child->self);
}

void BPlusTree::_bind_sibling(BPlusNode *lnode, BPlusNode *rnode) {
  // assume rnode is a new node
  assert(lnode != rnode && lnode->self != rnode->self);
  if (lnode->next != kInvalidIndex) {
    arena_->get_node(lnode->next)->prev = rnode->self;
  }
  rnode->prev = lnode->self;
  rnode->next = lnode->next;
  lnode->next = rnode->self;
  assert(rnode->self != rnode->prev && rnode->self != rnode->next);
  assert(lnode->self != lnode->prev && lnode->self != lnode->next);
}

void BPlusTree::_unlink_node(BPlusNode *node) {
  node->locked = true;
  if (node->prev != kInvalidIndex) {
    BPlusNode *lnode = arena_->get_node(node->prev);
    assert(lnode->next == node->self);
    lnode->next = node->next;
  }
  if (node->next != kInvalidIndex) {
    BPlusNode *rnode = arena_->get_node(node->next);
    assert(rnode->prev == node->self);
    rnode->prev = node->prev;
  }
  node->locked = false;
}

bool BPlusTree::_remove_term(BPlusNode *node, bkey_t key) {
  int idx = _binary_search(node, key);
  if (idx < 0) {
    // not found
    return false;
  }
  if (node->parent == kInvalidIndex) {
    // node is root
    if (node->child_num == 1) {
      arena_->remove_node(node);
    } else {
      _direct_remove_term(node, idx);
    }
    return true;
  }

  if (node->half()) {
    BPlusNode *target, *parent, *target_parent;
    int parent_target_key_idx, parent_key_index;
    _direct_remove_term(node, idx);
    // do merge or borrow
    RemoveStrategy strategy = _choose_remove_strategy(node, target);
    target->locked = true;
    switch (strategy) {
      case kBorrowFromLeft:
        _borrow_from_term_left(node, target);
        target->locked = false;
        break;
      case kBorrowFromRight:
        _borrow_from_term_right(node, target);
        target->locked = false;
        break;
      case kMergeToLeft:  // merge to left
        _merge_to_term_left(node, target);
        _unlink_node(node);
        parent = arena_->get_node(node->parent);
        parent_key_index = _binary_search(parent, key);
        parent_key_index = key_pos(parent_key_index);
        parent->locked = true;
        _remove_non_term(parent, parent_key_index);
        parent->locked = false;
        arena_->remove_node(node);
        target->locked = false;
        break;
      case kMergeFromRight:  // merge from right
        _merge_from_term_right(node, target);
        _unlink_node(target);
        target_parent = arena_->get_node(target->parent);
        parent_target_key_idx =
            _binary_search(target_parent, *(target->get_key_at(0)));
        parent_target_key_idx = key_pos(parent_target_key_idx);
        target_parent->locked = true;
        _remove_non_term(target_parent, parent_target_key_idx);
        target_parent->locked = false;
        arena_->remove_node(target);
        break;
      default:
        assert(false);
    }
  } else {
    _direct_remove_term(node, idx);
  }
  return true;
}

void BPlusTree::_remove_non_term(BPlusNode *node, index_t pos) {
  if (node->parent == kInvalidIndex) {
    // node is root
    if (node->child_num == 2) {
      index_t new_root =
          pos == 0 ? *(node->get_child_at(0)) : *(node->get_child_at(1));
      arena_->set_root(new_root);
    } else {
      _direct_remove_non_term(node, pos);
    }
    return;
  }

  if (node->half()) {
    bkey_t tmp_key;
    tmp_key = *(node->get_key_at(0));
    bkey_t overflow_key = _direct_remove_non_term(node, pos);
    BPlusNode *target, *parent;
    int parent_key_idx;
    RemoveStrategy strategy = _choose_remove_strategy(node, target);
    target->locked = true;
    switch (strategy) {
      case kBorrowFromLeft:
        _borrow_from_non_term_left(node, target, overflow_key);
        break;
      case kBorrowFromRight:
        _borrow_from_non_term_right(node, target, overflow_key);
        break;
      case kMergeToLeft:
        tmp_key = *(node->get_key_at(0));
        parent = arena_->get_node(node->parent);
        parent_key_idx = _binary_search(parent, tmp_key);
        parent_key_idx = key_pos(parent_key_idx);
        parent->locked = true;
        _merge_to_non_term_left(node, target);
        _unlink_node(node);
        _remove_non_term(parent, parent_key_idx);
        parent->locked = false;
        arena_->remove_node(node);
        break;
      case kMergeFromRight:
        tmp_key = *(target->get_key_at(0));
        parent = arena_->get_node(target->parent);
        parent_key_idx = _binary_search(parent, tmp_key);
        parent_key_idx = key_pos(parent_key_idx);
        parent->locked = true;
        _merge_from_non_term_right(node, target);
        _unlink_node(target);
        _remove_non_term(parent, parent_key_idx);
        parent->locked = false;
        arena_->remove_node(target);
        break;
      default:
        assert(false);
    }
    target->locked = false;
  } else {
    _direct_remove_non_term(node, pos);
  }
}

void BPlusTree::_direct_remove_term(BPlusNode *node, index_t pos) {
  memmove(node->get_key_at(pos), node->get_key_at(pos + 1),
          (node->child_num - (pos + 1)) * sizeof(bkey_t));
  memmove(node->get_val_at(pos), node->get_val_at(pos + 1),
          (node->child_num - (pos + 1)) * sizeof(val_t));
  --node->child_num;
}

bkey_t BPlusTree::_direct_remove_non_term(BPlusNode *node, index_t pos) {
  assert(node->child_num >= 2);
  bkey_t overflow_key;
  if (pos != kInvalidIndex) {
    // no over flow
    overflow_key = kInvalidIndex;
    memmove(node->get_key_at(pos), node->get_key_at(pos + 1),
            (node->child_num - pos - 2) * sizeof(bkey_t));
    memmove(node->get_child_at(pos + 1), node->get_child_at(pos + 2),
            (node->child_num - pos - 1) * sizeof(index_t));
  } else {
    overflow_key = *(node->get_key_at(0));
    memmove(node->get_key_at(0), node->get_key_at(1),
            (node->child_num - 2) * sizeof(bkey_t));
    memmove(node->get_child_at(0), node->get_child_at(1),
            (node->child_num - 1) * sizeof(index_t));
    // need to update parent key
    BPlusNode *current_node = node;
    while (true) {
      current_node = arena_->get_node(current_node->parent);
      int parent_key_idx = _binary_search(current_node, overflow_key);
      parent_key_idx = key_pos(parent_key_idx);
      if (parent_key_idx != kInvalidIndex) {
        current_node->set_key_at(parent_key_idx, overflow_key);
        break;
      }
    }
  }
  --node->child_num;
  // this key is useful when pos is kInvalidIndex
  return overflow_key;
}

void BPlusTree::_borrow_from_term_left(BPlusNode *node, BPlusNode *lnode) {
  // borrow the last pair from lnode
  index_t borrow_idx = lnode->child_num - 1;
  index_t insert_idx = 0;
  // _borrow_from_term_with(node, lnode, insert_idx, borrow_idx);
  bkey_t new_key = *(lnode->get_key_at(borrow_idx));
  bkey_t origin_key = *(node->get_key_at(insert_idx));
  _direct_insert_term(node, insert_idx, new_key,
                      *(lnode->get_val_at(borrow_idx)));
  _direct_remove_term(lnode, borrow_idx);
  // update parent
  BPlusNode *current_node = node;
  int parent_key_idx;
  while (true) {
    current_node = arena_->get_node(current_node->parent);
    parent_key_idx = _binary_search(current_node, origin_key);
    parent_key_idx = key_pos(parent_key_idx);
    if (parent_key_idx != kInvalidIndex) {
      current_node->set_key_at(parent_key_idx, new_key);
      break;
    }
  }
}

void BPlusTree::_borrow_from_term_right(BPlusNode *node, BPlusNode *rnode) {
  // borrow the first pair from rnode
  index_t borrow_idx = 0;
  index_t insert_idx = node->child_num;
  // _borrow_from_term_with(node, rnode, insert_idx, borrow_idx);
  bkey_t new_key = *(rnode->get_key_at(borrow_idx));
  bkey_t parent_key = *(rnode->get_key_at(borrow_idx + 1));
  _direct_insert_term(node, insert_idx, new_key,
                      *(rnode->get_val_at(borrow_idx)));
  _direct_remove_term(rnode, borrow_idx);
  // update parent
  BPlusNode *current_node = rnode;

  // BPlusNode *parent = arena_->get_node(rnode->parent);
  while (true) {
    current_node = arena_->get_node(current_node->parent);
    int parent_key_idx = _binary_search(current_node, parent_key);
    parent_key_idx = key_pos(parent_key_idx);
    if (parent_key_idx != kInvalidIndex) {
      current_node->set_key_at(parent_key_idx, parent_key);
      break;
    }
  }
}

void BPlusTree::_borrow_from_non_term_left(BPlusNode *node, BPlusNode *lnode,
                                           bkey_t overflow_key) {
  bkey_t origin_key = *(node->get_key_at(0));
  index_t borrow_idx = lnode->child_num - 1;
  bkey_t key = *(lnode->get_key_at(borrow_idx - 1));
  index_t child = *(lnode->get_child_at(borrow_idx));
  // move
  memmove(node->get_key_at(1), node->get_key_at(0),
          sizeof(bkey_t) * (node->child_num - 1));
  memmove(node->get_child_at(1), node->get_child_at(0),
          sizeof(index_t) * node->child_num);

  // update child
  BPlusNode *child_node = arena_->get_node(child);
  child_node->parent = node->self;
  // update child num
  ++node->child_num;
  --lnode->child_num;
  
  BPlusNode *current_node = node;
  int parent_key_idx;
  bkey_t parent_key;
  while (true) {
    current_node = arena_->get_node(current_node->parent);
    parent_key_idx = _binary_search(current_node, origin_key);
    parent_key_idx = key_pos(parent_key_idx);
    if (parent_key_idx != kInvalidIndex) {
      parent_key = *(current_node->get_key_at(parent_key_idx));
      current_node->set_key_at(parent_key_idx, key);
      if (overflow_key != kInvalidIndex) {
        node->set_key_at(0, overflow_key);
      } else {
        node->set_key_at(0, parent_key);
      }
      break;
    }
  }

  node->set_child_at(0, child);
}

void BPlusTree::_borrow_from_non_term_right(BPlusNode *node, BPlusNode *rnode,
                                            bkey_t overflow_key) {
  index_t borrow_idx = 0;
  index_t insert_pos = node->child_num - 1;
  bkey_t borrow_key = *(rnode->get_key_at(borrow_idx));
  index_t child = *(rnode->get_child_at(borrow_idx));

  BPlusNode *current_node = rnode;
  int parent_key_pos;
  bkey_t key;
  // get key and update rnode parent
  while (true) {
    current_node = arena_->get_node(current_node->parent);
    parent_key_pos = _binary_search(current_node, borrow_key);
    parent_key_pos = key_pos(parent_key_pos);
    if (parent_key_pos != kInvalidIndex) {
      key = *(current_node->get_key_at(parent_key_pos));
      current_node->set_key_at(parent_key_pos, borrow_key);
      break;
    }
  }

  // insert
  node->set_key_at(insert_pos, key);
  node->set_child_at(insert_pos + 1, child);

  // update rnode
  memmove(rnode->get_key_at(0), rnode->get_key_at(1),
          sizeof(bkey_t) * (rnode->child_num - 2));
  memmove(rnode->get_child_at(0), rnode->get_child_at(1),
          sizeof(index_t) * (rnode->child_num - 1));
  // update child
  BPlusNode *child_node = arena_->get_node(child);
  child_node->parent = node->self;
  // update child_num
  ++node->child_num;
  --rnode->child_num;
}

void BPlusTree::_merge_to_term_left(BPlusNode *node, BPlusNode *lnode) {
  memmove(lnode->get_key_at(lnode->child_num), node->get_key_at(0),
          node->child_num * sizeof(bkey_t));
  memmove(lnode->get_val_at(lnode->child_num), node->get_val_at(0),
          node->child_num * sizeof(val_t));
  lnode->child_num += node->child_num;
}

void BPlusTree::_merge_from_term_right(BPlusNode *node, BPlusNode *rnode) {
  memmove(node->get_key_at(node->child_num), rnode->get_key_at(0),
          rnode->child_num * sizeof(bkey_t));
  memmove(node->get_val_at(node->child_num), rnode->get_val_at(0),
          rnode->child_num * sizeof(val_t));
  node->child_num += rnode->child_num;
}

void BPlusTree::_merge_to_non_term_left(BPlusNode *node, BPlusNode *lnode) {
  bkey_t needle = *(node->get_key_at(0));
  BPlusNode *current_node = node;
  int parent_key_idx;
  bkey_t parent_key;
  while (true) {
    current_node = arena_->get_node(current_node->parent);
    parent_key_idx = _binary_search(current_node, needle);
    parent_key_idx = key_pos(parent_key_idx);
    if (parent_key_idx != kInvalidIndex) {
      parent_key = *(current_node->get_key_at(parent_key_idx));
      break;
    }
  }
  // update childs
  for (int i = 0; i < node->child_num; ++i) {
    arena_->get_node(*(node->get_child_at(i)))->parent = lnode->self;
  }
  memmove(lnode->get_key_at(lnode->child_num), node->get_key_at(0),
          (node->child_num - 1) * sizeof(bkey_t));
  memmove(lnode->get_child_at(lnode->child_num), node->get_child_at(0),
          (node->child_num) * sizeof(index_t));
  lnode->set_key_at(lnode->child_num - 1, parent_key);
  lnode->child_num += node->child_num;
}

void BPlusTree::_merge_from_non_term_right(BPlusNode *node, BPlusNode *rnode) {
  bkey_t needle = *(rnode->get_key_at(0));
  BPlusNode *current_node = rnode;
  int parent_key_idx;
  bkey_t parent_key;
  while (true) {
    current_node = arena_->get_node(current_node->parent);
    parent_key_idx = _binary_search(current_node, needle);
    parent_key_idx = key_pos(parent_key_idx);
    if (parent_key_idx != kInvalidIndex) {
      parent_key = *(current_node->get_key_at(parent_key_idx));
      break;
    }
  }
  // update childs
  for (int i = 0; i < rnode->child_num; ++i) {
    arena_->get_node(*(rnode->get_child_at(i)))->parent = node->self;
  }
  memmove(node->get_key_at(node->child_num), rnode->get_key_at(0),
          (rnode->child_num - 1) * sizeof(bkey_t));
  memmove(node->get_child_at(node->child_num), rnode->get_child_at(0),
          (rnode->child_num) * sizeof(index_t));
  node->set_key_at(node->child_num - 1, parent_key);
  node->child_num += rnode->child_num;
}

RemoveStrategy BPlusTree::_choose_remove_strategy(BPlusNode *node,
                                                  BPlusNode *&target) {
  assert(node->prev != kInvalidIndex || node->next != kInvalidIndex);
  assert(node->next != node->self && node->prev != node->self);
  RemoveStrategy strategy;
  if (node->prev == kInvalidIndex) {
    target = arena_->get_node(node->next);
    if (target->half()) {
      strategy = kMergeFromRight;
    } else {
      strategy = kBorrowFromRight;
    }
  } else if (node->next == kInvalidIndex) {
    target = arena_->get_node(node->prev);
    if (target->half()) {
      strategy = kMergeToLeft;
    } else {
      strategy = kBorrowFromLeft;
    }
  } else {
    BPlusNode *lnode = arena_->get_node(node->prev);
    BPlusNode *rnode = arena_->get_node(node->next);
    if (lnode->half() && rnode->half()) {
      target = lnode;
      strategy = kMergeToLeft;
    } else if (lnode->half()) {
      // can borrow from right
      target = rnode;
      strategy = kBorrowFromRight;
    } else if (rnode->half()) {
      // can borrow from left
      target = lnode;
      strategy = kBorrowFromLeft;
    } else if (lnode->child_num >= rnode->child_num) {
      target = lnode;
      strategy = kBorrowFromLeft;
    } else {
      target = rnode;
      strategy = kBorrowFromRight;
    }
  }
  assert(target->self != 0);
  return strategy;
}

// void BPlusTree::_find_parent_key_idx(BPlusNode *node, bkey_t needle, BPlusNode *&parent,
//                           index_t &parent_key_idx) {
//   BPlusNode *current_node = node;
//   while (true) {
//     current_node = arena_->get_node(current_node->parent);
//     parent
//   }
// }

FILE *BPlusTree::_create_empty(const char *path) {
  FILE *fp = fopen(path, "wb+");
  if (!fp) {
    throw IOException();
  }
  return fp;
}

void BPlusTree::load_raw_data(const char *data_path) {
  // TODO
}

#define MODE_PLAIN 0
#define MODE_PARENT_SELF 1
#define MODE_PREV_NEXT 2

void BPlusTree::print_node(BPlusNode *node, int mode) {
  int length;
  if (node->type == TERM) {
    length = node->child_num;
  } else {
    length = node->child_num - 1;
  }
  printf("[");
  if (mode == MODE_PARENT_SELF) {
    printf("{%02x,%02x}", node->parent, node->self);
  } else if (mode == MODE_PREV_NEXT) {
    printf("{%02x,%02x,%02x}", node->prev, node->self, node->next);
  }

  for (int i = 0; i < length; ++i) {
    if (i == length - 1) {
      printf("%02x] ", *(node->get_key_at(i)));
    } else {
      printf("%02x ", *(node->get_key_at(i)));
    }
  }
}

void BPlusTree::print_tree(BPlusTree *tree, int mode) {
  BPlusNode *node;
  std::deque<index_t> queue;
  std::deque<index_t> tmpq;
  node = tree->arena_->get_root();
  if (node == nullptr) {
    printf("[]");
    return;
  }
  queue.push_back(node->self);
  while (!queue.empty()) {
    tmpq = queue;
    for (auto idx : tmpq) {
      node = tree->arena_->get_node(idx);
      print_node(node, mode);
      queue.pop_front();
      if (!node->is_term()) {
        for (int i = 0; i < node->child_num; ++i) {
          queue.push_back(*(node->get_child_at(i)));
        }
      }
    }
    printf("\n");
  }
}
