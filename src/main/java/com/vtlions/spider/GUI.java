package com.vtlions.spider;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class GUI extends JFrame {
// this class created to draw frame with cards on it

	private static final long serialVersionUID = -3625358708793799514L;
	private final int QUANTITY_OF_COLUMNS_OF_BUTTONS = 10;
	private final int QUANTITY_OF_DISTRIBUTION_BUTTONS = 5;

	private final int STEP_X = 95;
	private final int STEP_Y = 35;

	private Map<Integer, JButton> emptyButtonsMap = new HashMap<>();
	private Map<Integer, List<JButton>> mapOfButtons = new HashMap<>();
	private List<JButton> listOfButtonsForDistribution = new ArrayList<>();
	private JLabel label1, errorDBLabel;
	private DBOperator dbOperator;
	private List<JLabel> listOfDoneLabels = new ArrayList<>();
	private List<Card> generalListOfCards;
	private Map<Integer, List<Card>> mapOfCards;
	private int firstChosenCardValue, secondChosenCardValue;
	private int columnFirstChosenButoon, rowFirstChosenButoon, columnSecondChosenButoon, rowSecondChosenButoon;
	private JFrame winFrame;
	private JButton saveButton, loadButton;
	private JButton firstChosenButton, secondChosenButton;
	private JButton soundButton;
	private ImageIcon icon = new ImageIcon("src/spider/cards/ico/ico.png");
	private TableCreator tableCreator;
	private EngineOfGame engine;
	private JLayeredPane layered = new JLayeredPane();
	private List<ImageIcon> listOfWhiteImages = new ArrayList<>();
	private List<ImageIcon> listOfChosenImages = new ArrayList<>();
	private Sound sound;
	private JLabel timerLabel;
	private Clip clip;
	private Timer timer;

	public GUI(final List<Card> generalListOfCards, final TableCreator tableCreator, EngineOfGame engine, Sound sound) {

		super("Spider solitaire");
		this.generalListOfCards = generalListOfCards;
		this.tableCreator = tableCreator;
		this.engine = engine;
		this.sound = sound;
		clip = sound.getClip();

		setIconImage(icon.getImage());
		setContentPane(layered);

		int x = STEP_X * 9;
		int y = 0;

		for (int i = 0; i < 14; i++) {
			// create list of card's pictures
			listOfWhiteImages.add(i, new ImageIcon("src/ico/" + i + ".png"));
			// create list of card's pictures when card was chosen by user
			listOfChosenImages.add(i, new ImageIcon("src/ico/" + i + "c.png"));

			if (listOfWhiteImages.get(i).getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
				handleErrors("ERROR GUI CONSTRUCTOR: can't find picture file 'src/ico/" + i + ".png'");
			}

			if (listOfChosenImages.get(i).getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
				handleErrors("ERROR GUI CONSTRUCTOR: can't find picture file 'src/spider/cards/ico/" + i + "c.png'");
			}

		}
		createColumnsOfCardButtons(false); // method to draw cards on frame

// creation of DISTRIBUTION BUTTONS	- 5 buttons to distributes unused cards on 10 columns of used cards
		y = 20;
		for (int k = 0; k < QUANTITY_OF_DISTRIBUTION_BUTTONS; k++) {

			listOfButtonsForDistribution.add(new JButton());
			listOfButtonsForDistribution.get(k).setBounds(x + 380, y, 88, 128);
			if (k != 4) {
				listOfButtonsForDistribution.get(k).setEnabled(false);
			}
			listOfButtonsForDistribution.get(k).setIcon(listOfWhiteImages.get(0));
			layered.add(listOfButtonsForDistribution.get(k), (Integer) (k + 1));
			initListenersForDistributionsButtons(listOfButtonsForDistribution.get(k));
			y = y + STEP_Y;
		}

		label1 = new JLabel("Distribution of cards");
		label1.setBounds(x + 365, 0, 200, 20);
		label1.setVisible(true);
		layered.add(label1);

//creation of DONE LABELS - these labels show how many packs of card have been completed by player	
		y = 100;
		for (int k = 0; k < 8; k++) {
			listOfDoneLabels.add(new JLabel("SET " + (k + 1) + " DONE!"));
			listOfDoneLabels.get(k).setBounds(x + 210, y + 50, 150, 96);
			listOfDoneLabels.get(k).setVisible(false);
			layered.add(listOfDoneLabels.get(k));
			y = y + 40;
		}

//creation buttons to save/load game 
		saveButton = new JButton();
		loadButton = new JButton();
		soundButton = new JButton();

		ImageIcon tempImage = new ImageIcon("src/ico/load64.png");
		if (tempImage.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
			handleErrors("ERROR GUI CONSTRUCTOR: can't find picture file 'src/spider/cards/ico/load64.png'");
		}
		loadButton.setBounds(x + 250, 30, 64, 64);
		loadButton.setIcon(tempImage);
		loadButton.setToolTipText("Load game");

		tempImage = new ImageIcon("src/ico/save64.png");
		if (tempImage.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
			handleErrors("ERROR GUI CONSTRUCTOR: can't find picture file 'src/spider/cards/ico/save64.png'");
		}

		saveButton.setBounds(x + 160, 30, 64, 64);
		saveButton.setIcon(tempImage);
		saveButton.setToolTipText("Save game");

		initListenersLoadSaveButtons();

		soundButton.setBounds(1258, 310, 40, 40);
		initListenersSoundButtons();

		timerLabel = new JLabel("");
		timerLabel.setBounds(1035, 125, 150, 50);
		timerLabel.setFont(new Font("Serif", Font.PLAIN, 50));
		timerLabel.setToolTipText("Time left until the end of game");

		tempImage = new ImageIcon("src/ico/soundOn.png");
		if (tempImage.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
			handleErrors("ERROR GUI CONSTRUCTOR: can't find picture file 'src/ico/soundOn.png'");
		}
		soundButton.setIcon(tempImage);

		layered.add(loadButton);
		layered.add(saveButton);
		layered.add(soundButton);
		layered.add(timerLabel);

		errorDBLabel = new JLabel("Can't connect to database. Save/Load imposible");
		errorDBLabel.setBounds(x + 105, 100, 300, 20);
		errorDBLabel.setVisible(false);
		layered.add(errorDBLabel);

// SETUP OF THE FRAME	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1450, 1000);
		setResizable(false);
		setLayout(null);
		setVisible(true);
	}

// WHAT TO DO IF CARD BUTTON IS CLICKED
	private void initListenersForCardButtons(JButton button1) {

		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Boolean isItEmptyButton = button1.getText().equals("EMPTY");

				if (!isItEmptyButton) { // Empty button created at columns with 0 cards

					if (firstChosenCardValue == 0) { // if player click on the first card
						columnFirstChosenButoon = (button1.getX() + 10) / STEP_X;
						rowFirstChosenButoon = (button1.getY() + 10) / STEP_Y;
						firstChosenCardValue = tableCreator.getCard(columnFirstChosenButoon, rowFirstChosenButoon)
								.getValue();
						firstChosenButton = mapOfButtons.get(columnFirstChosenButoon).get(rowFirstChosenButoon);
						firstChosenButton.setIcon(listOfChosenImages.get(firstChosenCardValue)); // show chosen card

					} else { // if player has already chose first card the next click will choose second card

						columnSecondChosenButoon = (button1.getX() + 10) / STEP_X;
						rowSecondChosenButoon = (button1.getY() + 10) / STEP_Y;
						secondChosenButton = mapOfButtons.get(columnSecondChosenButoon).get(rowSecondChosenButoon);
						secondChosenCardValue = tableCreator.getCard(columnSecondChosenButoon, rowSecondChosenButoon)
								.getValue();
						firstChosenButton.setIcon(listOfWhiteImages.get(firstChosenCardValue));
					}
				}

				if (firstChosenCardValue > 0 && isItEmptyButton) { // if as a second card player clicked on Empty button
					firstChosenButton.setIcon(listOfWhiteImages.get(firstChosenCardValue));
					secondChosenCardValue = firstChosenCardValue + 1;
					columnSecondChosenButoon = (button1.getX() + 10) / STEP_X;
					rowSecondChosenButoon = -1;
					remove(button1);
					secondChosenButton = firstChosenButton;
				}

				if (firstChosenCardValue != 0 && secondChosenCardValue != 0) {
					// nothing to do until chosen first and second card

					if ((secondChosenCardValue - firstChosenCardValue == 1)) {
						// value of first card must be higher on 1 than value of second card to start
						// moving cards on table

						layered.setComponentZOrder(secondChosenButton, (Integer) 20);
						layered.setComponentZOrder(firstChosenButton, (Integer) 10);

						// method to move chosen (first) card on second card
						moveButtonAndCard(columnFirstChosenButoon, rowFirstChosenButoon, columnSecondChosenButoon,
								rowSecondChosenButoon);
					}

					columnFirstChosenButoon = 0;
					rowFirstChosenButoon = 0;
					columnSecondChosenButoon = 0;
					rowSecondChosenButoon = 0;
					secondChosenCardValue = 0;
					firstChosenCardValue = 0;
				}
			}
		});
	}

