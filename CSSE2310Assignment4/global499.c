#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
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
    char rank;
    char suit;
    int played;
} Card;

/** cmp_cards_trick(Card c1, Card c2, char trumps, char lead)
**        Takes two cards, the trump suit, and the lead suit, and determines 
**        which one has the higher value.
*/
int cmp_cards_trick(Card c1, Card c2, char trumps, char lead) {
    char *ranks = "23456789TJQKA";
    char *suits = "SCDH";
    //Check if one is of trump suit and other is not
    if (c1.suit == trumps && c2.suit != trumps) {
        return 1;
    } else if (c2.suit == trumps && c1.suit != trumps) {
        return -1;
    }
    //Check if one is of lead suit and other is not
    if (c1.suit == lead && c2.suit != lead) {
        return 1;
    } else if (c2.suit == lead && c1.suit != lead) {
        return -1;
    }
    if (c1.rank == c2.rank) {
        //If card values are identical compare suits
        return (strchr(suits, c1.suit) - suits) - 
                (strchr(suits, c2.suit) - suits);
    } else {
        //Otherwise compare ranks
        return (strchr(ranks, c1.rank) - ranks) - 
                (strchr(ranks, c2.rank) - ranks);
    }
}

/** cmp_cards(const void * a, const void * b)
**        Takes two cards as void pointers, and determines which one has the
**        higher value. This can be used with the qsort function.
*/
int cmp_cards(const void * a, const void * b) {
    char *ranks = "23456789TJQKA";
    char *suits = "SCDH";
    Card c1, c2;
    c1 = *(Card*)a;
    c2 = *(Card*)b;
    if (c1.rank == c2.rank) {
        //If card values are identical compare suits
        return (strchr(suits, c1.suit) - suits) - 
                (strchr(suits, c2.suit) - suits);
    } else {
        //Otherwise compare ranks
        return (strchr(ranks, c1.rank) - ranks) - 
                (strchr(ranks, c2.rank) - ranks);
    }
}

/** cmp_cards(const void * a, const void * b)
**        Takes two cards as void pointers, and determines which one has the
**        higher value, considering suit over rank. This can be used with
**        the qsort function.
*/
int cmp_cards_suit(const void * a, const void * b) {
    char* ranks = "23456789TJQKA";
    char* suits = "HDCS";
    Card c1, c2;
    c1 = *(Card*)a;
    c2 = *(Card*)b;
    if (c1.suit == c2.suit) {
        //If card suits are identical compare ranks
        return (strchr(ranks, c1.rank) - ranks) - 
                (strchr(ranks, c2.rank) - ranks);
    } else {
        //Otherwise compare suit
        return (strchr(suits, c1.suit) - suits) - 
                (strchr(suits, c2.suit) - suits);
    }
}

/** bid_value (char rank, char suit)
**        Takes a rank and suit of a bid, and determines its value.
*/
int bid_value (char rank, char suit) {
    char *ranks = "456789", *suits = "SCDH";
    //Calculate bid based on table
    return ((strchr(suits, suit) - suits) * 10) + 20
            + ((strchr(ranks, rank) - ranks) * 50);
}

/** check_bid (char* bid, char* lastBid)
**        Takes this bid and the previous bid,
**        and determines if this bid is valid.
*/
int check_bid (char* bid, char* lastBid) {
    //Check bid - pass has already been checked if it applies
    if (strlen(bid) == 3 && strchr("SCDH", bid[1]) && 
            strchr("456789", bid[0])) {
        if (strlen(lastBid) == 1) {
            //First bid
            return 1;
        } else {
            //Check higher than existing bid            
            return (bid_value(bid[0], bid[1]) - 
                    bid_value(lastBid[0], lastBid[1]));
            
        }
    }
    return 0;
}

/** check_play (char* play, char lead, Card cards[13])
**        Takes the card that was played as a string, the lead suit, and the
**        player's hand, and determines if the play is valid.
*/
int check_play (char* play, char lead, Card cards[13]) {
    //Validate
    if (strlen(play) != 3 || !strchr("SCDH", play[1]) || 
            !strchr("AKQJT98765432", play[0])) {
        //Invalid play
        return -1;
    }
    //Check user has card and is following suit
    int match = -1;
    int canFollow = 0;
    for (int i = 0; i < 13; i++) {
        //Check user has card and that it is unplayed
        if (cards[i].rank == play[0] && cards[i].suit == play[1] && 
                !cards[i].played) {
            match = i;
        }
        //If user is not leading check they are following suit if possible
        if (lead != '\n' && cards[i].suit == lead && !cards[i].played) {
            canFollow = 1;
        }
    }
    //If user does not have card
    if (match == -1) {
        //printf("Don't have the card\n");//test
        return -1;
    }
    //Check following suit is possible
    if (lead != '\n' && canFollow && play[1] != lead) {
        //printf("Follow suit faggot\n");//test
        return -1;
    }
    //If successful, return match
    //printf("Success\n");//test
    return match;
}