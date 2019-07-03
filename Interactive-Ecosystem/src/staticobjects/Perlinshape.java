package staticobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import processing.core.PApplet;
import processing.core.PVector;

public class Perlinshape extends ObstacleShape {
	private float radius = 15;
	private GeneralPath perlinPath = new GeneralPath();
	private float noiseSeed = (float) (Math.random() * 15);
	private PApplet pApp = new PApplet();
	private float x, y;
	private float prevX = 0;
	private float prevY = 0;
	private float angle = 0;
	private int count = 150;

	private Area perlinShapeArea;
	private Ellipse2D.Double inconspicuousEllipse;
	public Perlinshape() {
		super();
		scale = 0.5;
		pos = new PVector(1200, 25);
		inconspicuousEllipse = new Ellipse2D.Double(1115, -75, 175, 175);
		setPerlinShapeAttributes();
	}

	public void drawPerlinShape(Graphics2D g, float angle) {
		if (count > 0) {
			radius += 0.6;
			noiseSeed += 0.1;
			count -= 1;
		}
		float noiseRadius = radius + (pApp.noise(noiseSeed) * 200) - 100;
		float radianAngle = (float) Math.toRadians(angle);
		AffineTransform dps = g.getTransform();
		g.translate(pos.x, pos.y);
		g.scale(scale, scale);
		x = noiseRadius * (float) Math.cos(radianAngle);
		y = noiseRadius * (float) Math.sin(radianAngle);
		perlinPath.moveTo(x, y);
		perlinPath.lineTo(prevX, prevY);
		g.setStroke(new BasicStroke(3));
		g.setColor(new Color(100, 20, 210));
		g.draw(perlinPath);
		g.setTransform(dps);
		
		prevX = x;
		prevY = y;
		
		g.setStroke(new BasicStroke(1));

	}
	
	public void setPerlinShapeAttributes() {
		perlinShapeArea = new Area();
		perlinShapeArea.add(new Area(perlinPath));
		perlinShapeArea.add(new Area(inconspicuousEllipse));
	}

	public Shape getBounds() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.scale(scale, scale);
		return at.createTransformedShape(perlinShapeArea);
	}

}
