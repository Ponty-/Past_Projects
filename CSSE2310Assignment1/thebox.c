#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*The struct used for storing the position and direction of the ball*/
struct Ball{
	int x;
	int y;
	int dir;
};

FILE* openCheckFile(char* mapfile);
int checkLineLength(char* line, int len);
char** makeStringArray(int sizeX, int sizeY);
int** make2DIntArray(int sizeX, int sizeY);
void fillMap(char** map, int** teleMap, FILE* file, int rows, int cols);
void checkChar(char c, int** teleMap, int xPos, int yPos);
void setupTeleportMap(int** teleMap);
void checkTeleMap(int** teleMap);
void simulation(char** map, int** teleMap, int rows, int cols, int maxSteps);
void printMap(char** map, int rows, int cols);
void getSidePos(struct Ball* ball, int rows, int cols);
void checkPosition(struct Ball* ball, char** map, int** teleMap);
void clearSpace(struct Ball* ball, char** map);
void copyMap(char** src, char** dest, int rows);
void moveBall(struct Ball* ball);
void reflectorNWSE(struct Ball* ball);
void reflectorNESW(struct Ball* ball);
void reflector(struct Ball* ball);
void launchpad(struct Ball* ball);
void teleport(int** teleMap, char in, struct Ball* ball);

int main (int argc, char* argv[]){
	char* mapfile; /*The path to the selected map file*/
	char dimLine[7]; /*The string storing the top line of the file*/
	FILE* f; /*The file stream for the selected file*/
	char** map; /*The 2D array of chars for the board*/
	int** teleMap; /*The 2D int array for the mapping of teleporters*/
	int rows, cols, maxSteps; /*The dimensions and maximum steps*/
	
	if (argc == 1 || argc > 3) {
		/*Incorrect number of arguments*/
		fprintf(stderr, "Usage: thebox mapfile [maxsteps]\n");
		exit(1);
	} else if (argc == 2) {
		/*1 argument - set the mapfile*/
		mapfile = argv[1];
		maxSteps = 10;
	} else if (argc == 3) {
		/*2 arguments - set mapfile and maxsteps*/
		mapfile = argv[1];
		maxSteps = atoi(argv[2]);
		/*Check if maxsteps is valid*/
		if (maxSteps < 1 || maxSteps >= 1000) {
			fprintf(stderr, "Bad max steps.\n");
			exit(2);
		}
	}
	
	/*Open the map file*/
	f = openCheckFile(mapfile);
	/*Get the dimensions and check they are valid*/
	fgets(dimLine, 7, f);
	if (sscanf(dimLine, "%d %d\n", &rows, &cols) != 2){
		fprintf(stderr, "Bad map dimensions.\n");
		exit(4);
	}
	if (rows <= 0 || cols <= 0) {
		fprintf(stderr,"Bad map dimensions.\n");
		exit(4);
	}
	
	/*Set up the 2D character array for the board*/
	map = makeStringArray(rows, cols);
	/*Set up the 2D int array for the teleporters*/
	teleMap = make2DIntArray(26,2);
	setupTeleportMap(teleMap);
	/*Populate the map with file data*/
	fillMap(map, teleMap, f, rows, cols);
	/*Transfer control to simuation function*/
	simulation(map, teleMap, rows, cols, maxSteps);
	return 0;
}

/*
** openCheckFile(char* mapfile)
**	Returns the file stream for the given file path.
**	If the attempt to open the file fails, exit the
**	program with status 3 and the appropriate message.
*/
FILE* openCheckFile (char* mapfile) {
	FILE* f;
	/*Attempt to open the given file. If it doesn exist, show the
	 error message and exit the program */
	if ((f = fopen(mapfile, "r")) == NULL) {
		fprintf(stderr, "Missing map file.\n");
		exit(3);
	}
	
	return f;
}

/*
** makeStringArray(int sizeX, int sizeY)
**	Allocates memory for a string array with the given dimensions
**	and returns it.
*/ 
char** makeStringArray(int sizeX, int sizeY) {
	char** array;
	int i;
	array = (char**)malloc(sizeX * sizeof(char*));
	for(i = 0; i < sizeX; i++) {
		array[i] = (char*)malloc(sizeY * sizeof(char));
	}
	return array;
}

/*
** make2DIntArray(int sizeX, int sizeY)
**	Allocates memory for a two-dimensional integer 
**	array with the given dimensions	and returns it.
*/ 
int** make2DIntArray(int sizeX, int sizeY) {
	int** array;
	int i;
	array = (int**)malloc(sizeX * sizeof(int*));
	for(i = 0; i < sizeX; i++) {
		array[i] = (int*)malloc(sizeY * sizeof(int));
	}
	return array;
}

