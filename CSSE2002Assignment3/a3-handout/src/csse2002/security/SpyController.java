package csse2002.security;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import csse2002.math.BigFraction;

/**
 * The controller for the Spy Simulator.
 */
public class SpyController implements ActionListener {

	// the model of the simulator
	private SpyModel model;
	// the view of the simulator
	private SpyView view;
	// the line separator to be used - this gets the one suitable for the
	// running OS
	private final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Initialises the SpyController for the Spy Simulator.
	 */
	public SpyController(SpyModel model, SpyView view) {
		// invariant: model != null && view != null

		this.model = model;
		this.view = view;
		// Set the action commands for the buttons
		view.cmdRead.setActionCommand("read");
		view.cmdMeet.setActionCommand("meet");
		view.cmdGuess.setActionCommand("guess");

		// set action listeners for the buttons
		view.cmdRead.addActionListener(this);
		view.cmdMeet.addActionListener(this);
		view.cmdGuess.addActionListener(this);

		// Put the initial knowledge of the spy into the window
		setSpyInfo(model.getKnowledgeState());
	}

	public void actionPerformed(ActionEvent e) {
		// determine which button was pressed and do the appropriate function
		if (e.getActionCommand().equals("read")) {
			readInformantsFile();
		}

		if (e.getActionCommand().equals("meet")) {
			meetInformant();
		}

		if (e.getActionCommand().equals("guess")) {
			guessSecret();
		}
	}

	/**
	 * Updates txtSpy with the given BigFraction (that BigFraction being the
	 * knowledge-state of the spy), formatting it appropriately.
	 */
	private void setSpyInfo(BigFraction ks) {
		// Build the string to put into txtSpy
		String spyText = "Spy thinks ...";
		spyText += LINE_SEPARATOR;
		spyText += "Secret is true with probability ";
		spyText += ks.toString();
		view.txtSpy.setText(spyText);
	}

	/**
	 * Updates txtInfo with the given string.
	 */
	private void setInfo(String info) {
		// Set txtInfo to the given string
		view.txtInfo.setText(info);
	}

	/**
	 * Tells the model to read informants from the given filepath (from the
	 * txtInformantsFile field) and sets txtInfo appropriately on a success or
	 * failure.
	 */
	private void readInformantsFile() {
		// Send the file path off to the model for processing
		// addInformants returns true if it adds successfully, false otherwise
		boolean success = model.addInformants(view.txtInformantsFile.getText());
		if (success) {
			setInfo("Informants added from file.");
		} else {
			setInfo("Error reading from file.");
		}

	}

	/**
	 * Calls the meetInformant method of the model, and updates the textArea
	 * with either information about the informant's interaction with the spy
	 * or, if there is no informant to meet, a message informing the user of
	 * this.
	 */
	private void meetInformant() {
		// Tell the model to meet the next informant
		TwoCoinChannel metInformant = model.meetInformant();
		// and set the text in the Spy box
		if (metInformant != null) {
			setSpyInfo(model.getKnowledgeState());
			// Start building the string to put in the info box
			String info = "Informant says ..." + LINE_SEPARATOR
					+ "Heads-bias if true: " + metInformant.getCoinBias(true)
					+ LINE_SEPARATOR + "Heads-bias if false: "
					+ metInformant.getCoinBias(false) + LINE_SEPARATOR
					+ "Result is ... "
					+ (model.getSecret() ? "heads" : "tails") + "!";
			setInfo(info);
		} else {
			setInfo("There is no informant to meet.");
		}
	}

	/**
	 * Has the spy guess the secret. Changes the txtInfo to reflect the result
	 * of the guess.
	 */
	private void guessSecret() {
		// the result of the guess
		boolean guess = model.guessSecret();
		// the value of the secret
		boolean secret = model.getSecret();
		// start building the string to put in txtInfo
		String guessText = "Spy guesses that secret is ";
		guessText += guess;
		guessText += LINE_SEPARATOR;
		guessText += "and is ... ";
		guessText += (secret == guess ? "correct" : "incorrect") + "!";

		setInfo(guessText);

		setSpyInfo(model.getKnowledgeState());
	}

	/**
	 * Checks if the class is internally consistent.
	 */
	public boolean checkInv() {
		return model != null && view != null;
	}
}
