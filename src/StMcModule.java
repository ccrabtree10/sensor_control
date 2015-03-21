

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.sound.midi.*;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.jidesoft.swing.RangeSlider;

public class StMcModule implements IModule
{
	private ArrayList<IMessageListenerMidi> midiListeners;
	private StMcMessageListenerSensor messageListener;
	private StMcMessageSender messageSender;
	private transient JLabel commandLabel, moduleLabel;
	private JComboBox<StMcIConverter> commandSelector;
	private transient JPanel converterControlPanel;
	private transient JPanel controlPanel;
	
	public StMcModule()
	{	
		midiListeners = new ArrayList<IMessageListenerMidi>();

		commandSelector = new JComboBox<StMcIConverter>();
		commandSelector.setModel(new DefaultComboBoxModel<StMcIConverter>());
		commandSelector.addItem(new StMcControllerConverter());
		commandSelector.addItem(new StmNoteConverter());
		
		messageListener = new StMcMessageListenerSensor();
		messageSender = new StMcMessageSender();
		
		initialize();
	}

	public Object[] getMessageSenders() 
	{
		return new Object[]{messageSender};
	}

	public Object[] getMessageListeners() 
	{
		return new Object[]{messageListener};
	}

	public String toString() 
	{
		return midiListeners.toString();
	}

	public JComponent getControlPanel()
	{		
		return controlPanel;
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}
	
	public void initialize()
	{
		moduleLabel = new JLabel("Sensor To MIDI Module");
		commandLabel = new JLabel("Command: ");
		
		converterControlPanel = ((StMcIConverter) commandSelector.getSelectedItem()).getControlPanel();
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		JPanel row3 = new JPanel();
		row1.add(moduleLabel);
		row3.add(commandLabel);
		row3.add(commandSelector);
		
		controlPanel.add(row1);
		controlPanel.add(row2);		
		controlPanel.add(row3);
		controlPanel.add(converterControlPanel);
		controlPanel.validate();
		controlPanel.repaint();
		
		commandSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) 
			{
				messageListener.setCurrentConverter(((StMcIConverter) commandSelector.getSelectedItem()));
				controlPanel.remove(converterControlPanel);
				converterControlPanel =((StMcIConverter) commandSelector.getSelectedItem()).getControlPanel();
				controlPanel.add(converterControlPanel);
				controlPanel.revalidate();
			}
		});
		
		messageListener.setCurrentConverter(((StMcIConverter) commandSelector.getSelectedItem()));
		messageListener.setMidiListeners(midiListeners);
		messageSender.setMidiListeners(midiListeners);
		
		for(int x=0; x<commandSelector.getItemCount(); x++)
		{
			commandSelector.getItemAt(x).initialize();
		}
	}

	@Override
	public void setModuleChangeListener(IModuleChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
	
}
