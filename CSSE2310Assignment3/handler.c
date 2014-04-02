#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <limits.h>
#include <unistd.h>
#include <signal.h>
#include <sys/wait.h>

#define PARENT_READ readPipe[0]
#define CHILD_WRITE readPipe[1]
#define CHILD_READ writePipe[0]
#define PARENT_WRITE writePipe[1]

typedef struct AgentData {
    int currX, currY; //Current x and y position of agent
    char position; //Char used to represent agent on board
    char program[80]; //The program and command line args
    int pid; //The pid when the child agent is created
    FILE* input; //The output pipe from parent to child
    FILE* output; //The input pipe from child to parent
} AgentData;

//Global variables used by signal handler
AgentData* agents; //Array of agent structs
int numAgents = 0; //Number of agents in array

void start_agents(int rows, int cols, char** map);
void handler_exit(int code);
char** open_map_file(char* mapPath, int* rows, int* cols);
char** make_string_array(int sizeX, int sizeY);
void fill_map(char** map, FILE* file, int rows, int cols);
void get_num_agents(char* agentPath);
void get_agents(char* agentPath, int rows, int cols);
void sig_handler(int signo);
void cleanup(int* childExitStatus, int* childExitSignal, char* exitedAgent);
void print_map(char** map, int rows, int cols);
void check_move(char** map, int current, int rows, int cols);
void agent_move(int current);
void try_exec_agent(int agentNo);
void check_agent_name(int agentNo);

int main(int argc, char* argv[]) {
    char *mapPath, *agentPath; //The location of the files
    int rows, cols, maxSteps; //Dimensions of the map and the max steps
    char** map; //Character map
    //Check the command line args
    if (argc != 4) {
        handler_exit(1);
    }

    mapPath = argv[1];
    agentPath = argv[3];

    long tempL;
    char** tempP = NULL;
    tempL = strtol(argv[2], tempP, 10);
        
    if (tempL < 1 || tempL > INT_MAX) {
        handler_exit(2);
    }
    
    //Set variables needed later in the program - max steps, map and agents
    maxSteps = (int)tempL;
    map = open_map_file(mapPath, &rows, &cols);
    get_num_agents(agentPath);
    agents = (AgentData *)malloc(sizeof(AgentData)*numAgents);
    get_agents(agentPath, rows, cols);

    //Set up signal handling
    signal(SIGINT, sig_handler);
    signal(SIGCHLD, sig_handler);

    //Start agents
    start_agents(rows, cols, map);

    //Start the game
    for (int i = 0; i < maxSteps; i++) {
        //Let each agent take turns, process their move and print the map
        for(int j = 0; j < numAgents; j++) {
            agent_move(j);
            check_move(map, j, rows, cols);
            print_map(map, rows, cols);
        }
    }
    //No agent succeeded with given steps
    handler_exit(10);
}

/** start_agents(int rows, int cols, char** map)
**      Takes all the info required to start up the agent programs.
**      Goes over the agent array, sets up pipes and executes each agent.
*/
void start_agents(int rows, int cols, char** map) {
    for(int i = 0; i < numAgents; i++) {
        //For each agent make a new int array 
        int readPipe[2];
        int writePipe[2];
        pipe(readPipe);
        pipe(writePipe);
        //Check the agent name is valid
        check_agent_name(i);
        agents[i].pid = (int)fork();
        if(agents[i].pid) {
            //Parent - close pipes and open streams
            close(CHILD_WRITE);
            agents[i].input = fdopen(PARENT_READ, "r");
            close(CHILD_READ);
            agents[i].output = fdopen(PARENT_WRITE, "w");
            //Send the child all the startup info
            fprintf(agents[i].output, "%d\n", numAgents);
            for (int j = 0; j < numAgents; j++) {
                fprintf(agents[i].output, "%c", agents[j].position);
            }
            fprintf(agents[i].output, "\n%d\n%d %d\n", i+1, rows, cols);
            for (int k = 0; k < rows; k++) {
                for(int l = 0; l < cols; l++) {
                    if (map[k][l] == '.') {
                        fprintf(agents[i].output, " ");
                    } else {
                        fprintf(agents[i].output, "#");
                    }
                }
                fprintf(agents[i].output, "\n");
            }
            fflush(agents[i].output);
        } else {
            //Child - disable signal handling and set up pipes/fds
            signal(SIGINT, SIG_IGN);
            signal(SIGCHLD, SIG_IGN);
            close(PARENT_WRITE);
            dup2(CHILD_WRITE, 1);
            close(CHILD_WRITE);
            close(PARENT_READ);
            dup2(CHILD_READ, 0);
            close(CHILD_READ);
            try_exec_agent(i);
        }
    } 
}


