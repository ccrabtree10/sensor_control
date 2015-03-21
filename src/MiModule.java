

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.sound.midi.ShortMessage;
import javax.swing.JComponent;


public class MiModule implements IModule
{
	private transient MiMessageSender sender;
	private ArrayList<IMessageListenerMidi> listeners;
	private transient TeVirtualMIDI midiPort;
	private String name;
	
	public MiModule(String name)
	{
		this.name = name;
		sender = new MiMessageSender();
		listeners = new ArrayList<IMessageListenerMidi>();
		initialize();	
	}

	public Object[] getMessageSenders() 
	{
		return new Object[]{sender};	}

	public Object[] getMessageListeners() 
	{
		return new Object[0];
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
		sender.setMidiListeners(listeners);
		Thread receiveThread = new Thread(){
			public void run()
			{
				while(true)
				{
					byte[] message = midiPort.getCommand();
					System.out.println(message);
				}
			}
		};
		receiveThread.start();
	}

	public void setModuleChangeListener(IModuleChangeListener listener) 
	{
		
	}
	
}
