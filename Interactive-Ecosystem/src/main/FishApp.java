package main;
//BONUS (2 points): when animal is moving, there should be a low fidelity animation of the movement, such as running, flying, swimming, crawling, etc.

// I have implemented a bonus low fidelity animation allowing the fish's tail to move while it is swimming (Fish.java, L106 - 112, L 214 - 231)

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFrame;

import config.CtrlPanel;

public class FishApp extends JFrame {

	private static int windowW = 1280;
	private static int windowH = 720;
	private static boolean ctrlPOn = true ;

	public FishApp(String title) {

		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(windowW, windowH);
		this.setVisible(true);

	}

	public static void main(String[] args) {	
		
		FishApp app = new FishApp("My Personal Aquarium");
		CtrlPanel mainControl = new CtrlPanel();
		Aquarium aquarium = new Aquarium(mainControl);
		Container contentPane = app.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;       
	    c.gridy = 0;       
	    contentPane.add(aquarium, c);
	    
	    c.fill = GridBagConstraints.VERTICAL;	
	    c.insets = new Insets(40,20,45,20);  	
	    c.gridx = 1;       
	    c.gridy = 0;       
	    contentPane.add(mainControl, c);
	  
		app.pack();
		app.setVisible(true);
	}

}
