package com.sensorcontrol.modules.midinote;
import com.sensorcontrol.main.MessageSensor;
import com.sensorcontrol.modules.IMessageListenerSensor;


public class StMnListenerVelocity implements IMessageListenerSensor
{
	StMnNoteConverter converter;

	public void receive(MessageSensor message) 
	{
		converter.setVelocity(message);
	}
	
	public void setConverter(StMnNoteConverter converter)
	{
		this.converter = converter;
	}

}
