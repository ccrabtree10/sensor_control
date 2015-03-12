package com.sensorcontrol.modules;
import com.sensorcontrol.modules.osc.OscMessage;


public interface IMessageListenerOsc 
{
	public void receive(OscMessage message);
}
