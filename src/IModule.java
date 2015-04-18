
import java.util.ArrayList;

import javax.swing.JComponent;


public interface IModule {
	public Object[] getMessageSenders();
	public Object[] getMessageListeners();
	public JComponent getControlPanel();
	public void delete();
	public String getListenerLabel(int index);
	public String getSenderLabel(int index);
	public String getLabel();
}
