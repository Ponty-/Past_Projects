package part2.test;

import part2.Portfolio;
import org.junit.Assert;
import org.junit.Test;

/**
 * Very basic tests for the Portfolio class. A much more extensive test suite
 * will be performed for assessment of your code, but this should get you
 * started.
 */
public class PortfolioTest {

	@Test
	public void basicTest() {
		Portfolio p = new Portfolio();
		Assert.assertEquals("Number of stocks incorrect.", 0, p.stocksHeld());
		p.buy(100, 20);
		p.buy(20, 24);
		p.buy(200, 36);
		Assert.assertEquals("Number of stocks incorrect.", 320, p.stocksHeld());
		Assert.assertEquals("Capital gain incorrect", 940, p.sell(150, 30));
		Assert.assertEquals("Number of stocks incorrect.", 170, p.stocksHeld());
		Assert.assertEquals("Capital gain incorrect", 400, p.sell(100, 40));
		Assert.assertEquals("Number of stocks incorrect.", 70, p.stocksHeld());
	}
	@Test
	public void testBaseState() {
		Portfolio p = new Portfolio();
		Assert.assertEquals(p.stocksHeld(), 0);
	}
	@Test
	public void testSingleBuy() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		Assert.assertEquals(p.stocksHeld(), 50);
	}
	@Test
	public void testManyBuysAtSamePrice() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 50);
		p.buy(100, 50);
		Assert.assertEquals(p.stocksHeld(), 200);
	}
	@Test
	public void testManyBuysAtDifferingPrices() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 100);
		p.buy(100, 200);
		Assert.assertEquals(p.stocksHeld(), 200);
	}
	@Test
	public void testSellWithSingleBuy() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.sell(25, 50);
		Assert.assertEquals(p.stocksHeld(), 25);
	}
	@Test
	public void testSellWithManyBuysAtSamePrice() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 50);
		p.buy(100, 50);
		p.sell(50, 50);
		p.sell(50, 50);
		p.sell(50, 50);
		Assert.assertEquals(p.stocksHeld(), 50);
	}
	@Test
	public void testSellWithManyBuysAtDifferingPrices() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 100);
		p.buy(100, 200);
		p.sell(50, 50);
		p.sell(50, 50);
		p.sell(50, 50);
		Assert.assertEquals(p.stocksHeld(), 50);
	}
	@Test
	public void testSellWithManyBuysAtSamePriceAndNonUniformSells() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 50);
		p.buy(100, 50);
		p.sell(150, 50);
		Assert.assertEquals(p.stocksHeld(), 50);
	}

	@Test
	public void testSellWithManyBuysAtDifferingPricesAndNonUniformSells() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 100);
		p.buy(100, 200);
		p.sell(150, 50);
		Assert.assertEquals(p.stocksHeld(), 50);
	}
	@Test
	public void testSellCaptialGainsWithSingleBuy() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		int gains = p.sell(50, 100);
		Assert.assertEquals(gains, 50 * 50);
	}
	@Test
	public void testCaptialGainsWithManyBuysAtSamePrice() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 50);
		p.buy(100, 50);
		int gains = p.sell(200, 100);
		Assert.assertEquals(gains, 200 * 50);
	}
	@Test
	public void testCaptialGainsWithManyBuysAtDifferingPrices() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 100);
		p.buy(100, 200);
		int gains = p.sell(50, 50);
		Assert.assertEquals(gains, 0);
		gains = p.sell(50, 50);
		Assert.assertEquals(gains, 50 * -50);
		gains = p.sell(100, 300);
		Assert.assertEquals(gains, 100 * 100);
	}
	@Test
	public void testCaptialGainsWithManyBuysAtSamePriceAndNonUniformSells() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 50);
		p.buy(100, 50);
		int gains = p.sell(25, 50);
		Assert.assertEquals(gains, 0);
		gains = p.sell(50, 100);
		Assert.assertEquals(gains, 50 * 50);
		gains = p.sell(50, 100); // should take 25 out of the 100
		Assert.assertEquals(gains, 50 * 50);
		gains = p.sell(75, 100);
		Assert.assertEquals(gains, 75 * 50); // should finish things off
	}
	@Test
	public void testCaptialGainsWithManyBuysAtDifferingPricesAndNonUniformSells() {
		Portfolio p = new Portfolio();
		p.buy(50, 50);
		p.buy(50, 100);
		p.buy(100, 200);
		int gains = p.sell(25, 50);
		Assert.assertEquals(gains, 0);
		gains = p.sell(50, 100);
		// sells the last 25 of the $50, then 25 of the $100
		Assert.assertEquals(gains, 25 * 50); // won't gain on the $100
		// sells the last 25 of the $100 and 25 of the $200
		gains = p.sell(50, 150); // should take 25 out of the 100
		Assert.assertEquals(gains, (25 * 50) + (25 * -50));
		gains = p.sell(75, 100);
		Assert.assertEquals(gains, 75 * -100); // should finish things off
	}
}