/*
** setupTeleportMap(int** teleMap)
**	Takes the teleport map and sets it up for later use.
**	The teleport map is 26 elements (one for each letter of the alphabet)
**	long, and there is an array of 2 ints for each element, indicating
**	the x and y position of that letter on the map/board. This function
**	for the initial setup sets them all to -1, which later parts of the
**	program use to see if that letter exists in the map.
*/ 
void setupTeleportMap(int** teleMap){
	int i, j;
	for (i = 0; i < 26; i++) {
		for(j = 0; j < 2; j++) {
			teleMap[i][j] = -1;
		}
	}
}

/*
** fillMap(char** map, int** teleMap, FILE* file, int rows, int cols)
**	Takes the map, teleport map, file stream, and the dimensions to fill
**	the map with data from the file, and prepare it for the simulation.
**	If the file is not formatted correctly, errors 5, 6 or 7 could occur
**	during this function, exiting the program.
*/ 
void fillMap(char** map, int** teleMap, FILE* file, int rows, int cols) {
	int xPos, yPos; /*The current position of the cursor in the file*/
	char current; /*The character at the current position*/
	xPos = 0;
	yPos = 0;
	current = '0';

	while(1) {
		/*Check the number of rows aren't larger than the dimensions*/
		if(yPos > rows) {
			fprintf(stderr,"Map file is the wrong size.\n");
			exit(6);
		}
		xPos = 0;
		/*For each row check all the columns*/
		while(1) {
			/*Check the row isn't larger than the dimensions*/
			if(xPos > cols) {
				fprintf(stderr,"Map file is the wrong size.\n");
				exit(6);
			}
			current = fgetc(file);
			/*If it was an end of line or file, leave the check row loop*/
			if(current == 10 || current == EOF) {
				break;
			}
			/*Check the character is valid, and if so put it in the map*/
			checkChar(current, teleMap, xPos, yPos);
			strcpy(&map[yPos][xPos],&current);
			xPos++;
		}
		/*If the EOF was on a blank line, skip the row and EOF checks*/
		if(current == EOF && xPos == 0) {
			break;
		} else if(xPos != cols) {
			fprintf(stderr,"Map file is the wrong size.\n");
			exit(6);
		} else if(current == EOF) {
			break;
		}
		yPos++;
	}
	/*Check the number of rows wasn't too small*/
	if (yPos != rows) {
		fprintf(stderr,"Map file is the wrong size.\n");
		exit(6);
	}
	/*Check the teleport map is valid*/
	checkTeleMap(teleMap);
}

/*
** checkChar(char c, int** teleMap, int xPos, int yPos)
**	Takes a character, the teleporter map, and the character's coordinates.
**	Checks if the character is valid. If it isn't, exit with status 5
**	and the appropriate message. If the character is a teleporter, add it
**	to the teleport map. If it already exists in the teleport map, exit
**	with status 7 and the appropriate message.
*/ 
void checkChar(char c, int** teleMap, int xPos, int yPos) {
	/*Check for invalid chars, modify teleport array as appropriate*/
	switch(c) {
		case '.':
			break;
		case '=':
			break;
		case '/':
			break;
		case '\\':
			break;
		case '@':
			break;
		default:
			/*Check if the character is a capital letter, ie a teleporter*/
			if (c < 65 || c > 90) {
				fprintf(stderr,"Bad map char.\n");
				exit(5);
			} else{
				/*Set the coordinates for the teleporter*/
				if (teleMap[c - 65][0] == -1 && teleMap[c - 65][0] == -1) {
					teleMap[c - 65][0] = xPos;
					teleMap[c - 65][1] = yPos;
				} else {
				/*If it already exists in the teleport map, exit with 
				status 7*/
					fprintf(stderr, "Missing letters.\n");
					exit(7);
				}
			}
	}
}

/*
** checkTeleMap(int** teleMap)
**	Takes the teleport map and checks that it is valid. If all the letters
**	do not follow on or there is only a single letter, exit with status 7
**	and the appropriate message.
*/ 
void checkTeleMap(int** teleMap){
	int i, previous, beenChar, count;
	previous = 0; /*Stores whether the previous letter was in the map*/
	beenChar = 0; /*Stores if there has been letters in the map thus far*/
	count = 0; /*Stores the number of letters in the map*/

	for (i = 0; i < 26; i++) {
		/*If there has been a gap, exit with status 7*/
		if (previous == 0 && beenChar == 1 && teleMap[i][0] != -1) {
			fprintf(stderr, "Missing letters.\n");
			exit(7);
		}
		/*Check if this letter has been set, ie exists in the map*/
		if (teleMap[i][0] != -1) {
			previous = 1;
			beenChar = 1;
			count+=1;
		} else {
			previous = 0;
		}
	}
	/*Check that if teleporters exist there is more than 1*/
	if (count == 1) {
		fprintf(stderr, "Missing letters.\n");
		exit(7);
	}
}

