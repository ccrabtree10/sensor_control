package com.sensorcontrol.modules.midicontroller;
import java.io.Serializable;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.sensorcontrol.main.MessageSensor;


public interface StMcIConverter extends Serializable
{
	public JPanel getControlPanel();
	public ShortMessage generateMessage(MessageSensor message) throws InvalidMidiDataException;
	public void initialize();
}
