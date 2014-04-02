#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "agentglobal.h"

int main (int argc, char* argv[]) {
    //Check the command line args
    if(argc != 2) {
        agent_exit(1);
    }
    if(strlen(argv[1]) != 1) {
        agent_exit(2);
    }
    char d = argv[1][0];
    if(d != 'N' && d != 'E' && d != 'S' && d != 'W') {
        agent_exit(2);
    }
    
    int numAgents, agentNo, rows, cols;
    get_agent_data(&numAgents, &agentNo, &rows, &cols);
    char** map = get_map_data(rows, cols);
    
    int steps = 0;
    int agentPos[numAgents][2];
    //Process finished setting data
    char line[255];
    char sendChar;
    while(1) {
        //Wait for data
        for(int i = 0; i < numAgents; i++) {
            try_get(line, 80);
            if (!sscanf(line, "%d %d", &agentPos[i][0], 
                    &agentPos[i][1])) {
                agent_exit(4);
            }
        }
        //Work out move
        sendChar = basic_move(d, map, rows, cols, agentPos[agentNo-1]);
        if (steps >= 10) {
            agent_exit(0);
        }
        //Increment steps and send move
        steps++;
        printf("%c\n", sendChar);
        fflush(stdout);
    }
}
