typedef struct {
    char rank;
    char suit;
    int played;
} Card;

int cmp_cards_trick(Card c1, Card c2, char trumps, char lead);
int cmp_cards_suit(const void * a, const void * b);
int cmp_cards(const void * a, const void * b);
int check_bid (char* bid, char* lastBid);
int check_play (char* play, char lead, Card cards[13]);
int bid_value (char rank, char suit);