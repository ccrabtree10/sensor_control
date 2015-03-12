import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.sound.midi.ShortMessage;
import javax.swing.JComponent;


public class MoModule implements IModule
{
	private MoMessageListener listener;
	private transient TeVirtualMIDI midiPort;
	private String name;
	
	public MoModule(String name)
	{
		this.name = name;
		listener = new MoMessageListener();
		initialize();	
	}

	public Object[] getMessageSenders() 
	{
		return new Object[0];
	}

	public Object[] getMessageListeners() 
	{
		return new Object[]{listener};
	}
	
	public String toString()
	{
		return name;
	}

	public JComponent getControlPanel() 
	{
		return null;
	}

	public void delete() 
	{
		midiPort.shutdown();
	}
	
	public void initialize()
	{
		midiPort = new TeVirtualMIDI(name);
		listener.setMidiPort(midiPort);
	}

	@Override
	public void setModuleChangeListener(IModuleChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
	
}
