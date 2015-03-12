package com.sensorcontrol.modules;

import com.sensorcontrol.main.MessageSensor;


public interface IMessageListenerSensor
{
	public void receive(MessageSensor message);
}
