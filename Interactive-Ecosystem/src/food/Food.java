package food;

import processing.core.PVector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

public class Food {

	private RoundRectangle2D.Double pellet;
	public int x, y;
	private int w = 10;
	private int h = 15;
	public PVector pos;
	private double scale = 1;
	private Area foodOutline;

	public Food(int x, int y) {
		this.x = x;
		this.y = y;
		pos = new PVector(x, y);
		setFoodAttribute();
	}

	public void drawFood(Graphics2D g) {
		AffineTransform af = g.getTransform();
		g.translate(pos.x, pos.y);
		g.setColor(new Color(97, 26, 9));
		g.scale(scale, scale);
//		g.fillRoundRect(-w/2, -h/2, w, h, 5, 25);
		g.fill(pellet);
		g.setTransform(af);
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public boolean checkMouseHit(MouseEvent e) {
		return (Math.abs(e.getX() - pos.x) < w / 2) && (Math.abs(e.getY() - pos.y) < h / 2);
	}

	private void setFoodAttribute() {
		pellet = new RoundRectangle2D.Double(-w / 2, -h / 2, w, h, 5, 25);
		foodOutline = new Area(pellet);
	}

	public Shape getBounds() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.scale(scale, scale);
		return at.createTransformedShape(foodOutline);
	}
}
