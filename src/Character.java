import java.awt.Graphics2D;
import java.io.IOException;
import java.security.Guard;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A character can move, pick up gold, pick up emeralds, move gold, and shoot.
 */
abstract class Character implements Drawable {
	/**
	 * current x coordinate of the character.
	 */
	protected double xPosition;
	/**
	 * current y coordinate of the character
	 */
	protected double yPosition;
	/**
	 * width of the character
	 */
	protected int scale;
	/**
	 * velocity in the x direction of the character's motion
	 */
	protected double xVelocity;
	/**
	 * velocity in the y direction of the character's motion
	 */
	protected double yVelocity;

	/**
	 * world that the character is on
	 */
	protected Level world;
	/**
	 * character falls
	 */
	protected double fallingDistance;
	/**
	 * character climbs
	 */
	protected double climbingDistance;
	/**
	 * character speed
	 */
	private double speed;
	/**
	 * hole duration
	 */
	protected int holeImmuneTime;
	/**
	 * reports if character in hole
	 */
	protected boolean inHole;
	/**
	 * brick that is treated as a hole
	 */
	protected Dirt hole;
	/**
	 * The amount of gold a character has.
	 */
	protected int goldCount;
	private boolean escapeOn;

	/**
	 * current character frame
	 */
	protected int frameCount;

	private static final double INTERACTION_DISTANCE = .06;
	private static final double DEATH_DISTANCE = .9;
	private static final int MAX_GOLD = 6;
	private static final int REFRESH = 10;
	private static final int Y_VALUE_TO_WIN = 1;
	private static final int X_VALUE_TO_WIN = 20;
	/**
	 * Error factor
	 */
	protected static final double ERROR_RANGE = .45;

	/**
	 * constructs the character in the given world
	 * 
	 * @param x
	 *            The x coordinate of the character.
	 * @param y
	 *            The y coordinate of the character.
	 * @param scale
	 *            The width of the character.
	 * @param world
	 *            The world that the character is in.
	 * @param speed
	 *            The speed of the character
	 */
	public Character(int x, int y, int scale, Level world, double speed) {
		this.xPosition = x;
		this.yPosition = y;
		this.scale = scale;
		this.xVelocity = 0;
		this.yVelocity = 0;
		this.world = world;
		this.speed = speed;
		this.goldCount = 0;
		this.escapeOn = false;
		this.holeImmuneTime = 0;
	}

	/**
	 * Draws the character on the graphics frame
	 * 
	 * @param g2
	 *            The graphics object
	 */
	@Override
	public void draw(Graphics2D g2) {
		// Implemented in subclasses
	}

	/**
	 * sets velocity to negative
	 * 
	 */
	public void moveLeft() {
		this.xVelocity = -this.speed;
	}

	/**
	 * sets velocity to positive
	 * 
	 */
	public void moveRight() {
		this.xVelocity = this.speed;
	}

	/**
	 * set y velocity to negative
	 * 
	 */
	public void moveUp() {
		this.yVelocity = -this.speed;
	}

	/**
	 * sets y velocity positive
	 * 
	 */
	public void moveDown() {
		this.yVelocity = this.speed;
	}

	/**
	 * stop vertical motion
	 * 
	 */

	public void stopVert() {
		this.yVelocity = 0;
	}

