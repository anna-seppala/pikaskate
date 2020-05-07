package org.pokemon.app;

import java.awt.event.*;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.awt.Insets;

public class MusicPlayer extends JPanel {
    String[] songs = {"sound/opening.wav"};
    ImageIcon muteImg; // to mute / unmute background music
    ImageIcon unmuteImg;
    JButton muteB;
    JButton unmuteB;
    int[] iconSize = {25,25};
    double iconScaling;

    public MusicPlayer() {
	this.muteImg = new ImageIcon(getClass().getClassLoader()
		.getResource("img/mute.png"));
	this.unmuteImg = new ImageIcon(getClass().getClassLoader()
		.getResource("img/unmute.png"));
	this.muteB = new JButton(this.muteImg);
	// make buttons flat and transparent
	this.muteB.setBorderPainted(false);
	this.muteB.setFocusPainted(false);
	this.muteB.setContentAreaFilled(false);
	this.muteB.setMargin(new Insets(0,0,0,0));
	this.muteB.setFocusable(false);
	this.unmuteB = new JButton(this.unmuteImg);
	this.unmuteB.setBorderPainted(false);
	this.unmuteB.setFocusPainted(false);
	this.unmuteB.setContentAreaFilled(false);
	this.unmuteB.setMargin(new Insets(0,0,0,0));
	this.unmuteB.setFocusable(false);


	this.muteB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            System.out.println("pushed mute");
         }          
      });

	this.unmuteB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            System.out.println("pushed unmute");
         }          
      });

	this.setLayout(new FlowLayout());
	this.add(this.muteB);
	this.add(this.unmuteB);
	this.playMusic();
    }


    public void playMusic() 
    {   
	try {
	    AudioInputStream bgMusic = AudioSystem.getAudioInputStream(getClass().getClassLoader()
		.getResource(this.songs[0]));
	    Clip clip = AudioSystem.getClip();
	    clip.open(bgMusic);
	    clip.start();
	    clip.loop(Clip.LOOP_CONTINUOUSLY);
	} catch (UnsupportedAudioFileException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (LineUnavailableException e) {
	     e.printStackTrace();
	}
    }


}// HealthMeter

