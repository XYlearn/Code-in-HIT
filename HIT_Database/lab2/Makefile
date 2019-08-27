TARGET_EXEC := dbmgr
BUILD_DIR := build
SRC_DIRS := src
TEST_DIRS := tests

SRCS := $(shell find $(SRC_DIRS) -name *.cpp)
OBJS := $(SRCS:%.cpp=$(BUILD_DIR)/%.o)
TEST_SRCS := $(shell find $(TEST_DIRS) -name *.cpp)
TEST_OBJS := $(filter-out $(BUILD_DIR)/src/main.o,$(OBJS))
TEST_OBJS += $(TEST_SRCS:%.cpp=$(BUILD_DIR)/%.o)

CPPFLAGS=-std=c++11 -O2 -Wall
CC=clang++

$(TARGET_EXEC): $(OBJS)
	$(CC) $(OBJS) -o $@ $(LDFLAGS)

$(BUILD_DIR)/%.o: %.cpp
	$(MKDIR_P) $(dir $@)
	$(CC) $(CPPFLAGS) -c $< -o $@

test: $(TEST_OBJS)
	$(CC) -lgtest_main -lgtest $(TEST_OBJS) -o $@

.PHONY: clean
clean:
	$(RM) -r $(BUILD_DIR)
MKDIR_P := mkdir -p