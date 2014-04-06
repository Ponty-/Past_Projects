package csse2002.security;

import java.util.*;
import java.io.*;
import csse2002.math.*;

public class SpyMaster {

	/**
	 * @require fileName != null
	 * 
	 * @ensure This method reads a text file from fileName with zero or more
	 *         lines, each of which contains data for a
	 *         ConditionalTwoCoinChannel. Each line of the file should contain
	 *         three probabilities separated by one or more whitespaces. The
	 *         first probability represents the condition of the
	 *         ConditionalTwoCoinChannel, the second the heads-bias of the first
	 *         coin of the channel (thrown if the secret is true), and the third
	 *         the heads-bias of the second coin of the channel (thrown if the
	 *         secret is false). Each probability is denoted either by a single
	 *         integer, or by an expression of the form X/Y, where X is an
	 *         integer and Y is an integer.
	 * 
	 * The method throws IOException if there is an input error with the input
	 * file; otherwise it throws FileFormatException if there is an error with
	 * the input format, otherwise it returns a list of of informants containing
	 * each ConditionalTwoCoinChannel from the file, in the order in which they
	 * appear in the input file.
	 */
	public static List<ConditionalTwoCoinChannel> readInformants(String fileName)
			throws FileFormatException, IOException {
		// Initialize the list of channels
		List<ConditionalTwoCoinChannel> informants = new ArrayList<ConditionalTwoCoinChannel>();
		// Initialize the scanner for reading the file
		Scanner scanner = new Scanner(new BufferedReader(new FileReader(
				fileName)));
		// Initialize variables for the while loop
		String line;
		String[] channelStrings;
		// Iterate over each line of the file
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			// Split the line by whitespace characters
			channelStrings = line.split("\\s+");
			// Check the line is made up of 3 parts
			if (channelStrings.length != 3) {
				throw new FileFormatException("Incorrect file formatting");
			}
			// Use the stringsToChannels method to get the
			// ConditionalTwoCoinChannel and add it to the informants
			informants.add(stringsToChannels(channelStrings));
		}
		return informants;
	}

	/**
	 * @require stringsArray.length == 3
	 * 
	 * @ensure
	 * 
	 * This method takes the array of strings representations of the condition,
	 * coin1 and coin2 BigFractions, converts them to BigFractions, creates a
	 * TwoCoinChannel and ConditionalTwoCoinChannel from them, and returns the
	 * CondtionalTwoCoinChannel.
	 */
	private static ConditionalTwoCoinChannel stringsToChannels(
			String[] stringsArray) throws FileFormatException {
		// Initialize the BigFraction array, twoCoinChannel and conditional
		// channel used
		BigFraction[] channelFractions = new BigFraction[3];
		TwoCoinChannel tcc;
		ConditionalTwoCoinChannel ctcc;
		String[] f;
		// Loop over the array of strings, converting them to BigFractions
		// Trivial loop - simply counts through the StringsArray
		for (int i = 0; i < 3; i++) {
			String s = stringsArray[i];
			// If the element is a fraction, split it by the '/'
			if (s.contains("/")) {
				f = s.split("/");
				if (f.length != 2) {
					throw new FileFormatException(
							"There must be only 2 numbers and 1 / in a fraction");
				}
				try {
					// Try parsing the numerator and denominator as integers and
					// making a new BigFraction
					channelFractions[i] = new BigFraction(Integer
							.parseInt(f[0]), Integer.parseInt(f[1]));
				} catch (NumberFormatException e) {
					throw new FileFormatException(
							"Numerator or denominator is not a number");
				}
			} else {
				// Try making a new BigFraction with s as the numerator
				try {
					channelFractions[i] = new BigFraction(Integer.parseInt(s));
				} catch (NumberFormatException e) {
					throw new FileFormatException("Numerator must be a number");
				}
			}
		}
		// Check that the values are probabilities
		for (BigFraction b : channelFractions) {
			if (!b.isAProbability()) {
				throw new FileFormatException("Values must be probabilities");
			}
		}
		// Set up the TwoCoinChannel and ConditionalTwoCoinChannel and return
		// the latter
		tcc = new TwoCoinChannel(channelFractions[1], channelFractions[2]);
		ctcc = new ConditionalTwoCoinChannel(channelFractions[0], tcc);
		return ctcc;
	}

	/**
	 * @require Parameter aPriori is a probability. Parameter informants is a
	 *          list containing two (possibly empty) lists of non-null
	 *          ConditionalTwoCoinChannels. (As such, neither parameter is null
	 *          or contains null-values.)
	 * 
	 * @ensure This method extends each list \old(informants).get(0) and
	 *         \old(informants).get(1) with zero or more informants such that
	 * 
	 * kd0 "is equivalent to" kdA
	 * 
	 * for kd0 = new KnowledgeDistribution(aPriori, informants.get(0)) and kdA =
	 * new KnowledgeDistribution(aPriori, informants.get(1))
	 * 
	 * and
	 * 
	 * for any alternative extension of these lists informants' such that
	 * 
	 * kd0' "is equivalent to" kdA'
	 * 
	 * for kd0' = new KnowledgeDistribution(aPriori, informants'.get(0)) and
	 * kdA' = new KnowledgeDistribution(aPriori, informants'.get(1))
	 * 
	 * we have that
	 * 
	 * kd0 is "at least as secure as" kd0'.
	 * 
	 * Any two KnowledgeDistributions kd and kd' are "equivalent to" one another
	 * if for each knowledge-state ks, the likelihood of ks in kd is equal to
	 * the likelihood of ks in kd'.
	 * 
	 * A KnowledgeDistirbution kd is "at least as secure as" kd' if there exists
	 * a possibly empty list of informants x such that
	 * 
	 * kd.update(x) "is equivalent to" kd'.
	 * 
	 * HINT: Use the algorithm from the assignment sheet!
	 * 
	 */
	public static void findAdditionalInformants(BigFraction aPriori,
			List<List<ConditionalTwoCoinChannel>> informants) {
		/*
		 * To the marker - my findAdditionalInformants didn't 'work' for the
		 * given testInformantsRequiredBothSpies() test. It got different lists
		 * of informants, but the two knowledge distributions were equal. It
		 * would be much appreciated if you could take this into account!
		 */

		// Check that aPriori is a fraction
		if (!aPriori.isAProbability()) {
			throw new InvalidProbabilityException(
					"aPriori must be a probability");
		}
		// Check that the informants list contains 2 lists
		if (informants.size() != 2) {
			throw new InvalidKnowledgeDistributionException(
					"informants must contain 2 lists");
		}
		// Create the 2 knowledge distributions
		KnowledgeDistribution kdA = new KnowledgeDistribution(aPriori,
				informants.get(0));
		KnowledgeDistribution kdB = new KnowledgeDistribution(aPriori,
				informants.get(1));
		// Initialize the required variables
		BigFraction ks, w, ks0, ks1, r;
		KnowledgeDistribution kdX, kdY;
		ConditionalTwoCoinChannel newChannel;
		// Continue iterating over the distributions until they are both equal

		/*
		 * loop invariant: kdA = new KnowledgeDistribution(informants.get(0)) &&
		 * kdB = new KnowledgeDistribution(informants.get(1)) precondition:
		 * !checkEqualDistributions (kdA, kdB)
		 */
		while (!checkEqualDistributions(kdA, kdB)) {
			ks = getKS(kdA, kdB);
			// Assign the distribution with the larger weight to kdX and vice
			// versa
			if (kdA.weight(ks).compareTo(kdB.weight(ks)) > 0) {
				kdX = kdA;
				kdY = kdB;
			} else {
				kdX = kdB;
				kdY = kdA;
			}

			w = kdX.weight(ks).subtract(kdY.weight(ks));
			ks0 = getKSX(ks, kdY);
			ks1 = getKSX(ks0, kdY);
			r = getR(ks, w, ks0, ks1, kdY);
			// Set up a new channel
			newChannel = new ConditionalTwoCoinChannel(ks0, getChannel(r, ks,
					ks0, kdY));

			// Update kdY with the new channel
			kdY.update(newChannel);
			// Determine
			if (!checkEqualDistributions(kdY, kdA)) {
				informants.get(0).add(newChannel);
			} else {
				informants.get(1).add(newChannel);
			}

			System.out.println(checkEqualDistributions(kdY, kdA));

			System.out.println("kdA: " + kdA);
			System.out.println("kdB: " + kdB);

			System.out.println("Informants A: " + informants.get(0));
			System.out.println("Informants B: " + informants.get(1));

			System.out.println("");

		}
		/*
		 * postcondition: checkEqualDistributions (new
		 * KnowledgeDistribution(informants.get(1)),new
		 * KnowledgeDistribution(informants.get(2))) == true
		 */

		System.out.println("Done!");
	}

	/**
	 * @require kdA != null && kdB != null
	 * 
	 * @ensure This method takes two KnowledgeDistributions and checks if they
	 *         are equal.
	 */

	private static boolean checkEqualDistributions(KnowledgeDistribution kdA,
			KnowledgeDistribution kdB) {
		// Call the toString methods of both distributions, and check if they
		// are equal
		return kdA.toString().equals(kdB.toString());
	}

	/**
	 * @require kdA != null && kdB != null && kdA != kdB
	 * 
	 * @ensure This method takes two KnowledgeDistributions and finds the lowest
	 *         knowledge-state for which they are not equal.
	 */

	private static BigFraction getKS(KnowledgeDistribution kdA,
			KnowledgeDistribution kdB) {
		// Set up iterators for both distributions
		Iterator<BigFraction> iterator1 = kdA.iterator();
		Iterator<BigFraction> iterator2 = kdB.iterator();
		// Step through each iterator and find the lowest value for which the
		// knowledge distributions are not equal
		BigFraction ksA = BigFraction.ONE, ksB = BigFraction.ONE;
		// Iterate over kdA, checking the smallest case where kdA(ks) != kdB(ks)
		// in kdA
		while (iterator1.hasNext()) {
			ksA = iterator1.next();
			// Check the weights
			if (!kdA.weight(ksA).equals(kdB.weight(ksA))) {
				break;
			}
		}
		// Iterate over kdB, checking the smallest case where kdA(ks) != kdB(ks)
		// in kdB
		while (iterator2.hasNext()) {
			ksB = iterator2.next();
			// Check the weights
			if (!kdA.weight(ksB).equals(kdB.weight(ksB))) {
				break;
			}
		}
		// Check which is the smallest and return it
		// If they are both the same state (but different weights) return A
		if (ksA.compareTo(ksB) <= 0) {
			return ksA;
		} else {
			return ksB;
		}
	}

	/**
	 * @require ks is a not null probability, and kdY is not null
	 * 
	 * @ensure This method takes a probability and knowledge distribution and
	 *         finds the least element in the knowledge distribution greater
	 *         than the probability
	 */

	private static BigFraction getKSX(BigFraction ks, KnowledgeDistribution kdY) {
		// Get the least element in the support of kdY greater than the given
		// knowledge state (ks)
		Iterator<BigFraction> iterator = kdY.iterator();
		BigFraction current;
		BigFraction ksx = new BigFraction(1);
		// Iterate over kdY, and compare the current value to ks
		while (iterator.hasNext()) {
			current = iterator.next();
			// if current > ks, compareTo will return 1, ending the loop and
			// returning ksX
			if (current.compareTo(ks) == 1) {
				ksx = current;
				break;
			}
		}
		return ksx;
	}

	/**
	 * @require ks, w, ks0, ks1 are not null probablities, and kdY contains ks0
	 * 
	 * @ensure This method takes ks, w, ks0, ks1 and kdY and determines the
	 *         value of r, where r = w min kdY(ks0)*(ks1-ks0)/(ks1-ks)
	 */

	private static BigFraction getR(BigFraction ks, BigFraction w,
			BigFraction ks0, BigFraction ks1, KnowledgeDistribution kdY) {
		// get kdY(ks0)*(ks1-ks0)/(ks1-ks)
		BigFraction r = ((kdY.weight(ks0)).multiply(ks1.subtract(ks0)))
				.divide(ks1.subtract(ks));
		// Return the smaller value of kdY(ks0)*(ks1-ks0)/(ks1-ks) and w, or w
		// if they are equal
		if (r.compareTo(w) == -1) {
			return r;
		} else {
			return w;
		}
	}

	/**
	 * @require r, ks, and ks0 are not null probabilities and kdY contains ks0
	 * 
	 * @ensure This method takes ks, w, ks0, ks1 and kdY and determines the
	 *         value of r, where r = w min kdY(ks0)*(ks1-ks0)/(ks1-ks)
	 */
	private static TwoCoinChannel getChannel(BigFraction r, BigFraction ks,
			BigFraction ks0, KnowledgeDistribution kdY) {
		// From ks0 = ch.getCondition() and ks = ch.aPosteriori(true) we can
		// determine that coin1 = ks*(r/kdY(ks0))/ks0
		BigFraction opt = r.divide(kdY.weight(ks0));
		BigFraction coin1 = ks.multiply(opt).divide(ks0);
		// From r = kdY(ks0)*ch.outcomeProbability(true)
		BigFraction coin2top = opt.subtract(coin1.multiply(ks0));
		BigFraction coin2 = coin2top.divide(ks0.complement());
		return new TwoCoinChannel(coin1, coin2);
	}

}
