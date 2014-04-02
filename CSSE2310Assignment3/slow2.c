#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "agentglobal.h"

typedef struct {
    char* dirs;
    int lastX;
    int lastY;
} Moves;

int check_dep(char* agents, int numAgents);
char get_move(Moves* moves, int currX, int currY);

int main (int argc, char* argv[]) {
    //Check the number of command line args
    if (argc != 1) {
        agent_exit(1);
    }
    
    int numAgents, agentNo, rows, cols, depNo;
    char* agents = get_agent_data(&numAgents, &agentNo, &rows, &cols);
    //Find '+' and get its number
    depNo = check_dep(agents, numAgents);
    get_map_data(rows, cols);
    //Set up the stored moves
    Moves moves;
    moves.dirs = (char*) malloc(sizeof(char));
    
    int targetSteps = 0;
    int targetStopped = 0;
    int replaySteps = 0;
    int agentPos[numAgents][2];
    //Process finished setting data
    char line[255];
    char sendChar, temp;
    while(1) {
        //Wait for data
        for(int i = 0; i < numAgents; i++) {
            try_get(line, 80);
            if (!sscanf(line, "%d %d", &agentPos[i][0], 
                    &agentPos[i][1])) {
                agent_exit(4);
            }
            if(i == depNo && !targetStopped) {
                //If getting data for the dependency and it has not 
                //stopped
                //Allocate more memory and save its move
                moves.dirs = (char*) realloc(moves.dirs, 
                        (sizeof(char) * (targetSteps+1)));
                if (targetSteps == 0) {
                    moves.lastX = agentPos[i][1];
                    moves.lastY = agentPos[i][0];
                } else {
                    temp = get_move(&moves, agentPos[i][1], agentPos[i][0]);
                    if (temp != 'H') {
                        moves.dirs[targetSteps - 1] = temp;
                    } else {
                        targetStopped = 1;
                    }
                }
            }
        }
        if (!targetStopped) {
            //If the dependency has not stopped, hold
            sendChar = 'H';
            targetSteps++;
        } else {
            //Otherwise continue replaying
            sendChar = moves.dirs[replaySteps];
            replaySteps++;
            if (replaySteps >= targetSteps) {
                agent_exit(0);
            }
        }
        printf("%c\n", sendChar);
        fflush(stdout);
    }
}

/** check_dep(char* agents, int numAgents)
**      Takes the agents string and number of agents. If the dependency
**      exists, return its index, otherwise there is an error.
*/
int check_dep(char* agents, int numAgents) {
    for (int i = 0; i < numAgents; i++) {
        if (agents[i] == '+') {
            return i;
        }
    }
    agent_exit(3);
    return 1;
}

/** get_move(Moves* moves, int currX, int currY)
**      Takes a pointer to the moves struct, and the current location
**      of the dependency. Determines the direction which the dependency
**      moved and stores it.
*/
char get_move(Moves* moves, int currX, int currY) {
    char returnChar;
    if (moves->lastX == currX && moves->lastY == currY) {
        returnChar = 'H';
    } else if (moves->lastX < currX) {
        returnChar = 'E';
    } else if (moves->lastX > currX) {
        returnChar = 'W';
    } else if (moves->lastY > currY) {
        returnChar = 'N';
    } else {
        returnChar = 'S';
    }
    moves->lastX = currX;
    moves->lastY = currY;
    return returnChar;
}