/*
** simulation(char** originalMap, int** teleMap, int rows, int cols,
** int maxSteps)
**	Takes the map of the board, the teleport map, the rows and columns
**	on the board, and the maximum number of steps. The simulation function
**	runs the simulation after the file input and checking is complete.
**	It displays the map, reads the side position from the user, plays and
**	displays the simulation.
*/ 
void simulation(char** originalMap, int** teleMap, int rows, int cols,
 int maxSteps) {
	int i; 
	struct Ball ball; /*The ball, storing the coordinates and direction*/
	char** map; /*The map used for the simulation*/
	/*Allocate memory for the board*/
	map = makeStringArray(rows, cols);
	/*Loop forever*/
	while(1) {
		/*Display the selected map*/
		printMap(originalMap,rows,cols);
		/*Set up the starting state of the ball*/
		getSidePos(&ball, rows, cols);
		/*Copy the original map*/
		copyMap(originalMap, map, rows);
		for (i = 0; i < maxSteps; i++) {
			/*For each step, check what kind of space the ball is on*/
			checkPosition(&ball, map, teleMap);
			/*Move the ball 1 space*/
			moveBall(&ball);
			/*Print the current state of the map*/
			printMap(map,rows,cols);
			/*Check that it's not outside the area*/
			if (ball.x < 0 || ball.y < 0 || ball.x >= cols || 
			ball.y >= rows) {
				break;
			}
		}
		/*Simulation finished from reaching max steps or exiting the area*/
		printf("End of simulation.\n");
	}
}

/*
** printMap(char** map, int rows, int cols)
**	Takes the map of the board and its dimensions, and prints out the
**	current state of the map.
*/ 
void printMap(char** map, int rows, int cols) {
	int i, j;
	/*Print the map*/
	for(i = 0; i < rows; i++){
		for(j = 0; j < cols; j++){
			printf("%c", map[i][j]);
		}
		printf("\n");
	}
	/*Print a blank line at the end*/
	printf("\n");
}

/*
** getSidePos(struct Ball* ball, int rows, int cols)
**	Reads the side and position from the user (stdin), checks if it is valid
**	using the given map dimensions and sets up the position and direction
**	of the ball appropriately. If an invalid position is entered, prompt
**	again. If the end of stdin is encountered, exit the program with status 0.
*/ 
void getSidePos(struct Ball* ball, int rows, int cols) {
	char sidePos[4]; /*The string with the data to read*/
	char side, inChar; /*The read side and current character from stdin*/
	int pos, i; /*The read side position*/
	/*Loop until valid input is given or the program is exited*/
	while(1) {
		/*Prompt for input*/
		printf("(side pos)>");
		i = 0;
		while(1) {
			inChar = getchar();	
			/*Check for ctrl-d (end of stdin)*/
			if (inChar <= 0) {
				exit(0);
			}
			/*Check for end of input*/
			if (inChar == 10) {
				break;
			}
			/*Otherwise set up the string to read side pos from*/
			if (i < 4) {
				sidePos[i] = inChar;
			}
			i++;
		}
		/*Get the side pos in the format (single character)
		(positive number up to 3 digits long)*/
		if (sscanf(sidePos,"%c%d", &side, &pos)) {
			/*Set up the ball appropriately for the given input*/
			if ((side == 'N')&&pos >= 1 && pos <= cols) {
				ball->y = 0;
				ball->x = pos-1;
				ball->dir = 1;
				break;
			}
			if ((side == 'S')&& pos >= 1 && pos <= cols) {
				ball->y = rows-1;
				ball->x = pos-1;
				ball->dir = 2;
				break;
			}
			if ((side == 'E') && pos >= 1 && pos <= rows) {
				ball->y = pos-1;
				ball->x = cols-1;
				ball->dir = 3;
				break;
			}
			if ((side == 'W')&&pos >= 1 && pos <= rows) {
				ball->y = pos-1;
				ball->x = 0;
				ball->dir = 4;
				break;
			}
		}
	}
}

