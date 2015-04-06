# CS620
# Makefile myshell and myhistory

CC = gcc

CFLAGS = -g -Wall -std=c99

all: myshell myhistory

myshell: myshell.c myshell.h
	$(CC) $(CFLAGS) myshell.c -o myshell
myhistory: myhistory.c myhistory.h
	$(CC) $(CFLAGS) myhistory.c -o myhistory
clean:
	-rm -f *.o myhistory myshell



