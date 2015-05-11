import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;

  
public class Digger extends JFrame{
	
	public static void main(String args[]){
		new Digger();
	}
	
	public Digger(){
		super("Digger (By T.Crody and R.Citron)");
		
		ControlBar ControlBar;
		
		Container mainframe = getContentPane();
		mainframe.setLayout(new GridLayout(2, 1));
		ControlBar = new ControlBar();
		mainframe.add(ControlBar);	

		
		setResizable(true);
		setSize(500,500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		setVisible(true);
	}

}
