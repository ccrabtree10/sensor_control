import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.phidgets.*;
import com.phidgets.event.*;

import javax.swing.*;

public class xPhidgetsModuleArray implements IModule, AttachListener, SensorChangeListener
{
	private transient InterfaceKitPhidget ikp;
	private PhidgetsMessageSender[] messageSenders;
	private transient IModuleChangeListener moduleChangeListener;
	private JComboBox<String> sensorType1;
	
	public xPhidgetsModuleArray() throws PhidgetException
	{
		sensorType1 = new JComboBox<String>(new DefaultComboBoxModel<String>());
		sensorType1.addItem("One");
		sensorType1.addItem("Two");
		
		messageSenders = new PhidgetsMessageSender[8];
		for (int x=0; x<8; x++)
		{
			messageSenders[x] = new PhidgetsMessageSender(x);
		}
		
		ikp = new InterfaceKitPhidget();
		ikp.open(117182);
		ikp.waitForAttachment();
		ikp.addAttachListener(this);
		ikp.addSensorChangeListener(this);
	}	
	
	public void attached(AttachEvent ae)
	{
		try 
		{
			System.out.println(ae.getSource().getSerialNumber());
		} 
		catch (PhidgetException pe) {}
	}
	
	public void sensorChanged(SensorChangeEvent sce) 
	{
		messageSenders[sce.getIndex()].send(sce);
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
		return null;
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
