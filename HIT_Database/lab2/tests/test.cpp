#include <gtest/gtest.h>
#include <cstdlib>
#include <ctime>
#include "../src/common.h"

namespace {

class BPTTest : public ::testing::Test, public BPlusTree {
 protected:
  BPTTest() : BPlusTree("./dbtmp", true) {}
  void SetUp() override {
    srand(time(0));
    bpt = new BPlusTree("./db", true);
  }

  void TearDown() override { delete bpt; }

  static void insert_range(BPlusTree *bpt, int start, int end, int step = 1) {
    val_t val;
    bkey_t key;
    for (int i = start; step > 0 ? i < end : i > end; i += step) {
      key = i;
      *((int *)val) = i;
      bpt->insert(key, val);
    }
  }

 protected:
  BPlusTree *bpt;
};

static void rand_order(int *order, size_t size) {
  for (int i = 0; i < size; i++) {
    order[i] = i;
  }
  for (int i = 0; i < size; i++) {
    int tmp = order[i];
    int j = rand() % size;
    order[i] = order[j];
    order[j] = tmp;
  }
}

TEST_F(BPTTest, BinarySearch) {
  BPlusNode *node = arena_->new_node(TERM);
  int idx;
  // 2 4 6 8
  node->child_num = 5;
  for (int i = 1; i <= node->child_num; i++) *(node->get_key_at(i - 1)) = i * 2;
  idx = _binary_search(node, 0);
  EXPECT_LT(idx, 0);
  idx = _binary_search(node, 2);
  EXPECT_EQ(idx, 0);
  idx = _binary_search(node, 8);
  EXPECT_EQ(idx, 3);
  idx = _binary_search(node, 9);
  EXPECT_LT(idx, 0);
  node->type = NON_TERM;
  idx = _binary_search(node, 0);
  idx = insert_pos(idx);
  EXPECT_EQ(idx, 0);
  idx = _binary_search(node, 2);
  idx = insert_pos(idx);
  EXPECT_EQ(idx, 1);
  idx = _binary_search(node, 5);
  idx = insert_pos(idx);
  EXPECT_EQ(idx, 2);
  idx = _binary_search(node, 9);
  idx = insert_pos(idx);
  EXPECT_EQ(idx, 4);

  node->child_num = 3;
  node->set_key_at(2, 22);
  node->set_key_at(3, 0);
  node->set_key_at(4, 0);
  idx = _binary_search(node, 0);
  idx = insert_pos(idx);
  EXPECT_EQ(idx, 0);
  idx = _binary_search(node, 3);
  idx = insert_pos(idx);
  EXPECT_EQ(idx, 1);
  idx = _binary_search(node, 4);
  idx = insert_pos(idx);
  EXPECT_EQ(idx, 2);
  idx = _binary_search(node, 22);
  idx = insert_pos(idx);
  EXPECT_EQ(idx, 2);
}

TEST_F(BPTTest, DirectInsertTerm) {
  BPlusNode *node = arena_->new_node(TERM);
  node->child_num = 3;
  val_t val;
  val[0] = 3;
  index_t index = 2;
  key_t key = 9;
  _direct_insert_term(node, index, key, val);
  EXPECT_EQ(*(node->get_key_at(index)), key);
  EXPECT_EQ(*(node->get_val_at(index)[0]), val[0]);
}

TEST_F(BPTTest, SplitInsertTermLeft) {
  BPlusNode *node = arena_->new_node(TERM);
  val_t val;
  key_t key;
  for (key = 0; key < kMaxEntries; key++) {
    *(node->get_key_at(key)) = key * 2;
    *((int *)val) = key;
  }
  node->type = TERM;
  node->child_num = kMaxEntries;
  BPlusNode *lnode = nullptr;
  BPlusNode *rnode = nullptr;
  key = kMaxEntries - 3;
  index_t pos = key / 2 + 1;
  *((int *)val) = key;
  _split_insert_term(node, lnode, rnode, pos, key, val);
  EXPECT_TRUE(lnode && rnode);
  EXPECT_EQ(*(lnode->get_key_at(pos)), key);
  EXPECT_EQ(*(lnode->get_val_at(pos))[0], val[0]);
  EXPECT_GE(lnode->child_num, (kMaxEntries + 1) / 2);
  EXPECT_GE(rnode->child_num, (kMaxEntries + 1) / 2);
  EXPECT_EQ(lnode->child_num + rnode->child_num, kMaxEntries + 1);
}

TEST_F(BPTTest, SplitInsertTermRight) {
  BPlusNode *node = arena_->new_node(TERM);
  val_t val;
  key_t key;
  for (key = 0; key < kMaxEntries; key++) {
    *(node->get_key_at(key)) = key * 2;
    *((int *)val) = key;
  }
  node->type = TERM;
  node->child_num = kMaxEntries;
  BPlusNode *lnode = nullptr;
  BPlusNode *rnode = nullptr;
  key = (kMaxEntries - 3) * 2;
  index_t pos = key / 2 + 1;
  *((int *)val) = key;
  _split_insert_term(node, lnode, rnode, pos, key, val);
  EXPECT_TRUE(lnode && rnode);
  EXPECT_EQ(*(rnode->get_key_at(pos - (kMaxEntries + 1) / 2)), key);
  EXPECT_EQ(*(rnode->get_val_at(pos - (kMaxEntries + 1) / 2))[0], val[0]);
  EXPECT_GE(lnode->child_num, (kMaxEntries + 1) / 2);
  EXPECT_GE(rnode->child_num, (kMaxEntries + 1) / 2);
  EXPECT_EQ(lnode->child_num + rnode->child_num, kMaxEntries + 1);
}

TEST_F(BPTTest, DirectInsertNonTerm) {
  BPlusNode *node = arena_->new_node(NON_TERM);
  BPlusNode *lch = arena_->new_node(TERM);
  BPlusNode *rch = arena_->new_node(TERM);
  *(node->get_key_at(0)) = 1;
  *(node->get_key_at(1)) = 3;
  *(node->get_key_at(2)) = 5;
  node->child_num = 4;
  key_t key = 2;
  int idx = _binary_search(node, key);
  idx = insert_pos(idx);
  _direct_insert_non_term(node, idx, key, lch, rch);
  EXPECT_EQ(*(node->get_key_at(idx)), key);
  EXPECT_EQ(*(node->get_child_at(idx)), lch->self);
  EXPECT_EQ(*(node->get_child_at(idx + 1)), rch->self);
}

TEST_F(BPTTest, SplitInsertNonTerm) {
  val_t val;
  bool res;
  int tmp = (kMaxEntries + 1) / 2;
  for (int k = 0; k < kMaxOrder + 1; k++) {
    for (int i = tmp * k; i < tmp * (1 + k); i++) {
      if (i == 8128) {
        *((int *)val) = i;
        insert(i, val);
      }
      *((int *)val) = i;
      insert(i, val);
    }
  }

  res = search(tmp, val);
  EXPECT_TRUE(res);
  EXPECT_EQ(*((int *)val), tmp);
}

TEST_F(BPTTest, InsertOrderEmpty) {
  val_t val;
  BPTTest::insert_range(bpt, 0, kMaxEntries);
  delete bpt;
  bpt = new BPlusTree("./db", false);
  bpt->search(kMaxEntries / 2, val);
  EXPECT_EQ(*((int *)&val), kMaxEntries / 2);
}

TEST_F(BPTTest, InsertRevOrderLevel2) {
  val_t val;
  BPTTest::insert_range(bpt, kMaxEntries * kMaxOrder, 0, -1);
  bpt->insert(24, val);
  // BPlusTree::print_tree(bpt);
  delete bpt;
  bpt = new BPlusTree("./db", false);
  // BPlusTree::print_tree(bpt);
  bpt->search(kMaxEntries, val);
  EXPECT_EQ(*((int *)&val), kMaxEntries);
}

TEST_F(BPTTest, InsertOrderLevel2) {
  val_t val;
  BPTTest::insert_range(bpt, 0, kMaxEntries * kMaxOrder);
  bpt->insert(24, val);
  // BPlusTree::print_tree(bpt);
  delete bpt;
  bpt = new BPlusTree("./db", false);
  // BPlusTree::print_tree(bpt);
  bpt->search(kMaxEntries, val);
  EXPECT_EQ(*((int *)&val), kMaxEntries);
}

TEST_F(BPTTest, InsertRandOrder) {
  val_t val;
  const int element_count = kMaxEntries * kMaxOrder;
  int *order = (int *)malloc(element_count * sizeof(int));
  rand_order(order, element_count);
  // int order[] =
  // {7,24,26,16,3,29,9,21,22,25,14,4,1,33,28,34,31,23,32,6,27,18,2,8,20,15,5,10,30,0,17,13,19,11,35,12};
  // for (int i = 0; i < element_count; ++i) {
  //   printf("%d,", order[i]);
  // }
  puts("");
  for (int i = 0; i < element_count; ++i) {
    *((int *)val) = order[i];
    EXPECT_TRUE(insert(order[i], val));
    // printf("%02x\n", order[i]);
    // BPlusTree::print_tree(this);
    // puts("");
  }
  // puts("Searching");
  for (int i = 0; i < element_count; ++i) {
    EXPECT_TRUE(search(order[i], val));
    EXPECT_EQ(*((int *)val), order[i]);
  }
}

TEST_F(BPTTest, InsertRandOrderMany) {
  val_t val;
  const int element_count = 10000000;
  int *order = (int *)malloc(element_count * sizeof(int));
  rand_order(order, element_count);
  for (int i = 0; i < element_count; ++i) {
    *((int *)val) = order[i];
    EXPECT_TRUE(insert(order[i], val));
    assert(search(order[i], val));
    search(order[i], val);
    assert(*((int *)val) == order[i]);
  }
  for (int i = 0; i < element_count; ++i) {
    EXPECT_TRUE(search(order[i], val));
    EXPECT_EQ(*((int *)val), order[i]);
  }
}

TEST_F(BPTTest, RemoveRoot) {
  val_t val;
  bkey_t key;
  bool res;
  key = 3;
  bpt->insert(key, val);
  res = bpt->remove(3);
  EXPECT_TRUE(res);
  res = bpt->search(3, val);
  EXPECT_FALSE(res);
}

TEST_F(BPTTest, RemoveDirect) {
  val_t val;
  bool res;
  BPTTest::insert_range(bpt, 0, 258);
  res = bpt->remove(250);
  EXPECT_TRUE(res);
  res = bpt->search(250, val);
  EXPECT_FALSE(res);
}

// TEST_F(BPTTest, RemoveTemp) {
//   int insert_order[] = {19, 31, 3,  20, 6,  18, 1,  27, 12, 24, 0,  26,
//                         14, 35, 2,  29, 25, 32, 23, 30, 7,  34, 5,  9,
//                         28, 17, 22, 11, 10, 16, 4,  8,  13, 21, 15, 33};
//   int remove_order[] = {12, 16, 18, 21, 14, 1,  24, 0,  30, 27, 31, 11,
//                         13, 32, 35, 17, 6,  25, 8,  7,  29, 3,  22, 15,
//                         34, 9,  33, 26, 23, 28, 19, 20, 10, 2,  5,  4};
//   int element_count = sizeof(insert_order) / sizeof(int);
//   val_t val;
//   for (int i = 0; i < element_count; i++) {
//     *((bkey_t *)val) = insert_order[i];
//     EXPECT_TRUE(insert(insert_order[i], val));
//   }
//   for (int i = 0; i < element_count; i++) {
//     EXPECT_TRUE(remove(remove_order[i]));
//     printf("%02x\n", remove_order[i]);
//     BPlusTree::print_tree(this);
//     puts("");
//   }
// }

TEST_F(BPTTest, RemoveRandOrder) {
  const int element_count = kMaxEntries * kMaxOrder;
  int *order = (int *)malloc(element_count * sizeof(int));
  rand_order(order, element_count);
  val_t val;
  bkey_t key;
  for (int i = 0; i < element_count; i++) {
    *((bkey_t *)val) = order[i];
    EXPECT_TRUE(insert(order[i], val));
  }
  // printf("insert_order: ");
  // for (int i = 0; i < element_count; ++i) {
  //   printf("%d,", order[i]);
  // }
  // printf("\nremove_order: ");
  rand_order(order, element_count);
  // for (int i = 0; i < element_count; ++i) {
  //   printf("%d,", order[i]);
  // }
  // puts("");
  // print_tree(this);
  // puts("");
  for (int i = 0; i < element_count; i++) {
    key = order[i];
    EXPECT_TRUE(search(key, val));
    EXPECT_EQ(*((bkey_t *)val), key);
    EXPECT_TRUE(remove(key));
    // printf("%02x\n", key);
    // print_tree(this);
    // puts("");
    EXPECT_FALSE(search(key, val));
  }
  free(order);
}

TEST_F(BPTTest, RemoveRandOrderMany) {
  const int element_count = 10000000;
  int *order = (int *)malloc(element_count * sizeof(int));
  rand_order(order, element_count);
  val_t val;
  bkey_t key;
  for (int i = 0; i < element_count; i++) {
    *((bkey_t *)val) = order[i];
    EXPECT_TRUE(insert(order[i], val));
  }
  rand_order(order, element_count);
  for (int i = 0; i < element_count; i++) {
    key = order[i];
    EXPECT_TRUE(search(key, val));
    EXPECT_EQ(*((bkey_t *)val), key);
    EXPECT_TRUE(remove(key));
    EXPECT_FALSE(search(key, val));
  }
  free(order);
}

TEST_F(BPTTest, BorrowTerm) {
  val_t val;
  bool res;
  int ending = kMaxEntries + 1;
  BPTTest::insert_range(bpt, 1, ending + 1);
  bpt->insert(0, val);
  res = bpt->search(ending, val);
  EXPECT_TRUE(res);
  res = bpt->remove(ending);
  EXPECT_TRUE(res);
  res = bpt->search(ending, val);
  EXPECT_FALSE(res);
  res = bpt->search(ending / 2, val);
  EXPECT_TRUE(res);
  EXPECT_EQ((*(int *)val), ending / 2);
  // test right
  bpt->insert(ending + 1, val);
  res = bpt->search(ending + 1, val);
  EXPECT_TRUE(res);
  res = bpt->remove(ending + 1);
  EXPECT_TRUE(res);
  res = bpt->search(ending + 1, val);
  EXPECT_FALSE(res);
  res = bpt->search((ending + 1) / 2, val);
  EXPECT_TRUE(res);
  EXPECT_EQ((*(int *)val), (ending + 1) / 2);
}

TEST_F(BPTTest, MergeTerm) {
  val_t val;
  bool res;
  // three nodes
  int ending = (kMaxEntries + 1) / 2 * 3;
  // test merge to left
  BPTTest::insert_range(bpt, 0, ending);
  key_t key = (kMaxEntries + 1) / 2;
  res = bpt->search(key, nullptr);
  EXPECT_TRUE(res);
  res = bpt->remove(key);
  EXPECT_TRUE(res);
  res = bpt->search(key, nullptr);
  EXPECT_FALSE(res);
  // test merge to right
  BPTTest::insert_range(bpt, ending, ending + (kMaxEntries + 1) / 2);
  bpt->insert(ending + (kMaxEntries + 1) / 2, val);
  key = ending - 1;
  res = bpt->search(key, nullptr);
  EXPECT_TRUE(res);
  res = bpt->remove(key);
  EXPECT_TRUE(res);
  res = bpt->search(key, nullptr);
  EXPECT_FALSE(res);
}

/*-------------------------------------------------------------
 * Merge Sort Test
 *------------------------------------------------------------*/
TEST(MergeSortTest, TestSort) {
  MergeSort merge_sort("./data", "./sorted_data", 1024 * 1024 * 2 / kBlockSize);
  merge_sort.set_group_count(2);
  merge_sort.sort();
  FILE *fp = fopen("./sorted_data", "rb");
  fseek(fp, 0, SEEK_END);
  int file_size = ftell(fp);
  EXPECT_TRUE(file_size % kRecordSize == 0);
  Record *records = (Record *)malloc(file_size);
  fseek(fp, 0, SEEK_SET);
  fread(records, 1, file_size, fp);
  bkey_t prev_key = records[0].key;
  for (int i = 0; i < file_size / kRecordSize; ++i) {
    EXPECT_GE(records[i].key, prev_key);
    // if (records[i].key < prev_key) {
    //   printf("0x%x\n", i);
    // }
    prev_key = records[i].key;
  }
}

}  // namespace
