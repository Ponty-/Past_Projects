#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <stdio.h> 
#include <unistd.h>
#include <netdb.h>
#include <string.h>
#include <errno.h>
#include "global499.h"

int connect_to(struct in_addr* ipAddress, int port);
struct in_addr* name_to_ip_addr(char* hostname);
void client_exit(int code);
void read_message();
void read_cards(char *cards);
void bid(char *current);
void play_card(char *suit, char type);
void print_cards();

int state; /*Represents the game state - 0 is getting hand, 1 is bidding,
 2-14 are plays*/
Card hand[13];
int lastCard; //The last card that was played
FILE *serverRead, *serverWrite;

int main(int argc, char* argv[]) {
    int fd, port;
    struct in_addr* ipAddress;
    char* hostname, *name, *game;
    //Check number of arguments
    if(argc < 4 || argc > 5) {
        client_exit(1);
    }
    //Set and check name and game
    name = argv[1];
    game = argv[2];
    if (strlen(name) == 0 || strlen(game) == 0) {
        client_exit(4);
    }
    //Set and check port
    port = atoi(argv[3]);
    if (port > 65535 || port < 1) {
        client_exit(4);
    }
    //Set host to localhost, if optional host argument given use that
    hostname = "localhost";
    if (argc == 5) {
        hostname = argv[4];
    }
    
    //Try and connect to server
    ipAddress = name_to_ip_addr(hostname);
    fd = connect_to(ipAddress, port);
    serverRead = fdopen(fd, "r");
    serverWrite = fdopen(fd, "w");
    
    //Send server player name and game to connect to
    if(!fprintf(serverWrite, "%s\n%s\n", name, game)) {
        client_exit(6);
    }
    
    //Set the game state to bidding
    state = 0;
    //Set the last card to -1
    lastCard = -1;
    //Read and follow server prompts until the game ends
    while(1) {
        read_message();
    }
    return 0;
}

/** read_message()
**        Takes the file stream for the server, recieves a message, and
**        acts appropriately.
*/
void read_message() {
    char buffer[128] = "";
    if(fgets(buffer, 128, serverRead) == NULL) {
        client_exit(6);
    }
    if (strlen(buffer) < 2) {
        client_exit(6); 
    }
    switch(buffer[0]) {
        case 'M':
            printf("Info: %s", buffer+1);
            break;
        case 'O':
            fclose(serverRead);
            fclose(serverWrite);
            exit(0);
        case 'H':
            read_cards(buffer+1); //Read in cards
            break;
        case 'B':
            bid(buffer+1); //Bid
            break;
        case 'L':
            play_card(buffer+1, 'L'); //Play a card
            break;
        case 'P':
            play_card(buffer+1, 'P'); //Play a card following suit
            break;
        case 'T':
            if (state != 1) {
                client_exit(6);
            }
            state++; //Advance game state
            break;
        case 'A':
            if (state < 2 || state > 14 || lastCard < 0) {
                client_exit(6); 
            }
            //Set last card as used
            hand[lastCard].played = 1;
            lastCard = -1;
            state = (state + 1) % 15;
            break;
        default:
            client_exit(6);
    }
    fflush(serverWrite);
}

/** print_cards()
**        Prints out the current hand, sorted by suit in descending order.
*/
void print_cards() {
    char buffer[128] = "", temp[8];
    int current = 0;
    //Print all spades
    strcat(buffer, "S:");
    while(hand[current].suit == 'S') {
        if(hand[current].played == 0) {
            sprintf(temp, " %c", hand[current].rank);
            strcat(buffer, temp);
        }
        current++;
    }
    //Print all clubs
    strcat(buffer, "\nC:");
    while(hand[current].suit == 'C') {
        if(hand[current].played == 0) {
            sprintf(temp, " %c", hand[current].rank);
            strcat(buffer, temp);
        }
        current++;
    }
    //Print all diamonds
    strcat(buffer, "\nD:");
    while(hand[current].suit == 'D') {
        if(hand[current].played == 0) {
            sprintf(temp, " %c", hand[current].rank);
            strcat(buffer, temp);
        }
        current++;
    }
    //Print all hearts
    strcat(buffer, "\nH:");
    for (current = current; current < 13; current++) {
        if(hand[current].played == 0) {
            sprintf(temp, " %c", hand[current].rank);
            strcat(buffer, temp);
        }
    }
    printf("%s\n", buffer);
}

