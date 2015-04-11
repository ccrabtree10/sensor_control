import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;


public class Port extends mxCell {
	private String label;	
	
	public Port(Object value, mxGeometry geo, String label) {
		super(value, geo, "shape=ellipse;perimter=ellipsePerimeter");
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
}