// method MOVING CARDS AND BUTTONS BETWEEN COLUMNS
	private void moveButtonAndCard(int column1, int row1, int column2, int row2) {

		JButton movingButton;// upperButton;
		Card movingCard;// upperCard;

		int quantityOfAdditionallyMovingCards = 0;

		quantityOfAdditionallyMovingCards = mapOfButtons.get(column1).size() - 1 - row1;
		movingButton = mapOfButtons.get(column1).get(row1);
		movingButton.setBounds(column2 * STEP_X + 10, (row2 + 1) * STEP_Y + 10, 88, 128);
		mapOfButtons.get(column2).add(movingButton);
		movingCard = tableCreator.getCard(column1, row1);
		tableCreator.addCard(column2, movingCard);

		if (row1 > 0) {

			mapOfButtons.get(column1).remove(row1);
			tableCreator.removeCard(column1, row1);

			mapOfButtons.get(column1).get(row1 - 1).setDisabledIcon(null);
			mapOfButtons.get(column1).get(row1 - 1).setEnabled(true);

			tableCreator.getCard(column1, row1 - 1).setActive(true);
			tableCreator.getCard(column1, row1 - 1).setOpened(true);

		} else {
			// creation of EMPTY BUTTON
			mapOfButtons.get(column1).remove(row1);
			tableCreator.removeCard(column1, row1);
			createEmptyButton(column1);
		}

		setEnabledMultipleCards(column1, row1);

		if (quantityOfAdditionallyMovingCards > 0) {

			for (int u = 0; u < quantityOfAdditionallyMovingCards; u++) {
				movingButton = mapOfButtons.get(column1).get(row1);
				movingButton.setBounds(column2 * STEP_X + 10, (row2 + 1 + u + 1) * STEP_Y + 10, 88, 128);
				mapOfButtons.get(column2).add(movingButton);
				movingCard = tableCreator.getCard(column1, row1);
				tableCreator.addCard(column2, movingCard);
				mapOfButtons.get(column1).remove(row1);
				tableCreator.removeCard(column1, row1);
			}
		}

		repaint();

		CheckCompletionOfColumn(column2);

		properOrderDrawingCards(mapOfButtons.get(column2));
	}

