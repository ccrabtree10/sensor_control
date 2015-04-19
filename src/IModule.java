
import java.util.ArrayList;

import javax.swing.JComponent;

/**
 * Interface which a class must implement in order to be used as a module.
 * @author Christopher Crabtree
 *
 */
public interface IModule {
	/**
	 * Get an array of cells representing the output ports of this module.
	 * @return The array of output ports.
	 */
	public Object[] getMessageSenders();
	/**
	 * Get an array of cells representing the input ports of this module.
	 * @return The array of input ports.
	 */
	public Object[] getMessageListeners();
	/**
	 * Get the control panel of this module which contains any controls offered by the module.
	 * @return The control panel.
	 */
	public JComponent getControlPanel();
	/**
	 * Run any cleanup code that the module has.
	 */
	public void delete();
	/**
	 * Get the label for the input port at the given index.
	 * @param index The index of the input port to get the label for.
	 * @return The label of the input port.
	 */
	public String getListenerLabel(int index);
	/**
	 * Get the label for the output port at the given index.
	 * @param index The index of the output port to get the label for.
	 * @return The label of the output port.
	 */
	public String getSenderLabel(int index);
	/**
	 * Get the label for this module.
	 * @return The module label.
	 */
	public String getLabel();
}