/** handler_exit(int code)
**       Takes an error code for handler errors and prints the appropriate
**       message. The program then exits with the given code.
*/
void handler_exit(int code) {
    switch(code) {
        case 1:
            fprintf(stderr, "Usage: handler mapfile maxsteps agentfile\n");
            break;
        case 2:
            fprintf(stderr, "Invalid maxsteps.\n");
            break;
        case 3:
            fprintf(stderr, "Unable to open map file.\n");
            break;
        case 4:
            fprintf(stderr, "Corrupt map.\n");
            break;
        case 5:
            fprintf(stderr, "Unable to open agent file.\n");
            break;
        case 6:
            fprintf(stderr, "Corrupt agents.\n");
            break;
        case 7:
            fprintf(stderr, "Error running agent.\n");
            break;
        case 10:
            fprintf(stderr, "Too many steps.\n");
            break;
        case 14:
            fprintf(stderr, "Exiting due to INT signal.\n");
            break;
        case 20:
            fprintf(stderr, "ARRGGGGGG\n");
            break;
        default:
            fprintf(stderr, "Error %d\n", code);
    }
    exit(code);
}


/** open_map_file(char* mapFile)
**      Takes a file path, and then opens it to read as a map file.
**      Returns the map as a 2D array of characters.
*/
char** open_map_file (char* mapFile, int* rows, int* cols) {
    FILE* f;
    char** map;
    char dimLine[7];
    //Attempt to open the given file.
    if ((f = fopen(mapFile, "r")) == NULL) {
        handler_exit(3);
    }
    //Get the dimensions and check they are valid
    fgets(dimLine, 7, f);
    if (sscanf(dimLine, "%d %d\n", rows, cols) != 2) {
        handler_exit(4);
    }
    if (*rows <= 0 || *cols <= 0) {
        handler_exit(4);
    }
        
    //Set up the 2D character array for the board
    map = make_string_array(*rows, *cols);
    //Validate the map file and fill the map
    fill_map(map, f, *rows, *cols);
    fclose(f);   
    return map;
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


/** fill_map(char** map, FILE* file, int rows, int cols)
**      Takes the map, file stream, and the dimensions to fill
**      the map with data from the file, and prepare it for the simulation.
**      If the file is not formatted correctly, the handler will exit with
**      error 4.
*/ 
void fill_map(char** map, FILE* file, int rows, int cols) {
    int xPos, yPos; //The current position of the cursor in the file
    char current; //The character at the current position
    xPos = 0;
    yPos = 0;
    current = '0';

    while(1) {
    //Check the number of rows aren't larger than the dimensions
        if(yPos > rows) {
            handler_exit(4);
        }
        xPos = 0;
        //For each row check all the columns
        while(1) {
            //Check the row isn't larger than the dimensions
            if(xPos > cols) {
                handler_exit(4);
            }
            current = fgetc(file);
            //If it was an end of line or file, leave the check row loop
            if(current == 10 || current == EOF) {
                break;
            }
            //Check the character is valid, and if so put it in the map
            if (current != '.' && current != '#') {
                handler_exit(4);
            }
            strcpy(&map[yPos][xPos], &current);
            xPos++;
        }
        //If the EOF was on a blank line, skip the row and EOF checks
        if(current == EOF && xPos == 0) {
            break;
        } else if(xPos != cols) {
            handler_exit(4);
        } else if(current == EOF) {
            break;
        }
        yPos++;
    }
    //Check the number of rows wasn't too small
    if (yPos != rows) {
        handler_exit(4);
    }
}

/** get_agents(char* agentPath, int rows, int cols)
**      Takes the path to the agent file and the dimensions of the map.
**      Fills the array of agent structs with data from the file,
**      and checks the data is valid
*/
void get_agents(char* agentPath, int rows, int cols) {
    FILE* f; //File stream for agent file
    char line[255]; //Line buffer
    //Try to open the file
    if ((f = fopen(agentPath, "r")) == NULL) {
        handler_exit(5);
    }

    int current = 0;
    //Go through the agent file, store the agent info on non comment lines
    //and check that the data is valid
    while(fgets(line, 80, f) != NULL) {
        if (line[0] != '#') {
            if(sscanf(line, "%d %d %c %[^\t\n]\n", &agents[current].currY, 
                    &agents[current].currX, &agents[current].position, 
                    agents[current].program) && agents[current].currX
                    <= cols && agents[current].currX >= 0 &&
                    agents[current].currY <= rows && 
                    agents[current].currY >= 0) {
                agents[current].pid = -2;
                current++;
            } else {
                handler_exit(6);
            }
        }
    }
}

/** get_num_agents(char* agentPath)
**      Takes the path to the agent file and works out how many lines are not
**      commented - tis is the number of agents in the file.
*/
void get_num_agents(char* agentPath) {
    FILE* f; //File stream for agent file
    //Try and open the agent file
    if ((f = fopen(agentPath, "r")) == NULL) {
        handler_exit(5);
    }
    char line[255]; //Line buffer
    numAgents = 0; //Number of agents
    //Loop over the agent file, counting the number of agent lines
    while(fgets(line, 80, f) != NULL) {
        if(line[0]!='#') {
            numAgents++;
        }
    }
    //If there was no agents, error
    if (numAgents < 1) {
        handler_exit(6);
    }
    fclose(f);
}

/** sig_handler(int signo)
**      Handler for SIGINT and SIGCHLD. Checks which signal it recieved,
**      and handles it appropriately.
*/
void sig_handler(int signo) {
    //Disable signal handling
    signal(SIGCHLD, SIG_IGN);
    signal(SIGINT, SIG_IGN);
    //Sentinel values for children
    int childExitStatus = -2;
    int childExitSignal = -2;
    char exitedAgent = ' ';
    
    switch(signo) {
        case SIGINT:
            handler_exit(14);
            break;
        case SIGCHLD:
            cleanup(&childExitStatus, &childExitSignal, &exitedAgent);
            if (childExitStatus != -2) {
                //Exit from status
                if (childExitStatus == 19) {
                    handler_exit(7);
                } else if (childExitStatus == 0) {
                    //re-enable signals and return to normal to processing
                    signal(SIGINT, sig_handler);
                    signal(SIGCHLD, sig_handler);                
                    break;
                } else {
                    fprintf(stderr, "Agent %c exited with status %d.\n", 
                            exitedAgent, childExitStatus);
                    exit(12);
                }
            } else {
                //Exit from signal
                fprintf(stderr, "Agent %c exited due to signal %d.\n", 
                        exitedAgent, childExitSignal);
                fflush(stderr);
                fflush(stdout);
                exit(13);
            }
    }
}

/** cleanup(int* childExitStatus, int* childExitSignal, char* exitedAgent)
**      Called in the signal handler if it recieves SIGCHLD.Takes pointers 
**      to ints for the child exit status and signal, and a char pointer for
**      the exited agent. The function sets these based on how and which agent
**      exited.
*/
void cleanup(int* childExitStatus, int* childExitSignal, char* exitedAgent) {
    //Kill all the children
    int childStatus;
    int exitStatus;
    int exitSignal;
    for (int i = 0; i < numAgents; i++) {
        //Check if the agent had started
        if (agents[i].pid != -1) {
            //Check if it is already exited and get the code/signal
            waitpid(agents[i].pid, &childStatus, WNOHANG);
            if(WIFEXITED(childStatus)) {
                //Get the exit status
                exitStatus = WEXITSTATUS(childStatus);
                if(exitStatus >= 0) {
                    *childExitStatus = exitStatus;
                    *exitedAgent = agents[i].position;
                }
            } else if(WIFSIGNALED(childStatus)) {
                //Get the signal
                exitSignal = WTERMSIG(childStatus);
                *childExitSignal = exitSignal;
                *exitedAgent = agents[i].position;
            } else {
                //Kill the child
                kill(agents[i].pid, SIGTERM);
            }
        }
    }

}

/** print_map(char** map, int rows, int cols)
**      Takes the map and its dimensions, printing it out with
**      the agent positions.
*/
void print_map(char** map, int rows, int cols) {
    //Make a copy of the map to edit the agent positions in
    char** mapCopy = make_string_array(rows, cols);
    for (int i = 0; i < rows; i++) {
        strncpy(mapCopy[i], map[i], cols);
    }
    //Put the agents on the map
    for(int i = 0; i < numAgents; i++) {
        mapCopy[agents[i].currY-1][agents[i].currX-1] = agents[i].position;
    }
    //Print the map
    for(int i = 0; i < rows; i++) {
        for(int j = 0; j < cols; j++) {
            printf("%c", mapCopy[i][j]);
        }
        printf("\n");
    }
    //Print a blank line at the end
    printf("\n");
}

/** check_move(char** map, int current, int rows, int cols)
**      Takes the map, its directions, and the current agent number.
**      Checks for collisions with other agents and walls.
*/
void check_move(char** map, int current, int rows, int cols) {
    //Check walling
    if (map[agents[current].currY-1][agents[current].currX-1] == '#') {
        fprintf(stderr, "Agent %c walled.\n", agents[current].position);
        exit(8);
    }
    //Check leaving the area
    if (agents[current].currY > rows || agents[current].currX > cols ||
            agents[current].currY < 1 || agents[current].currX < 1) {
        fprintf(stderr, "Agent %c walled.\n", agents[current].position);
        exit(8);
    }
    //Check collisions
    for (int i = 0; i < numAgents; i++) {
        if (i != current && agents[i].currX == agents[current].currX &&
                agents[i].currY == agents[current].currY) {
            fprintf(stderr, "Agent %c collided.\n", 
                    agents[current].position);
            exit(9);
        }
    }
}

/** agent_move(int current)
**      Takes the current agent number and facilitates I/O between the 
**      parent and agent for a turn, modifying the agent appropriately.
*/
void agent_move(int current) {
    char line[255];
    //Send the agent all the agent positions
    for (int i = 0; i < numAgents; i++) {
        fprintf(agents[current].output, "%d %d\n", agents[i].currY, 
                agents[i].currX);
        fflush(agents[current].output);
    }
    fgets(line, 80, agents[current].input);
    //Check if it is an error
    if (strlen(line) > 2) {
        //Wait for signal handler to handle it
        if(!strcmp(line, "Incorrect number of params.\n") || 
                !strcmp(line, "Invalid params.\n") || !strcmp(line, 
                "Dependencies not met.\n") || 
                !strcmp(line, "Handler communication breakdown.\n")) {
            //If it error message, let the signal handler handle it
            sleep(1);
        } else if (!strcmp(line, "execf\n")) {
            handler_exit(7);
        } else {
            fprintf(stderr, "Agent %c sent invalid response.\n",
                    agents[current].position);
            exit(11);
        }
    }
    switch (line[0]) {
        case 'N':
            agents[current].currY -= 1;
            break;
        case 'S':
            agents[current].currY += 1;
            break;
        case 'E':
            agents[current].currX += 1;
            break;
        case 'W':
            agents[current].currX -= 1;
            break;
        case 'H':
            //do nothing
            break;
        case 'D':
            printf("Agent %c succeeded.\n", agents[current].position);
            exit(0);
        default:
            fprintf(stderr, "Agent %c sent invalid response.\n",
                    agents[current].position);
            exit(11);
    }
}

/** try_exec_agent(int agentNo)
**      Takes the current agent number, and attempts to exec the agent.
**      On a failure, prints a code the handler knows how to handle
*/
void try_exec_agent(int agentNo) {
    //argv is 4 elements - program, up to 2 args (agents only need 1,
    //this is used to check errors) and NULL
    char *argv[4];
    argv[0] = strtok(agents[agentNo].program, " ");
    argv[1] = strtok(NULL, " ");
    argv[2] = strtok(NULL, " ");
    argv[3] = NULL;
    execvp(argv[0], argv);
    //If child is still running here something bad happened
    printf("execf\n");
    fflush(stdout);
    exit(19);
}

/** check_agent_name(int agentNo)
**      Takes an agent number and checks its program to see if it is a valid
**      agent program.
*/
void check_agent_name(int agentNo) {
    //Make a copy of the string and get the agent name with strtok
    char temp[strlen(agents[agentNo].program)];
    strcpy(temp, agents[agentNo].program);
    char* agentName = strtok(temp, " ");
    //Compare it to valid agent names
    if (strcmp(agentName, "./waller") && strcmp(agentName, "./slow") && 
            strcmp(agentName, "./slow2") && strcmp(agentName, "./fromfile") &&
            strcmp(agentName, "./simple")) {
        handler_exit(7);
    }
}