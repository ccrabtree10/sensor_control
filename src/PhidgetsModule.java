import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.phidgets.*;
import com.phidgets.event.*;

import javax.swing.*;

public class PhidgetsModule implements IModule, AttachListener, SensorChangeListener
{
	private transient InterfaceKitPhidget ikp;
	private PhidgetsMessageSender[] messageSenders;
	private transient IModuleChangeListener moduleChangeListener;
	private JPanel controlPanel, moduleControlPanel;
	private JLabel debugLabel;
	private ExecutorService exe;
	private ExecutorService[] exes;
	
	// !!! debug
	private static int counter = 0;
	
	public PhidgetsModule() throws PhidgetException {
		messageSenders = new PhidgetsMessageSender[8];
		for (int x=0; x<8; x++) {
			messageSenders[x] = new PhidgetsMessageSender(x);
		}
		
		//exe = Executors.newSingleThreadExecutor();
		//exe = Executors.newCachedThreadPool();
		
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
	
	public void attached(AttachEvent ae) {
		try {
			System.out.println(ae.getSource().getSerialNumber());
		} catch (PhidgetException pe) {}
	}
	
	public synchronized void sensorChanged(final SensorChangeEvent sce) {
		exes[sce.getIndex()].execute(new Runnable() {
			int c = counter;
			public void run() {
				messageSenders[sce.getIndex()].send(new SensorChangeEvent(ikp, 0, c));	
			}
		});
		/*Thread t = new Thread(new Runnable() {
			int c = counter;
			public void run() {
				messageSenders[sce.getIndex()].send(new SensorChangeEvent(ikp, 0, c));	
			}
		});
		t.start();*/
		counter++;
		//messageSenders[sce.getIndex()].send(sce);	
	}

	public Object[] getMessageSenders()
	{
		return messageSenders;
	}
	
	public Object[] getMessageListeners()
	{
		return new Object[0];
	}

	public JComponent getControlPanel()
	{
		return controlPanel;
	}

	public void delete() 
	{
		try 
		{
			ikp.close();
		} catch (PhidgetException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void initialize()
	{
		
	}

	public void setModuleChangeListener(IModuleChangeListener listener) 
	{
		moduleChangeListener = listener;
	}
}
