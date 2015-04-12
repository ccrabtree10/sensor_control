

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public class StMnModule implements IModule, IMessageSender
{
	private ArrayList<IMessageListenerMidi> midiListeners;
	private IMessageListenerSensor[] messageListeners;
	private String[] listenerLabels;
	private StMnConverter converter;
	private ExecutorService exe;

	
	public StMnModule() {	
		midiListeners = new ArrayList<IMessageListenerMidi>();
		
		messageListeners = new IMessageListenerSensor[3];
		messageListeners[0] = new IMessageListenerSensor() {
			public synchronized void receive(MessageSensor message) {
				try {
					//final ShortMessage midiMessage = converter.generateMessage(message);
					final ShortMessage midiMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, 0, 0, message.getValue());
					Iterator<IMessageListenerMidi> iterator = midiListeners.iterator();
					while(iterator.hasNext()) {
						final IMessageListenerMidi midiListener = (IMessageListenerMidi) iterator.next();
						exe.execute(new Runnable() {
							public void run() {
								midiListener.receive(midiMessage);
							}
						});
					}
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}
		};
		
		messageListeners[1] = new IMessageListenerSensor() {
			public void receive(MessageSensor message) {
				converter.setPitch(message);
			}
		};
		
		messageListeners[2] = new IMessageListenerSensor() {
			public void receive(MessageSensor message) {
				converter.setVelocity(message);
			}
		};
		
		listenerLabels = new String[3];
		listenerLabels[0] = "Trg";
		listenerLabels[1] = "Pit";
		listenerLabels[2] = "Vel";
		
		converter = new StMnConverter();
		
		exe = Executors.newCachedThreadPool();
	}

	public Object[] getMessageSenders() {
		return new Object[]{this};
	}

	public Object[] getMessageListeners() {
		return messageListeners;
	}

	public String toString() {
		return midiListeners.toString();
	}

	public JComponent getControlPanel() {		
		return converter.getControlPanel();
	}

	public void delete() {
		
	}
	
	public void setModuleChangeListener(IModuleChangeListener listener) {
		
	}

	public String getListenerLabel(int index) {
		return listenerLabels[index];
	}

	public String getSenderLabel(int index) {
		return "Out";
	}

	public String getLabel() {
		return "S-to-MIDI-Note";
	}

	public void addMessageListener(Object listener) throws ClassCastException {
		IMessageListenerMidi midiListener = (IMessageListenerMidi) listener;
		midiListeners.add(midiListener);	
	}

	public void removeMessageListener(Object listener) {
		
	}
	
}
