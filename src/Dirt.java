
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * This creates the dirt
 */
public class Dirt extends Coords implements Drawable {
	private boolean isHole;
	private int holeTime;
	private int trapTime;
	private Enemy trapped;
	private Level world;
	private static final int HOLE_TIME = 7000;
	private static final int GUARD_TRAP_TIME = 4000;
	private static final int TIME_WENT_BY = 10;


	/**
	 * refills hole
	 * 
	 * @param x
	 *            the x coordinate of the square.
	 * @param y
	 *            the y coordinate of the square.
	 * @param scale
	 *            the width of the square.
	 * @param world
	 *            the World that the brick is in.
	 */
	public Dirt(int x, int y, int scale, Level world) {
		super(x, y, scale);
		this.isHole = false;
		this.world = world;
		this.trapTime = 0;
		this.holeTime = 0;
	}

	@Override
	public void draw(Graphics2D g2) {
		if (!this.isHole) {
			Image brickImage = new ImageIcon("images\\brick.jpg").getImage();
			g2.drawImage(brickImage, this.x * this.scale, this.y * this.scale,
					null);
		}
	}

	@Override
	public void time() {
		//implement later
	}

	@Override
	public char getChar() {
		return 'b';
	}
}

