import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;


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
		return !((mxCell) cell).isEdge();
	}
	
	
}
