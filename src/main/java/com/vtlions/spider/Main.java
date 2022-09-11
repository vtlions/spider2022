package com.vtlions.spider;

import java.sql.SQLException;

public class Main {

	public static void main(String[] args) {

		final String LINKTODB = "jdbc:mysql://localhost/?user=root&password=";

		TableCreator table = new TableCreator();
		EngineOfGame engine = new EngineOfGame(table.getMapOfCards(), table.getGeneralListOfCards());
		Sound sound = new Sound();
		GUI gui = new GUI(table.getGeneralListOfCards(), table, engine, sound);
		Timer timer=new Timer(gui);

		engine.setGUILink(gui);
		gui.setTimerLink(timer);

		try {
			DBOperator dbOperator = new DBOperator(LINKTODB, gui, timer);
			gui.setDBOperator(dbOperator);
			dbOperator.checkIsThereSomeSaving();
		} catch (SQLException e) {
			gui.cantConnectToDB();
			e.printStackTrace();
		}

	}
}
