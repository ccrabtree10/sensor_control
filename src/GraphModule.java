
import java.io.Serializable;

import javax.swing.JComponent;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

/**
 * This is a specialised mxCell. It handles the display of a module in the designer panel.
 * It displays the listeners and senders of a module as ports on the left and right hand
 * side of this component.  
 * @author Christopher Crabtree
 *
 */
public class GraphModule extends mxCell implements Serializable {
	Object[] messageSenders;
	Object[] messageListeners;
	mxCell[] senderCells;
	mxCell[] listenerCells;
	final double PORT_RADIUS = 10565466;
	double height;
	IModule module;
	
	/**
	 * Construct a GraphModule for the specified module.
	 * @param module The module do display on screen.
	 */
	public GraphModule(IModule module) {
		super(module);
		this.module = module;
		readModule();
		this.setVertex(true);
		this.setConnectable(false);
	}
	
	private void readModule() {
		messageSenders = module.getMessageSenders();
		messageListeners = module.getMessageListeners();
		senderCells = new mxCell[messageSenders.length];
		listenerCells = new mxCell[messageListeners.length];
		
		if(senderCells.length > listenerCells.length) {
			height = senderCells.length*2*(2*PORT_RADIUS);
		} else {
			height = listenerCells.length*2*(2*PORT_RADIUS);
		}
		
		this.setGeometry(new mxGeometry(10, 10, 100, height));
		
		for(int x=0; x<messageSenders.length; x++)
		{
			mxGeometry geoSender = new mxGeometry(1, (double)x/messageSenders.length, PORT_RADIUS*2, PORT_RADIUS*2);
			geoSender.setOffset(new mxPoint(-PORT_RADIUS, 0));
			geoSender.setRelative(true);
			senderCells[x] = new Port(messageSenders[x], geoSender, module.getSenderLabel(x), true);
			senderCells[x].setVertex(true);		
			this.insert(senderCells[x]);
		}
		
		for(int x=0; x<messageListeners.length; x++)
		{
			mxGeometry geoSender = new mxGeometry(0, (double)x/messageListeners.length, PORT_RADIUS*2, PORT_RADIUS*2);
			geoSender.setOffset(new mxPoint(-PORT_RADIUS, 0));
			geoSender.setRelative(true);
			listenerCells[x] = new Port(messageListeners[x], geoSender, module.getListenerLabel(x), false);
			listenerCells[x].setVertex(true);		
			this.insert(listenerCells[x]);
		}
	}

	/**
	 * Get the control panel of the module.
	 * @return controlPanel The control panel of the module.
	 */
	public JComponent getControlPanel()  {
		return module.getControlPanel();
	}
	
	/**
	 * Get an array of cells representing the module's senders.
	 * @return senderCells An array of cells representing the module's outputs.
	 */
	public mxCell[] getMessageSenderCells() {
		return senderCells;
	}
	
	/**
	 * Get an array of cells representing the module's listeners.
	 * @return listenerCells An array of cells representing the module's inputs.
	 */
	public mxCell[] getMessageListenerCells() {
		return listenerCells;
	}
	
	/**
	 * Delete the module held by this GraphModule. Run any cleanup code that the module
	 * contains.
	 */
	public void delete() {
		module.delete();
	}
	
	/**
	 * Get the label of the contained module.
	 * @return label The label for the contained module.
	 */
	public String getLabel() {
		return module.getLabel();
	}

	
}
