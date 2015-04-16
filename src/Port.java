import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;


public class Port extends mxCell {
	private String label;
	private boolean isOutput;
	
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
