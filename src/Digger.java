import javax.swing.JFrame;
import javax.swing.JPanel;

public class Digger extends JFrame {

	Hero hero = new Hero();

	public static void main(String args[]) {
		Digger d = new Digger();
		while (true) {
			d.gethero().moving();
			d.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException exception) {
				// TODO Auto-generated catch-block stub.
				exception.printStackTrace();
			}
		}
	}

	public Digger() {
		super("Digger (By T.Crody and R.Citron)");

		ControlBar ControlBar;
		
		JPanel game = new JPanel();
		
		ControlBar = new ControlBar();
		add(ControlBar);
		this.hero = new Hero();
		game.add(hero);
		add(game);
		setSize(300, 400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public Hero gethero(){
		return hero;
	}

}
