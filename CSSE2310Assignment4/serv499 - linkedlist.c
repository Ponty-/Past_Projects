#include "global499.h"
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <netdb.h>
#include <pthread.h>

#define MAXHOSTNAMELEN 128

typedef struct {
    FILE* sockRead;
    FILE* sockWrite;
    char name[50];
    Card hand[13];
    int tricks;
} Player;

typedef struct {
    Player players[4];
    int numPlayers;
    char name[50];
    Card bid;
    int team1Score;
    int team2Score;
    int currentDeck;
} Game;

typedef struct {
    Card **decks;
    int numDecks;
} Decks;

struct Node{
    Game game;
    struct Node *next;
};

void* game_thread(void* arg);
int open_listen(int port);
void process_connections(int fdServer, char* greeting);
void serv_exit(int code);
void open_decks(char* deckfile);
void new_player(Player player, char* game);
int sort_players(const void *a, const void *b);
void send_info (Game game, int targets, char* message);
void deal_cards(Game *game);
int bidding(Game *game);
int trick(Game *game, int lead, char trumps);
void score_hand(Game *game);
void read_client(char* str, int size, Game game, int client);

struct Node *games; //Pointer to start of linked list of games
Decks decks;

int main(int argc, char* argv[]) {
    int portnum;
    int fdServer;
    if(argc != 4) {
        serv_exit(1);
    }
    //Get the port number from the arguments and check range 
    portnum = atoi(argv[1]);
    if (portnum == 0) {
        serv_exit(4);
    }
    if(portnum < 1024 || portnum > 65535) {
        serv_exit(5);
    }
    //Set the greeting
    char* greeting = argv[2];
    //Open decks
    open_decks(argv[3]);
    //Set the games list to null
    games = NULL;
    //Start listening on the given port
    fdServer = open_listen(portnum);
    //Process connections on the given port
    process_connections(fdServer, greeting);
    return 0;
}

/** process_connections(int fdServer, char* greeting)
**        Processes connections on the given fd. Gives clients a greeting.
*/
void process_connections(int fdServer, char* greeting) {
    int fd;
    struct sockaddr_in fromAddr;
    socklen_t fromAddrSize;
    int error;
    char hostname[MAXHOSTNAMELEN];
    //pthread_t threadId;

    while(1) {
        fromAddrSize = sizeof(struct sockaddr_in);
        /* Block, waiting for a connection request to come in and accept it.
        * fromAddr structure will get populated with the address of the client
        */
        fd = accept(fdServer, (struct sockaddr*)&fromAddr,  &fromAddrSize);
        if(fd < 0) {
            serv_exit(8);
        }
     
        /* Lookup hostname of client and print it out (along with port num) */
        error = getnameinfo((struct sockaddr*)&fromAddr, fromAddrSize, hostname,
                MAXHOSTNAMELEN, NULL, 0, 0);
        if(error) {
            serv_exit(8);
        } else {
            //Accepted a new connection, build player struct
            Player player;
            player.sockRead = fdopen(fd, "r");
            player.sockWrite = fdopen(fd, "w");
			setvbuf(player.sockWrite, NULL, _IONBF, 512);
            fprintf(player.sockWrite, "M%s\n", greeting);
			//fflush(player.sockWrite);
            fgets(player.name, 50, player.sockRead);
            //Drop the \n off the end
            player.name[strlen(player.name)-1] = '\0';
            //Get the game they are trying to join
            char gameName[50];
            fgets(gameName, 50, player.sockRead);
            //Drop the \n off the end
            gameName[strlen(gameName)-1] = '\0';
            //If pending game exists, add them, otherwise make new game
            new_player(player, gameName);
        }
    }
}