//method to prioritize drawing of layout elements on a frame 	
	private void properOrderDrawingCards(List<JButton> list) {

		int a = 0;

		for (int i = list.size() - 1; i >= 0; i--) {
			a++;
			layered.setComponentZOrder(list.get(i), (Integer) (a));
		}

	}

//if some cards in the column are in proper order (value of upper card is bigger on 1 that value of lower card) all these cards must be active	
	private void setEnabledMultipleCards(int column1, int row1) {
		JButton upperButton;
		Card upperCard;

		for (int k = row1 - 2; k >= 0; k--) {

			upperButton = mapOfButtons.get(column1).get(k);
			upperCard = tableCreator.getCard(column1, k);

			if (upperCard.isOpened()) {

				if (upperCard.getValue() - tableCreator.getCard(column1, k + 1).getValue() == 1) {
					upperButton.setEnabled(true);
					upperCard.setActive(true);
					repaint();
				} else {
					break;
				}

			} else {
				break;
			}
		}
	}

//create Empty button if column has 0 cards
	private void createEmptyButton(int column) {

		JButton tempButton = new JButton("EMPTY");
		emptyButtonsMap.put(column, tempButton);
		emptyButtonsMap.get(column).setBounds(column * STEP_X + 10, 10, 88, 128);
		layered.add(emptyButtonsMap.get(column), (Integer) (10));
		// repaint();

		initListenersForCardButtons(emptyButtonsMap.get(column));
		repaint();
	}

