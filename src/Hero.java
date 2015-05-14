import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;


public class Hero extends JComponent {
	
	private int x;
	private int y;
	
	public Hero(){
		x = 0;
		y = 0;
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub.
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.fillOval(x, y, 30, 30);
		
	}
	
	public void moving(){
		this.x++;
		this.y++;
	}

}
