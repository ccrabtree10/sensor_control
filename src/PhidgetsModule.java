import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.phidgets.*;
import com.phidgets.event.*;

import javax.swing.*;

public class PhidgetsModule implements IModule, AttachListener, SensorChangeListener, KryoSerializable
{
	private transient InterfaceKitPhidget ikp;
	private PhidgetsMessageSender[] messageSenders;
	private transient IModuleChangeListener moduleChangeListener;
	private JPanel controlPanel;
	private transient ExecutorService exe;
	private transient ExecutorService[] exes;
		
	public PhidgetsModule() throws PhidgetException {
		messageSenders = new PhidgetsMessageSender[8];
		for (int x=0; x<8; x++) {
			messageSenders[x] = new PhidgetsMessageSender(x);
		}
		controlPanel = new JPanel();
		init();
	}	
	
	public void attached(AttachEvent ae) {
		try {
			System.out.println(ae.getSource().getSerialNumber());
		} catch (PhidgetException pe) {}
	}
	
	// The caller of this method will wait for it to return before sending
	// the next event. Therefore, it doesn't need to be synchronized.
	public void sensorChanged(final SensorChangeEvent sce) {
		long start = System.currentTimeMillis();
		exes[sce.getIndex()].execute(new Runnable() {
			public void run() {
				messageSenders[sce.getIndex()].send(new SensorChangeEvent(ikp, 0, sce.getValue()));	
			}
		});
	}

	public Object[] getMessageSenders() {
		return messageSenders;
	}
	
	public Object[] getMessageListeners() {
		return new Object[0];
	}

	public JComponent getControlPanel() {
		return controlPanel;
	}

	public void delete() {
		try {
			ikp.close();
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
	}
	
	public void setModuleChangeListener(IModuleChangeListener listener) {
		moduleChangeListener = listener;
	}

	public String getListenerLabel(int index) {
		return null;
	}

	public String getSenderLabel(int index) {
		return Integer.toString(index);
	}

	public String getLabel() {
		return "Phidgets Board";
	}
	
	private void init() throws PhidgetException {
		exes = new ExecutorService[8];
		for (int x=0; x<exes.length; x++) {
			exes[x] = Executors.newSingleThreadExecutor();
		}
		
		ikp = new InterfaceKitPhidget();
		ikp.open(117182);
		ikp.waitForAttachment();
		ikp.addAttachListener(this);
		ikp.addSensorChangeListener(this);
	}

	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, controlPanel);
		kryo.writeObject(output, messageSenders);
	}

	public void read(Kryo kryo, Input input) {
		controlPanel = kryo.readObject(input, JPanel.class);
		messageSenders = kryo.readObject(input, PhidgetsMessageSender[].class);
		try {
			init();
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
		//System.out.println(controlPanel);
	}
}
