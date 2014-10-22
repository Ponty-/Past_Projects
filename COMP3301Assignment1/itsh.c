#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/fcntl.h>
#include <errno.h>
#define MAXDIR 1024
#define MAXLINE 128

typedef struct Command {
	char cmdString[MAXLINE]; //The full command
	char** cmdArgs; //The command split by spaces
	FILE* redirectIn; //The given file for the input redirection
	FILE* redirectOut; //The given file for the output redirection
	int background; //1 = run process in background
	int numArgs; //The number of arguments for the process
	pid_t pid; //The pid of the child program once it is forked
} Command;

typedef struct Background {
	pid_t pid; //the pid of the process
	int running; //whether the process is running
} Background;

Background* background;
int numBackground;

void prompt();
void interpret_command(char* line);
char** split_string(char* string, int numSplits, char* splitOn);
int count_char (char* string, char countOn);
void setup_command (Command* cmd);
void run_command (Command* cmd);
void run_pipe_commands (Command* cmd1, Command* cmd2);
void exec_child(Command* cmd);
void add_background(pid_t pid);
void background_handler(int sig);
int wait_status (pid_t pid, int flags);

int main(int argc, char* argv[]){
	char buf[MAXLINE + 3];//Extra 3 chars used for testing long lines
	FILE* input;
	
	if (argc > 2) {
		fprintf(stderr, "Usage: itsh [script]\n");
		exit(1);
	}
	//Set up read - either stdin or script file
	if (argc == 2) {
		//Open file
		input = fopen(argv[1], "r");
		if (!input) {
			fprintf(stderr, "File %s not found\n", argv[1]);
			exit(2);
		}
	} else {
		input = stdin;
	}
	
	//Set up signal handling
	//Ignore ctrl-c signals for parent and background children
	signal(SIGINT, SIG_IGN);
	//Use handler for child signal
	signal(SIGCHLD, background_handler);
	
	prompt();
	while (fgets(buf, MAXLINE + 3, input) != NULL) {
		if (strlen(buf) > 129) {
			//Line too long
			fprintf(stderr, "Line too long - input limited to %d characters.\n", MAXLINE);
			//Read until newline - finish off too long line
			do {
				fgets(buf, MAXLINE, input);
			} while (!count_char(buf, '\n'));
			prompt();
			continue;
		}
		//Cut off the newline character (for a properly sized command)
		buf[strlen(buf) - 1] = '\0';
		if (strlen(buf) > 0) {
			interpret_command(buf);
		}
		prompt();
	}
	
	return(0);
}

/** prompt()
**		Print the prompt based on the current directory.
*/
void prompt () {
	char cwd[MAXLINE];
	
	if (getcwd(cwd, sizeof(cwd)) != NULL)
		printf("%s# ", cwd);
    else
		perror("Error getting current directory: ");
}

/** interpret_command(char* line)
**		Take the input from stdin or a script file and react appropriately.
**		This means executing a command, changing the directory or exiting.
*/
void interpret_command(char* line) {
	if (line[0] == '#') { 
		/*Comment - do nothing*/
		return;
	}
	/*Check for pipes*/
	else if (count_char(line, '|') > 0) {
		/*Split the two commands*/
		char** pipeCmds = split_string(line, 1, "|");
		//Make two commands based on [0] and [1] of pipeCmds
		Command cmd1, cmd2;
		//Change last space of pipeCmds to null
		pipeCmds[0][strlen(pipeCmds[0]) - 1] = '\0';
		strcpy(cmd1.cmdString, pipeCmds[0]);
		//Copy from 2nd char, first char is space
		strcpy(cmd2.cmdString, pipeCmds[1] + 1);
		setup_command(&cmd1);
		setup_command(&cmd2);
		//Run the commands
		run_pipe_commands(&cmd1, &cmd2);
	}
	/*Check for in-built command 'exit'*/
	else if (!strcmp(line, "exit") || !strncmp(line, "exit ", 5)) {
		printf("Exiting...\n");
		exit(0);
	}
	/*Check for in-built command 'cd' - no arguments*/
	else if (!strcmp("cd", line) || !strcmp("cd ", line)) {
		chdir(getenv("HOME")); //Change to HOME directory
	}
	//Check for in-built command 'cd' with arguments
	else if (!strncmp("cd ", line, 3)) {
		char* cdDest = split_string(line, count_char(line, ' '), " ")[1];
		chdir(cdDest);
	}
	//Run command normally
	else {
		Command cmd;
		strcpy(cmd.cmdString, line);
		//Set up the command
		setup_command(&cmd);
		run_command(&cmd);
	}
}

