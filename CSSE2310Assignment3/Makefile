CFLAGS=-g -Wall -pedantic -std=gnu99
OBJ = handler

ass3make: handler.c simple.c agentglobal.c
	gcc $(CFLAGS) -o handler handler.c
	gcc $(CFLAGS) -o simple simple.c agentglobal.c
	gcc $(CFLAGS) -o fromfile fromfile.c agentglobal.c
	gcc $(CFLAGS) -o waller waller.c agentglobal.c
	gcc $(CFLAGS) -o slow slow.c agentglobal.c
	gcc $(CFLAGS) -o slow2 slow2.c agentglobal.c
