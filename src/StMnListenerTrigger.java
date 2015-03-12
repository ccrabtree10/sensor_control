import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;


public class StMnListenerTrigger implements IMessageListenerSensor
{
	StMnNoteConverter converter;
	ArrayList<IMessageListenerMidi> midiListeners;
	MIDITest synth;

	public StMnListenerTrigger()
	{
		synth = new MIDITest();
		try {
			synth.getSystemInfo();
			synth.init();
			//synth.playPiano(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void receive(MessageSensor message) 
	{
		try 
		{
			ShortMessage midiMessage = converter.generateMessage(message);
			// DEBUG.
			synth.playSound(midiMessage);
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
	
	public void setConverter(StMnNoteConverter converter)
	{
		this.converter = converter;
	}
	
	public void setMidiListeners(ArrayList<IMessageListenerMidi> midiListeners)
	{
		this.midiListeners = midiListeners;
	}

}
