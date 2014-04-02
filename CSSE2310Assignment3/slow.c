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
            if(i == depNo) {
                //Store the moves of the agent with char '+'
                moves.dirs = (char*) realloc(moves.dirs, 
                        (sizeof(char) * (steps+1)));
                if (steps == 0) {
                    moves.lastX = agentPos[i][1];
                    moves.lastY = agentPos[i][0];
                } else {
                    moves.dirs[steps] = get_move(&moves, agentPos[i][1],
                            agentPos[i][0]);
                }
            }
        }
        //Work whether to replay moves, and if so, send the next move
        if (steps < 11) {
            sendChar = 'H';
        } else {
            sendChar = moves.dirs[steps - 10];
        }
        steps++;
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