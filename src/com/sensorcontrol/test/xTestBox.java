package com.sensorcontrol.test;
import javax.swing.JLabel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.logging.Level;

public class xTestBox implements Serializable {//, KryoSerializable { 
	String astring, bstring, cstring;
	JLabel alabel;
	public xTestBox() {
		log("cons");
		astring = "astring in box";
		bstring = "bstring in box";
		cstring = "cstring in box";
		//alabel = new JLabel("Box label");
	}
	
	
	
	public void log(String message) {
		System.out.println("box: " + message);
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
		kryo.writeObject(output, astring);
		//kryo.writeObject(output, bstring);
		//kryo.writeObject(output, cstring);
    }

     public void read (Kryo kryo, Input input) {
    	 log("Kryo reading...");
    	 
    }
     
    
	
}
