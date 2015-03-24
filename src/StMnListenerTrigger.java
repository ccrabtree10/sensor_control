
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;


public class StMnListenerTrigger implements IMessageListenerSensor {
	StMnNoteConverter converter;
	ArrayList<IMessageListenerMidi> midiListeners;
	MIDITest synth;
	private ExecutorService exe;


	public StMnListenerTrigger() {
		exe = Executors.newCachedThreadPool();
	}
	
	public synchronized void receive(MessageSensor message) 
	{
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
	
	public void setConverter(StMnNoteConverter converter) {
		this.converter = converter;
	}
	
	public void setMidiListeners(ArrayList<IMessageListenerMidi> midiListeners) {
		this.midiListeners = midiListeners;
	}

}
