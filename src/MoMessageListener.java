
import javax.sound.midi.ShortMessage;


public class MoMessageListener implements IMessageListenerMidi 
{
	private TeVirtualMIDI midiPort;
	
	public void setMidiPort(TeVirtualMIDI midiPort)
	{
		this.midiPort = midiPort;
	}
	
	public synchronized void receive(ShortMessage message) 
	{
		//midiPort.sendCommand(message.getMessage());
		su.log.log(su.f, "received " + message.getData2());
		
		
	}
	
	public String toString()
	{
		return "In";
	}
}