/** new_player(Player player, char* game)
**        Takes a player struct and a game name and adds them to a game.
*/
void new_player(Player player, char* game) {
    pthread_t threadId;
    //If games list is empty, create a game and add the player to it
    if (games == NULL){
        games = malloc(sizeof(struct Node));
        games->next = 0;
        games->game.players[0] = player;
        games->game.numPlayers = 1;
        strcpy(games->game.name, game);
		printf("Player %s created first game %s (%d/4 players)\n", player.name, games->game.name, games->game.numPlayers);
        return;
    }
    //Check if game exists with that name and is not full
    //Loop over games list until match is found
    int match = 0;
    struct Node *current;
	current	= games;
    while (current != 0) {
        //Found a game with space and matching name
		printf("Searching - current game name:%s Game to join:%s\n", current->game.name, game);
        if (!strcmp(current->game.name, game) && current->game.numPlayers != 4) {
			printf("Game matched\n");
            match = 1;
            break;
        }
        current = current->next;
    }
    //If there was a match, add player to game
    if (match) {
        current->game.players[current->game.numPlayers] = player;
        current->game.numPlayers++;
        if (current->game.numPlayers == 4) {
            //Start a new game with current->game
            Game startGame;
            startGame = current->game;
            pthread_create(&threadId, NULL, game_thread, (void*)&startGame);
            pthread_detach(threadId);
        }
    } else {
        //Otherwise, make a new game
		printf("Making new game - game to create:%s\n", current->game.name, game);
        current = malloc(sizeof(struct Node));
        current->next = 0;
        current->game.players[0] = player;
        current->game.numPlayers = 1;
        strcpy(current->game.name, game);
    }
	printf("Player %s joined game %s (%d/4 players)\n", player.name, current->game.name, current->game.numPlayers);
}

/** game_thread(void* arg)
**        Takes a game struct cast to a void pointer and runs the game.
*/
void * game_thread(void* arg) {
	printf("Game thread started\n");
    int lead;
    char buffer[1024];
    Game game;
    game = *(Game*)arg;
    //Work out teams
    qsort(game.players, 4, sizeof(Player), sort_players);
    //Set scores
    game.team1Score = 0;
    game.team2Score = 0;
    //Inform clients of teams
    sprintf(buffer, "MTeam1: %s, %s\nMTeam2: %s, %s\n", game.players[0].name,
    game.players[2].name, game.players[1].name, game.players[3].name);
    printf("Sending info\n");
    send_info(game, 0, buffer);
    
    //While scores are less than |499|
    while(abs(game.team1Score) < 499 && abs(game.team2Score) < 499){
		printf("--------------------- Starting hand for game %s---------------------\n", game.name);
        //Reset tricks
        for(int i = 0; i < 4; i++) {
            game.players[i].tricks = 0;
        }
        //Deal cards
        deal_cards(&game);
		printf("Dealt cards\n");
        //Do my bidding
        lead = bidding(&game);
		printf("Lead: %d\n", lead);
		printf("Done bidding\n");
        //Inform users of bidding result
        sprintf(buffer, "T%c%c\n", game.bid.rank, game.bid.suit);
        send_info(game, 0, buffer);
        //Run 13 tricks
        for (int i = 0; i < 13; i++) {
            lead = trick(&game, lead, game.bid.suit);
            sprintf(buffer, "M%s won\n", game.players[lead].name);
            send_info(game, 0, buffer);
        }
        //Calculate and print scores
		printf("Scoring hand\n");
        score_hand(&game);
        sprintf(buffer, "MTeam 1=%d, Team 2=%d\n", game.team1Score, game.team2Score);
        send_info(game, 0, buffer);
		Card bid;
		game.bid = bid;
        //Increment decks
        game.currentDeck = (game.currentDeck + 1) % decks.numDecks;
    }
    //printf("a winner is you\n");
    //Inform clients of the winner
    if (game.team1Score >= 499 || game.team2Score <= -499) {
        send_info(game, 0, "MWinner is Team 1\nO\n");
    } else {
        send_info(game, 0, "MWinner is Team 2\nO\n");
    }
    //Close sockets
    for (int i = 0; i < 4; i++) {
        fclose(game.players[i].sockRead);
        fclose(game.players[i].sockWrite);
    }
    //Exit
    printf("Done game %s\n", game.name);
    fflush(stdout);
    pthread_exit(NULL);
}

