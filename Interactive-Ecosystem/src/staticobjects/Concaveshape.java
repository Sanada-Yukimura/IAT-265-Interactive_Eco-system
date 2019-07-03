package staticobjects;

import processing.core.PVector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class Concaveshape extends ObstacleShape {

	private PVector pos;

	private Path2D concaveShape;

	private Area concaveShapeArea;

	public Concaveshape() {
		super();
		concaveShape = new Path2D.Double();
		pos = new PVector(30, 35);
		scale = 0.5;

	}

	public void drawCCShape(Graphics2D g) {
		setConcaveShapeAttribute();
		AffineTransform af = g.getTransform();
		g.translate(pos.x, pos.y);
		g.setColor(Color.RED);
		g.scale(-scale, -scale);

		g.fill(concaveShape);
		g.setTransform(af);

	}

	private void setConcaveShapeAttribute() {
		concaveShape.moveTo(0, 0);
		concaveShape.lineTo(-50, 50);
		concaveShape.lineTo(0, 25);
		concaveShape.lineTo(50, 50);

		concaveShape.closePath();
		concaveShapeArea = new Area();
		concaveShapeArea.add(new Area(concaveShape));
	}

	public Shape getBounds() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.scale(scale, scale);
		return at.createTransformedShape(concaveShapeArea);
	}

}
