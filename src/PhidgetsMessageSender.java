
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.jidesoft.swing.RangeSlider;
import com.phidgets.event.SensorChangeEvent;


public class PhidgetsMessageSender implements IMessageSender, Serializable
{
	private int conToSwitchThreshold, switchToConOff, switchToConOn;
	private ArrayList<IMessageListenerSensor> listeners;
	private String index;
	private JLabel sensorLabel, sensorTypeLabel, outputModeLabel;
	private JComboBox<String> sensorType, outputMode;
	private JSlider thresholdControl;
	private RangeSlider onOffControl;
	private JComponent[][] controls;
	private ControlPanel controlPanel;
	
	public static final int CON_TO_SWITCH_THRESH_DEF = 500, SWITCH_TO_CON_OFF_DEF = 200, SWITCH_TO_CON_ON_DEF = 800;
	
	public PhidgetsMessageSender(int index)
	{
		listeners = new ArrayList<IMessageListenerSensor>();
		this.index = String.valueOf(index);
	}

	public void addMessageListener(Object listener) throws ClassCastException
	{
		IMessageListenerSensor newListener = (IMessageListenerSensor) listener;
		listeners.add(newListener);
	}
	
	public void send(SensorChangeEvent sce)
	{
			MessageSensor message = new MessageSensor(sce.getValue()); 
			Iterator iterator = listeners.iterator();
			while(iterator.hasNext())
			{
				IMessageListenerSensor listener = (IMessageListenerSensor) iterator.next();
				//System.out.println("PhiMod send: "+listener);
				listener.receive(message);
			}
	}
	
	public String toString()
	{
		return index;
	}

	public void removeMessageListener(Object listener) {
		listeners.remove(listener);
	}
}