/** read_client(char* str, int size, Game game, int client)
**        Reads info from the given client.
*/
void read_client(char* str, int size, Game game, int client) {
    //Try reading from client
    if (!fgets(str, size, game.players[client].sockRead)) {
        //If fgets fails client has disconnected
        char buffer[100];
        sprintf(buffer, "M%s disconnected early\nO\n", 
        game.players[client].name);
        send_info(game, 0, buffer);
    }
}

/** score_hand(Game *game)
**        Scores the current hand for the given game.
*/
void score_hand(Game *game) {
    int bidValue, totalTricks;
    //Work out value of bid
    bidValue = bid_value(game->bid.rank, game->bid.suit);
    //Work out if team met their bid, and modify score appropriately
    if (game->bid.played == 1 || game->bid.played == 3) {
        totalTricks = game->players[0].tricks + game->players[2].tricks;
        if (totalTricks < (game->bid.rank - '0')) {
            bidValue *= -1;
        }
        game->team1Score += bidValue;
    } else {
        totalTricks = game->players[1].tricks + game->players[3].tricks;
        if (totalTricks < (game->bid.rank - '0')) {
            bidValue *= -1;
        }
        game->team2Score += bidValue;
    }
}

/** trick(Game *game, int lead, char trumps)
**        Takes the game, player to lead and trump suit, and runs a trick.
**        Returns the winner of the trick.
*/
int trick(Game *game, int lead, char trumps) {
    Card play[4];
    char buffer[128];
    int playedCard, winner;
    //For response checking
    play[0].suit= '\n';
    //For each player
    for(int i = 0; i < 4; i++) {
		//printf("i = %d\n", i);
        while (1){
            //Send prompt to player
            if (i == 0) {
                strcpy(buffer, "L\n");
            } else {
                sprintf(buffer, "P%c\n", play[0].suit);
            }
            send_info(*game, ((lead + i) % 4) + 1, buffer);
            //Get response
            //fgets(buffer, 4, game->players[(lead + i) % 4].sockRead);
            read_client(buffer, 128, *game, (lead + i) % 4);
            //Check response
            if((playedCard = check_play(buffer, play[0].suit, 
                game->players[(lead + i) % 4].hand)) != -1) {
                break;
            }
        }
        //Send A
        send_info(*game, ((lead + i) % 4) + 1, "A\n");
        //Set card as played, add to card array
        game->players[(lead + i) % 4].hand[playedCard].played = 1;
        play[i] = game->players[(lead + i) % 4].hand[playedCard];
        play[i].played = (lead + i) % 4;
        //Inform other players of the result
        sprintf(buffer, "M%s plays %c%c\n", game->players[(lead + i) % 4].name,
        play[i].rank, play[i].suit);
        send_info(*game, ((((lead + i) % 4) + 1) * -1), buffer);
    }
    //Determine winning player
    for (int i = 0; i < 4; i++) {
        if (cmp_cards_trick(play[i], play[(i + 1) % 4], trumps, play[0].suit)
		>= 0 && cmp_cards_trick(play[i], play[(i + 2) % 4], trumps, 
		play[0].suit) >= 0 && cmp_cards_trick(play[i], play[(i + 3) % 4],
		trumps, play[0].suit) >= 0) {
			winner = play[i].played;
            break;
        }
    }
	
    //Increment player's wins
    game->players[winner].tricks++;
    //Return winning player
    return winner;
}

