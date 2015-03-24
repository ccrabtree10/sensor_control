
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;


public class StMcMessageListenerSensor implements IMessageListenerSensor
{
	ArrayList<IMessageListenerMidi> midiListeners;
	StMcIConverter currentConverter;
	private ExecutorService exe;
	
	public StMcMessageListenerSensor() {
		exe = Executors.newCachedThreadPool();
	}

	public void setMidiListeners(ArrayList<IMessageListenerMidi> midiListeners)
	{
		this.midiListeners = midiListeners;
	}
	
	public void setCurrentConverter(StMcIConverter currentConverter)
	{
		this.currentConverter = currentConverter;
	}
	
	public synchronized void receive(MessageSensor message) {
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
	
	public String toString()
	{
		return "In";
	}
}
