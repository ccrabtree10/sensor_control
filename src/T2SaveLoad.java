import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.minlog.Log;
import com.jidesoft.swing.RangeSlider;
import com.sensorcontrol.main.ControlPanel;


public class T2SaveLoad 
{

	public static void main(String[] args) throws FileNotFoundException 
	{
		Scanner scanner = new Scanner(System.in);
		System.out.print("Choice: ");
		int choice = scanner.nextInt();
		if(choice==1)
		{
			save();
		}
		if(choice==2)
		{
			load();
		}
		if(choice==3)
		{
			saveLoad();
		}
	}
	
	public static void save() throws FileNotFoundException
	{
		System.out.println("Saving...");
		Output output = new Output(new FileOutputStream("/tempTest/newTest.kr"));
		Kryo kryo = new Kryo();
		
		ArrayList objectToWrite = new ArrayList();
		objectToWrite.add("Chris");
		
		kryo.writeObject(output, objectToWrite);
		output.flush();
		output.close();
		System.out.println("Done.");
	}
	
	public static void load() throws FileNotFoundException
	{
		System.out.println("Loading...");
		Input input = new Input(new FileInputStream("/tempTest/newTest.kr"));
		Kryo kryo = new Kryo();
				
		ArrayList array = kryo.readObject(input, ArrayList.class);
		System.out.println(array.get(0));
		
		System.out.println("Done.");
	}
	
	public static void saveLoad() throws FileNotFoundException
	{
		/// !!! SET UP SAVE !!!
		System.out.println("Doing save/load...");
		System.out.println("Saving...");
		Output output = new Output(new FileOutputStream("/tempTest/newTest.kr"));
		Kryo kryo = new Kryo();
		Log.set(Log.LEVEL_TRACE);
		
		// !!! REGISTER KRYO ETC. !!!
		//kryo.register(ArrayList.class, new ModuleSerializer(kryo, ArrayList.class));
		kryo.register(JComponent.class, new JavaSerializer());
		kryo.register(JLabel.class, new JavaSerializer(), 1);
		kryo.register(JComponent.class, new JavaSerializer(), 3);
		kryo.register(RangeSlider.class, new JavaSerializer(), 4);
		kryo.register(ControlPanel.class, new JavaSerializer(), 5);
		kryo.register(Color.class, new JavaSerializer(), 6);
		kryo.register(JPanel.class, new JavaSerializer(), 7);
		
		
		// !!! MAKE OBJECT TO SAVE !!!
		//MidiOutModule objectToWrite = new MidiOutModule();
		GraphModule objectToWrite = new GraphModule(new MoModule("test"));
		//ArrayList objectToWrite = new ArrayList();
		
		// !!! SAVE OBJECT !!!
		kryo.writeObject(output, objectToWrite);
		output.flush();
		output.close();
		System.out.println("Done Save.");
		
		
		
		
		// !!! SET UP LOAD !!!
		System.out.println("Loading...");
		Input input = new Input(new FileInputStream("/tempTest/newTest.kr"));
		
		// !!! SET UP INSTANTIATOR !!!				
		((DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
		//kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		
		// !!! LOAD OBJECT !!!
		//JLabel label = kryo.readObject(input, JLabel.class);
		//MidiOutModule cbox = kryo.readObject(input, MidiOutModule.class);
		GraphModule cbox = kryo.readObject(input, GraphModule.class);
		
		// !!! FINISH !!!
		System.out.println("Done Load.");
		
		
	}

}