/** count_char (char* string, char countOn)
**		Return the number of times the given char appears in the given string.
*/
int count_char (char* string, char countOn) {
	int charCount = 0;
	
	/*count number of times character exists*/
	int len = strlen(string);
	for (int i = 0; i < len; i++) {
		if (string[i] == countOn) {
			charCount++;
		}
	}
	
	return charCount;
}

/** split_string(char* string, int n_splits, char* splitOn)
**		Split the string a number of times on the given character.
**		Return the result as a string array.
*/
char** split_string(char* string, int n_splits, char* splitOn) {
	/*allocate memory for new split string*/
	char** split = (char**)malloc(n_splits + 1 * sizeof(char*));
	for(int i = 0; i < n_splits + 1; i++) {
		split[i] = (char*)malloc(MAXLINE * sizeof(char));
	}
	
	/*Start splitting the string*/
	char* tk = strtok(string, splitOn);
	split[0] = tk;
	
	for(int i = 1; i < n_splits + 1; i++) {
		tk = strtok(NULL, splitOn);
		split[i] = tk;
	}
	return split;
}

/** setup_command(Command* cmd)
**		Take a command struct with the command line already set.
**		Set the arguments, redirects and whether to run in the background.
*/
void setup_command(Command* cmd) {
	cmd->redirectIn = NULL;
	cmd->redirectOut = NULL;
	cmd->background = 0;
	//Split command by spaces
	int numSpaces = count_char(cmd->cmdString, ' ');
	cmd->cmdArgs = split_string(cmd->cmdString, numSpaces, " ");
	//Get the input and output redirects, storing the index of the earliest arrow word
	int lastArg = numSpaces + 1;
	for (int i = 1; i < numSpaces + 1; i++) {
		if (!strcmp("<", cmd->cmdArgs[i])) {
			if (lastArg > i) {
				lastArg = i;
			}
			i++; //Increment 'i' to the file
			//Attempt to open the file for reading (input)
			cmd->redirectIn = fopen(cmd->cmdArgs[i], "r");
			//Check for errors
			if (!cmd->redirectIn) {
				perror("Redirect input error:");
			}
		} else if (!strcmp(">", cmd->cmdArgs[i])) {
			if (lastArg > i) {
				lastArg = i;
			}
			i++; //Increment 'i' to the file
			//Attempt to open the file for writing (output)
			cmd->redirectOut = fopen(cmd->cmdArgs[i], "w");
			//Check for errors
			if (!cmd->redirectOut) {
				perror("Redirect output error:");
			}
		}
	}
	//Get whether this is to be a background process
	if (!strcmp(cmd->cmdArgs[numSpaces], "&")){
		cmd->background = 1;
		
		if (lastArg > numSpaces) {
				lastArg = numSpaces;
		}
	}
	//Set up the argument list for execing (last element null)
	cmd->numArgs = lastArg;
}

/** run_command(Command* cmd)
**		Take a filled-out Command struct and run the command as specified.
*/
void run_command (Command* cmd) {
	//Fork
	cmd->pid = fork();
	if(cmd->pid) {
		//Parent - add to background process list or wait
		if (cmd->background) {
			//Add to running background processes list
			add_background(cmd->pid);
		} else {
			wait_status(cmd->pid, 0);
		}
	} else {
		//Child
		//Input redirection (file or dev/null for background)
		if (cmd->background && !cmd->redirectIn) {
			dup2(open("/dev/null", O_RDONLY), 0);
		} else if (cmd->redirectIn) {
			dup2(fileno(cmd->redirectIn), STDIN_FILENO);
			fclose(cmd->redirectIn);
		}
		//Output redirection
		if (cmd->redirectOut) {
			dup2(fileno(cmd->redirectOut), STDOUT_FILENO);
			fclose(cmd->redirectOut);
		}
		//Reset normal signal handling
		signal(SIGINT, SIG_DFL);

		//Exec the child
		exec_child(cmd);
	}
}

