package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import comparisons.CompareFishPreyByEnergy;
import comparisons.CompareFishPreyByEnergyAndSize;
import comparisons.CompareFishPreyBySize;
import config.CtrlPanel;
import processing.core.PVector;
import sound.MinimHelper;
import fish.Fish;
import fish.FishPredator;
import fish.FishPrey;
import config.CtrlPanel;
import food.Food;
import staticobjects.Concaveshape;
import staticobjects.ObstacleShape;
import staticobjects.Perlinshape;
import ddf.minim.*;
// Bonus Points: When the mouse button is held as long until it is released, the scale of the food will increase incrementally. Go to Line 76 to begin.
import ddf.minim.spi.MinimServiceProvider;

// Bonus (1 point): After an animal has been eaten, make the new animal appear with a delay of 3 seconds (or other value that makes sense in your simulation).
// For this one, a new Fish Prey will reappear every 1.5 seconds approximately. Code begins at Line 140.

// Bonus (2 points): Logging Capability using Logger to create a text file that logs events according to the operating system's local time in seconds.

public class Aquarium extends JPanel implements ActionListener {

	public static double randomDirection;

	private Timer t;

	private ObstacleShape ccShape;
	private ObstacleShape perlinShape;

	// Log file instantiation:
	private Logger aquariumLog;
	private FileHandler fileHandle;

	private DateFormat currentDate = new SimpleDateFormat("dd_mm_yy_hh_mm_ss");
	private Date currentDateObject = new Date();

	private DateTimeFormatter systemTime = DateTimeFormatter.ofPattern("ss.S");
	private LocalDateTime rightNow;

	// Fish-related instantiation:
	private double fishSize;
	private Fish fish;
	private Fish selectThis;
	private int cooldown = 45;
	double randomSpeedX, randomSpeedY;
	double randomFoodX, randomFoodY, randomFoodScale;
	private int sortChoice = 0;
	private boolean alreadySorted = false;
	private int randomX, randomY;
	private boolean isFishDead = false;
	private int prey = 1;
	private int predator = 2;
	List<Fish> fishTank = new ArrayList<Fish>();

	// Food-related instantiation:
	private Food food;
	private boolean doNotCreateFood = false;
	private Food closestFood;
	private double currentScale = 1;
	private boolean isMouseHeld = false;
	ArrayList<Food> foodJar = new ArrayList<Food>();

	// Control panel-related instantiation:
	private CtrlPanel controlP;

	// Audio-visual-related instantiation
	private BufferedImage background;
	private Image bubbles;
	private Minim thisMinim;
	private AudioPlayer ambience;
	private AudioPlayer eatSound;
	private AudioPlayer hatch;
	private AudioPlayer death;
	private Dimension initialSize;
	private float angle = 0;

	public Aquarium(CtrlPanel ctrlP) {
		super();
		setPreferredSize(new Dimension(1280, 720));
		t = new Timer(33, this);
		t.start();
		controlP = ctrlP;
		initialSize = new Dimension(1280, 720);
		addMouseListener(new MyMouseAdapter());
		this.setFocusable(true);
		try {
			background = ImageIO.read(new File("images\\background.png"));
			bubbles = Toolkit.getDefaultToolkit().createImage("images\\bubbles.gif");
		}

		catch (IOException ioe) {
			System.out.println("Image was not found.");
		}

		ccShape = new Concaveshape();
		perlinShape = new Perlinshape();
		aquariumLog = Logger.getLogger("myLog");
		try {
			fileHandle = new FileHandler("aquariumLog_" + currentDate.format(currentDateObject) + ".txt");
			aquariumLog.addHandler(fileHandle);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandle.setFormatter(formatter);
			aquariumLog.info("Beginning Simulation\n");
		} catch (SecurityException se) {
		} catch (IOException ioe) {
		}
		thisMinim = new Minim(new MinimHelper());
		try {
			ambience = thisMinim.loadFile("ambience.mp3");
			if (controlP.ambienceToggle) {
				ambience.loop();
			}

			eatSound = thisMinim.loadFile("eat.mp3");
			hatch = thisMinim.loadFile("hatch.mp3");
			death = thisMinim.loadFile("death.mp3");
		} 
		catch (Exception e) {
			System.out.println("There is something wrong with an audiofile");
		}

	}
	
	

