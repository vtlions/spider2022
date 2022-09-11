package com.vtlions.spider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBOperator {
	// this class was created to save/load current state of a game into/from a
	// database

	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	final int QUANTITY_OF_COLUMNS_OF_CARDS = 10;
	private Timer timer;
	GUI gui;

	public DBOperator(final String linkToDB, final GUI gui, final Timer timer) throws SQLException {
		this.gui = gui;
		this.timer=timer;
		try {
			System.out.print("Loading driver to connect to database...");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.print(" Successfully\n");
		} catch (Exception ex) {
			System.out.print("UNSUCCESSFULLY!!!\n The program will be terminated.\n");
			System.exit(0);
		}

		System.out.print("Connecting to the database...");

		if (linkToDB != null) {
			connection = DriverManager.getConnection(linkToDB);
		} else {
			System.out.println(
					"UNSUCCESSFULLY!!!\nThe link to database is null. Impossible to establish connections. The program will be terminated. ");
			System.exit(0);
		}

		System.out.print(" Succesfully\n\n");

		if (connection != null) {
			statement = connection.createStatement();
		} else {
			System.out.println("The connection is null. Is your database server online?");
		}

	}

	// method was created to save current state of game into database
	public void saveStateOfGameToDB(final Map<Integer, List<Card>> mapOfCards, final List<Card> generalListOfCards,
			int winCounter) throws SQLException {

		int valueOfCard = 0;
		int cardOpenedFlag = 0;
		int cardActiveFlag = 0;
		Card currentCard;

		statement.execute("DROP DATABASE IF EXISTS spider;"); // drop all previous saved data from database
		statement.execute("CREATE DATABASE IF NOT EXISTS spider;"); // create database
		statement.execute("USE spider;");

		for (int i = 0; i < QUANTITY_OF_COLUMNS_OF_CARDS; i++) {

			statement.execute("CREATE TABLE IF NOT EXISTS column" + i + "(row TINYINT PRIMARY KEY, "
					+ "card_value TINYINT, card_opened TINYINT, card_active TINYINT); ");
		}

		for (int column = 0; column < QUANTITY_OF_COLUMNS_OF_CARDS; column++) {

			for (int row = 0; row < mapOfCards.get(column).size(); row++) {
				currentCard = mapOfCards.get(column).get(row);
				valueOfCard = currentCard.getValue();
				if (currentCard.isActive()) {
					cardActiveFlag = 1;
				}

				if (currentCard.isOpened()) {
					cardOpenedFlag = 1;
				}

				// save used cards into database column by column
				statement.execute(
						"INSERT INTO column" + column + " (row,card_value, card_opened, card_active) " + "VALUES ('"
								+ row + "','" + valueOfCard + "','" + cardOpenedFlag + "','" + cardActiveFlag + "')");
				cardOpenedFlag = 0;
				cardActiveFlag = 0;
			}
		}

		// save list of still unused cards
		statement.execute(
				"CREATE TABLE IF NOT EXISTS list_of_unused_cards (id TINYINT PRIMARY KEY, " + "card_value TINYINT); ");

		for (int i = 0; i < generalListOfCards.size(); i++) {
			currentCard = generalListOfCards.get(i);
			valueOfCard = currentCard.getValue();
			statement.execute(
					"INSERT INTO list_of_unused_cards (id,card_value) " + "VALUES ('" + i + "','" + valueOfCard + "')");
		}

		// save win counter. It shows how many sets of cards (from King to Ace) are done
		statement.execute(
				"CREATE TABLE IF NOT EXISTS win(id TINYINT PRIMARY KEY AUTO_INCREMENT, win_counter TINYINT); ");
		statement.execute("INSERT INTO win (win_counter) " + "VALUES ('" + winCounter + "')");
		
		//save timer position
		statement.execute(
				"CREATE TABLE IF NOT EXISTS timer (id TINYINT PRIMARY KEY AUTO_INCREMENT, timer INT); ");
		statement.execute("INSERT INTO timer (timer) " + "VALUES ('" + timer.getSeconds() + "')");
	}

	// method was created to load previously saved state of game from database and
	// set it up into current game
	public void loadStateOfGameFromDB() throws SQLException {

		int valueOfCard = 0;
		int cardOpenedFlag = 0;
		int cardActiveFlag = 0;
		Card currentCard;
		int winCounter = 0;

		int index, row;
		List<Card> generalListOfCards = new ArrayList<>();
		List<Card> columnOfCards;

		Map<Integer, List<Card>> mapOfCards = new HashMap<>();

		statement.execute("USE spider");

		for (int column = 0; column < QUANTITY_OF_COLUMNS_OF_CARDS; column++) {
			columnOfCards = new ArrayList<>();
			resultSet = statement.executeQuery("SELECT row,card_value, card_opened, card_active FROM column" + column);

			// load information about used cards
			while (resultSet.next()) {

				row = resultSet.getInt("row");
				valueOfCard = resultSet.getInt("card_value");
				cardOpenedFlag = resultSet.getInt("card_opened");
				cardActiveFlag = resultSet.getInt("card_active");
				currentCard = new Card(valueOfCard);

				if (cardOpenedFlag == 1) {
					currentCard.setOpened(true);
				}

				if (cardActiveFlag == 1) {
					currentCard.setActive(true);
				}

				columnOfCards.add(row, currentCard);
			}

			mapOfCards.put(column, columnOfCards);
		}

		resultSet = statement.executeQuery("SELECT id,card_value FROM list_of_unused_cards");

		// load list of still unused cards
		while (resultSet.next()) {
			valueOfCard = resultSet.getInt("card_value");
			index = resultSet.getInt("id");
			currentCard = new Card(valueOfCard);
			generalListOfCards.add(index, currentCard);
		}

		// load winCounter. It shows how many sets of cards (from King to Ace) are done
		resultSet = statement.executeQuery("SELECT win_counter FROM win");

		while (resultSet.next()) {
			winCounter = resultSet.getInt("win_counter");
		}

		gui.restoreWindowFromSave(mapOfCards, generalListOfCards, winCounter); // send loaded data into class GUI to
		
		// draw necessary order of cards
		
		resultSet = statement.executeQuery("SELECT timer FROM timer");
		while (resultSet.next()) {
			timer.setSeconds(resultSet.getInt("timer"));
		}
		//restore timer position
	}

	//method to check if there is some saved data in the DB
	public Boolean checkIsThereSomeSaving() throws SQLException {

		resultSet = statement.executeQuery("SHOW DATABASES LIKE 'spider'");

		while (resultSet.next()) {
			return resultSet.getString(1).equals("spider");
		}
		
		return false;
	}
}
