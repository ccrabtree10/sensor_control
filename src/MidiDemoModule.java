
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
/**
 * Module which loads the systems default synthesiser. It has one input which accepts MIDI 
 * messages (of type ShortMessage).
 * @author Christopher Crabtree
 */
public class MidiDemoModule implements IModule, IMessageListenerMidi, KryoSerializable {
	private transient Synthesizer synth;
	private transient Receiver rcvr;
	private transient JPanel controlPanel;
	private transient JButton resetButton;
	private Integer instrument;
	
	
	public MidiDemoModule() throws MidiUnavailableException {
		// Initial values.
		instrument = 0;
		init();
	}
	
	private void init() throws MidiUnavailableException {
		// Get system's default synthesiser.
		synth = MidiSystem.getSynthesizer();
		
		// Open synth - tells synth to start up and accept MIDI and generate audio.
		synth.open();
		
		// Unload and load soundbank - not always needed, but ensure the synth is
		// properly initialised.
		synth.unloadAllInstruments(synth.getDefaultSoundbank());
		synth.loadAllInstruments(synth.getDefaultSoundbank());
		
		// Select an instrument on channel 1.
		synth.getChannels()[0].programChange(instrument);
		
		// Get a reference the the synth's receiver.
		rcvr = synth.getReceiver();	
		
		// Setup GUI.
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		JLabel selectorLabel = new JLabel("Instrument: ");
		resetButton = new JButton("Reset synth");
		final JComboBox instrumentSelector = new JComboBox(new DefaultComboBoxModel());
		
		// Get list of instruments from instrument bank and add to JComboBox.
		Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
		for (int x = 0; x < instruments.length; x++ ) {
			instrumentSelector.addItem(instruments[x].getName());
		}
		instrumentSelector.setSelectedIndex(instrument);
		
		instrumentSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synth.getChannels()[0].programChange(instrumentSelector.getSelectedIndex());
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MidiChannel[] channels = synth.getChannels();
				for (int channel = 0; channel < channels.length; channel++) {
					channels[channel].allNotesOff();
				}
			}
		});
		
		row1.add(selectorLabel);
		row1.add(instrumentSelector);
		row2.add(resetButton);
		controlPanel.add(row1);
		controlPanel.add(row2);
	}

	public Object[] getMessageSenders() {
		return new Object[0];
	}

	public Object[] getMessageListeners() {
		return new Object[]{this};
	}

	public JComponent getControlPanel() {
		return controlPanel;
	}

	public void delete() {
		synth.close();
	}

	public String getListenerLabel(int index) {
		return "In";
	}

	public String getSenderLabel(int index) {
		return null;
	}

	public String getLabel() {
		return "Midi Demo Synth Module";
	}
	/**
	 * Receive this short message and send to the modules internal MIDI synthesiser.
	 */
	public void receive(ShortMessage message) {
		rcvr.send(message, -1);
	}

	public void read(Kryo kryo, Input input) {
		instrument = kryo.readObject(input, Integer.class);
		try {
			init();
		} catch (MidiUnavailableException e) {
			JOptionPane.showMessageDialog(null, 
					"An error occurred while this Midi Demo module was loading: \n" + e.getMessage());
		}
	}

	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, instrument);
	}
	
	// Note: these methods are not used in the application, they were used in testing to compare 
	// the speed of Java native serialization and Kryo serilaization.
	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeObject(instrument);
	}
	
	private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
		instrument = (int) input.readObject();
	}	
			
}
