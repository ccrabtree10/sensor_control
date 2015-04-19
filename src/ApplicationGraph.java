import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 * A specialised mxGraph. Overrides methods for getLabel and isCellMoveable. This ensures that
 * only GraphModules are moveable (not wires or ports). 
 * @author Christopher Crabtree
 *
 */
public class ApplicationGraph extends mxGraph {
	
	public ApplicationGraph() {
		super();
		this.cellsDisconnectable = false;
	}
	
	public String getLabel(Object cell) {
		String label = null;
		if (cell instanceof Port) {
			label = ((Port)cell).getLabel();
		} else if (cell instanceof GraphModule) {
			label = ((GraphModule)cell).getLabel();
		}
		return label;
	}
	
	public boolean isCellMovable(Object cell) {
		// Only let the user move graph modules. Wires and ports should not be
		// moveable.
		return (cell instanceof GraphModule);
	}
	
	
}
