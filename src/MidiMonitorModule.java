import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.midi.ShortMessage;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Module for monitoring MIDI messages. The module has one input which accepts MIDI 
 * messages (of type ShortMessage). The details of the MIDI message are shown on the 
 * modules control panel.
 * @author Christopher Crabtree
 */
public class MidiMonitorModule implements IModule, IMessageListenerMidi, KryoSerializable {

	private transient JPanel controlPanel;
	private transient JLabel nPitch, nVelocity, ccNumber, ccValue, channel;
	
	public MidiMonitorModule() {
		init();
	}

	private void init() {
		// Instantiate GUI components.
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		channel = new JLabel();
		nPitch = new JLabel();
		nVelocity = new JLabel();
		ccNumber = new JLabel();
		ccValue = new JLabel();
		ArrayList<JPanel> rows = new ArrayList<JPanel>();
		for (int x=0; x<7; x++) {
			JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			rows.add(row);
		}
		rows.get(0).add(new JLabel("Channel: "));
		rows.get(0).add(channel);
		rows.get(1).add(new JLabel("Note Message"));
		rows.get(2).add(new JLabel("Note: "));
		rows.get(2).add(nPitch);
		rows.get(3).add(new JLabel("Velocity: "));
		rows.get(3).add(nVelocity);
		rows.get(4).add(new JLabel("Channel Message"));
		rows.get(5).add(new JLabel("Controller Number: "));
		rows.get(5).add(ccNumber);
		rows.get(6).add(new JLabel("Controller Value: "));
		rows.get(6).add(ccValue);
		Iterator<JPanel> it = rows.iterator();
		while (it.hasNext()) {
			controlPanel.add(it.next());
		}
	}

	/**
	 * Receive this MIDI message and show its contents on the module's control panel.
	 */
	public void receive(ShortMessage message) {
		channel.setText(Integer.toString(message.getChannel()));
		if (message.getCommand() == ShortMessage.NOTE_ON) {
			nPitch.setText(Integer.toString(message.getData1()));
			nVelocity.setText(Integer.toString(message.getData2()));
		} else if (message.getCommand() == ShortMessage.CONTROL_CHANGE) {
			ccNumber.setText(Integer.toString(message.getData1()));
			ccValue.setText(Integer.toString(message.getData2()));
		}
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
		// Nothing to clean up.
	}

	public String getListenerLabel(int index) {
		return "In";
	}

	public String getSenderLabel(int index) {
		return null;
	}

	public String getLabel() {
		return "Midi Monitor";
	}

	public void read(Kryo arg0, Input arg1) {
		// Initialise GUI components.
		init();
	}

	public void write(Kryo arg0, Output arg1) {
		// Nothing to write.
	}
	
}
