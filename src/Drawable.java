import java.awt.Graphics2D;

/**
 * Drawable interface; used to create all of the things needing to be drawn
 */
public interface Drawable {

	/**
	 * Returns the character object.
	 * 
	 * @return the character object
	 */
	char getChar();

	/**
	 * This draws with the graphics object
	 * 
	 * @param g2
	 *            the given graphics object.
	 */
	void draw(Graphics2D g2);

	/**
	 * Updates position when called.
	 */
	void time();
}
