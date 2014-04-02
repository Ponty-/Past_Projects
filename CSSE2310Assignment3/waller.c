#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "agentglobal.h"

int check_surrounded(char** map, int currX, int currY, int rows, int cols);
char check_turn_left(char** map, int currX, int currY, int rows, int cols,
        char d);
int check_blocking(int** agentPos, int agentNo, char d, int numAgents);
char turn_right(char** map, int currX, int currY, int rows, int cols, char d);
char right_rotations(int freeSpaces[4], char d);
char check_args(int argc, char* argv[]);

int main (int argc, char* argv[]) {
    //Check the arguments and set variables
    char d = check_args(argc, argv);
    int numAgents, agentNo, rows, cols;
    get_agent_data(&numAgents, &agentNo, &rows, &cols);
    char** map = get_map_data(rows, cols);
    int** agentPos;
    agentPos = (int**)malloc(numAgents * sizeof(int*));
    for(int i = 0; i < numAgents; i++) {
        agentPos[i] = (int*)malloc(2 * sizeof(int));
    }
    //Process finished setting data
    char line[255];
    char sendChar;
    char tempD;
    int currX, currY;
    while(1) {
        //Wait for data
        for(int i = 0; i < numAgents; i++) {
            try_get(line, 80);
            if (!sscanf(line, "%d %d", &agentPos[i][0], 
                    &agentPos[i][1])) {
                agent_exit(4);
            }
        }
        //Set current x and y position
        currX = agentPos[agentNo-1][1] - 1;
        currY = agentPos[agentNo-1][0] - 1;
        //Determine a move based on the waller algorithm
        if(check_surrounded(map, currY, currX, rows, cols)) {
            sendChar = 'H';
        } else if((tempD = check_turn_left(map, currX, currY, rows, cols, d))
                != d) {
            d = tempD;
            if(check_blocking(agentPos, agentNo, d, numAgents)) {
                sendChar = 'H';
            } else {
                sendChar = d;
            }
        } else {
            d = turn_right(map, currX, currY, rows, cols, d);
            if(check_blocking(agentPos, agentNo, d, numAgents)) {
                sendChar = 'H';
            } else {
                sendChar = d;
            }
        }        
        printf("%c\n", sendChar);
        fflush(stdout);
    }
}

/** check_surrounded(char** map, int currX, int currY, int rows, int cols)
**      Takes the map and dimensions, and the current position.
**      Returns 1 if the agent is surrounded and 0 otherwise.
*/
int check_surrounded(char** map, int currX, int currY, int rows, int cols) {
    //Check N
    if (currY == 0) {
        //Blocked at edge - continue
    } else if (map[currY-1][currX] == '#') {
        //Blocked - continue
    } else {
        //Not blocked
        return 0;
    }
    
    //Check S
    if (currY >= (rows-1)) {
        //Blocked at edge - continue
    } else if (map[currY+1][currX] == '#') {
        //Blocked - continue
    } else {
        //Not blocked
        return 0;
    }
    
    //Check E
    if (currX >= (cols-1)) {
        //Blocked at edge - continue
    } else if (map[currY][currX+1] == '#') {
        //Blocked - continue
    } else {
        //Not blocked
        return 0;
    }
    
    //Check W
    if (currX == 0) {
        //Blocked at edge - continue
    } else if (map[currY][currX-1] == '#') {
        //Blocked - continue
    } else {
        //Not blocked
        return 0;
    }
    
    return 1;
}

/** check_turn_left(char** map, int currX, int currY, int rows, int cols,
        char d) {
**      Takes the map and dimensions, the current position, and direction.
**      Determines if the case in which the agent should turn left as per
**      the waller algorithm is met, and returns the new direction if it is.
*/
char check_turn_left(char** map, int currX, int currY, int rows, int cols,
        char d) {
    char newD = d;
    switch(d) {
        case 'N':
            if(currX > 0 && currY < rows - 1) {
                if(map[currY][currX-1] == ' ' && map[currY+1][currX-1]
                        == '#') {
                    newD = 'W';
                }
            }
            break;
        case 'S':
            if(currX < cols - 1 && currY > 0) {
                if(map[currY][currX+1] == ' ' && map[currY-1][currX+1]
                        == '#') {
                    newD = 'E';
                }
            }
            break;
        case 'E':
            if(currX > 0 && currY > 0) {
                if(map[currY-1][currX] == ' ' && map[currY-1][currX-1]
                        == '#') {
                    newD = 'N';
                }
            }
            break;
        case 'W':
            if(currY < rows - 1 && currX < cols - 1) {
                if(map[currY+1][currX] == ' ' && map[currY+1][currX+1]
                        == '#') {
                    newD = 'S';
                }
            }
            break;
    }
    return newD;
    
}

