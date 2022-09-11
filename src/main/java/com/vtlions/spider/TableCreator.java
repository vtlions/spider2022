package com.vtlions.spider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableCreator {
	// this class creates the game table of cards - sets list of used cards and its
	// order, list of still unused cards

	final int QUANTITY_OF_TYPES_OF_CARDS = 13; // Ace-1, 2,3,...,Jack-11, Queen-12, King-13
	final int QUANTITY_OF_COLUMNS_OF_CARDS = 10; // spider solitaire has 10 columns of cards from the beginning
	final int QUANTITY_OF_PACK_OF_CARDS = 8; // spider solitaire uses for game 8 full packs of cards (from Ace to King)

	List<Card> generalListOfCards;
	Map<Integer, List<Card>> mapOfCards;

	TableCreator() {
		generalListOfCards = new ArrayList<>();
		// create playing cards necessary for game: 8 full packs of cards (from Ace to
		// King) (8 sets*13 types=104 cards)
		for (int j = 0; j < QUANTITY_OF_PACK_OF_CARDS; j++) {
			for (int i = 1; i < QUANTITY_OF_TYPES_OF_CARDS + 1; i++) {
				generalListOfCards.add(new Card(i));
			}
		}

		mapOfCards = new HashMap<>();

		// create map of cards from 10 columns of cards with 5-6 rows of cards

		for (int column = 0; column < QUANTITY_OF_COLUMNS_OF_CARDS; column++) {
			List<Card> columnsOfCards = new ArrayList<>();
			int random;

			for (int row = 0; row < 5; row++) {
				random = (int) (Math.random() * generalListOfCards.size());
				// randomly choose a card from earlier created 104 cards and add it into column
				columnsOfCards.add(generalListOfCards.get(random));
				generalListOfCards.remove(random); // remove chosen card from unused list of cards
			}

			if (column < 4) { // the first 4 columns must have 6 rows of cards
				random = (int) (Math.random() * generalListOfCards.size());
				columnsOfCards.add(generalListOfCards.get(random)); // add additional random card from unused cards into
																	// column 0-3
				generalListOfCards.remove(random);
			}
			columnsOfCards.get(columnsOfCards.size() - 1).setOpened(true); // last card of each column must be opened
			columnsOfCards.get(columnsOfCards.size() - 1).setActive(true); // last card of each column must be active

			mapOfCards.put(column, columnsOfCards); // put each column into map of cards
		}
	}

	public List<Card> getGeneralListOfCards() {
		return generalListOfCards;
	}

	public void setGeneralListOfCards(final List<Card> generalListOfCards) {
		this.generalListOfCards = generalListOfCards;
	}

	public Map<Integer, List<Card>> getMapOfCards() {
		return mapOfCards;
	}

	public void setMapOfCards(final Map<Integer, List<Card>> mapOfCards) {
		this.mapOfCards = mapOfCards;
	}

	public void addCard(final int column, final Card card) {
		mapOfCards.get(column).add(card);
	}

	public void removeCard(final int column, final int row) {
		mapOfCards.get(column).remove(row);
	}

	public Card getCard(final int column, final int row) {
		return mapOfCards.get(column).get(row);
	}

	public int getSizeOfColumn(final int column) {
		return mapOfCards.get(column).size();
	}
}
