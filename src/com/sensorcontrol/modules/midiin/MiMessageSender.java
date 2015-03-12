package com.sensorcontrol.modules.midiin;
import java.util.ArrayList;

import com.sensorcontrol.modules.IMessageListenerMidi;
import com.sensorcontrol.modules.IMessageSender;


public class MiMessageSender implements IMessageSender
{

	private ArrayList<IMessageListenerMidi> listeners;
	
	public void addMessageListener(Object listener) throws ClassCastException 
	{
		IMessageListenerMidi midiListener = (IMessageListenerMidi) listener;
		listeners.add(midiListener);
	}

	public void removeMessageListener(Object listener) 
	{
		
	}
	
	public void setMidiListeners(ArrayList<IMessageListenerMidi> listeners)
	{
		this.listeners = listeners;
	}
	
	public String toString()
	{
		return "In";
	}
	
}
