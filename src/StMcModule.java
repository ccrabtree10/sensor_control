

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

public class StMcModule implements IModule, IMessageListenerSensor, IMessageSender
{
	private ArrayList<IMessageListenerMidi> midiListeners;
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
		initialize();
	}

	public Object[] getMessageSenders() 
	{
		return new Object[]{this};
	}

	public Object[] getMessageListeners() 
	{
		return new Object[]{this};
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
				System.out.println("remove me!!!");
			}
		});
				
		for(int x=0; x<commandSelector.getItemCount(); x++)
		{
			commandSelector.getItemAt(x).initialize();
		}
	}

	public void setModuleChangeListener(IModuleChangeListener listener) {
		
	}

	public String getListenerLabel(int index) {
		return "In";
	}

	public String getSenderLabel(int index) {
		return "Out";
	}

	public String getLabel() {
		return "FUCKING BULL";
	}

	public void receive(MessageSensor message) {
		System.out.println("receive");
		// !!! Change this try catch to just set shortmessage, then test for null,
		// or could just return in the exception handler.
		/*try {
			final ShortMessage midiMessage = currentConverter.generateMessage(message);
			Iterator<IMessageListenerMidi> iterator = midiListeners.iterator();
			while(iterator.hasNext()) {
				final IMessageListenerMidi midiListener = (IMessageListenerMidi) iterator.next();
				exe.execute(new Runnable() { 
					public void run() {
						su.log.log(su.f, "sending to midi out module");
						midiListener.receive(midiMessage);
					}
				});
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		*/
	}

	public void addMessageListener(Object listener) throws ClassCastException {
		IMessageListenerMidi midiListener = (IMessageListenerMidi) listener;
		midiListeners.add(midiListener);
		
	}

	public void removeMessageListener(Object listener) {
		midiListeners.remove(listener);
	}
	
	
}
