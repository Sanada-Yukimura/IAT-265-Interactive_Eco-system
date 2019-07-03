package fish;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import processing.core.PVector;
import staticobjects.Concaveshape;

public class FishPredator extends Fish implements ActionListener {

	private Arc2D.Double evilEye;
	private Ellipse2D.Double iris;
	private Ellipse2D.Double pupil;

	public FishPredator(int x, int y, int size, double xSpeed, double ySpeed, double scale) {
		super(x, y, size, xSpeed, ySpeed, scale);
		// TODO Auto-generated constructor stub
		evilEye = new Arc2D.Double();
		iris = new Ellipse2D.Double();
		pupil = new Ellipse2D.Double();
		setPredatorEye();
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
		g.setColor(Color.white);
		g.fill(evilEye);
		g.setColor(Color.BLACK);
		g.draw(evilEye);
		g.setColor(Color.red);
		g.fill(iris);
		g.setColor(Color.black);
		g.fill(pupil);
		g.setTransform(af);
//		System.out.println(energy);
	}

	protected void setPredatorEye() {
		evilEye.setArc(-45, 2, getSize() / 2, getSize(), 25, 100, Arc2D.CHORD);
		iris.setFrame(-35, 3, getSize() / 7, getSize() / 7);
		pupil.setFrame(-34, 4, getSize() / 12, getSize() / 12);
	}

	public void attractedBy(Fish targetFood) {
		PVector path = PVector.sub(targetFood.pos, pos);
		if (scale == idealWeight) {
			vel.add(path.normalize().mult(5 * .2f)).limit(5);
		}

		if (scale != idealWeight) {
			vel.add(path.normalize().mult(5 * .2f)).limit((float) (5 * slowdown));
		}

	}

	public void actionPerformed(ActionEvent arg0) {
		super.actionPerformed(arg0);
		if (Math.abs(accel.x) > 3 || Math.abs(accel.y) > 3) {
			energy -= 2;
		} else if ((Math.abs(accel.x) > 1 && (Math.abs(accel.x) < 3)
				|| (Math.abs(accel.y) > 1 && (Math.abs(accel.y) < 3)))) {
			energy -= 1;
		}

	}

}
