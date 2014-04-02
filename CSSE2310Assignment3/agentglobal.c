#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

void try_get (char* output, int length);
void agent_exit (int code);
char** make_string_array(int sizeX, int sizeY);
char** get_map_data (int rows, int cols);
char* get_agent_data (int* numAgents, int* agentNo, int* rows, int* cols);
char basic_move (char dir, char** map, int rows, int cols, int pos[2]);

/** try_get (char* output, int length)
**      Takes a string to place output in and a maximum length.
**      Does an fgets from stdin with the given parameters and
**      checks the result.
*/
void try_get (char* output, int length) {
    //Try to get output and place it in the given char pointer
    if(fgets(output, length, stdin) == NULL) {
        agent_exit(4);
    }
}

/** agent_exit(int code)
**       Takes an error code for agent errors and prints the appropriate
**       message. The program then exits with the given code.
*/
void agent_exit(int code) {
    //Print the appropriate error message and exit
    switch(code) {
        case 0:
            printf("D\n");
            fflush(stdout);
            //Exit quietly so the handler doesn't catch the signal
            _exit(0);
            break;
        case 1:
            printf("Incorrect number of params.\n");
            break;
        case 2:
            printf("Invalid params.\n");
            break;
        case 3:
            printf("Dependencies not met.\n");
            break;
        case 4:
            printf("Handler communication breakdown.\n");
            break;
        default:
            printf("Error %d.\n", code);
    }
    fflush(stdout);
    exit(code);
}

/** make_string_array(int sizeX, int sizeY)
**      Allocates memory for a 2D character array of the specified size.
*/
char** make_string_array(int sizeX, int sizeY) {
    char** array;
    int i;
    array = (char**)malloc(sizeX * sizeof(char*));
    for(i = 0; i < sizeX; i++) {
        array[i] = (char*)malloc(sizeY * sizeof(char));
    }
    return array;
}

/** get_agent_data (int* numAgents, int* agentNo, int* rows, int* cols)
**      Reads in the number of agents, agent chars, number of this agent,
**      and the rows and columns in the map from the pipe.
*/
char* get_agent_data (int* numAgents, int* agentNo, int* rows, int* cols) {
    //Get things from pipe
    char line[255];
    //Get number of agents
    try_get(line, 80);
    *numAgents = atoi(line);
    if (*numAgents < 1) {
        agent_exit(4);
    }
    //Get agent chars
    char* agents = (char*)malloc((*numAgents + 2) * sizeof(char));
    try_get(line, 80);
    strncpy(agents, line, *numAgents + 2);
    if ((strlen(agents) - 1) != *numAgents) {
        agent_exit(4);
    }
    //Get the number of this agent
    try_get(line, 80);
    *agentNo = atoi(line);
    if (*agentNo < 1 || *agentNo > *numAgents) {
        agent_exit(4);
    }
    //Get the size of the map
    try_get(line, 80);
    if(!sscanf(line, "%d %d", rows, cols) || *rows < 1 || *cols < 1) {
        agent_exit(4);
    }
    
    return agents;
}

/** get_map_data (int rows, int cols)
**      Takes the dimensions of the map and reads the chars in from the pipe.
*/
char** get_map_data (int rows, int cols) {
    char line[255];
    char** map;
    //Get the map. Cols+1 so there is space for null terminator
    //Allocate memory
    map = make_string_array(rows, cols+1);
    //Fill it in
    for(int i = 0; i < rows; i++) {
        try_get(line, 80);
        strncpy(map[i], line, cols+1);
        for(int j = 0; j < cols; j++) {
            if (map[i][j] != ' ' && map[i][j] != '#') {
                agent_exit(4);
            }
        }
    }
    //Finished read in
    return map;
}

/** basic_move (char dir, char** map, int rows, int cols, int pos[2])
**      Takes a direction, the map and its dimensions, and a position.
**      Returns the direction if it is possible to move that way, or a H
**      if it is blocked by a wall.
*/
char basic_move (char dir, char** map, int rows, int cols, int pos[2]) {
    //Move
    int moved = 0;
    char sendChar;
    switch(dir) {
        case 'N':
            //Try moving up
            if(pos[0] > 1) {
                if(map[pos[0]-2][pos[1]-1] == ' ') {
                    sendChar = 'N';
                    moved = 1;
                }
            }
            break;
        case 'S':
            //Try moving down
            if(pos[0] < rows) {
                if(map[pos[0]][pos[1]-1] == ' ') {
                    sendChar = 'S';
                    moved = 1;
                }
            }
            break;
        case 'E':
            //Try moving right
            if(pos[1] < cols) {
                if(map[pos[0]-1][pos[1]] == ' ') {
                    sendChar = 'E';
                    moved = 1;
                }
            }
            break;
        case 'W':
            //Try moving left
            if(pos[1] > 0) {
                if(map[pos[0]-1][pos[1]-2] == ' ') {
                    sendChar = 'W';
                    moved = 1;
                }
            }
            break;
    }
    if (moved == 0) {
        sendChar = 'H';
    }
    return sendChar;
}