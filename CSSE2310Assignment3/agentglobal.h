void try_get (char* output, int length);
void agent_exit (int code);
char** get_map_data (int rows, int cols);
char* get_agent_data (int* numAgents, int* agentNo, int* rows, int* cols);
char basic_move (char dir, char** map, int rows, int cols, int pos[2]);