/*
** checkPosition(struct Ball* ball, char** map, int** teleMap)
**	Given the ball, map and teleport map, determine what kind of space the
**	ball is on and take the appropriate action.
*/ 
void checkPosition(struct Ball* ball, char** map, int** teleMap) {
	switch(map[ball->y][ball->x]) {
		case '/':
			reflectorNESW(ball);
			break;
		case '\\':
			reflectorNWSE(ball);
			break;
		case '=':
			reflector(ball);
			break;
		case '@':
			launchpad(ball);
			break;
		default:
			/*If it is a capital letter it is a teleporter*/
			if (map[ball->y][ball->x] >= 65 && map[ball->y][ball->x] <= 90) {
				teleport(teleMap, map[ball->y][ball->x], ball);
			} else {
				/*Otherwise it's an empty space*/
				clearSpace(ball, map);
			}
	}
}

/*
** moveBall(struct Ball* ball)
**	Moves the ball 1 space in the current direction.
*/ 
void moveBall(struct Ball* ball) {
	/*Continue moving*/
	switch(ball->dir) {
		case 1:
			ball->y++;
			break;
		case 2:
			ball->y--;
			break;
		case 3:
			ball->x--;
			break;
		case 4:
			ball->x++;
		break;
	}
}

/*
** clearSpace(struct Ball* ball, char** map)
**	Called when the ball lands on an empty space (without launch pads,
**	reflectors or teleporters) and updates it with the number of times it
**	has been over that space
*/ 
void clearSpace(struct Ball* ball, char** map) {
	/*if it is '.', make it 1*/
	if (map[ball->y][ball->x] == '.') {
		map[ball->y][ball->x] = '1';
	}
	/*if it is 1-8, increment it, otherwise leave it*/
	else if (map[ball->y][ball->x] >= '1' && map[ball->y][ball->x] < '9') {
		map[ball->y][ball->x]++;
	}
	
}

/*
** reflectorNESW(struct Ball* ball)
**	Changes the ball's direction as appropriate for a NESW reflector.
*/ 
void reflectorNESW(struct Ball* ball) {
	/*change direction appropriately*/
	switch(ball->dir) {
		case 1:
			ball->dir = 3;
			break;
		case 2:
			ball->dir = 4;
			break;
		case 3:
			ball->dir = 1;
			break;
		case 4:
			ball->dir = 2;
		break;
	}
}

/*
** reflectorNWSE(struct Ball* ball)
**	Changes the ball's direction as appropriate for a NWSE reflector.
*/ 
void reflectorNWSE(struct Ball* ball) {
	/*change direction appropriately*/
	switch(ball->dir) {
		case 1:
			ball->dir = 4;
			break;
		case 2:
			ball->dir = 3;
			break;
		case 3:
			ball->dir = 2;
			break;
		case 4:
			ball->dir = 1;
		break;
	}
}

/*
** reflector(struct Ball* ball)
**	Reverses the ball's direction.
*/ 
void reflector(struct Ball* ball) {
	/*reverse direction*/
	switch(ball->dir) {
		case 1:
			ball->dir = 2;
			break;
		case 2:
			ball->dir = 1;
			break;
		case 3:
			ball->dir = 4;
			break;
		case 4:
			ball->dir = 3;
		break;
	}
}

/*
** launchpad(struct Ball* ball)
**	Launches the ball 4 cells forward in the current direction.
*/ 
void launchpad(struct Ball* ball) {
	/*Jump forward to land on the 5th cell*/
	switch(ball->dir) {
		case 1:
			ball->y += 4;
			break;
		case 2:
			ball->y -= 4;
			break;
		case 3:
			ball->x -= 4;
			break;
		case 4:
			ball->x += 4;
		break;
	}
}

/*
** copyMap(char** src, char** dest, int rows)
**	Copies the data from one string array to another string array up to the
**	number of rows given.
*/ 
void copyMap(char** src, char** dest, int rows) {
	int i;
	for (i = 0; i < rows; i++) {
		strcpy(dest[i], src[i]);
	}
}

/*
** teleport(int** teleMap, char in, struct Ball* ball)
**	Takes the teleport map, the character the ball landed on, and the ball
**	itself, and handles teleportation. Jumps the ball to the coordinates of
**	the next letter, or if it landed on the last letter go back to the first.
*/ 
void teleport(int** teleMap, char in, struct Ball* ball) {
	int i;
	/*If the next letter has no coordinates or the ball landed on Z,
	go back to the first letter*/
	if (in == 90 || teleMap[in - 64][0] == -1) {
		for (i = 0; i < 26; i++) {
			if(teleMap[i][0] != -1){
				ball->x = teleMap[i][0];
				ball->y = teleMap[i][1];
				break;
			}
		}
	} else {
		/*Otherwise, go to the next teleporter*/
		ball->x = teleMap[in-64][0];
		ball->y = teleMap[in-64][1];
	}
}