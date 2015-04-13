
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class xTestContainer implements Serializable {// , KryoSerializable { 
	String astring, bstring, cstring;
	JLabel alabel;
	JButton abutton;
	xTestBox abox;
	HashMap<String, String> ahashmap;
	StMcModule module;
	GraphModule gmod;
	mxCell cell;
	Port port;
	IMessageListenerSensor listener;
	ArrayList<Object> arrayList;
	DefaultComboBoxModel model;

	public xTestContainer() {
		log("cons");
		astring = "a string in continer";
		alabel = new JLabel("Container label");
		abutton = new JButton("touch me");
		abox = new xTestBox();
		ahashmap = new HashMap<String, String>();
		ahashmap.put("name", "chris");
		gmod = new GraphModule(new StMcModule());
		
		arrayList = new ArrayList<Object>();
		arrayList.add(port);
	}
	
	public void doSomething() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 320);
				frame.setVisible(true);
				frame.add(gmod.getControlPanel());
				//gmod.getControlPanel().validate();
				//gmod.getControlPanel().repaint();
				
			}
		});
		
		
	}
	
	public void log(String message) {
		System.out.println("container: " + message);
	}
	
	
	
	// Java Serialization.
	private void XwriteObject(java.io.ObjectOutputStream out) throws IOException {
		log("JavaS writing...");
	}
	
	private void XreadObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		log("JavaS reading...");
	}

	// Kryo Serialization.
	public void write (Kryo kryo, Output output) {
		log("Kryo writing...");
		//kryo.writeObject(output, astring);
		//kryo.writeObject(output, bstring);
		//kryo.writeObject(output, cstring);
    }

     public void read (Kryo kryo, Input input) {
    	 log("Kryo reading...");
    	 
    }
     
    
	
}