	@Override
	public void time() {
		int roundx = (int) (Math.round(this.xPosition));
		int roundy = (int) (Math.round(this.yPosition));

		// factoring in error
		int errorx = (int) (Math.round(this.xPosition - ERROR_RANGE));
		int y = (int) Math.round(this.yPosition - ERROR_RANGE);
		if (y == Y_VALUE_TO_WIN && this.escapeOn == true
				&& errorx > X_VALUE_TO_WIN) {
			try {
				this.world.nextLevel();
			} catch (UnsupportedAudioFileException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			} catch (LineUnavailableException exception) {
				exception.printStackTrace();
			}
		}

		Drawable onright = this.world.getObject(errorx + 1, y);
		Drawable onleft = this.world.getObject(errorx, y);

		Drawable directbelow = this.world.getObject(roundx, y + 1);
		Drawable twobelow = this.world.getObject(roundx, y + 2);
		Drawable belowright = this.world.getObject(errorx + 1, y + 1);
		Drawable belowleft = this.world.getObject(errorx, y + 1);
		if (this.holeImmuneTime > 0) {
			this.holeImmuneTime -= REFRESH;
		}

		// falling into a hole or off something
		if (this.fallingDistance > 0) {
			this.yPosition += this.speed;
			this.fallingDistance -= this.speed;
			OverVariables.moving = "down";
		} else if (this.climbingDistance > 0) {
			this.yPosition -= this.speed;
			this.climbingDistance -= this.speed;
			OverVariables.moving = "up";
		}
		// if trapped in a hole
		else if (this.inHole) {
			// act on nothing
		}
		// if they are on a hole
		else if ((this.holeImmuneTime <= 0) && directbelow != null
				&& directbelow.getChar() == 'b'
				&& ((Dirt) directbelow).isHole()
				&& !((Dirt) directbelow).hasGuard
				&& Math.abs(this.xPosition - roundx) < INTERACTION_DISTANCE) {
			this.hole = ((Dirt) directbelow);
			fallInHole(twobelow);
		}
		// free fall
		else if ((belowright == null || belowright.getChar() == 'r')
				&& (belowleft == null || belowleft.getChar() == 'r')
				&& onright == null && onleft == null) {
			if (!((belowright != null && belowright instanceof Guard) || (belowleft != null && belowleft instanceof Guard))) {
				this.yPosition += this.speed;
				OverVariables.moving = "down";
			}
		} else {
			// trying to move vertically
			if (this.yVelocity != 0) {
				// ladder to the left
				if ((onleft != null && onleft.getChar() == 'l')) {
					// either moving up or objects below aren't bricks
					if (this.yVelocity < 0
							|| (belowright != null
									&& belowright.getChar() != 'p' && belowright
									.getChar() != 'b')
							|| (belowleft != null && belowleft.getChar() != 'p' && belowleft
									.getChar() != 'b')) {
						// if close enough to move up
						if (Math.abs(this.xPosition - errorx) < INTERACTION_DISTANCE) {
							this.yPosition += this.yVelocity;
							OverVariables.moving = "up";
						}
						// move close enough
						else {
							this.xPosition -= this.speed;
							OverVariables.moving = "left";
						}
					}
				}
				// on a ladder to the right
				else if ((onright != null && onright.getChar() == 'l')) {
					// either moving up or objects below aren't bricks
					if (this.yVelocity < 0
							|| (belowright != null
									&& belowright.getChar() != 'p' && belowright
									.getChar() != 'b')
							|| (belowleft != null && belowleft.getChar() != 'p' && belowleft
									.getChar() != 'b')) {
						// if close enough to climb up
						if (Math.abs(this.xPosition - errorx - 1) < INTERACTION_DISTANCE) {
							this.yPosition += this.yVelocity;
							OverVariables.moving = "up";
						}
						// move character slightly closer
						else {
							this.xPosition += this.speed;
							OverVariables.moving = "right";
						}
					}
				}
				// climbing to top of right ladder
				else if ((belowright != null && belowright.getChar() == 'l')) {
					// if close enough to climb up
					if (Math.abs(this.xPosition - errorx - 1) < INTERACTION_DISTANCE) {
						this.yPosition += this.yVelocity;
						OverVariables.moving = "up";
					}
					// move slightly closer
					else {
						this.xPosition += this.speed;
						OverVariables.moving = "right";
					}
				}
				// climbing to the top of a ladder left
				else if ((belowleft != null && belowleft.getChar() == 'l')) {
					// if close enough to climb up
					if (Math.abs(this.xPosition - errorx) < INTERACTION_DISTANCE) {
						this.yPosition += this.yVelocity;
						OverVariables.moving = "up";
					}
					// move slightly closer
					else {
						this.xPosition -= this.speed;
						OverVariables.moving = "left";
					}
				}
				// falling off rope
				else if (((onright != null && onright.getChar() == 'r') || (onleft != null && onleft
						.getChar() == 'r')) && this.yVelocity > 0) {
					if ((belowright == null || belowright.getChar() == 'r')
							&& (belowleft == null || belowleft.getChar() == 'r')) {
						this.yPosition += this.yVelocity;
						this.fallingDistance = 1;
						OverVariables.moving = "down";
					}
				}
			}

			// move right if empty space
			if (this.xVelocity > 0
					&& (onright == null || onright.getChar() == 'l'
							|| onright.getChar() == 'g' || (onright.getChar() == 'b' && ((Dirt) onright)
							.isHole()))) {
				// check for obstacle
				if ((belowright == null
						|| (belowright.getChar() != 'b' && belowright.getChar() != 'p') || (Math
						.abs(this.yPosition - y) < INTERACTION_DISTANCE))) {
					// System.out.println("Move right");
					this.xPosition += this.xVelocity;
					OverVariables.moving = "right";
				}
				// move slightly above obstacle
				else {
					// System.out.println("MOVE right");
					this.yPosition -= this.speed;
					OverVariables.moving = "up";
				}
			}
			// trying to get on rope, check if close enough
			else if (this.xVelocity > 0 && onright.getChar() == 'r') {
				if ((Math.abs(this.yPosition - y) < INTERACTION_DISTANCE)) {
					this.xPosition += this.xVelocity;
					// rightrope
					OverVariables.moving = "right";
				} else {
					this.yPosition -= this.speed;
					OverVariables.moving = "up";
				}
			}

			// move left if empty/ladder/rope
			if (this.xVelocity < 0
					&& (onleft == null || onleft.getChar() == 'l'
							|| onleft.getChar() == 'g' || (onleft.getChar() == 'b' && ((Dirt) onleft)
							.isHole()))) {
				if ((belowleft == null
						|| (belowleft.getChar() != 'b' && belowleft.getChar() != 'p') || (Math
						.abs(this.yPosition - y) < INTERACTION_DISTANCE))) {
					// System.out.println("Movin left");
					this.xPosition += this.xVelocity;
					OverVariables.moving = "left";
				}
				// move above block if short dist
				else {
					this.yPosition -= this.speed;
					OverVariables.moving = "up";
					// System.out.println("left");
				}

			}
			// check if rope close enough
			else if (this.xVelocity < 0 && onleft.getChar() == 'r') {
				if ((Math.abs(this.yPosition - y) < INTERACTION_DISTANCE)) {
					this.xPosition += this.xVelocity;
					// leftrope
					OverVariables.moving = "left";
				} else {
					this.yPosition -= this.speed;
					OverVariables.moving = "up";
				}
			}
			// picking up gold
			if (this instanceof Hero) {
				if (onright != null) {
					if (onright.getChar() == 'g') {
						this.goldCount++;
						Gold gold = (Gold) onright;
						gold.taken();
					}
				}
				if (onleft != null) {
					if (onleft.getChar() == 'g') {
						this.goldCount++;
						Gold gold = (Gold) onleft;
						gold.taken();
					}
				}
				if (belowleft != null) {
					if (belowleft.getChar() == 'g') {
						this.goldCount++;
						Gold gold = (Gold) belowleft;
						gold.taken();
					}
				}
				if (belowright != null) {
					if (belowright.getChar() == 'g') {
						this.goldCount++;
						Gold gold = (Gold) belowright;
						gold.taken();
					}
				}
			}
			// guard picking up gold
			if (this instanceof Guard) {
				if (onright != null) {
					if (onright.getChar() == 'g') {
						if (this.goldCount == 0) {
							this.goldCount++;
							Gold gold = (Gold) onright;
							gold.taken();
						}
					}
				}
				if (onleft != null) {
					if (onleft.getChar() == 'g') {
						if (this.goldCount == 0) {
							this.goldCount++;
							Gold gold = (Gold) onleft;
							gold.taken();
						}
					}
				}
				if (belowleft != null) {
					if (belowleft.getChar() == 'g') {
						if (this.goldCount == 0) {
							this.goldCount++;
							Gold gold = (Gold) belowleft;
							gold.taken();
						}
					}
				}
				if (belowright != null) {
					if (belowright.getChar() == 'g') {
						if (this.goldCount == 0) {
							this.goldCount++;
							Gold gold = (Gold) belowright;
							gold.taken();
						}
					}
				}
			}
			// escape ladder
			if (!this.escapeOn) {
				if (this.goldCount == MAX_GOLD) {
					this.world.escapeLadder();
					this.escapeOn = true;
				}
			}
		}
		// update char location
		this.world.updateCharacter(this, roundx, roundy,
				(int) Math.round(this.xPosition),
				(int) Math.round(this.yPosition));
	}

	/**
	 * Sets the character's x Velocity to zero.
	 * 
	 */
	public void stop() {
		this.xVelocity = 0;
	}

	/**
	 * character falls into a rule
	 * 
	 * @param twobelow
	 */
	abstract void fallInHole(Drawable twobelow);

	/**
	 * removes character from array
	 * 
	 */
	abstract void death();

	/**
	 * deals with rounding error for character respawn and holes
	 * 
	 * @param x
	 * @param y
	 */
	protected void checkDeath(double x, double y) {
		if (Math.abs(this.xPosition - x) < DEATH_DISTANCE) {
			this.death();
		}
	}

	@Override
	public char getChar() {
		// implemented subclass
		return 0;
	}
}