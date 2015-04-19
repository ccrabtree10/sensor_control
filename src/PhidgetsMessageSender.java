
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jidesoft.swing.RangeSlider;
import com.phidgets.event.SensorChangeEvent;

/**
 * Represents an individual output in a PhidgetsModule.
 * @author Christopher Crabtree
 */
public class PhidgetsMessageSender implements IMessageSender, KryoSerializable {
	private ArrayList<IMessageListenerSensor> listeners;
	private int index;
	private transient ExecutorService exe;
	
	public static final int CON_TO_SWITCH_THRESH_DEF = 500, SWITCH_TO_CON_OFF_DEF = 200, SWITCH_TO_CON_ON_DEF = 800;
	
	/**
	 * Create a new sender with the specified index.
	 * @param index The index of the output.
	 */
	public PhidgetsMessageSender(int index) {
		listeners = new ArrayList<IMessageListenerSensor>();
		this.index = index;
		exe = Executors.newCachedThreadPool();
	}

	/**
	 * Add the listener to the outputs list of listeners.
	 * @param listener The listener to add.
	 */
	public void addMessageListener(Object listener) throws ClassCastException {
		IMessageListenerSensor newListener = (IMessageListenerSensor) listener;
		listeners.add(newListener);
	}
	
	/**
	 * Convert the SensorChangeEvent to a sensor message (of type MessageSensor) and 
	 * send to the output's list of listeners.
	 * @param sce The SensorChangeEvent to convert to a sensor message and send.
	 */
	public void send(SensorChangeEvent sce) {
		final MessageSensor message = new MessageSensor(sce.getValue());
		Iterator<IMessageListenerSensor> iterator = listeners.iterator();
		while(iterator.hasNext()) {
			final IMessageListenerSensor listener = (IMessageListenerSensor) iterator.next();
			exe.execute(new Runnable() { 
				public void run() {
					listener.receive(message);
				}
			});
		}
	}
	
	public String toString() {
		return String.valueOf(index);
	}

	/**
	 * Remove the message listener from this output's list of listeners.
	 * @param listener The listener to remove.
	 */
	public void removeMessageListener(Object listener) {
		listeners.remove(listener);
	}

	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, listeners);
		kryo.writeObject(output, index);
	}

	public void read(Kryo kryo, Input input) {
		listeners = kryo.readObject(input, ArrayList.class);
		index = kryo.readObject(input, int.class);
		exe = Executors.newCachedThreadPool();
	}
	
	// Note: these methods are not used in the application, they were used in testing to compare 
	// the speed of Java native serialization and Kryo serilaization.
	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeObject(listeners);
		output.writeObject(index);
	}
	
	private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
		listeners = (ArrayList) input.readObject();
		index = (int) input.readObject();
		exe = Executors.newCachedThreadPool();
	}	
}
