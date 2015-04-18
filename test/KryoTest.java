import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.sound.midi.MidiUnavailableException;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Output;
import com.mxgraph.model.mxCell;
import com.phidgets.PhidgetException;


public class KryoTest {
	// Note: This is not a jUnit test.
	
	public static void main (String[] args) throws FileNotFoundException, IOException, PhidgetException, MidiUnavailableException {
		// Initialise arrays.
		GraphModule[] gPhidModules = new GraphModule[20];
		GraphModule[] gStMcModules = new GraphModule[20];
		GraphModule[] gMidiDemoModules = new GraphModule[20];
		
		// Populate arrays.
		for (int x=0; x<20; x++) {
			gPhidModules[x] = new GraphModule(new PhidgetsModule());
			gStMcModules[x] = new GraphModule(new StMcModule());
			gMidiDemoModules[x] = new GraphModule(new MidiDemoModule());
		}
		
		// Connect phidgets modules to StMcModules.
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
		
		Kryo kryo = new Kryo();
		DefaultInstantiatorStrategy defStrategy = new DefaultInstantiatorStrategy();
		StdInstantiatorStrategy stdStrategy = new StdInstantiatorStrategy();
		defStrategy.setFallbackInstantiatorStrategy(stdStrategy);
		kryo.setInstantiatorStrategy(stdStrategy);
		kryo.getRegistration(ArrayList.class).setInstantiator(defStrategy.newInstantiatorOf(ArrayList.class));
		
		
		Output kryout = new Output(new FileOutputStream("C:\\save_load\\kryo_session.session"));
		kryo.writeObject(kryout, allCells);
		kryout.flush();
		kryout.close();
		
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("C:\\save_load\\java_session.session"));
		out.writeObject(allCells);
		
		out.flush();
		out.close();
		
		System.out.println("done");
	}
	
}
