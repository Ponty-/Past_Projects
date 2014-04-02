#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "agentglobal.h"

int main (int argc, char* argv[]) {
    //Check the number of command line args
    if (argc != 2) {
        agent_exit(1);
    }
    
    //Try open the given file
    FILE* fromfile;
    if((fromfile = fopen(argv[1], "r")) == NULL) {
        agent_exit(2);
    }
    
    int numAgents, agentNo, rows, cols;
    get_agent_data(&numAgents, &agentNo, &rows, &cols);
    char** map = get_map_data(rows, cols);
    char d;
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
    
        d = fgetc(fromfile);
        //Check if the run is complete
        if(d == EOF) {
            if (sendChar == '\0') {
                agent_exit(3);
            }
            agent_exit(0);
        }
        
        //Work out move and send it
        sendChar = basic_move(d, map, rows, cols, agentPos[agentNo-1]);
        printf("%c\n", sendChar);
        fflush(stdout);
    }
}
