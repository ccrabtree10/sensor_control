import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;



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

	public void setModuleChangeListener(IModuleChangeListener listener) {
		
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
