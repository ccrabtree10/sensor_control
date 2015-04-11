

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

public class StMnModule implements IModule
{
	private ArrayList<IMessageListenerMidi> midiListeners;
	private IMessageListenerSensor[] messageListeners;
	private StMnListenerTrigger listenerTrigger;
	private StMnListenerPitch listenerPitch;
	private StMnListenerVelocity listenerVelocity;
	private StMnMessageSender messageSender;
	private StMnNoteConverter noteConverter;
	private transient JLabel moduleLabel;
	private transient JPanel converterControlPanel;
	private transient JPanel controlPanel;
	
	public StMnModule()
	{	
		midiListeners = new ArrayList<IMessageListenerMidi>();
		
		messageListeners = new IMessageListenerSensor[3];
		listenerTrigger = new StMnListenerTrigger();
		listenerPitch = new StMnListenerPitch();
		listenerVelocity = new StMnListenerVelocity();
		noteConverter = new StMnNoteConverter();
		
		messageListeners[0] = listenerTrigger;
		messageListeners[1] = listenerPitch;
		messageListeners[2] = listenerVelocity;
		
		messageSender = new StMnMessageSender();
		
		initialize();
	}

	public Object[] getMessageSenders() 
	{
		return new Object[]{messageSender};
	}

	public Object[] getMessageListeners() 
	{
		return messageListeners;
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
		moduleLabel = new JLabel("Sensor To MIDI Note Module");
		
		
		converterControlPanel = noteConverter.getControlPanel();
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		JPanel row1 = new JPanel();
		row1.add(moduleLabel);
		
		controlPanel.add(row1);		
		controlPanel.add(converterControlPanel);
		controlPanel.validate();
		controlPanel.repaint();
		
		// Set all references etc.
		listenerTrigger.setMidiListeners(midiListeners);
		messageSender.setMidiListeners(midiListeners);
		
		listenerTrigger.setConverter(noteConverter);
		listenerPitch.setConverter(noteConverter);
		listenerVelocity.setConverter(noteConverter);
		
		// initialize needed here !!!
	}

	@Override
	public void setModuleChangeListener(IModuleChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getListenerLabel(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSenderLabel(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
