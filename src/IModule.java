import java.util.ArrayList;

import javax.swing.JComponent;


public interface IModule 
{
	public Object[] getMessageSenders();
	public Object[] getMessageListeners();
	public JComponent getControlPanel();
	public void initialize();
	public void delete();
	public void setModuleChangeListener(IModuleChangeListener listener);
}