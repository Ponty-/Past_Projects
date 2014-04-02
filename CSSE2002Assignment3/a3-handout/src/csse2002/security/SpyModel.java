package csse2002.security;

import java.util.ArrayList;
import java.util.List;

import csse2002.math.*;

/**
 * Model for the Spy Simulator.
 * 
 * It should keep track of the value of the secret, and the knowledge-state of
 * the spy, and the informants that the spy is yet to meet.
 */
public class SpyModel {

	/**
	 * The heads-bias of the coin used to decide the secret.
	 */
	public static final BigFraction SECRET_BIAS = new BigFraction(1, 2);

	// The value of the secret
	boolean secret;
	// The current knowledge-state of the spy
	BigFraction knowledgeState;
	// The list of informants for the spy to meet
	List<TwoCoinChannel> informants;

	/**
	 * Initialises the model for the Spy Simulator.
	 */
	public SpyModel() {
		// invariant: SECRET_BIAS.isAProbability() &&
		// knowledgeState.isAProbability();

		// determine the value of the secret
		if (SECRET_BIAS.getDoubleValue() > Math.random()) {
			secret = true;
		} else {
			secret = false;
		}
		// the spy knows the bias of the decision making coin
		knowledgeState = SECRET_BIAS;
		// Set up the list of informants
		informants = new ArrayList<TwoCoinChannel>();
	}

	/**
	 * This method takes a file path in the form of a string. If the file exists
	 * and is the correct format, the informants detailed in the file are added
	 * to the informants list, and the method returns true. If this fails, it
	 * returns false.
	 */
	public boolean addInformants(String filepath) {
		// Try to open the file using InformantsReader and add the contents
		// Returning true means it succeeds and vice versa
		try {
			informants.addAll(InformantsReader.readInformants(filepath));
			return true;
		}// If readInformants throws an exception return false
		// Seems like kinda bad practice but it says on the spec sheet
		// 'If that method throws an exception trying to read the informants'
		// Implies any exception
		catch (Exception e) {
			return false;
		}

	}

	/**
	 * This method has the spy meet the next informant in the list (at the 0th
	 * position), and returns the informant that met the spy. If there is no
	 * informant to meet, the method returns null.
	 */
	public TwoCoinChannel meetInformant() {
		// Method returns true if informant was met, false otherwise
		// Check that there is informants in the list
		if (informants.size() >= 1) {
			// Have the spy meet the first informant in the list (at index 0)
			TwoCoinChannel informant = informants.get(0);
			knowledgeState = informant.aPosteriori(knowledgeState, secret);
			// Remove that informant from the list
			informants.remove(0);
			return informant;
		} else {
			return null;
		}
	}

	/**
	 * This method has the spy guess the value of the secret based on their
	 * current knowledge-state. If it is greater than or equal to 1/2 they guess
	 * true, otherwise they guess false.
	 */
	public boolean guessSecret() {
		// Guess the secret. If knowledgeState is larger than 1/2 return true
		// and vice versa
		boolean guess;
		if (knowledgeState.getDoubleValue() >= 0.5) {
			knowledgeState = BigFraction.ONE;
			guess = true;
		} else {
			knowledgeState = BigFraction.ZERO;
			guess = false;
		}
		// The spy finds out the secret and updated knowledge accordingly
		if (secret) {
			knowledgeState = BigFraction.ONE;
		} else {
			knowledgeState = BigFraction.ZERO;
		}

		return guess;

	}

	/**
	 * Returns the value of the secret.
	 */
	public boolean getSecret() {
		// return the value of the secret
		return secret;
	}

	/**
	 * Returns the Spy's current knowledge state.
	 */
	public BigFraction getKnowledgeState() {
		// return the current knowledgeState
		return knowledgeState;
	}

	/**
	 * Checks if the class is internally consistent.
	 */
	public boolean checkInv() {
		return SECRET_BIAS.isAProbability() && knowledgeState.isAProbability();
	}

}
