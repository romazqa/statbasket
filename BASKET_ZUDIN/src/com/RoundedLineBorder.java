package com;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.*;

public class RoundedLineBorder extends AbstractBorder {
    private Color color;
    private int thickness;
    private int radius;

    public RoundedLineBorder(Color color, int thickness, int radius) {
        this.color = color;
        this.thickness = thickness;
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Double(x, y, width - thickness, height - thickness, radius, radius);
        g2d.draw(roundedRectangle);
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }
}