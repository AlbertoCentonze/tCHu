package ch.epfl.tchu.gui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

final public class Audio {
    public static void main(String[] args){
        play("music.wav");
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
}
