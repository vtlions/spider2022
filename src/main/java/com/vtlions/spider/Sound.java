package com.vtlions.spider;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound implements Runnable {
	Clip clip;

	public Sound() {
		new Thread(this).start();
	}

	public Clip getClip() {
		return clip;
	}

	public void stop() {
		clip.stop();
	}

	public void start() {
		clip.start();
		clip.loop(1000000000);
	}

	@Override
	public void run() {
		try {
			File soundFile = new File("src/sound/snd2.wav");

			AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);

			clip = AudioSystem.getClip();

			clip.open(ais);

			clip.setFramePosition(0);
			clip.start();
			clip.loop(1000000000);

		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
