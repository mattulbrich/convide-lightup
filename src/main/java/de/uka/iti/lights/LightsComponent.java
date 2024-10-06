// Copyright (C) 2008 Universitaet Karlsruhe, Germany
//
// This source is protected by the GNU General Public License. 
//

package de.uka.iti.lights;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

/**
 * This component is used to draw a {@link Lights} board.
 * 
 * (The numbers are not very pretty.)
 * 
 * @author Mattias Ulbrich
 * @version 2008.1
 */
public class LightsComponent extends JComponent {

	private static final long serialVersionUID = 3288809465948920985L;
	
	private static final Font FONT = new Font("Sansserif", Font.BOLD, 12);
	private static final BasicStroke BOLD_STROKE = new BasicStroke(2f);
	private Lights lights;

	public LightsComponent(Lights lightup) {
		this.lights = lightup;
	}

	@Override
	protected void paintComponent(Graphics g) {
	
		// switch on anti aliasing
		((Graphics2D) g).setRenderingHint(
		        RenderingHints.KEY_ANTIALIASING, 
		        RenderingHints.VALUE_ANTIALIAS_ON);
		
		Rectangle bounds = g.getClipBounds();
		int d = lights.getDimension();
		float deltax = (bounds.width-20) / (float)d;
		float deltay = (bounds.height-20) / (float)d;
		
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, bounds.width, bounds.height);

		// This font thing is rather empiric and looks ok on my system ...
		// Feel free to improve this ... (and tell me)
		AffineTransform at = new AffineTransform();
		at.scale(deltax / 14, deltay / 14);
		Font theDerivedFont = FONT.deriveFont(at);
		g.setFont(theDerivedFont);
		
		((Graphics2D)g).setStroke(BOLD_STROKE);
		g.translate(10,10);
		
		for(int row = 0; row < d; row++) {
			for(int col = 0; col < d; col++) {
				
				if(lights.isLit(row, col)) {
					g.setColor(Color.yellow);
					g.fillRect((int)(deltax * col), (int)(deltay * row),
							(int)deltax+1, (int)deltay+1);
				}
				
				if(lights.isBlock(row,col)) {
					g.setColor(Color.black);
					g.fillRect((int)(deltax * col), (int)(deltay * row),
							(int)deltax+1, (int)deltay+1);
					if(lights.isConstrainedBlock(row, col)) {
						int v = lights.getBlockConstraint(row, col);
						g.setColor(Color.white);
						
						g.drawString(Integer.toString(v), (int)(deltax * col) + 5, (int)(deltay * (row+1)) - 5);
					}
				}
				
				if(lights.isLight(row, col)) {
					g.setColor(Color.white);
					g.fillOval((int)(deltax * col)+5, (int)(deltay * row)+5,
							(int)deltax-10, (int)deltay-10);
					g.setColor(Color.black);
					g.drawOval((int)(deltax * col)+5, (int)(deltay * row)+5,
							(int)deltax-10, (int)deltay-10);
				}
			}
		}		
		
		g.setColor(Color.darkGray);
		for (int i = 0; i <= d; i++) {
			g.drawLine(0, (int)(i*deltay), bounds.width-20, (int)(i*deltay));
			g.drawLine((int)(i*deltax), 0, (int)(i*deltax), bounds.height-20);
		}

	}
	
	@Override
	public Dimension getPreferredSize() {
		int v = lights.getDimension() * 20 + 20;
		return new Dimension(v,v);
	}
	
}
