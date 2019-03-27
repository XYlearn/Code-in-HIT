CC=clang
CFLAGS=-g
LDFLAGS=-g
DEPS=utils.h
OBJS=main.o proxy.o psocket.o plist.o utils.o md5.o
EXE=proxy
DEPS=utils.h
SRC=src
BUILD=build

all: $(EXE)

$(EXE): $(OBJS)
	$(CC) $(LDFLAGS) -o $(EXE) $(patsubst %.o,$(BUILD)/%.o,$(OBJS))

%.o: $(SRC)/%.c $(SRC)/%.h $(SRC)/$(DEPS)
	$(CC) -c $(CFLAGS) -o $(BUILD)/$@ $<

%.o: $(SRC)/%.c $(SRC)/$(DEPS)
	$(CC) -c $(CFLAGS) -o $(BUILD)/$@ $<

clean:
	rm -f $(BUILD)/*.o $(EXE)
