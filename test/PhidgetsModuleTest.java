import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

import org.junit.Test;

import com.phidgets.PhidgetException;


public class PhidgetsModuleTest {

	@Test
	public void testGetMessageSenders() throws PhidgetException {
		PhidgetsModule module = new PhidgetsModule();
		Object[] senders = module.getMessageSenders();
		for (Object sender : senders) {
			assertEquals(true, sender instanceof IMessageSender);
		}
	}

	@Test
	public void testGetControlPanel() {
		StMcModule module = new StMcModule();
		JPanel controlPanel = (JPanel) module.getControlPanel();
		assertEquals(true, controlPanel.isVisible());
	}

}
