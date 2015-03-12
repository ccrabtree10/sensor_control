import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.objenesis.strategy.SerializingInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.minlog.Log;

public class xTestKryo {
	
	//String kryoPath = "/Users/Chris/test/kryo.txt";
	//String javaSPath = "/Users/Chris/test/javaS.txt";
	// added this from windows. also added this.
	String kryoPath = "C:\\tempTest\\kryo.txt";
	String javaSPath = "C:\\tempTest\\javaS.txt";
	Kryo kryo;
	public static Logger JLOG;
	
	public static void main(String args[]) {
		xTestKryo x = new xTestKryo();
		xTestContainer obToSave = new xTestContainer();
		x.log("saving with kryo");
		x.saveWithKryo(obToSave);
		//x.log("saving with javaS");
		//x.saveWithJavaS(obToSave);
		
		
		
		x.log("loading with kryo");
		x.loadWithKryo();
		//x.log("loading with javaS");
		//x.loadWithJavaS();
		
		
		/*Set<Thread> threads = Thread.getAllStackTraces().keySet();
		Iterator it = threads.iterator();
		while (it.hasNext()) {
			Thread thread = (Thread) it.next();
			JLOG.log(Level.FINEST, "trace", thread.getStackTrace());
		}*/
		
	}
	
	class MyFormatter extends SimpleFormatter {
		public String format(LogRecord rec) {
			String s = rec.getSourceClassName() + " - " + rec.getMessage() + ":\n";
			Object[] params = rec.getParameters();
			if (params != null) {
				for (int x=0; x<params.length; x++) {
					s += params[x].toString() + "\n"; 
				}
			}
			return s;
		}
	}
	
	public xTestKryo() {
		//Log.set(Log.LEVEL_DEBUG);
		//Log.set(Log.LEVEL_TRACE);
		kryo = new Kryo(); 
		// Set general strategy.
		kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		//kryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
		//DefaultInstantiatorStrategy strategy = new DefaultInstantiatorStrategy();
		//strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
		//kryo.setInstantiatorStrategy(strategy);
		
		// Set individual strategy for classes - java strategy - no constructor.
		//SerializingInstantiatorStrategy strategy = new SerializingInstantiatorStrategy();
		//StdInstantiatorStrategy strategy = new StdInstantiatorStrategy();
		//DefaultInstantiatorStrategy strategy = new DefaultInstantiatorStrategy();
		//strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
		
		//kryo.getRegistration(HashMap.class).setInstantiator(strategy.newInstantiatorOf(HashMap.class));
		//kryo.getRegistration(PropertyChangeSupport.class).setInstantiator(strategy.newInstantiatorOf(PropertyChangeSupport.class));
		//kryo.getRegistration(xTestContainer.class).setInstantiator(strategy.newInstantiatorOf(xTestContainer.class));
		
		// Set individual serializers.
		//kryo.getRegistration(JLabel.class).setSerializer(new JavaSerializer());
		//kryo.getRegistration(HashMap.class).setSerializer(new JavaSerializer());
		//kryo.getRegistration(xTestContainer.class).setSerializer(new JavaSerializer());
		
		
		
		// Logging
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.FINEST);
		handler.setFormatter(new MyFormatter());
		JLOG = Logger.getLogger("general");
		JLOG.addHandler(handler);
		JLOG.setLevel(Level.FINEST);
	}
	
	// Java Serialization.
	public void saveWithJavaS(Object obToSave) {
		//xTestContainer obj = new xTestContainer();
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(javaSPath));
			output.writeObject(obToSave);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (output != null) {
				try {
					output.flush();
					output.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public void loadWithJavaS() {
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream(javaSPath));
			xTestContainer con = (xTestContainer) input.readObject();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	// Kryo Serialization.
	public void saveWithKryo(Object obToSave) {		
		Output output = null;
		try {
			output = new Output(new FileOutputStream(kryoPath));
			kryo.writeObject(output, obToSave);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {
				PrintWriter writer = new PrintWriter(new FileOutputStream("C:\\tempTest\\kryo_save_log.txt"));
				e.printStackTrace(writer);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (output != null) {
				output.flush();
				output.close();
			}
		}
	}
	
	public void loadWithKryo() {
		Input input = null;
		try {
			input = new Input(new FileInputStream(kryoPath));
			xTestContainer con = (xTestContainer) kryo.readObject(input, xTestContainer.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {
				PrintWriter writer = new PrintWriter(new FileOutputStream("C:\\tempTest\\kryo_load_log.txt"));
				e.printStackTrace(writer);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	public void log(String message) {
		System.out.println("xTestKryo: " + message);
	}
	
}
