package fish;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import javax.swing.Timer;
import food.Food;
import processing.core.PVector;
import staticobjects.Concaveshape;
import staticobjects.ObstacleShape;

// BONUS (2 points): Change the model to reflect a concept of an ‘ideal weight’. Animal with an ideal weight will move the fastest. Underweight and overweight animals will move progressively slower.
// This is implemented with the "swim" method located starting at line 291, as well as the actionPerformed method starting at line 343.

// BONUS (2 points): Change the energy model in the way that animal with energy above certain threshold will grow, which will consume some energy. As a larger animal it will start consuming more energy to move and it will move slower (or if you have implemented an ‘ideal weight’ model from above, then it will move accordingly based on the model.)
// This is implemented at the actionPerformed method starting at line 343.

public class Fish implements ActionListener {

	public PVector pos, vel, accel;
	public double scale;
	private int size;

	private Timer t;

	protected double slowdown = 5;

	private String identifierCode;

	private double hue = Math.random();
	private double sat = Math.random();
	private double bright = Math.random();

	private double hue2 = Math.random();
	private double sat2 = Math.random();
	private double bright2 = Math.random();

	public int hungerRed = 0;
	public int hungerGreen = 255;

	float rotationAngle;
	float currentRotationAngle;

	private int tailRotateVar = 0;

	private int range = (50 - 25) + 1;

	public boolean hungry = false;

	int tailWidth = (int) Math.ceil(Math.random() * range) + 15;

	int tailHeight = ((int) Math.random() + 1);

	private int tailRotate = 0;

	public int foodHere = 0;

	private Ellipse2D.Double fishBody;

	private Arc2D.Double fishHead;

	public boolean stringShow = false;

	private boolean posXRecorded = false;
	protected float notHungryPosX;
	private boolean posYRecorded = false;
	protected float notHungryPosY;

	private Path2D fishTail;

	private Area fishBodyOutline;
	private Area fishTailOutline;

	protected double idealWeight = 1.0;
	private boolean notIdealWeight = false;

	public boolean deadFish = false;
	private Area fishFOVArea;
	private Arc2D.Double fishFOV;
	protected int energy = 50000;
	public int runAway = 0;

	public Fish(int x, int y, int size, double xSpeed, double ySpeed, double scale) {

		t = new Timer(100, this);
		t.start();

		pos = new PVector(x, y);
		this.setSize(size);
		vel = new PVector(0, 0);
		accel = new PVector((int) xSpeed, (int) ySpeed * 2);

		fishBody = new Ellipse2D.Double();
		fishHead = new Arc2D.Double();
		fishFOV = new Arc2D.Double();

		fishTail = new Path2D.Double();
		this.scale = scale;
		deadFish = false;
		setFishBodyOutline();
		setFishTailOutline();
		setFishFOV();
		setFishFOVArea();
	}

	public Fish() {
		
	}

	public void drawFish(Graphics2D g) {

		setFishAttributes();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

		g.setColor(Color.getHSBColor((float) hue, (float) sat, (float) bright));

		// This piece of code here allows the fish tail to move independently from the
		// fish body, allowing it to move both along with the fish and rotate so the
		// fish tail appears wagging.
		AffineTransform ag = g.getTransform();
		g.rotate(Math.toRadians(tailRotate), 0, 0);
		g.fill(fishTail);
		g.setColor(Color.BLACK);
		g.draw(fishTail);
		g.setTransform(ag);
		g.setColor(Color.getHSBColor((float) hue2, (float) sat2, (float) bright2));
		g.fill(fishBody);
		g.setColor(Color.black);

		g.draw(fishBody);
		g.setColor(Color.orange);
		
		if (energy < 250) {
			g.setColor(Color.green);
		}

		g.fill(fishHead);
		g.setColor(Color.black);

		g.draw(fishHead);
		g.draw(fishBody);
		g.setTransform(af);
		setFishBodyOutline();
		setFishTailOutline();
		if (stringShow) {
			g.setColor(Color.RED);
			g.draw(getBounds().getBounds2D());
			g.draw(getTransformedFishTailOutline().getBounds2D());

		}

	}

	public void changeColorRandom() {
		hue = Math.random();
		sat = Math.random();
		bright = Math.random();
	}

	protected void setFishAttributes() {

		fishBody.setFrame(-getSize(), -getSize() / 2, getSize() * 2, getSize());
		fishHead.setArc(-getSize(), -getSize() / 2, getSize() * 2, getSize(), 90, 180, Arc2D.CHORD);

		int setOrigin = -25;
		// Fish Tail Triangle
		fishTail.moveTo(setOrigin + getSize() * 1.3, 0);
		fishTail.lineTo((setOrigin + getSize() * 1.3) + tailWidth, 0 - getSize() / 2 - tailHeight);
		fishTail.lineTo((setOrigin + getSize() * 1.3) + tailWidth, 0 + getSize() / 2 + tailHeight);
		fishTail.closePath();
	}

