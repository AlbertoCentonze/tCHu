package ch.epfl.tchu.gui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

final public class Audio {
    private Clip background;

    public Audio(String fileToPlay) {
        try{
            File soundFile = new File("./resources/" + fileToPlay);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( soundFile );
            this.background = AudioSystem.getClip();
            background.open(audioInputStream);
            background.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void play(String fileToPlay) {
        try{
            File soundFile = new File("./resources/" + fileToPlay);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( soundFile );
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void playBackgroundMusic() {
        background.start();
    }

    public void stopBackgroundMusic() {
        background.stop();
    }
}
