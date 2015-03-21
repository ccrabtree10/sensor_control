
import java.io.Serializable;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JComponent;
import javax.swing.JPanel;


public interface StMcIConverter extends Serializable
{
	public JPanel getControlPanel();
	public ShortMessage generateMessage(MessageSensor message) throws InvalidMidiDataException;
	public void initialize();
}
