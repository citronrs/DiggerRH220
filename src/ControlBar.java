import javax.swing.JButton;
import javax.swing.JPanel;


public class ControlBar extends JPanel{
	
	private JButton newButton;
	JPanel controlBar;
	
	public ControlBar(){
		controlBar = new JPanel();
		JButton Button = new JButton();
		this.add(Button);
		setSize(100, 500);
	}

}
