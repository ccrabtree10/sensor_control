package com.sensorcontrol.test;
import javax.sound.midi.*;

public class MIDITest
{
	Synthesizer synth;
	MidiChannel[] mc;
	Receiver rcvr;
	
	public MIDITest()
	{
	}
	
	public static void main(String[] args) throws Exception
	{
		MIDITest midiTest = new MIDITest();
		midiTest.getSystemInfo();
		
	}
	
	public void getSystemInfo() throws Exception
	{
		MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
		System.out.println("Number of devices: "+info.length);
		for (int x=0; x<info.length; x++)
		{
			System.out.println("*** Device "+x+" ***");
			System.out.println("    Name: "+info[x].getName());
			System.out.println("    Vendor: "+info[x].getVendor());
			System.out.println("    Version: "+info[x].getVersion());
			System.out.println("    Description: "+info[x].getDescription());
			System.out.println("    Class: "+info[x].getClass());
		}
	}
	
	public void playPiano(int deviceNumber) throws Exception
	{
		// Get array of info objects about midi devices currently in system.
		MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
		// Get device at deviceNumber - I know which number to put in from system info method above.
		synth = (Synthesizer) MidiSystem.getMidiDevice(info[deviceNumber]);
		// Open synth - tells synth to start up and accept MIDI and generate audio.
		synth.open();
		// Unload and load soundbank - probably not needed.
		synth.unloadAllInstruments(synth.getDefaultSoundbank());
		synth.loadAllInstruments(synth.getDefaultSoundbank());
		// Get array of MIDI channels
		mc = synth.getChannels();
		
		// Display name of instrument(program) assigned to channel 0.
		Instrument[] instruments = synth.getLoadedInstruments();
		System.out.println(instruments[0].getName());
		
		// Change some values of midi channel 1.
		mc[0].controlChange(0,0);
		mc[0].programChange(1);
		mc[0].controlChange(7, 100);
		mc[0].controlChange(39, 100);
		
		// Send note on/off commands to midi channel 1.
		System.out.println("Playing...");
		/*
		mc[0].noteOn(50, 100);
		Thread.sleep(5000);
		mc[0].noteOff(50);
		*/
		
		System.out.println("Channel 0 Program: "+mc[0].getProgram());
		System.out.println("Channel 0 Volume: "+mc[0].getController(7));
		Thread thread = new Thread(new Runnable(){
			public void run()
			{
				for (int x=0; x<100; x++)
				{
					// Alternatively can send a midi message to synth's receiver.
					ShortMessage myMsg = new ShortMessage();
					// Start playing the note Middle C (60), 
					// moderately loud (velocity = 93).
					try 
					{
						myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
						long timeStamp = -1;
						Receiver rcvr = MIDITest.this.synth.getReceiver();
						//Receiver rcvr = synth.getReceiver();
						rcvr.send(myMsg, timeStamp);
						Thread.sleep(500);
					} catch (Exception e) {}
				}
				
				// Close synth to free up system resources.	
				synth.close();
				System.out.println("Closed.");
			}
		});
		
		thread.start();
		
	}
	
	public void init() throws MidiUnavailableException
	{
		// Get array of info objects about midi devices currently in system.
				MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
				// Get device at deviceNumber - I know which number to put in from system info method above.
				synth = (Synthesizer) MidiSystem.getMidiDevice(info[0]);
				// Open synth - tells synth to start up and accept MIDI and generate audio.
				synth.open();
				// Unload and load soundbank - probably not needed.
				synth.unloadAllInstruments(synth.getDefaultSoundbank());
				synth.loadAllInstruments(synth.getDefaultSoundbank());
				// Get array of MIDI channels
				mc = synth.getChannels();
				
				// Display name of instrument(program) assigned to channel 0.
				Instrument[] instruments = synth.getLoadedInstruments();
				System.out.println(instruments[0].getName());
				
				// Change some values of midi channel 1.
				mc[0].controlChange(0,0);
				mc[0].programChange(1);
				mc[0].controlChange(7, 100);
				mc[0].controlChange(39, 100);
				
				rcvr = synth.getReceiver();
				
	}
	
	public void playSound(ShortMessage message)
	{
		long timeStamp = -1;
		rcvr.send(message, timeStamp);
	}
	
	
	public MidiChannel getMidiChannel()
	{
		return mc[0];
	}
			
}
