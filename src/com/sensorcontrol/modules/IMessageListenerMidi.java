package com.sensorcontrol.modules;
import java.io.Serializable;

import javax.sound.midi.ShortMessage;


public interface IMessageListenerMidi  
{
	public void receive(ShortMessage message);
}