//method to check if full pack of cards has been completed
	private void CheckCompletionOfColumn(int column2) {

		int valueOfLowerCard, valueOfUpperCard;
		int sizeOfColumn2 = tableCreator.getSizeOfColumn(column2);
		int checkColumnCounter = 0;

		if (sizeOfColumn2 >= 13) {
			for (int m = sizeOfColumn2 - 1; m > sizeOfColumn2 - 1 - 12; m--) {
				valueOfLowerCard = tableCreator.getCard(column2, m).getValue();
				valueOfUpperCard = tableCreator.getCard(column2, m - 1).getValue();

				if (valueOfUpperCard - valueOfLowerCard == 1) {
					checkColumnCounter++;
				}
			}

			if (checkColumnCounter == 12) {
				engine.increaseWinCounter();
				listOfDoneLabels.get(engine.getWinCounter() - 1).setVisible(true);

				for (int m = 0; m < 13; m++) {
					remove(mapOfButtons.get(column2).get(sizeOfColumn2 - 1));
					mapOfButtons.get(column2).remove(sizeOfColumn2 - 1);
					tableCreator.removeCard(column2, sizeOfColumn2 - 1);
					sizeOfColumn2 = tableCreator.getSizeOfColumn(column2);
				}

				if (sizeOfColumn2 == 0) {
					createEmptyButton(column2);
				}

				if (sizeOfColumn2 == 1) {
					mapOfButtons.get(column2).get(sizeOfColumn2 - 1).setEnabled(true);
					tableCreator.getCard(column2, sizeOfColumn2 - 1).setActive(true);
					tableCreator.getCard(column2, sizeOfColumn2 - 1).setOpened(true);
				}

				if (sizeOfColumn2 > 1) {
					mapOfButtons.get(column2).get(sizeOfColumn2 - 1).setEnabled(true);
					tableCreator.getCard(column2, sizeOfColumn2 - 1).setActive(true);
					tableCreator.getCard(column2, sizeOfColumn2 - 1).setOpened(true);
					setEnabledMultipleCards(column2, sizeOfColumn2);
				}
				repaint();
			}
		}
	}

//method to show victory/loosing window 	
	public void toWinOrLose(String message) {
		setEnabled(false);
		int width = 300;
		int hight = 300;
		JButton winButton = new JButton();
		winFrame = new JFrame(message);
		winFrame.setResizable(false);
		winFrame.add(winButton);
		winButton.setIcon(new ImageIcon("src/ico/win.png"));
		winFrame.setIconImage(icon.getImage());
		winFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		winFrame.setBounds((getX() + getWidth() - width) / 2, (getY() + getHeight() - hight) / 2, width, hight);
		winFrame.setVisible(true);
		clip.stop();
		clip.close();
		timer.setGameOver(true);
	}

	// what to do if save/load button was clicked
	private void initListenersLoadSaveButtons() {
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// if save current game into DB background of loadButton must be green to show
				// available saving
				try {
					dbOperator.saveStateOfGameToDB(tableCreator.getMapOfCards(), tableCreator.getGeneralListOfCards(),
							engine.getWinCounter());
					loadButton.setBackground(Color.GREEN);
					loadButton.setEnabled(true);

				} catch (SQLException e1) {

					loadButton.setBackground(null);
					cantConnectToDB();
					e1.printStackTrace();
				}
			}
		});

		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					dbOperator.loadStateOfGameFromDB();
				} catch (SQLException e1) {
					cantConnectToDB();
					e1.printStackTrace();
				}
			}
		});
	}

	// what to do if sound button was pressed
	private void initListenersSoundButtons() {

		soundButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageIcon tempImage;
				if (clip.isRunning()) {
					sound.stop();
					tempImage = new ImageIcon("src/ico/soundOff.png");
					if (tempImage.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
						handleErrors("ERROR GUI: can't find picture file 'src/ico/soundOff.png'");
					}
					soundButton.setIcon(tempImage);

				} else {
					sound.start();
					tempImage = new ImageIcon("src/ico/soundOn.png");
					if (tempImage.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
						handleErrors("ERROR GUI CONSTRUCTOR: can't find picture file 'src/ico/soundOn.png'");
					}
					soundButton.setIcon(tempImage);
				}
			}
		});

	}

