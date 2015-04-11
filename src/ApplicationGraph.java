import com.mxgraph.view.mxGraph;


public class ApplicationGraph extends mxGraph {
	public String getLabel(Object cell) {
		String label = null;
		if (cell instanceof Port) {
			label = ((Port)cell).getLabel();
		} else if (cell instanceof GraphModule) {
			label = ((GraphModule)cell).getLabel();
		}
		return label;
	}
}
