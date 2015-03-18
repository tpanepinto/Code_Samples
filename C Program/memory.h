#ifndef _myShell_h
#define _myShell_h
#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include  <sys/types.h>
#include  <sys/wait.h>

void executeCommand(char**);
void parse(char*, char**);
void removeEmptyLine(char*);

#endif