/** check_blocking(int** agentPos, int agentNo, char d, int numAgents)
**      Takes the agent positions, current agent number, direction and 
**      number of agents. Returns 1 if any of the agents will collide with
**      the current agent or 0 otherwise.
*/
int check_blocking(int** agentPos, int agentNo, char d, int numAgents) {
    int tempX = agentPos[agentNo-1][1];
    int tempY = agentPos[agentNo-1][0];
    switch(d) {
        case 'N':
            tempY--;
            break;
        case 'S':
            tempY++;
            break;
        case 'E':
            tempX++;
            break;
        case 'W':
            tempY--;
            break;
    }
    for(int i = 0; i < numAgents; i++) {
        if (agentPos[i][0] == tempY && agentPos[i][1] == tempX) {
            return 1;
        }
    }
    return 0;
}

/** turn_right(char** map, int currX, int currY, int rows, int cols, char d)
**      Takes the map and dimensions, current position and direction.
**      Returns the direction of the first open space in a clockwise
**      direction from the current direction.
*/
char turn_right(char** map, int currX, int currY, int rows, int cols, char d) {
    int freeSpaces[4] = {0, 0, 0, 0};
    //Check N
    if (currY == 0) {
    } else if (map[currY-1][currX] == '#') {
    } else {
        //Not blocked
        freeSpaces[0] = 1;
    }
    //Check S
    if (currY >= (rows-1)) {
    } else if (map[currY+1][currX] == '#') {
    } else {
        //Not blocked
        freeSpaces[1] = 1;
    }
    //Check E
    if (currX >= (cols-1)) {
    } else if (map[currY][currX+1] == '#') {
    } else {
        //Not blocked
        freeSpaces[2] = 1;
    }
    //Check W
    if (currX == 0) {
    } else if (map[currY][currX-1] == '#') {
    } else {
        //Not blocked
        freeSpaces[3] = 1;
    }
    return right_rotations(freeSpaces, d);
}

/** right_rotations(int freeSpaces[4], char d)
**      Takes the array of free spaces generated by turn_right and the current
**      direction. Returns the new direction based on the first free space
**      found by turning clockwise.
*/
char right_rotations(int freeSpaces[4], char d) {
    //Perform up to 3 rotations
    switch(d) {
        case 'N':
            if(freeSpaces[0]) {
                //Already facing the right direction
                break;
            } else if (freeSpaces[2]) {
                d = 'E';
            } else if (freeSpaces[1]) {
                d = 'S';
            } else {
                d = 'W';
            }
            break;
        case 'S':
            if(freeSpaces[1]) {
                break;
            } else if (freeSpaces[3]) {
                d = 'W';
            } else if (freeSpaces[0]) {
                d = 'N';
            } else {
                d = 'E';
            }
            break;
        case 'E':
            if(freeSpaces[2]) {
                break;
            } else if (freeSpaces[1]) {
                d = 'S';
            } else if (freeSpaces[3]) {
                d = 'W';
            } else {
                d = 'N';
            }
            break;
        case 'W':
            if(freeSpaces[3]) {
                break;
            } else if (freeSpaces[0]) {
                d = 'N';
            } else if (freeSpaces[2]) {
                d = 'E';
            } else {
                d = 'S';
            }
            break;
    }
    return d;
}

/** check_args(int argc, char* argv[])
**      Checks if the command line arguments are valid for the waller
**      program.
*/
char check_args(int argc, char* argv[]) {
    //Check the number of command line args
    if (argc != 2) {
        agent_exit(1);
    }
    if (strlen(argv[1]) != 1) {
        agent_exit(2);
    }
    char d = argv[1][0];
    if (d != 'N' && d != 'E' && d != 'S' && d != 'W') {
        agent_exit(2);
    }
    return d;
}