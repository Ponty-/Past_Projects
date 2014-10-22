CFLAGS=-g -Wall -pedantic -std=gnu99

all: itsh.c
	gcc $(CFLAGS) -o itsh itsh.c
	
clean:
	rm -rf *o itsh
	
dist:
	make clean
	tar --gzip -cf 42878601.tar.gz itsh.c Makefile itsh.1