499codedump

//Bidding with not quite done checks

void bid(char *current, FILE* stream){
	char bid[4];
	//State check
	if (state != 1){
		client_exit(6);
	}
	//If it's the first bid
	if (strlen(current) == 1){
		while(1) {
			printf("Bid>");
			fgets(bid, 4, stdin);
			//Check valid
			if (strlen(bid) == 3 && strchr("SDCH", bid[1]) && strchr("456789", bid[0])){
				fprintf(stream "%s\n", bid)
				return;
			}
		}
	} else if (strlen(current) == 3) {
		//Get the current bid
		char rank = current[0];
		char suit = current[1];
		while(1) {
			printf("[%c%c] - Bid (or pass)>", rank, suit);
			fgets(bid, 4, stdin);
			printf("%s\n", bid);
			fprintf(stream, "%s\n", bid);
			//Check valid
			if (strlen(bid) == 3 && (strchr"SDCH", bid[1]) && strchr("456789", bid[0])){
				//Check higher
				
				fprintf(stream, "%s\n", bid)
				return;
			} else if (strlen(bid) == 3 && !strcmp(bid, "PP\n")) {
				fprintf(stream, "%s\n", bid);
			}
		}
	}
}