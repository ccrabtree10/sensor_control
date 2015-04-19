import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.sound.midi.MidiUnavailableException;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Output;
import com.mxgraph.model.mxCell;
import com.phidgets.PhidgetException;


public class KryoTest {
	// Note: This is not a jUnit test.
	
	public static void main (String[] args) throws FileNotFoundException, IOException, PhidgetException, MidiUnavailableException, InterruptedException {
		// Setup logging.
		SimpleLog.init();
		Logger log = SimpleLog.log;
		
		// Initialise arrays.
		log.fine("Initialising arrays of modules.");
		GraphModule[] gPhidModules = new GraphModule[20];
		GraphModule[] gStMcModules = new GraphModule[20];
		GraphModule[] gMidiDemoModules = new GraphModule[20];
		
		// Populate arrays.
		log.fine("Populating arrays of modules.");
		for (int x=0; x<20; x++) {
			gPhidModules[x] = new GraphModule(new PhidgetsModule());
			gStMcModules[x] = new GraphModule(new StMcModule());
			gMidiDemoModules[x] = new GraphModule(new MidiDemoModule());
		}
		
		// Connect phidgets modules to StMcModules.
		log.fine("Connecting modules.");
		for (int x=0; x<20; x++) {
			IMessageListenerSensor listener = (IMessageListenerSensor) ((mxCell) gStMcModules[x].getMessageListenerCells()[0]).getValue();
			for (mxCell senderCell : gPhidModules[x].getMessageSenderCells()) {
				((IMessageSender) senderCell.getValue()).addMessageListener(listener);
			}
		}
		
		// Connect StMcModules to midi demo modules.
		for (int x=0; x<20; x++) {
			IMessageListenerMidi listener = (IMessageListenerMidi) ((mxCell) gMidiDemoModules[x].getMessageListenerCells()[0]).getValue();
			for (mxCell senderCell : gStMcModules[x].getMessageSenderCells()) {
				((IMessageSender) senderCell.getValue()).addMessageListener(listener);
			}
		}
		
		// Make more connections so there will be several occurrences of references to individual
		// in the serialised data.
		for (int x=2; x<18; x++) {
			IMessageListenerSensor listener = (IMessageListenerSensor) ((mxCell) gStMcModules[x].getMessageListenerCells()[0]).getValue();
			for (mxCell senderCell : gPhidModules[x+1].getMessageSenderCells()) {
				((IMessageSender) senderCell.getValue()).addMessageListener(listener);
			}
			for (mxCell senderCell : gPhidModules[x-1].getMessageSenderCells()) {
				((IMessageSender) senderCell.getValue()).addMessageListener(listener);
			}
		}
		
		// Put all modules in a SessionContainer.
		log.fine("Constructing session container.");
		GraphModule[] allCells = new GraphModule[60];
		for (int x=0; x<20; x++) {
			allCells[x] = gPhidModules[x];
		}
		for (int x=20; x<40; x++) {
			allCells[x] = gPhidModules[x-20];
		}
		for (int x=40; x<60; x++) {
			allCells[x] = gPhidModules[x-40];
		}
		
		SessionContainer container = new SessionContainer();
		container.setCells(allCells);
		
		// Setup Kryo.
		log.fine("Setting up Kryo serialisation.");
		Kryo kryo = new Kryo();
		DefaultInstantiatorStrategy defStrategy = new DefaultInstantiatorStrategy();
		StdInstantiatorStrategy stdStrategy = new StdInstantiatorStrategy();
		defStrategy.setFallbackInstantiatorStrategy(stdStrategy);
		kryo.setInstantiatorStrategy(stdStrategy);
		kryo.getRegistration(ArrayList.class).setInstantiator(defStrategy.newInstantiatorOf(ArrayList.class));
		
		ArrayList<Long> kryo_times = new ArrayList<Long>();
		ArrayList<Long> java_times = new ArrayList<Long>();
		
		// Run save process multiple times to get an average.
		for (int x=0; x<10; x++) {
			log.fine("Iteration: " + String.valueOf(x));
			// Create kryo output.
			log.fine("Making kryo outputstream.");
			Output kryout = new Output(new FileOutputStream("C:\\save_load\\kryo_session"+String.valueOf(x)+".session"));
	
			// Write using kryo.
			log.fine("Writing with kryo...");
			long kryo_start = System.nanoTime();
			kryo.writeObject(kryout, container);
			kryout.flush();
			kryout.close();
			long kryo_finish = System.nanoTime();
			long kryo_time = kryo_finish - kryo_start;
			log.fine("Finished writing with kryo.");
						
			// Set up Java native serialisation.
			log.fine("Making java output stream.");
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("C:\\save_load\\java_session"+String.valueOf(x)+".session"));
			
			// Write with java native.
			log.fine("Writing with Java native...");
			long java_start = System.nanoTime();
			out.writeObject(container);
			out.flush();
			out.close();
			long java_finish = System.nanoTime();
			long java_time = java_finish - java_start;
			log.fine("Finished writing with Java native.");
			
			log.fine("Kryo time: " + String.valueOf(kryo_time));
			log.fine("Java time: " + String.valueOf(java_time));
			log.fine("Kryo/Java Ratio: " + String.valueOf((double)java_time/kryo_time));
			kryo_times.add(kryo_time);
			java_times.add(java_time);
		}
		
		// Calculate average times.
		log.fine("Calculating average times.");
		
		long kryo_total_time = 0;
		long java_total_time = 0;
		
		for (int x=0; x<kryo_times.size(); x++) {
			kryo_total_time = kryo_total_time + kryo_times.get(x);
			java_total_time = java_total_time + java_times.get(x);
		}
		
		double kryo_average_time = kryo_total_time/kryo_times.size();
		double java_average_time = java_total_time/java_times.size();
		double java_kryo_ratio = java_average_time/kryo_average_time;
		log.fine("Kryo average time: " + String.valueOf(kryo_average_time));
		log.fine("Java average time: " + String.valueOf(java_average_time));
		log.fine("Java/Kryo ratio: " + String.valueOf(java_kryo_ratio));
		
		
		
		log.fine("Test has finished.");
	}
	
}