	protected void setFishBodyOutline() {
		fishBodyOutline = new Area();
		fishBodyOutline.add(new Area(fishBody));

	}

	protected void setFishTailOutline() {
		fishTailOutline = new Area();
		fishTailOutline.add(new Area(fishTail));
	}

	public void rotate(float heading) {
		rotationAngle = heading;

		vel = PVector.fromAngle(rotationAngle);
		vel.mult(5);

	}

	public Shape getTransformedFishTailOutline() {

		AffineTransform fto = new AffineTransform();

		fto.translate(pos.x, pos.y);

		if (vel.x > 0) {

			fto.rotate(Math.toRadians(tailRotate), 0, 0);
		}
		if (vel.x < 0) {

			fto.rotate(Math.toRadians(-tailRotate), 0, 0);
		}
		fto.rotate(vel.heading());
		fto.scale(-scale, scale);

		return fto.createTransformedShape(fishTailOutline);
	}

	protected Shape getBounds() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.rotate(vel.heading());
		at.scale(scale, scale);

		return at.createTransformedShape(fishBodyOutline);

	}

	public void swim() {

		vel.add(accel);
		if (runAway == 0) {
			if (scale != idealWeight) {
				notIdealWeight = true;
				vel.limit(7 - (float) slowdown);
				if (energy < 100) {
					vel.limit((7 - (float) slowdown) / 2);
				}
			}
			if (scale == idealWeight) {
				vel.limit(7);
				if (energy < 100) {
					vel.limit(7 / 2);
				}
			}
		}
		if (runAway == 1) {
			vel.limit(10);
		}

		pos.add(vel);


	}

	public void checkBounds(Dimension panelSize) {
		if ((pos.x < 100 * scale)) {
			accel.x += 0.75;
		}

		if ((pos.x + getSize() * scale > panelSize.width - 80 * scale)) {

			accel.x -= 0.75;
		}
		if ((pos.y < 80 * scale)) {
			accel.y += 0.75;
		}

		if (pos.y + getSize() > panelSize.height - 80 * scale) {

			accel.y -= 0.75;
		}

		if (!hungry && runAway != 1) {

			if (pos.x < notHungryPosX - (75 * scale)) {
				accel.x += 0.5;
			}
			if (pos.x > notHungryPosX + (75 * scale)) {
				accel.x -= 0.5;
			}

			if (pos.y < notHungryPosY - (75 * scale)) {
				accel.y += 0.5;
			}
			if (pos.y > notHungryPosY + (75 * scale)) {
				accel.y -= 0.5;
			}

		}
		

	}

	public void actionPerformed(ActionEvent arg0) {
		tailRotateAnim();
		// if the fish is not ideal weight and the slowdown has not reached zero, the
		// slowdown variable will tick down at a rate of 0.02 per tick.
		if (notIdealWeight && slowdown > 0) {
			slowdown -= 0.002;
		}

		if (energy < 350) {
			hungry = true;
			posXRecorded = false;
			posYRecorded = false;

		}
		if (energy > 350) {
			hungry = false;
			if (!posXRecorded) {
				notHungryPosX = pos.x;
				posXRecorded = true;
			}
			if (!posYRecorded) {
				notHungryPosY = pos.y;
				posYRecorded = true;
			}

		}

		if (energy > 0) {
			energy -= 1;

		}

		if (energy >= 500) {
			hungerGreen = 255;
			hungerRed = 0;
		}
		if (energy < 380 && energy > 250) {
			hungerGreen -= 1;
			hungerRed += 2;
		}

		if (energy <= 250) {
			hungerGreen -= 2;
			hungerRed += 3;
		}

		if (energy <= 100) {
			hungerRed -= 3;
			hungerGreen -= 3;
		}

		if (hungerRed < 0) {
			hungerRed = 0;
		}

		if (hungerGreen < 0) {
			hungerGreen = 0;
		}

		if (hungerRed > 255) {
			hungerRed = 255;
		}
		if (hungerGreen > 255) {
			hungerGreen = 255;
		}

		// If the fish's weight is beyond the ideal weight, then the fish will consume
		// energy at a higher rate. This rate is defined by the current scale.
		if (energy > 0 && scale > 1.1) {
			energy -= scale * 2;

		}

		if (scale > 1.1) {
			if (energy < 420 && energy > 280) {
				hungerGreen -= 2;
				hungerRed += 4;
			}
		}

		if (energy <= 0) {
			deadFish = true;
		}

		// If the fish has consumed enough food pellets to reach an energy capacity of
		// 300 or more, the fish's energy capacity will be set to 200 and increase in
		// scale by a factor of 25%

		if (energy >= 600) {
			energy = 500;
			if (scale <= 2.5) {
				scale += 0.25;
			}
		}

	}

	// applies to both subclasses, will stay in superclass
	public boolean checkDeadFish() {
		if (deadFish == true) {
			return true;
		}
		return false;
	}

	private void tailRotateAnim() {
		int tailRotateMax = 15;
		if (tailRotate < tailRotateMax - 10 && tailRotateVar == 0) {
			tailRotate++;
		}
		if (tailRotate == tailRotateMax - 10) {
			tailRotateVar = 1;
			tailRotate--;
		}
		if (tailRotate > -tailRotateMax + 15 && tailRotateVar == 1) {
			tailRotate--;
		}
		if (tailRotate == -tailRotateMax + 15) {
			tailRotateVar = 0;
		}

	}

	public void attractedBy(Food targetFood) {
		PVector path = PVector.sub(targetFood.pos, pos);
		if (scale == idealWeight) {
			vel.add(path.normalize().mult(5 * .2f)).limit(5);
		}

		if (scale != idealWeight) {
			vel.add(path.normalize().mult(5 * .2f)).limit((float) (5));
		}

	}

	private void setFishFOVArea() {

		fishFOVArea = new Area();
		fishFOVArea.add(new Area(fishFOV));
	}

	private void setFishFOV() {
		fishFOV.setArc(-getSize() * 2.2, -getSize() * 1.5, getSize() * 3, getSize() * 3, 100, 140, Arc2D.PIE);

	}

	protected Shape getFOV() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.rotate(vel.heading());
		at.scale(-scale, scale);

		return at.createTransformedShape(fishFOVArea);

	}

	public boolean collidesFOV(Fish p) {
		return (getFOV().intersects(p.getBounds().getBounds2D()) && p.getBounds().intersects(getFOV().getBounds2D()));

	}

	public boolean collides(Food food) {
		return (getBounds().intersects(food.getBounds().getBounds2D())
				&& food.getBounds().intersects(getBounds().getBounds2D()));
	}

	public boolean collides(Fish fish) {
		return (getBounds().intersects(fish.getBounds().getBounds2D())
				&& fish.getBounds().intersects(getBounds().getBounds2D()));

	}

	public boolean collidesTail(Fish fish) {
		return (getBounds().intersects(fish.getTransformedFishTailOutline().getBounds2D())
				&& fish.getTransformedFishTailOutline().intersects(getBounds().getBounds2D()));

	}

	public boolean collidesFOV(ObstacleShape ccs) {
		return (getFOV().intersects(ccs.getBounds().getBounds2D())
				&& ccs.getBounds().intersects(getFOV().getBounds2D()));
	}

	public boolean checkMouseHit(MouseEvent e) {
		return (getBounds().contains(e.getPoint()) || getTransformedFishTailOutline().contains(e.getPoint()));
	}

	public void knockedAside(Fish f) {
		PVector path = PVector.sub(pos, f.pos);
		if (scale == idealWeight) {
			vel.add(path.normalize().mult(20 * .2f)).limit(10);
		}

		if (scale != idealWeight) {
			vel.add(path.normalize().mult(20 * .2f)).limit((float) (20 * slowdown));
		}
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	// For drawing text

	public void drawInfo(Graphics2D g) {
		AffineTransform ap = g.getTransform();
		g.translate(pos.x, pos.y);
		g.rotate(vel.heading());
		if (vel.x < 0) {
			g.scale(-1, -1);
			g.translate(10, 0);
		}
		Font f = new Font("Comic Sans MS", Font.BOLD, 10);
		g.setFont(f);
		g.setColor(Color.BLACK);
		FontMetrics metrics = g.getFontMetrics(f);
		String energyVal = String.format("%d", energy);
		g.drawString("energy: " + energyVal, -metrics.stringWidth(energyVal) - 15,
				(int) (-fishBody.getHeight() / 2 * scale - 30));
		String velocityX = String.format("%.2f", Math.abs(vel.x));
		g.drawString("velX: " + velocityX, -metrics.stringWidth(velocityX) - 10,
				(int) (-fishBody.getHeight() / 2 * scale - 20));
		String velocityY = String.format("%.2f", Math.abs(vel.y));
		g.drawString("velY: " + velocityY, -metrics.stringWidth(velocityY) - 10,
				(int) (-fishBody.getHeight() / 2 * scale - 10));

		g.setTransform(ap);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getIdentifierCode() {
		return identifierCode;
	}

	public void setIdentifierCode(String idCode) {
		identifierCode = idCode;
	}

}
