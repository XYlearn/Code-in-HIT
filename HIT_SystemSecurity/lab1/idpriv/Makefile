CC=gcc
CFLAGS=-D_GNU_SOURCE
LDFLAGS=
OBJS=test.o idpriv.o
EXE=test
SRC=src
BUILD=build

all: $(EXE)

$(EXE): $(OBJS)
	$(CC) $(LDFLAGS) -o $(EXE) $(patsubst %.o,$(BUILD)/%.o,$(OBJS))

%.o: $(SRC)/%.c $(SRC)/%.h
	$(CC) -c $(CFLAGS) -o $(BUILD)/$@ $<

%.o: $(SRC)/%.c
	$(CC) -c $(CFLAGS) -o $(BUILD)/$@ $<

clean:
	rm -f $(BUILD)/*.o $(EXE)

run:
	./$(EXE)
