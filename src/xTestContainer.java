
import javax.swing.JButton;
import javax.swing.JLabel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;

public class xTestContainer implements Serializable {// , KryoSerializable { 
	String astring, bstring, cstring;
	JLabel alabel;
	JButton abutton;
	xTestBox abox;
	HashMap<String, String> ahashmap;

	public xTestContainer() {
		log("cons");
		astring = "a string in continer";
		alabel = new JLabel("Container label");
		abutton = new JButton("touch me");
		abox = new xTestBox();
		ahashmap = new HashMap<String, String>();
		ahashmap.put("name", "chris");
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
