package com.vtlions.spider;

import java.util.List;
import java.util.Map;

public class EngineOfGame {
	// this class created to check whether victory condition has been achieved
	List<Card> generalListOfCards;
	Map<Integer, List<Card>> map;
	int winCounter = 0;
	GUI gui;

	EngineOfGame(final Map<Integer, List<Card>> map, final List<Card> generalListOfCards) {
		this.generalListOfCards = generalListOfCards;
		this.map = map;
	}

	public void increaseWinCounter() {
		winCounter++;
		if (winCounter == 8) {
			gui.toWinOrLose("YOU WIN!!!!"); //when all 8 sets of card have already been complete this method asks GUI to show a victory frame 
		}
	}

	public int getWinCounter() {
		return winCounter;
	}

	public void setWinCounter(int winCounter) {
		this.winCounter = winCounter;
	}

	public void setGUILink(GUI gui) {
		this.gui = gui;
	}
}
