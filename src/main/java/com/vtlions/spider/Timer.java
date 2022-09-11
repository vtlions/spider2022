package com.vtlions.spider;

import java.util.concurrent.TimeUnit;

public class Timer implements Runnable {

	private GUI gui;
	private Integer counter = 601;
	private Long minutes;
	private Long seconds;
	private String secondsStr;
	private String minutesStr;
	private boolean isGameOver;

	public Timer(GUI gui) {

		this.gui = gui;
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (counter-- > 0 && !isGameOver) {

			minutes = TimeUnit.SECONDS.toMinutes(counter);
			seconds = counter - TimeUnit.SECONDS.toMinutes(counter) * 60;

			secondsStr = seconds.toString();
			if (seconds == 0) {
				secondsStr = seconds + "0";
			}

			if (seconds > 0 & seconds < 10) {
				secondsStr = "0" + seconds;
			}

			minutesStr = minutes.toString();

			if (minutes < 10) {
				minutesStr = "0" + minutesStr;
			}

			gui.setTimerLabelText(minutesStr + ":" + secondsStr);

			try {
				TimeUnit.SECONDS.sleep(1);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (!isGameOver) {
			gui.toWinOrLose("YOU LOST!!!!");
		}
	}

	public Integer getSeconds() {
		return counter;
	}

	public void setSeconds(Integer counter) {
		this.counter = counter;
	}

	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}

}
