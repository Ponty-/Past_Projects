package csse2002.security;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * The view for the Spy Simulator.
 */
@SuppressWarnings("serial")
public class SpyView extends JFrame {

	// the model of the Spy Simulator
	private SpyModel model;

	// Create JPanel for holding components along the bottom
	private JPanel bottomBar;

	/** THESE FIELDS USED FOR TESTING: DO NOT CHANGE DECLARATION! */
	// text area for displaying the current knowledge state of the spy
	public JTextArea txtSpy;
	// scroll panel for containing txtSpy
	private JScrollPane scpSpy;
	// text area for displaying informant interactions and errors
	public JTextArea txtInfo;
	// scroll panel for containing txtInfo
	private JScrollPane scpInfo;
	// informants field: for entering the name of informants to be read
	public JTextField txtInformantsFile;
	// read button: for reading the informants in txtInformantsFile
	public JButton cmdRead;
	// meet informants button: for updating knowledge state of spy after
	// encounter with next informant
	public JButton cmdMeet;
	// guess button: for making the spy guess the secret
	public JButton cmdGuess;

	/** END DECLARATION OF TESTING FIELDS */

	/**
	 * Creates a new Spy Simulator window.
	 */
	public SpyView(SpyModel model) {
		this.model = model;

		// Set up the frame with size, layout and close operation
		setSize(800, 500);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up the spy knowledge area and add it to the GUI
		// White text on back background, not editable
		txtSpy = new JTextArea("", 5, 35);
		txtSpy.setEditable(false);
		txtSpy.setForeground(Color.WHITE);
		txtSpy.setBackground(Color.BLACK);
		scpSpy = new JScrollPane(txtSpy);
		getContentPane().add(scpSpy, BorderLayout.WEST);

		// Set up the informant info area and add it to the GUI
		// White text on back background, not editable
		txtInfo = new JTextArea("", 5, 35);
		txtInfo.setEditable(false);
		txtInfo.setForeground(Color.WHITE);
		txtInfo.setBackground(Color.BLACK);
		scpInfo = new JScrollPane(txtInfo);
		getContentPane().add(scpInfo, BorderLayout.EAST);

		// Set up the size and value of the filepath field
		txtInformantsFile = new JTextField("", 35);

		// Set up the button for the reading informants
		cmdRead = new JButton("Read Informants");

		// Set up the button for meeting informants
		cmdMeet = new JButton("Meet Informant");

		// Set up the button for guessing the secret
		cmdGuess = new JButton("Guess Secret");

		// Create a new JPanel for the buttons and text field on the bottom of
		// the GUI, and add those components
		bottomBar = new JPanel();
		bottomBar.add(txtInformantsFile);
		bottomBar.add(cmdRead);
		bottomBar.add(cmdMeet);
		bottomBar.add(cmdGuess);

		// Add the bottom bar to the GUI at the bottom
		add(bottomBar, BorderLayout.SOUTH);

	}

	/**
	 * Checks if the class is internally consistent.
	 */
	public boolean checkInv() {
		return bottomBar != null && txtSpy != null && scpSpy != null
				&& txtInfo != null && scpInfo != null
				&& txtInformantsFile != null && cmdRead != null
				&& cmdMeet != null && cmdGuess != null;
	}

}
