import java.io.Serializable;


/**
 * Container for the contents of the designer panel. Used when serialising a session. Although 
 * not implemented at the moment, this could be used to store additional data that isn't available 
 * from the contents of the designer panel alone, for example, the date and time at which the 
 * session was stored, or the version of the application with which the session was saved. The latter 
 * case could be used to ensure saved sessions are compatible between different versions of the 
 * application.
 * @author Christopher Crabtree
 *
 */
public class SessionContainer {
	private Object[] cells;
	
	public void setCells(Object[] cells) {
		this.cells = cells;
	}
	
	public Object[] getCells() {
		return cells;
	}
}
