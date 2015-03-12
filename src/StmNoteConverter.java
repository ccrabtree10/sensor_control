import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class StmNoteConverter implements StMcIConverter
{
	JComponent[][] controls;
	private JPanel controlPanel;
	
	public StmNoteConverter()
	{
		controls = new JComponent[3][];
		controls[0] = new JComponent[2];
		controls[1] = new JComponent[2];
		controls[2] = new JComponent[2];
		controls[0][0] = new JLabel("Channel");
		controls[0][1] = new JComboBox();
		controls[1][0] = new JLabel("Note");
		controls[1][1] = new JComboBox();
		controls[2][0] = new JLabel("Velocity");
		controls[2][1] = new JComboBox();
		
		controlPanel = new JPanel();
		controlPanel.add(new JLabel("test"));
	}
	
	public JPanel getControlPanel()
	{
		return controlPanel;
	}
	
	public String toString()
	{
		return "Note On";
	}

	@Override
	public ShortMessage generateMessage(MessageSensor message) throws InvalidMidiDataException {
		return null;
	}
	
	public void initialize()
	{
		
	}
}
