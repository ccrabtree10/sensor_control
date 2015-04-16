
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JPanel;


public class InspectorPanel extends JPanel {	
	public void viewControlPanel(JComponent controlPanel) {
		this.removeAll();
		this.add(controlPanel);
	}
}