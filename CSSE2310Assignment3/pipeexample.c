#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char** argv) 
{
    int fd[2];
    pipe(fd);
    printf("%d %d\n", fd[0], fd[1]);
    if(fork()) // parent
    {
        close(fd[1]);
        dup2(fd[0], 0);
        char buff[80]; // output may be longer, be careful
        while(fgets(buff, 80, stdin)) {
            printf("[%s]\n", buff);
        }
    }
    else
    {
        close(fd[0]);
        dup2(fd[1], 1);
        close(fd[1]);
        execlp("ls", "ls", "-a", 0);
        exit(4);
    }
}