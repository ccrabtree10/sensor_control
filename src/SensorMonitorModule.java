import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


/**
 * Module for monitoring sensor messages. The module has one input which accepts sensor 
 * messages (of type MessageSensor). The details of the sensor message are shown on the 
 * modules control panel.
 * @author Christopher Crabtree
 */
public class SensorMonitorModule implements IModule, IMessageListenerSensor, KryoSerializable {

	private transient JPanel controlPanel;
	private transient JLabel value;
	
	public SensorMonitorModule() {
		init();
	}
	
	private void init() {
		// Instantiate GUI components.
		JLabel valueLabel = new JLabel("Sensor Value: ");
		value = new JLabel();
		controlPanel = new JPanel();
		controlPanel.add(valueLabel);
		controlPanel.add(value);
	}
	
	/**
	 * Receive this sensor message and show its contents on the module's control panel.
	 */
	public void receive(MessageSensor message) {
		value.setText(Integer.toString(message.getValue()));
	}

	public Object[] getMessageSenders() {
		return new Object[0];
	}

	public Object[] getMessageListeners() {
		return new Object[]{this};
	}

	public JComponent getControlPanel() {
		return controlPanel;
	}

	public void delete() {
		// Nothing to clear up.
	}

	public String getListenerLabel(int index) {
		return "In";
	}

	public String getSenderLabel(int index) {
		return null;
	}

	public String getLabel() {
		return "Sensor Monitor";
	}

	public void read(Kryo arg0, Input arg1) {
		// Initialise GUI etc.
		init();
	}

	public void write(Kryo arg0, Output arg1) {
		// Nothing to store.
	}

}
