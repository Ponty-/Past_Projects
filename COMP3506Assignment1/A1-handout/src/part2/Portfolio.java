package part2;

import java.util.ArrayDeque;

/**
 * This class is used to represent a portfolio of company stocks. It keeps track
 * of the purchase price of each individual stock held, and the order in which
 * they were purchased. Stocks may be purchased and added to the portfolio via
 * the buy operation, and removed from the portfolio using the sell operation.
 * The sell operation calculates the capital gain (or loss) from the sale.
 */
public class Portfolio {

	// The deque used for the stocks
	ArrayDeque<int[]> stocks;

	/**
	 * @post: Creates a new portfolio that doesn't contain any stocks.
	 */
	public Portfolio() {
		/* Deque of arrays. Each element in the deque is an array of length 2,
		with the first being the number of stocks and the second being
		the price each stock was sold for.*/
		stocks = new ArrayDeque<int[]>();
	}

	/**
	 * @post: Returns the number of stocks currently held.
	 */
	public int stocksHeld() {
		int[] stock;
		int totalStocks = 0;
		/*For each entry in the stocks deque, remove it, get the value,
		 * and add it back in. At the end of the loop the deque will be
		 * in the same state as at the beginning.
		 */
		for (int i = 0; i<stocks.size(); i++){
			stock = stocks.removeFirst();
			totalStocks+=stock[0];
			stocks.add(stock);
		}
		return totalStocks;
	}

	/**
	 * @pre: q > 0 and p >= 0
	 * @post: q stocks, each purchased for price p are added to the portfolio.
	 */
	public void buy(int q, int p) {
		// Add the quantity and price of the new stocks to the end of the deque
		int[] newStocks = {q, p};
		stocks.add(newStocks);
	}

	/**
	 * @pre: q > 0 and p >= 0 and q <= this.stocksHeld().
	 * @post: The oldest q stocks held are each sold for price p, and the
	 *        capital gain (or loss) from the sale is returned. The capital gain
	 *        (or loss) is calculated to be the total selling price of the
	 *        stocks (q*p) minus the total value of the stocks being sold when
	 *        they were originally purchased.
	 */
	public int sell(int q, int p) {
		int gain = 0;
		int[] oldest;
		while (q > 0){
			//Check that stocks is not empty
			if (stocks.size() == 0){
				break;
			}

			oldest = stocks.peekFirst();
			//If the oldest stocks entry is larger than the stocks to sell
			if(oldest[0] > q){
				//subtract q and set gain appropriately before exiting loop
				oldest[0] -= q;
				gain += (q*p)-(q*oldest[1]);
				break;
			}
			//If the oldest stocks entry is the same amount as stocks to sell
			else if (oldest[0] == q){
				// Remove the oldest, set gain and exit loop
				oldest = stocks.remove();
				gain += (q*p)-(q*oldest[1]);
				break;
			}
			//If the oldest stocks entry has less than the stocks to sell
			else{
				/*Remove the oldest entry, the gain and subtract the quantity
				from q*/
				oldest = stocks.removeFirst();
				q -= oldest[0];
				gain += (oldest[0]*p)-(oldest[0]*oldest[1]);
			}
		}
		return gain;
	}
		

}