/** exec_child(Command* cmd)
**		Take a filled-out Command struct and exec the specified process.
*/
void exec_child(Command* cmd) {
	char* args[cmd->numArgs + 1];
	for (int i = 0; i < cmd->numArgs; i++) {
		args[i] = cmd->cmdArgs[i];
	}
	args[cmd->numArgs] = NULL;
	
	execvp(args[0], args);
	fprintf(stderr, "'%s' failed - %s\n", cmd->cmdArgs[1], strerror(errno));
	exit(3);
}

/** run_pipe_commands (Command* cmd1, Command* cmd2)
**		Take two commands and run them, piped together.
*/
void run_pipe_commands (Command* cmd1, Command* cmd2) {
	int fds[2];
	
	//Make pipe between commands - 0 is read end, 1 is write end
	pipe(fds);
	
	//Fork first command
	cmd1->pid = fork();
	if(!cmd1->pid) {
		//Child
		//Do input redirection
		if (cmd1->redirectIn) {
			dup2(fileno(cmd1->redirectIn), STDIN_FILENO);
			fclose(cmd2->redirectIn);
		}
		//Set stdout to write end of pipe
		dup2(fds[1] , 1);
		
		//Close files
		close(fds[0]);
		close(fds[1]);
		//Exec
		exec_child(cmd1);
	}
	
		
	//Fork second command
	cmd2->pid = fork();
	if(!cmd2->pid) {
		//Child
		//Do output redirection
		if (cmd2->redirectOut) {
			dup2(fileno(cmd2->redirectOut), STDOUT_FILENO);
			fclose(cmd2->redirectOut);
		}
		//Set stdin to read end of pipe
		dup2(fds[0], 0);
		close(fds[0]);
		close(fds[1]);
		//Exec
		exec_child(cmd2);
	}
	
	//Close files
	close(fds[0]);
	close(fds[1]);
	
	
	int status[2];
	//Wait on first process - if it fails, terminate the second
	if (wait_status(cmd1->pid, 0) == 1) {
		//Error occured - terminate
		kill(cmd2->pid, SIGINT);
		//Not a background command so we need to reap cmd2.
		//Don't want to display a message so don't use that command.
		waitpid(cmd2->pid, NULL, 0);
	}
	
	//Wait on second process
	waitpid(cmd2->pid, &status[1], 0);
}

/** add_background(pid_t pid)
**		Add the given process id to the background process list.
**		If the list has a process that is not running, replace it.
*/
void add_background(pid_t pid) {
	//If there is no background list entries, start the list
	if(!numBackground) {
		background = (Background*)malloc(sizeof(Background));
		background[0].pid = pid;
		background[0].running = 1;
		numBackground++;
		return;
	}
	
	//Check for non-running process ids to replace
	for (int i = 0; i < numBackground; i++) {
		if (!background[i].running) {
			background[i].pid = pid;
			background[i].running = 1;
			return;
		}
	}
	
	//Otherwise add another list entry
	numBackground++;
	background = (Background*)realloc(background, (sizeof(Background) * (numBackground)));
	background[numBackground - 1].pid = pid;
	background[numBackground - 1].running = 1;
}

/** background_handler(int sig)
**		Handler for SIGCHLD. Find the newly closed process,
**		and remove it to the background process list.
*/
void background_handler(int sig) {
	int status;
	//Loop over background process list to find the closed process
	for (int i = 0; i < numBackground; i++) {
		//Use waitpid with nohang to determine if process has closed
		if (background[i].running) {
			status = wait_status(background[i].pid, WNOHANG);
			if (status != 2) {
				background[i].running = 0;
			}
		}
	}
}

/** wait_status (pid_t pid, int flags)
**		Take the pid of the process to wait on and any flags (ie WNOHANG).
**		Return 0 for a successful exit, 1 for failed run
**		and 2 for process still running.
*/
int wait_status (pid_t pid, int flags) {
	int status;
	
	waitpid(pid, &status, flags);
	if (WIFEXITED(status)) {
		if(WEXITSTATUS(status) != 0) {
			return 1; //Return 1 for failed run
		} else {
			return 0; //Return 0 for successful exit
		}
	} else if (WIFSIGNALED(status)) {
		return 1; //Return 1 for failed run
	}

	return 2; //Return 2 for still running	
}