/** bidding(Game *game)
**        Runs bidding for the given game. Returns the winner.
*/
int bidding(Game *game) {
    int passed[4] = {0, 0, 0, 0}, currPlayer = 0;
    char buffer[4], lastBid[4];
    //Loop until 3 people have passed or someone bids 9H
    while(1) {
        //Check if first bid (played  = 0 in bid card) and send prompt
        if (!game->bid.played) { 
            sprintf(lastBid, "B\n");
        } else {
            sprintf(lastBid, "B%c%c\n", game->bid.rank, game->bid.suit);
        }
        send_info (*game, currPlayer + 1, lastBid);
		//printf("Sending %s to players\n", lastBid);
        //Get data back
        //fgets(buffer, 4, game->players[currPlayer].sockRead);
        read_client(buffer, 4, *game, currPlayer);
        //If it was a pass set the pass array and check if 3 have passed
        if(!strcmp(buffer, "PP\n") && game->bid.played) {
            passed[currPlayer] = 1;
            //Inform others of pass
            sprintf(buffer, "M%s passes\n", game->players[currPlayer].name);
            send_info(*game, ((currPlayer + 1) * -1), buffer);
            if ((passed[0] + passed[1] + passed[2] + passed[3]) == 3){
                //3 people have passed
				while (passed[currPlayer = (currPlayer + 1) % 4]){}
                return currPlayer;
            }
        } else {
            //Check it
            if(!check_bid(buffer, lastBid + 1)) {
                continue;
            }
            //Set bid card as appropriate
            game->bid.played = currPlayer + 1;
            game->bid.rank = buffer[0];
            game->bid.suit = buffer[1];
            //Inform others of bid
            sprintf(buffer, "M%s bids %c%c\n", game->players[currPlayer].name,
            game->bid.rank, game->bid.suit);
            send_info(*game, ((currPlayer + 1) * -1), buffer);
            //Check if it is 9H
            if(game->bid.rank == '9' && game->bid.suit == 'H') {
                return currPlayer;
            }
        }
        //Increment current player to a player who has not passed
        while (passed[currPlayer = (currPlayer + 1) % 4]){}
    }
}

/** deal_cards(Game *game)
**        Deals cards to players in the given game.
*/
void deal_cards(Game *game){
    char buffer[2];
    Card* deck = decks.decks[game->currentDeck];
    //Send 'H' to all clients so that they know hand is coming
    send_info(*game, 0, "H");
    //Deal cards and set the game s
    for(int i = 0; i < 52; i += 4) {
        //Player 1
        game->players[0].hand[i/4] = deck[i];
        sprintf(buffer, "%c%c", deck[i].rank, deck[i].suit);
        send_info(*game, 1, buffer);
        //Player 2
        game->players[1].hand[i/4] = deck[i + 1];
        sprintf(buffer, "%c%c", deck[i + 1].rank, deck[i + 1].suit);
        send_info(*game, 2, buffer);
        //Player 3
        game->players[2].hand[i/4] = deck[i + 2];
        sprintf(buffer, "%c%c", deck[i + 2].rank, deck[i + 2].suit);
        send_info(*game, 3, buffer);
        //Player 4
        game->players[3].hand[i/4] = deck[i + 3];
        sprintf(buffer, "%c%c", deck[i + 3].rank, deck[i + 3].suit);
        send_info(*game, 4, buffer);
    }
    //Send '\n' to all clients so that they know hand is finished
    send_info(*game, 0, "\n");
    game->currentDeck = (game->currentDeck + 1) % decks.numDecks;
}

/** sort_players(const void *a, const void *b)
**        Sorts players alphabetically by name. Can be used by qsort.
*/
int sort_players(const void *a, const void *b) {
    Player p1, p2;
    p1 =  *(Player*)a;
    p2 =  *(Player*)b;
    //Use strcmp to compare the names of each player
    return (strcmp(p1.name, p2.name));
}

