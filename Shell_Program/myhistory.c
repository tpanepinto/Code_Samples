//@author tml62 Tim Panepinto
//CS620

#include "myhistory.h"

char history[100][80];


/*parse(char*, char**)
 *parses the input string from the shell by tokenizing
 */
void parse (char *buffer, char** args)
{
    //give str token a place to start
    char* currentArg = strtok(buffer, " ");
    int counter =0;
    
    //while we have not yet hit the end of the string
    while (currentArg != NULL)
    {
        //add the argument string to the list of arguments
        args[counter] = currentArg;
        currentArg = strtok(NULL, " ");//see if the next string is NULL
        counter++; //increase the counter
    }
    
    args[counter] = '\0';
}
/*executeCommand(char*)
 *takes in a string of arguments then creates a new process and executes the
 *commands that were taken in
 */
void executeCommand(char** args)
{
    int childStatus;
    //fork a new process
    pid_t pid = fork();
    
    //if the pid is zero then we are at the child and the command shall be executed
    if ( pid == 0)
    {
        if(execvp(args[0], args) < 0)
            perror("Unknown Command" );
        
    }
    //error checking
    else if( pid < 0)
        printf("Fork FAILED");
    else //if the pid is not 0 then we are at the parent and need to wait for the child
    {
        while (wait(&childStatus) != pid);
        
        //perror("Command Failed");
        
        
    }
    
    
    
    
    
}
/*viewHistory(int)
 *shows the history of commands to the user
 */
void viewHistory(int historyCount)
{
    int lineCount = 0;
    
    while(lineCount< historyCount)
    {
        //print the command
        printf("%d %s\n",lineCount+1,history[lineCount]);
        lineCount++;
    }
}
//This method gets rid of the new line that is created when fgets is used to get input from the command line

void removeEmptyLine(char* buffer)
{
    if (strlen(buffer) > 0 && buffer[strlen(buffer)-1] == '\n')
        buffer[strlen(buffer)-1] = '\0';
}
int main ()
{
    //create a buffer to hold the string from the command line and an array for the arguments that will be parsed
    char buffer[512];
    char* arguments[100];
    int historyCount = 0;
    
    
    while(1)
    {
        //print the shell heading to the screen
        printf("myshell>");
        //get args from user
        fgets(buffer,512,stdin);
        removeEmptyLine(buffer);
        //parse the arguments from the stream of strings
        parse(buffer, arguments);
        
        //if nothing was entered dont waste time
        if (arguments[0] != NULL)
        {
            //if the user has enetred quit then end the program
            if(strcmp(arguments[0], "quit") == 0)
                exit(0);
            //if the user enters history then the history of commands is listed in the shell
            else if(strcmp(arguments[0], "history") == 0)
                viewHistory(historyCount);
            else
                executeCommand(arguments);//execute the command
            //copy the currentcommand into the next place in history
            strcpy(history[historyCount], arguments[0]);
            historyCount++;

        }
        
        
    }
    return 0;
    
    
}
