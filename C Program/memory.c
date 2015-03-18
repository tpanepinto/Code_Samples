#include "memory.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>

struct memLinkList{
    int pid;
    struct linkList *next;
};
struct holeLinkList{
	int location;
	int size;
	struct holeLinkList *next;
};
struct procLinkList{
	int location;
	int size;
	struct procLinkList *next;
};	
typedef struct memLinkList memory;
typedef struct holeLinkList holes;
typedef struct procLinkList processes;

int memoryAllocation;
memory *curNode, *head;
holes *holeHead;
processes *procHead;

// memory* findHole(int holeLoc)
// {
//     memory *currentNode = head;
//     
//     for (int i = 0; i < holeLoc -1; i++)
//     {
//         currentNode = currentNode->next;
//     }
//     if(currentNode->pid == -1)
//         fprintf(stderr, "WRONG NODE!");
//     return currentNode;
//     
// }



void MemoryManager(int bytes)
{  // intialize memory with these many bytes.
    
    
    head = NULL;
    
    for (int i = 0; i < bytes -1; i++)
    {
        curNode = (memory*)malloc(sizeof(memory));
        curNode->pid = -1;
        curNode->next = head;
        head = curNode;
    }
    
    head = curNode;
    
    memoryAllocation = bytes;
    
    holeHead = (*holes)malloc(sizeof(holes));
    holeHead->next = NULL
    holeHead->size = 0;
    holeHead->location = 0;
    
    procHead = (*processes)malloc(sizeof(processes));
    procHead->next = NULL
    

}

int allocate(int bytes, int pid)
{ // allocate these many bytes to the process with this id
  //  assume that each pid is unique to a process
  // return 1 if successful
  // return -1 if unsuccessful; print an error indicating
  // whether there wasn't sufficient memory or whether 
  // there you ran into external fragmentation
    
    memory* currentMemNode;
    int hole = 0;
    int memLoc = 0;
    
    f (bytes > memoryAllocation)
    {
        fprintf(stderr, "OUT OF MEMORY!!");
        return -1;
    }
    else
    {
       
       if (holeHead->next == NULL)
       {
           if(procHead->next == NULL)
           {
               currProc = 
           }
       }
        
        
        
        
        return 1;
    }
    

}

int deallocate(int pid)
{ //deallocate memory allocated to this process
  // return 1 if successful, -1 otherwise with an error message

    return 1;
}


void printMemoryState()
{ // print out current state of memory 
  // Example: 
  // Memory size = 1024 bytes, allocated bytes = 24, free = 1000
  // There are currently 10 holes and 3 active process
  // Hole list:
  // hole 1: start location = 0, size = 202
  // ...
  // Process list:
  // process  id=34, start location=203, size=35
  // ...

}
int holeSize(memory *startNode)
{
    memory *currentNode = startNode;
    int holeWidth = 0;
    
    while(currentNode->pid == -1)
    {
        holeWidth++;
        currentNode = currentNode->next;
    }
    
    return holeWidth;
}
int main(int argc, char *argv[])
{
    if (argc < 2)
    {
        fprintf(stderr, "Please include the name of the test file\n");
        exit(-1);
    }
    if (argc > 2)
    {
        fprintf(stderr, "Please only use one file name at a time\n");
        exit(-1);
    }

    
   
    int fileOpen = open(argv[1], O_RDONLY);
        
    // open returns -1 if open of file failed
    if (fileOpen == -1)
    {
        fprintf(stderr, "can't open %s for reading!\n", argv[1]);
        exit(-1);
    }
        
    int BUFLEN = (int)lseek(fileOpen,0, SEEK_END);
    lseek(fileOpen,0,0);
        //int totalCount = 0;
    int lastRead;
    char buf[BUFLEN];
        
    // read will return 0 on EOF or the number of characters read
    lastRead = read( fileOpen, buf, BUFLEN);
    // read could also return -1 if there is an error (not EOF)
    if (lastRead == -1)
    {
        fprintf(stderr, "error reading file %s!\n", argv[1]);
        exit(-1);
    }
    char *currString = NULL;
    printf("Spltting this \"%s\"into tokens:\n", buf);
    
    currString = strtok (buf," ,.-");
    while (currString != NULL)
    {
        printf ("%s\n",currString);
        currString = strtok (NULL, " ,.-");
    }
    
   // MemoryManager(564);
    
        //sort through the file for words that are greater than 8
//        char tmpStr[50];
//        int count = 0;
//        for (int i = 0; i< lastRead; i++)
//        {
//            if((buf[i] >= 'A' && buf[i] <= 'Z'))
//            {
//                buf[i] += 32;
//            }
//            if(buf[i] >= 'a' && buf[i] <= 'z')
//            {
//                //add the next char to the string.
//                tmpStr[count] = buf[i];
//                count++;
//            }
//            else
//            {
//                if(strlen(tmpStr) >= 8)
//                {
//                    int hashVal = Hash(tmpStr);
//                    storeHash(tmpStr, hashVal, k);
//                }
//                
//                for (int j = 0; j < count; j++)
//                    tmpStr[j] = '\0';
//                count = 0;
//            }
//            
//            
//            
//            
//        }
//        
        close(fileOpen);
        
    
       return 0;
    
}
