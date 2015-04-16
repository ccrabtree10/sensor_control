
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class MidiDemoModule implements IModule, IMessageListenerMidi, KryoSerializable {
	private transient Synthesizer synth;
	private transient Receiver rcvr;
	private transient JPanel controlPanel;
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
		JLabel selectorLabel = new JLabel("Instrument: ");
		final JComboBox instrumentSelector = new JComboBox(new DefaultComboBoxModel());
		
		Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
		for (int x = 0; x < instruments.length; x++ ) {
			instrumentSelector.addItem(instruments[x].getName());
		}
		
		controlPanel.add(selectorLabel);
		controlPanel.add(instrumentSelector);
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

	public void setModuleChangeListener(IModuleChangeListener listener) {
		
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

	public void receive(ShortMessage message) {
		su.log.fine("D1: " + message.getData1());
		su.log.fine("D2: " + message.getData2());
		
		rcvr.send(message, -1);
	}

	@Override
	public void read(Kryo arg0, Input arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(Kryo arg0, Output arg1) {
		// TODO Auto-generated method stub
		
	}
			
}