// DISTRIBUTION OF NEW CARDS

	private void initListenersForDistributionsButtons(JButton button1) {

		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				listOfButtonsForDistribution.remove(listOfButtonsForDistribution.size() - 1);

				if (listOfButtonsForDistribution.size() > 0) {

					listOfButtonsForDistribution.get(listOfButtonsForDistribution.size() - 1).setEnabled(true);
				}

				Card addingCard, upperCard;

				JButton addingButton, upperButton;
				int row;
				int valueOfAddingCard;
				int random;

				for (int column = 0; column < QUANTITY_OF_COLUMNS_OF_BUTTONS; column++) {

					random = (int) (Math.random() * generalListOfCards.size());

					addingCard = generalListOfCards.get(random);
					addingCard.setActive(true);
					addingCard.setOpened(true);
					generalListOfCards.remove(random);
					tableCreator.addCard(column, addingCard);
					mapOfButtons.get(column).add(new JButton());
					row = mapOfButtons.get(column).size() - 1;
					addingButton = mapOfButtons.get(column).get(row);
					addingButton.setBounds(10 + column * STEP_X, 10 + row * STEP_Y, 88, 128);
					valueOfAddingCard = addingCard.getValue();
					addingButton.setIcon(listOfWhiteImages.get(valueOfAddingCard));

					if (row >= 1) {
						upperButton = mapOfButtons.get(column).get(row - 1);
						upperCard = tableCreator.getCard(column, row - 1);

						if (upperCard.getValue() - valueOfAddingCard != 1) {
							upperButton.setDisabledIcon(null);
							upperButton.setEnabled(false);
							upperCard.setActive(false);
							upperCard.setOpened(true);

							for (int v = row - 2; v >= 0; v--) {

								if (mapOfButtons.get(column).get(v).isEnabled()) {
									mapOfButtons.get(column).get(v).setEnabled(false);
									tableCreator.getCard(column, v).setActive(false);
									tableCreator.getCard(column, v).setOpened(true);
									mapOfButtons.get(column).get(v).setDisabledIcon(null);
								}
							}
						}
					} else {
						remove(emptyButtonsMap.get(column));
					}

					layered.add(addingButton, (Integer) (6));
					properOrderDrawingCards(mapOfButtons.get(column));
					initListenersForCardButtons(addingButton);
					repaint();
					remove(button1);
				}
			}
		});
	}

	public void setDBOperator(DBOperator dbOperator) throws SQLException {
		this.dbOperator = dbOperator;

		if (dbOperator.checkIsThereSomeSaving()) { // if there is some saved game in DB - loadButton must be green
			loadButton.setBackground(Color.GREEN);
		} else {
			loadButton.setBackground(null);
			loadButton.setEnabled(false);
		}
	}

