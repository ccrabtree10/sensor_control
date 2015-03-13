package com.sensorcontrol.main;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
// Added this in mac. Commit. Push.
// Also added this in mac. Commit. Push.

public class ControlPanel extends JPanel
{
	public ControlPanel()
	{
		this.setLayout(new GridLayout(0, 1));
	}
	
	public void updateView(JComponent[][] controls)
	{
		this.removeAll();
		if(controls != null)
		{
			for(int row=0; row<controls.length; row++)
			{
				JPanel cell = new JPanel();
				for(int col=0; col<controls[row].length; col++)
				{
					cell.add(controls[row][col]);
				}
				this.add(cell);
			}
		}
		this.validate();
	}
}