/** read_cards(char *cards)
**        Takes a string representing the cards for the current hand, and
**        stores them in the user's hand as Cards.
*/
void read_cards(char *cards) {
    //State check and line length check
    if (state != 0 || cards[26] != '\n') {
        client_exit(6); 
    }
    //Read in the 13 cards for the hand
    for (int i = 0; i < 13; i++) {
        //Get the cards
        hand[i].rank = cards[2*i];
        hand[i].suit = cards[(2*i) + 1];
        hand[i].played = 0;
        //Check suit and rank are valid
        if(!strchr("SDCH", hand[i].suit) ||
                !strchr("AKQJT98765432", hand[i].rank)) {
            client_exit(6);
        }
    }
    //Sort the cards
    qsort(hand, 13, sizeof(Card), cmp_cards_suit);
    //Reverse to descending order
    Card temp[13];
    for (int i = 12, j = 0; i >= 0; i--, j++) {
        temp[j] = hand[i];
    }
    for (int i = 0; i < 13; i++) {
        hand[i] = temp[i];
    }
    //Advance the state to bidding
    state++;
    print_cards();
}

/** bid(char *current)
**        Takes a string representing the prompt from the server and the 
**        server stream. Prompts the user for a valid bid and returns 
**        it to the server.
*/
void bid(char *current) {
    char bid[128];
    //State check
    if (state != 1) {
        client_exit(6);
    }
    while (1) {
        //If it's the first bid
        if (strlen(current) == 1) {
            printf("Bid> ");
            fgets(bid, 128, stdin);
        } else if (strlen(current) == 3) {
            //Check given bid is valid
            if(!strchr("SDCH", current[1]) || !strchr("456789", current[0])) {
                client_exit(6);
            }
            printf("[%c%c] - Bid (or pass)> ", current[0], current[1]);
            fgets(bid, 128, stdin);
            //Check PP
            if (!strcmp(bid, "PP\n")) {
                if(fprintf(serverWrite, "%s", bid) != 3) {
                    client_exit(6);
                }
                return;
            }
        } else {
            client_exit(6); 
        }
        
        if ((check_bid(bid, current)) > 0) {
            if((fprintf(serverWrite, "%s", bid)) != 3) {
                client_exit(6);
            }
            break;
        }
    }
}

/** play_card(char *suit, char type)
**        Takes a string representing the prompt from the server,
**        whether the player is leading or not. and the 
**        server stream. Prompts the user for a valid play and returns 
**        it to the server.
*/
void play_card(char *suit, char type) {
    print_cards();
    char play[128];
    int match;
    //Check state
    if (state < 2 || state > 15) {
        client_exit(6); 
    }
    //Check line length/input and prompt
    switch (type) {
        case 'L':
            if (strlen(suit) != 1 || suit[0] != '\n') {
                client_exit(6); 
            }
            printf("Lead> ");
            break;
        case 'P':
            if (strlen(suit) != 2 || !strchr("SDCH", suit[0])) {
                client_exit(6);
            }
            printf("[%c] play> ", suit[0]);
    }
    fgets(play, 128, stdin);
    match = check_play(play, suit[0], hand);
    //Send play to server
    if(!fprintf(serverWrite, "%s", play)) {
        client_exit(6);
    }
    lastCard = match;
}

/** name_to_ip_addr(char* hostname)
**        Get the IP address for that hostname.
*/
struct in_addr* name_to_ip_addr(char* hostname) {
    int error;
    struct addrinfo* addressInfo;

    /* Convert hostname to an address */
    error = getaddrinfo(hostname, NULL, NULL, &addressInfo);
    if(error) {
        client_exit(2);
    }
    /* Extract the IP address from the address structure and return it */
    return &(((struct sockaddr_in*)(addressInfo->ai_addr))->sin_addr);
}

/** connect_to(struct in_addr* ipAddress, int port)
**        Connects to the machine with the given IP on the given port.
*/
int connect_to(struct in_addr* ipAddress, int port) {
    struct sockaddr_in socketAddr;
    int fd;
    
    /* Create TCP socket */
    fd = socket(AF_INET, SOCK_STREAM, 0);
    if(fd < 0) {
        client_exit(8);
    }

    /* Create structure that represents the address (IP address and port
     * number) to connect to
     */
    socketAddr.sin_family = AF_INET; /* Address family - IPv4 */
    socketAddr.sin_port = htons(port); /* Port number - network byte order */
    socketAddr.sin_addr.s_addr = ipAddress->s_addr; /* IP address -
    already in network byte order */

    /* Attempt to connect to that remote address */
    if(connect(fd, (struct sockaddr*)&socketAddr, sizeof(socketAddr)) < 0) {
        client_exit(2);
    }

    return fd;
}

/** client_exit(int code)
**        Takes an error code and prints an appropriate message before exiting.
*/
void client_exit(int code) {
    char* message;
    switch(code) {
        case 1:
            message = "Usage: client499 name game port [host]";
            break;
        case 2:
            message = "Bad Server.";
            break;
        case 4:
            message = "Invalid Arguments.";
            break;
        case 6:
            message = "Protocol Error.";
            break;
        case 7:
            message = "User Quit.";
            break;
        case 8:
            message = "System Error.";
            break;
        default:
            message = "An error occured.";
    }
    fprintf(stderr, "%s\n", message);
    exit(code);
}