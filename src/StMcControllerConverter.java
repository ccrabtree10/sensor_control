
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jidesoft.swing.RangeSlider;
import com.phidgets.event.SensorChangeEvent;




public class StMcControllerConverter implements StMcIConverter
{
	JComboBox channelSelector, controllerSelector;
	RangeSlider rangeControl, onOffSwitchControl;
	JSlider thresholdControl;
	private JLabel channelLabel, controllerLabel, rangeLabel,
		thresholdLabel, onOffSwitchLabel, modeLabel;
	int channel, controller, rangeMin, rangeMax, onOffSwitchMin, onOffSwitchMax, conToSwitchThreshold;
	private JComboBox<String> modeSelector;
	private JPanel controlPanel, rowChannel, rowController, rowRange, rowThreshold, rowMode, rowOnOffSwitch;
	// Could make all the methods into objects - then
	// wouldn't need to use reflection!!!
	private transient Method conversionMethod;
	private final float CONVERSION_FACTOR = 0.127f;
	
	public StMcControllerConverter()
	{
		Object[] tempChannelArray = new Object[16];
		Object[] tempControllerArray = new Object[128];
		for(int x=0; x<tempChannelArray.length; x++)
		{
			tempChannelArray[x] = x;
		}
		for(int x=0; x<tempControllerArray.length; x++)
		{
			tempControllerArray[x] = x;
		}
		modeLabel = new JLabel("Mode: ");
		channelLabel = new JLabel("Channel ");
		controllerLabel = new JLabel("Controller: ");
		rangeLabel = new JLabel("Range: ");
		thresholdLabel = new JLabel("Threshold: ");
		onOffSwitchLabel = new JLabel("On/Off Values:");
		channelSelector = new JComboBox(new DefaultComboBoxModel(tempChannelArray));
		controllerSelector = new JComboBox(new DefaultComboBoxModel(tempControllerArray));
		rangeControl = new RangeSlider(0, 127, 0, 127);
		onOffSwitchControl = new RangeSlider(0, 127, 0, 127);
		thresholdControl = new JSlider();
		modeSelector = new JComboBox<String>(new DefaultComboBoxModel<String>());
		modeSelector.addItem("Continuous");
		modeSelector.addItem("Switch");
		modeSelector.addItem("Con > Swi");
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		rowMode = new JPanel();
		rowChannel = new JPanel();
		rowController = new JPanel();
		rowRange = new JPanel();
		rowThreshold = new JPanel();
		rowOnOffSwitch = new JPanel();
		
		rowMode.add(modeLabel);
		rowMode.add(modeSelector);
		rowChannel.add(channelLabel);
		rowChannel.add(channelSelector);
		rowController.add(controllerLabel);
		rowController.add(controllerSelector);
		rowRange.add(rangeLabel);
		rowRange.add(rangeControl);
		rowThreshold.add(thresholdLabel);
		rowThreshold.add(thresholdControl);
		rowOnOffSwitch.add(onOffSwitchLabel);
		rowOnOffSwitch.add(onOffSwitchControl);
				
		controlPanel.add(rowMode);
		controlPanel.add(rowChannel);
		controlPanel.add(rowController);
		controlPanel.add(rowRange);
		
		channel = (int) channelSelector.getSelectedItem();
		controller = (int) controllerSelector.getSelectedItem();
		rangeMin = rangeControl.getLowValue();
		rangeMax = rangeControl.getHighValue();
		
		try {
			conversionMethod = StMcControllerConverter.class.getDeclaredMethod("convertContinuous", MessageSensor.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public JPanel getControlPanel()
	{
		return controlPanel;
	}
	
	public ShortMessage generateMessage(MessageSensor message) throws InvalidMidiDataException {
		ShortMessage midiMessage = null;
		try {
			midiMessage = (ShortMessage) conversionMethod.invoke(this, message);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return midiMessage;
	}
	
	public String toString()
	{
		return "Controller Change";
	}
	
	public void initialize()
	{
		modeSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				// Update view and set conversion method.
				controlPanel.remove(rowRange);
				controlPanel.remove(rowThreshold);
				controlPanel.remove(rowOnOffSwitch);
				try 
				{
					switch (modeSelector.getSelectedIndex())
					{
						case 0: controlPanel.add(rowRange);
								conversionMethod = StMcControllerConverter.class.getDeclaredMethod("convertContinuous", MessageSensor.class);
								break;
						case 1: controlPanel.add(rowOnOffSwitch);
								conversionMethod = StMcControllerConverter.class.getDeclaredMethod("convertSwitch", MessageSensor.class);
								break;
						case 2: controlPanel.add(rowThreshold);
								conversionMethod = StMcControllerConverter.class.getDeclaredMethod("convertConToSwitch", MessageSensor.class);
								break;
					}
				} 
				catch (NoSuchMethodException | SecurityException e) 
				{
					e.printStackTrace();
				}
				controlPanel.revalidate();
			}
		});
		
		channelSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				channel = (int) channelSelector.getSelectedItem();
				System.out.println("listener channel");
			}
		});
		
		controllerSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller = (int) controllerSelector.getSelectedItem();
				System.out.println("control change");
			}
		});

		rangeControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				rangeMin = rangeControl.getLowValue();
				rangeMax = rangeControl.getHighValue();
			}
		});
		
		onOffSwitchControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				onOffSwitchMin = onOffSwitchControl.getLowValue();
				onOffSwitchMax = onOffSwitchControl.getHighValue();
			}
		});
		
		thresholdControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				conToSwitchThreshold = thresholdControl.getValue();
				System.out.println(conToSwitchThreshold);
			}
		});
		
		channel = (int) channelSelector.getSelectedItem();
		controller = (int) controllerSelector.getSelectedItem();
		rangeMin = rangeControl.getLowValue();
		rangeMax = rangeControl.getHighValue();
		onOffSwitchMin = onOffSwitchControl.getLowValue();
		onOffSwitchMax = onOffSwitchControl.getHighValue();
		conToSwitchThreshold = thresholdControl.getValue();
	}
	
	private ShortMessage convertContinuous(MessageSensor message) throws InvalidMidiDataException
	{
		int midiValue = Math.round(message.getValue()*CONVERSION_FACTOR);
		float ratio = (rangeMax - rangeMin)/127;
		midiValue = Math.round((midiValue * ratio) + rangeMin);
		return new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controller, midiValue);
	}
	
	private ShortMessage convertSwitch(MessageSensor message) throws InvalidMidiDataException
	{
		System.out.println("switch convert");
		int midiValue;
		if (message.getValue() < 100)
		{
			midiValue = onOffSwitchMin;
		}
		else
		{
			midiValue = onOffSwitchMax;
		}
		return new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controller, midiValue);
	}
	
	private ShortMessage convertConToSwitch(MessageSensor message) throws InvalidMidiDataException
	{
		System.out.println("conToSwitch convert");
		int midiValue;
		if(message.getValue() < conToSwitchThreshold*10)
		{
			midiValue = 0;
		}
		else
		{
			midiValue = 127;
		}
		return new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controller, midiValue);
	}
	
}
