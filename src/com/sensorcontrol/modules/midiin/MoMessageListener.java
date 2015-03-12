package com.sensorcontrol.modules.midiin;
import javax.sound.midi.ShortMessage;

import com.sensorcontrol.modules.IMessageListenerMidi;


public class MoMessageListener implements IMessageListenerMidi 
{
	private TeVirtualMIDI midiPort;
	
	public void setMidiPort(TeVirtualMIDI midiPort)
	{
		this.midiPort = midiPort;
	}
	
	public void receive(ShortMessage message) 
	{
		midiPort.sendCommand(message.getMessage());
		System.out.println("MoLis receive: "+message.getChannel()+", "+message.getData1()+", "+message.getData2());
	}
	
	public String toString()
	{
		return "In";
	}
}
