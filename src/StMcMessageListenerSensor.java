import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;


public class StMcMessageListenerSensor implements IMessageListenerSensor
{
	ArrayList<IMessageListenerMidi> midiListeners;
	StMcIConverter currentConverter;

	public void setMidiListeners(ArrayList<IMessageListenerMidi> midiListeners)
	{
		this.midiListeners = midiListeners;
	}
	
	public void setCurrentConverter(StMcIConverter currentConverter)
	{
		this.currentConverter = currentConverter;
	}
	
	public void receive(MessageSensor message) 
	{
		try 
		{
			ShortMessage midiMessage = currentConverter.generateMessage(message);
			Iterator iterator = midiListeners.iterator();
			while(iterator.hasNext())
			{
				IMessageListenerMidi midiListener = (IMessageListenerMidi) iterator.next();
				System.out.println("StmLis: Message sent to: "+midiListener+", "+midiMessage.getChannel()+", "+midiMessage.getData1()+", "+midiMessage.getData2());
				midiListener.receive(midiMessage);
			}
		} 
		catch (InvalidMidiDataException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return "In";
	}
}
