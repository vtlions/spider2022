package com.vtlions.spider;

public class Card {
	// this class was written to create an object "card"

	private int value; // value of a card: 1-Ace, 2, 3,...., 11-Jack, 12-Queen, 13-King

	private boolean isActive; // active card can be taken by player
	private boolean isOpened; // opened card means player can see its value

	public Card(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isOpened() {
		return isOpened;
	}

	public void setOpened(boolean isOpen) {
		this.isOpened = isOpen;
	}

	@Override
	public String toString() {
		return "Card [value=" + value + ", isActive=" + isActive + ", isOpened=" + isOpened + "]";
	}
}
