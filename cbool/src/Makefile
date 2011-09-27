
CFLAGS = -std=c99 `pkg-config --cflags glib-2.0` -O3 -march=native -mtune=native
CFLAGS += -I/usr/include/gc
LDFLAGS = `pkg-config --libs glib-2.0`
LDFLAGS += -lgc

all: main main.s

main.s: main.c
	gcc $(CFLAGS) $(LDFLAGS) -S main.c