//method to restore window of the saved game (method is called by DBOperator class)
	public void restoreWindowFromSave(Map<Integer, List<Card>> mapOfCards, List<Card> generalListOfCards,
			int winCounter) {
		engine.setWinCounter(winCounter); // wincounter loaded from save send into engine of the game
		this.mapOfCards = mapOfCards; // get loaded from DB map of used cards
		this.generalListOfCards = generalListOfCards; // get loaded from DB list of unused cards
		int temp = 0;
		int x = 0;
		int y = 0;
		int quantityOfDistributionCards = generalListOfCards.size() / 10;

		tableCreator.setGeneralListOfCards(generalListOfCards); // send loaded from DB list of unused card to
																// TableCreator class
		tableCreator.setMapOfCards(mapOfCards); // send loaded from DB map of used card to TableCreator class

		// restore saved state of DONE LABELS
		for (int i = 0; i < 8; i++) {
			if (i < winCounter) {
				listOfDoneLabels.get(i).setVisible(true);
			} else {
				listOfDoneLabels.get(i).setVisible(false);
			}
		}

		// restore saved state of DISTRIBUTION BUTTONS
		if (listOfButtonsForDistribution.size() < quantityOfDistributionCards) {
			temp = listOfButtonsForDistribution.size();

			if (temp > 0) {
				listOfButtonsForDistribution.get(temp - 1).setEnabled(false);
				x = listOfButtonsForDistribution.get(temp - 1).getX();
				y = listOfButtonsForDistribution.get(temp - 1).getY();

			} else {
				y = 20 - STEP_Y;
				x = STEP_X * 10 + 380;
			}

			int quantityOfAddinButton = quantityOfDistributionCards - temp;

			for (int i = 0; i < quantityOfAddinButton; i++) {
				listOfButtonsForDistribution.add(new JButton());
				listOfButtonsForDistribution.get(temp).setBounds(x, y + STEP_Y, 88, 128);
				listOfButtonsForDistribution.get(temp).setEnabled(false);
				listOfButtonsForDistribution.get(temp).setIcon(listOfWhiteImages.get(0));
				layered.add(listOfButtonsForDistribution.get(temp), (Integer) (3));
				initListenersForDistributionsButtons(listOfButtonsForDistribution.get(temp));
				temp = listOfButtonsForDistribution.size();
				y = listOfButtonsForDistribution.get(temp - 1).getY();
			}

			listOfButtonsForDistribution.get(listOfButtonsForDistribution.size() - 1).setEnabled(true);

		} else if (listOfButtonsForDistribution.size() > quantityOfDistributionCards) {

			temp = listOfButtonsForDistribution.size();
			int quantityOfDeletingButtons = temp - quantityOfDistributionCards;

			for (int i = 0; i < quantityOfDeletingButtons; i++) {
				remove(listOfButtonsForDistribution.get(temp - 1));
				listOfButtonsForDistribution.remove(temp - 1);
				temp = listOfButtonsForDistribution.size();
			}

			if (listOfButtonsForDistribution.size() > 0) {
				listOfButtonsForDistribution.get(listOfButtonsForDistribution.size() - 1).setEnabled(true);
			}
		}

		properOrderDrawingCards(listOfButtonsForDistribution);

		createColumnsOfCardButtons(true);

		repaint();
	}

