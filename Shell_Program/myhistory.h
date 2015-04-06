//
//  myhistory.h
//  
//
//  Created by Tim Panepinto on 10/1/13.
//
//

#ifndef _myhistory_h
#define _myhistory_h
#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include  <sys/types.h>
#include  <sys/wait.h>

extern char history[100][80];
void executeCommand(char**);
void parse(char*, char**);
void viewHistory(int);
void removeEmptyLine(char*);

#endif
