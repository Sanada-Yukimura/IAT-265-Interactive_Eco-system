package fish;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Comparator;

import staticobjects.Concaveshape;

public class FishPrey extends Fish implements ActionListener, Comparator<Fish> {

	private Ellipse2D.Double fishEye;
	private int runAwayCountdown = 60;

	public FishPrey(int x, int y, int size, double xSpeed, double ySpeed, double scale) {
		super(x, y, size, xSpeed, ySpeed, scale);
		fishEye = new Ellipse2D.Double();
		setPreyEye();

	}

	public FishPrey() {
		super();
	}

	public void drawFish(Graphics2D g) {
		super.drawFish(g);
		AffineTransform af = g.getTransform();
		g.translate(pos.x, pos.y);
		g.scale(scale, scale);
		g.scale(-1, -1);
		if (foodHere == 0) {
			g.rotate(vel.heading());
			accel.x = 0;
			accel.y = 0;
		}

		if (vel.x < 0) {
			g.scale(1, -1);
		}
		g.setColor(Color.black);
		g.fill(fishEye);
		g.setTransform(af);

	}

	private void setPreyEye() {
		fishEye.setFrame(-30, -2, getSize() / 10, getSize() / 4);

	}

	public void actionPerformed(ActionEvent arg0) {
		super.actionPerformed(arg0);
		if (runAway == 1) {
			if (runAwayCountdown > 0) {
				hungry = false;
				runAwayCountdown -= 2;
			}
			if (runAwayCountdown <= 0) {
				runAway = 0;
				hungry = true;
				runAwayCountdown = 60;

			}

		}

	}

	public int compare(Fish fishOne, Fish fishTwo) {
		return (fishOne.energy - fishTwo.energy);
	}

}