//method to draw playing cards on frame
	public void createColumnsOfCardButtons(Boolean isNecessaryCleanFromAlreadyCreatedButtons) {
//the method can create cards button from the start of a game or after loading data from DB.
// key isNecessaryCleanFromAlreadyCreatedButtons: false - start of a game, true - restore saved game
		Map<Integer, List<JButton>> tempMapOfButtons = new HashMap<>();
		JButton tempButton, removingButton;
		List<JButton> listOfButtons;
		int x = 0;
		int y = 0;
		int maxRow = 0;

		// creation of 10 columns and 5 rows of buttons
		for (int column = 0; column < QUANTITY_OF_COLUMNS_OF_BUTTONS; column++) {
			listOfButtons = new ArrayList<>();

			if (isNecessaryCleanFromAlreadyCreatedButtons) { // need to clean Empty buttons from game before loading
																// from DB
				if (emptyButtonsMap.get(column) != null) {
					layered.remove(emptyButtonsMap.get(column));
					emptyButtonsMap.remove(column);
				}

				maxRow = tableCreator.getSizeOfColumn(column); // the last row of a column
				int temp = mapOfButtons.get(column).size() - 1; // get the last index of a column of card buttons
																// (current state before loading from DB)

				if (maxRow == 0) { // create new Empty button if loaded column of cards has 0 size
					createEmptyButton(column);
				}

				for (int i = temp; i >= 0; i--) {
					removingButton = mapOfButtons.get(column).get(i);
					layered.remove(removingButton); // remove all card buttons from game before loading
				}

			} else {
				maxRow = 5; // quantity of rows of card buttons from the start of game
			}

			for (int row = 0; row < maxRow; row++) { // create new card buttons
				listOfButtons.add(row, new JButton());
				tempButton = listOfButtons.get(row);
				tempButton.setBounds(10 + x, 10 + y, 88, 128);
				tempButton.setEnabled(false);

				if (isNecessaryCleanFromAlreadyCreatedButtons) { // restore loaded states of cards
					if (tableCreator.getCard(column, row).isActive()) { // restore ACTIVE state of loaded card
						tempButton.setEnabled(true);
					}

					if (tableCreator.getCard(column, row).isOpened()) { // restore OPENED state of loaded card
						tempButton.setDisabledIcon(null);
					}

					layered.add(tempButton, (Integer) (row + 1));
				}

				else { // used to create card buttons during the start of the program
					if (row < 4) {
						layered.add(tempButton, (Integer) (row + 1));
					}

					if (column > 3 & row == 4) {
						layered.add(tempButton, (Integer) (5));
					} else if (column <= 3 & row == 4) {
						layered.add(tempButton, (Integer) (row + 1));
					}

				}
				y = y + STEP_Y;
			}

			// adding of additional one button to each of first 4 columns

			if (!isNecessaryCleanFromAlreadyCreatedButtons) {

				if (column < 4) {
					listOfButtons.add(new JButton());
					tempButton = listOfButtons.get(listOfButtons.size() - 1);
					tempButton.setBounds(10 + x, 10 + y, 88, 128);
					layered.add(tempButton, (Integer) (6));
				}

				listOfButtons.get(listOfButtons.size() - 1).setEnabled(true);
				tableCreator.getCard(column, listOfButtons.size() - 1).setActive(true);
				tableCreator.getCard(column, listOfButtons.size() - 1).setOpened(true);

			}

			if (isNecessaryCleanFromAlreadyCreatedButtons) {
				tempMapOfButtons.put(column, listOfButtons);
			} else {
				mapOfButtons.put(column, listOfButtons);
			}

			y = 0;
			x = x + STEP_X;
		}

		if (isNecessaryCleanFromAlreadyCreatedButtons) {
			mapOfButtons.clear();
			mapOfButtons = tempMapOfButtons;
		}

		connectButtonsAndCards();
	}

// SET CONNECTION BETWEEN BUTTONS AND PICTURES OF CARDS	
	private void connectButtonsAndCards() {

		JButton tempButton;

		int valueOfCard = 0;
		for (int column = 0; column < QUANTITY_OF_COLUMNS_OF_BUTTONS; column++) {

			for (int row = 0; row < mapOfButtons.get(column).size(); row++) {
				valueOfCard = tableCreator.getCard(column, row).getValue();
				tempButton = mapOfButtons.get(column).get(row);
				tempButton.setIcon(listOfWhiteImages.get(valueOfCard));
				if (!tableCreator.getCard(column, row).isOpened()) {
					tempButton.setDisabledIcon(listOfWhiteImages.get(0));
				}

				initListenersForCardButtons(tempButton);
			}
		}
	}

	public void cantConnectToDB() {
		errorDBLabel.setVisible(true);
		loadButton.setBackground(null);
		loadButton.setEnabled(false);
		saveButton.setEnabled(false);
	}

	private void handleErrors(String message) {
		System.out.println(message);
		System.out.println("The program is terminated!");
		System.exit(0);

	}

	public void setTimerLabelText(String time) {
		timerLabel.setText(time);
		;
	}

	public void setTimerLink(Timer timer) {
		this.timer = timer;
	}

}