	private class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			try {
				// This boolean variable checks if the mouse button is held down or not. If it
				// is, then begin counting and increasing the scale factor for the food at line
				// 143.
				isMouseHeld = true;
				doNotCreateFood = false;
				for (Food food : foodJar) {
					if (food.checkMouseHit(e)) {
						doNotCreateFood = true;
						double currentClickScale = food.getScale();
						food.setScale(currentClickScale += 0.1);
						if (e.isControlDown()) {
							foodJar.remove(food);
						}
					}
				}
			} catch (ConcurrentModificationException cme) {
				System.out.println("You are removing food too fast. Please wait to remove food.");
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (doNotCreateFood) {
				currentScale = 1;
			}

			if (doNotCreateFood == false && e.isShiftDown() == false) {
				if (e.getX() < 160 && e.getY() < 120) {
					System.out.println("Unable to create food in this space");
				} else {
					createFood(e.getX(), e.getY(), currentScale);
					currentScale = 1;
				}
			}
			isMouseHeld = false;

			for (Fish f : fishTank) {
				if (f.checkMouseHit(e) && e.isShiftDown()) {
					if (selectThis == null) {
						selectThis = f;
						selectThis.stringShow = true;
						if (selectThis instanceof FishPredator) {
							int count = 3;
							for (Fish fishType : fishTank) {
								if (fishType instanceof FishPrey && count > 0) {
									fishType.stringShow = true;
									count -= 1;
								}
							}
						}
					} 
					else if (selectThis != null) {
						for (Fish currentFish : fishTank) {
							currentFish.stringShow = false;
						}

						selectThis = f;
						selectThis.stringShow = true;
						if (selectThis instanceof FishPredator) {
							int count = 3;
							for (Fish fishType : fishTank) {
								if (fishType instanceof FishPrey && count > 0) {
									fishType.stringShow = true;
									count -= 1;
								}
							}
						}

					}

				}
			}

		}

	}

	public void paintComponent(Graphics g) {

		setBackground(new Color(0, 191, 255));
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
		
		// As the animation causes the simulation to slow down, please uncomment it to see it active.
//		g.drawImage(bubbles, 550, 495, this);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((Concaveshape) ccShape).drawCCShape(g2);
		((Perlinshape) perlinShape).drawPerlinShape(g2, angle);

		for (Fish fish : fishTank) {
			fish.drawFish(g2);
		}
		if (foodJar.isEmpty() != true) {

			for (Food food : foodJar) {
				food.drawFood(g2);
			}
		}

		// Simulate a glass in front of the aquarium.
		Color glass = new Color(220, 220, 220, 55);
		g.setColor(glass);
		g.fillRect(0, 0, 1280, 720);

	}

	public void actionPerformed(ActionEvent arg0) {
		try {
			if (controlP.ambienceToggle && ambience.isPlaying() == false) {
				ambience.loop();
			}
		} catch (NullPointerException npe) {

		}
		if (!controlP.ambienceToggle) {
			ambience.pause();
		}

		if (controlP.addPredatorToggle) {
			createFish(predator);
			controlP.addPredatorToggle = false;
		}
		if (controlP.addPreyToggle) {
			createFish(prey);
			controlP.addPreyToggle = false;

		}

		if (controlP.sortByEnergyToggle) {
			sortChoice = 0;

		}

		if (controlP.sortByEnergyAndSizeToggle) {
			sortChoice = 1;

		}

		if (controlP.sortBySizeToggle) {
			sortChoice = 2;
		}

		if (!fishTank.isEmpty()) {
			if (sortChoice == 0) {
				Collections.sort(fishTank, new CompareFishPreyByEnergy());
			} 
			else if (sortChoice == 1) {
				Collections.sort(fishTank, new CompareFishPreyByEnergyAndSize());
			} 
			else if (sortChoice == 2) {
				Collections.sort(fishTank, new CompareFishPreyBySize());
			}
		}

		// If the boolean "isFishDead" is true, then the cooldown begins to tick down
		// every 33 milliseconds. Once the cooldown has ticked down to zero, then a new
		// Fish Prey will be created, the cooldown will be reset and the boolean
		// "isFishDead" will be reset to false.
		if (isFishDead) {
			if (cooldown != 0) {
				cooldown -= 1;
			}
			if (cooldown == 0) {
				createFish(prey);
				cooldown = 45;
				isFishDead = false;

			}
		}
		for (int i = 0; i < fishTank.size(); i++) {
			Fish f = fishTank.get(i);
			if (f.checkDeadFish()) {
				if (controlP.deathSoundToggle) {
					death.play(0);
				}
				if (f instanceof FishPrey) {
					rightNow = LocalDateTime.now();
					aquariumLog.info("[" + systemTime.format(rightNow) + "] " + "Prey: " + f.getIdentifierCode()
							+ " has died of hunger, its size was: " + f.scale + " and energy at time of death was: "
							+ f.getEnergy() / 5f + "%\n");
					fishTank.remove(f);
					isFishDead = true;
				}
				if (f instanceof FishPredator) {
					rightNow = LocalDateTime.now();
					aquariumLog.info("[" + systemTime.format(rightNow) + "] " + "Predator: " + f.getIdentifierCode()
							+ " has died of hunger, its size was: " + f.scale + " and energy at time of death was: "
							+ f.getEnergy() / 5f + "%\n");
					fishTank.remove(f);
					createFish(predator);
					
				}
			}
		}

		// When the mouse button is held down, it is set to true. This "if-statement"
		// checks to see if the button is held down and adds to the current scale factor
		// by 0.05 per timer tick. Once the mouse button is released, the "isMouseHeld"
		// will turn to false, and the food is created with the current scale factor
		// according to how long the mouse button was held for.
		if (isMouseHeld) {
			currentScale += 0.05;
		}

		for (int i = 0; i < fishTank.size(); i++) {
			Fish fishOne = fishTank.get(i);
			for (int j = i + 1; j < fishTank.size(); j++) {
				Fish fishTwo = fishTank.get(j);

				if (fishOne instanceof FishPredator && fishTwo instanceof FishPrey) {
					FishPrey thisFish = (FishPrey) fishTwo;
					if (thisFish.collidesFOV(fishOne)) {
						if(thisFish.getEnergy() > 150) {
							thisFish.runAway = 1;
	
							thisFish.accel.mult(-25);
							thisFish.vel.mult(-25);
						}
					}
				} else if (fishOne instanceof FishPrey && fishTwo instanceof FishPredator) {
					FishPrey thisFish = (FishPrey) fishOne;
					if (thisFish.collidesFOV(fishTwo)) {
						if(thisFish.getEnergy() > 150) {
							thisFish.runAway = 1;
	
							thisFish.accel.mult(-25);
							thisFish.vel.mult(-25);
						}
					}
				}

			}
		}

		if (fishTank.isEmpty() != true) {
			for (int i = 0; i < fishTank.size(); i++) {
				Fish fish = fishTank.get(i);

				fish.checkBounds(initialSize);
				if (foodJar.isEmpty()) {
					fish.foodHere = 0;

					closestFood = null;
				}

				fish.swim();

				for (Fish f : fishTank) {

					// if the current fish is an instance of FishPrey, then it will eat the food
					// pellets.
					if (f instanceof FishPrey && f.runAway != 1 && f.hungry) {
						Food targetFood = setClosestFood(f);
						if (f.runAway == 1) {
							targetFood = null;
						}
						if (targetFood != null) {
							f.attractedBy(targetFood);
						}

						for (int fd = 0; fd < foodJar.size(); fd++) {
							Food thisFood = foodJar.get(fd);
							if (f.collides(thisFood)) {
								if (controlP.eatSoundToggle) {
									eatSound.play(0);
								}
								rightNow = LocalDateTime.now();
								aquariumLog.info("[" + systemTime.format(rightNow) + "] " + "Prey: "
										+ f.getIdentifierCode() + " has regained some energy, it currently now has: "
										+ f.getEnergy() / 5f + "% energy");
								if (thisFood.getScale() < 1.5) {
									f.setEnergy(f.getEnergy() + 50);
									f.hungerGreen += 50;
									f.hungerRed -= 50;
								}

								if (thisFood.getScale() > 1.5 && thisFood.getScale() < 2) {
									f.setEnergy(f.getEnergy() + 75);
									f.hungerGreen += 75;
									f.hungerRed -= 75;
								}

								if (thisFood.getScale() > 2) {
									f.setEnergy(f.getEnergy() + 100);
									f.hungerGreen += 100;
									f.hungerRed -= 100;
								}

								foodJar.remove(fd);
								randomFoodX = Math.random() * (600 - 200) + 200;
								randomFoodY = Math.random() * (600 - 200) + 200;
								randomFoodScale = Math.random() * (3.0 - 0.5) + 0.5;
								createFood((int) randomFoodX, (int) randomFoodY, randomFoodScale);
							}
						}

					}
					if ((f instanceof FishPredator && fish instanceof FishPredator)
							|| (f instanceof FishPrey && fish instanceof FishPrey)) {
						if (f != fish) {
							if (f.collides(fish) || f.collidesTail(fish)) {

								if (fish.scale < f.scale) {
									fish.knockedAside(f);
								} else if (fish.scale > f.scale) {
									f.knockedAside(fish);
								}
							}
						}
					}

				}
			}
			for (int k = 0; k < fishTank.size(); k++) {
				Fish thisOne = fishTank.get(k);
				if (thisOne instanceof FishPredator) {
					for (int i = 0; i < fishTank.size(); i++) {
						Fish thisFish = fishTank.get(i);
						if (thisFish instanceof FishPredator && thisFish.hungry) {
							FishPredator currentFish = (FishPredator) thisFish;
							Fish targetPrey = setClosestFish(currentFish);
							if (targetPrey != null) {
								currentFish.attractedBy(targetPrey);
								if (currentFish.collides(targetPrey)) {
									if (controlP.deathSoundToggle) {
										eatSound.play(0);
									}
									rightNow = LocalDateTime.now();
									aquariumLog.info("[" + systemTime.format(rightNow) + "] " + "Prey: "
											+ targetPrey.getIdentifierCode() + " Slaughtered, its size was: "
											+ targetPrey.scale + " and energy at time of death was: "
											+ targetPrey.getEnergy() / 5f + "%\n");
									if (fish.scale < 1.5) {

										currentFish.setEnergy(currentFish.getEnergy() + 50);

									}

									if (fish.scale > 1.5 && fish.scale < 2) {
										currentFish.setEnergy(currentFish.getEnergy() + 75);

									}

									if (fish.scale > 2) {
										currentFish.setEnergy(currentFish.getEnergy() + 100);
									}
									rightNow = LocalDateTime.now();
									aquariumLog.info("[" + systemTime.format(rightNow) + "] " + "Predator: "
											+ currentFish.getIdentifierCode()
											+ " has regained some energy, it currently now has: "
											+ currentFish.getEnergy() / 5f + "% energy");
									fishTank.remove(targetPrey);
									isFishDead = true;
								}
							}

						}
					}
				}
			}
		}
		if (fishTank.contains(selectThis) == false) {
			selectThis = null;
		}
		repaint();
		controlP.update(selectThis);
		try {
			for (Fish f : fishTank) {
				if (f.pos.x < -50 || f.pos.x > 1330) {
					if (selectThis == f) {
						selectThis = null;
					}
					fishTank.remove(f);
					System.out.println("A fish has gone out of bounds!");
					System.out.println("");
				}
				if (f.pos.y < -50 || f.pos.y > 770) {
					if (selectThis == f) {
						selectThis = null;
					}
					fishTank.remove(f);
					System.out.println("A fish has gone out of bounds!");
				}
			}
		}

		catch (ConcurrentModificationException cme) {
			System.out.println("");
		}

		for (Fish f : fishTank) {
			if (f.collidesFOV(ccShape) || f.collidesFOV(perlinShape)) {
				System.out.println("true");
				f.vel.mult(-1);

			}
		}

		angle += 10;
		if (angle < 800) {
			repaint();
		}
	}

	public void createFish(int preyOrPredator) {

		randomSpeedX = (Math.random() * 20);
		randomSpeedX += (Math.floor(randomSpeedX)) == 0 ? 1 : 0;
		randomSpeedX *= (Math.floor(Math.random() * 2)) == 0 ? 1 : -1;
		randomSpeedY = (Math.random() * 20);
		randomSpeedY += (Math.floor(randomSpeedY)) == 0 ? 1 : 0;
		randomSpeedY *= (Math.floor(Math.random() * 2)) == 0 ? 1 : -1;

		fishSize = Math.random() * ((1.2 - 0) + 1) + 0;

		double randomIDD = Math.floor(Math.random() * ((300) - (60) + 1) + 60);
		int randomID = (int) randomIDD;

		if (fishSize < 1) {
			fishSize = 0.25;
		} else if (fishSize < 1.2) {
			fishSize = 0.5;
		} else if (fishSize > 1.2) {
			fishSize = 0.75;
		}

		if (preyOrPredator == 1) {
			randomX = (int) (Math.random() * initialSize.width / 2 + 100);

			randomY = (int) (Math.random() * initialSize.height / 2 + 100);

			fish = new FishPrey(randomX, randomY, Math.min(initialSize.width, initialSize.height) / 15, randomSpeedX,
					randomSpeedY, fishSize);
			fish.setIdentifierCode("Py" + randomID);

			fishTank.add(fish);
			if (controlP.hatchSoundToggle) {
				hatch.play(0);
			}
			rightNow = LocalDateTime.now();
			aquariumLog.info("[" + systemTime.format(rightNow) + "] " + "Prey: " + fish.getIdentifierCode()
					+ " has been added, its size is: " + fishSize + "\n");
			
		}

		if (preyOrPredator == 2) {
			randomX = (int) (Math.random() * initialSize.width / 2 + 100);

			randomY = (int) (Math.random() * initialSize.height / 2 + 100);

			fish = new FishPredator(randomX, randomY, Math.min(initialSize.width, initialSize.height) / 15, 5,
					randomSpeedY, fishSize);

			fish.setIdentifierCode("Pr" + randomID);

			fishTank.add(fish);
			if (controlP.hatchSoundToggle) {
				hatch.play(0);
			}
			rightNow = LocalDateTime.now();
			aquariumLog.info("[" + systemTime.format(rightNow) + "] " + "Predator: " + fish.getIdentifierCode()
					+ " has been added, its size is: " + fishSize + "\n");
		}

	}

	public void createFood(int x, int y, double currentScale) {

		food = new Food((int) x, (int) y);
		food.setScale(currentScale);
		foodJar.add(food);

	}

	public Food setClosestFood(Fish fish) {
		Food closestFood = null;
		float attractionForce = 0;
		if (foodJar.isEmpty() != true) {
			for (int i = 0; i < foodJar.size(); i++) {
				Food thisFood = foodJar.get(i);
				if (closestFood == null) {
					closestFood = thisFood;
					// (AF=FOOD_SIZE/DISTANCE)
					attractionForce = (float) (closestFood.getScale() / closestFood.pos.dist(fish.pos));
				}

				if (closestFood != null && thisFood.getScale() / thisFood.pos.dist(fish.pos) > attractionForce) {

					closestFood = thisFood;
					attractionForce = (float) (closestFood.getScale() / closestFood.pos.dist(fish.pos));
				}
			}
		}
		return closestFood;
	}

	public Fish setClosestFish(Fish fish) {
		Fish closestFood = null;
		float attractionForce = 0;
		if (!fishTank.isEmpty()) {
			int count = 1;
			for (Fish f : fishTank) {
				if (f instanceof FishPrey && count > 0) {
					Fish thisFood = f;
					count -= 1;
					if (closestFood == null) {
						closestFood = thisFood;
						// (AF=FOOD_SIZE/DISTANCE)
						attractionForce = (float) (closestFood.scale / closestFood.pos.dist(fish.pos));
					}
					if (closestFood != null && thisFood.scale / thisFood.pos.dist(fish.pos) > attractionForce) {
						closestFood = thisFood;
						attractionForce = (float) (closestFood.scale / closestFood.pos.dist(fish.pos));
					}
				}
			}
		}
		return closestFood;
	}

}
