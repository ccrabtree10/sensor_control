
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.sound.midi.ShortMessage;
import javax.swing.JComponent;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Module which uses the TeVirtualMIDI library to send MIDI messages to external software.
 * Note: This module uses the library teVirtualMIDI.dll along with its driver, therefore to 
 * use it, these files must be installed. These can be installed by by installing the loopMIDI 
 * software from Tobias Erichsen's website: http://www.tobias-erichsen.de/software/loopmidi.html
 * @author Christopher Crabtree
 */
public class MoModule implements IModule, IMessageListenerMidi, KryoSerializable {
	private transient TeVirtualMIDI midiPort;
	private String name;
	
	/**
	 * Construct a new virtual MIDI port with this name.
	 * @param name The name of the virtual MIDI port to create.
	 */
	public MoModule(String name) {
		this.name = name;
		midiPort = new TeVirtualMIDI(name);
	}

	public Object[] getMessageSenders() {
		return new Object[0];
	}

	public Object[] getMessageListeners() {
		return new Object[]{this};
	}
	
	public String toString() {
		return name;
	}

	public JComponent getControlPanel() {
		return null;
	}

	public void delete() {
		midiPort.shutdown();
	}

	public void initialize() {
	}

	public String getListenerLabel(int index) {
		return "In";
	}

	public String getSenderLabel(int index) {
		return null;
	}

	public String getLabel() {
		return "Midi-Out";
	}

	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, name);
	}

	public void read(Kryo kryo, Input input) {
		name = kryo.readObject(input, String.class);
		midiPort = new TeVirtualMIDI(name);
	}
	
	/**
	 * Receive this MIDI message and send to the module's virtual MIDI port.
	 */
	public void receive(ShortMessage message) {
		midiPort.sendCommand(message.getMessage());
	}
}
