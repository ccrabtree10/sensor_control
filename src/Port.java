import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

/**
 * A specialised mxCell. Represents an input/output port of a module.
 * @author Christopher Crabtree
 *
 */
public class Port extends mxCell {
	private String label;
	private boolean isOutput;
	
	/**
	 * Create an input/output port with the values passed in.
	 * @param value The user object attached to this port. This will be the listener for an input port 
	 * and a sender for an output port.
	 * @param geo Controls the size and position of this port.
	 * @param label The label for this port.
	 * @param isOutput Boolean flag indicating if this port is an output or input.
	 */
	public Port(Object value, mxGeometry geo, String label, boolean isOutput) {
		super(value, geo, "shape=ellipse;perimter=ellipsePerimeter");
		this.label = label;
		this.isOutput = isOutput;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public boolean isOutput() {
		return this.isOutput;
	}
}