/** send_info (Game game, int targets, char* message)
**        Sends the message to player(s) in game specified by targets.
*/
void send_info (Game game, int targets, char* message) {
	//printf("Sending %s", message);
    //0 = all targets
    //-1 - -4 = not that player
    //1 - 4 = only that player
    //Print
    if(targets > 0) {
        fprintf(game.players[targets - 1].sockWrite, "%s", message);
    } else {
        for (int i = 1; i < 5; i++) {
            if (i + targets) {
                fprintf(game.players[i - 1].sockWrite, "%s", message);
            }
        }
    }
	/*for (int i = 0; i < 4; i++) {
		fflush(game.players[i].sockWrite);
	}*/
}

/** serv_exit(int code)
**        Exits the server with the given code and an appropriate message.
*/
void serv_exit(int code) {
    char* message;
    switch(code){
        case 1:
            message = "Usage: serv499 port greeting deck";
            break;
        case 4:
            message = "Invalid Port";
            break;
        case 5:
            message = "Port Error";
            break;
        case 6:
            message = "Deck Error";
            break;
        case 8:
            message = "System Error";
            break;
        default:
            message = "An error occured";
    }
    fprintf(stderr, "%s\n", message);
    exit(code);
}

/** open_decks(char* deckfile)
**        Opens the decks in filepath given, adding them to the decks struct.
*/
void open_decks(char* deckfile){
    FILE* f;
    char line[106];
    //Attempt to open the deckfile
    if(!(f = fopen(deckfile, "r"))){
        serv_exit(6);
    }
    //Verify the line length and get the number of lines
    while(fgets(line, 106, f)){
        decks.numDecks++;
        if(line[104] != '\n' && line[104] != 0) {
            //printf("Deck error in strlen check - 104 was %d lel\n", line[104]);
            serv_exit(6);
        }
    }
    //Allocate memory
    decks.decks = (Card**)malloc(decks.numDecks * sizeof(Card*));
    for(int i = 0; i < decks.numDecks; i++) {
        decks.decks[i] = (Card*)malloc(52 * sizeof(Card));
    }
    //Read and verify each card
    rewind(f);
    for (int i = 0; i < decks.numDecks; i++) {
        //Verify the deck
        for(int j = 0; j < 52; j++) {
            decks.decks[i][j].rank = fgetc(f);
            decks.decks[i][j].suit = fgetc(f);
            decks.decks[i][j].played = 0;
            if(!strchr("SDCH", decks.decks[i][j].suit) ||
                !strchr("AKQJT98765432", decks.decks[i][j].rank)){
                //printf("Deck error in valid char check\n");
                serv_exit(6);
            }
        }
        //fgetc again to go over the \n
        fgetc(f);
    }
}

/** open_listen(int port)
**        Listen for connections on the given port.
*/
int open_listen(int port) {
    int fd;
    struct sockaddr_in serverAddr;
    int optVal;

    /*  Create TCP socket */
    fd = socket(PF_INET, SOCK_STREAM, 0);
    if(fd < 0) {
        serv_exit(8);
    }

    /* Set the option to reuse the socket address immediately */
    optVal = 1;
    if(setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &optVal, sizeof(int)) < 0) {
        serv_exit(8);
    }
	/*Set the no delay option*/
	optVal = 1;
	if(setsockopt(fd, IPPROTO_TCP, TCP_NODELAY, (char*)&optVal, sizeof(int)) < 0) {
        serv_exit(8);
    }
	
    /* Set up the address structure for the server side of the connection
     * - any local IP address is OK (INADDR_ANY) 
     * - given port number - convert to network byte order
     */
    serverAddr.sin_family = AF_INET;    
    serverAddr.sin_port = htons(port);
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);

    /* Bind our socket to the given address (IP address and port number) */
    if(bind(fd, (struct sockaddr*)&serverAddr, sizeof(struct sockaddr_in))
        < 0) {
        serv_exit(5);
    }

    /* Start listening for incoming connection requests - queue up to SOMAXCONN
     * of them
     */
    if(listen(fd, SOMAXCONN) < 0) {
        serv_exit(5);
    }

    return fd;
}