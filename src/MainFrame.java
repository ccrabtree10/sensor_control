import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.minlog.Log;
import com.jidesoft.swing.RangeSlider;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.phidgets.PhidgetException;
import com.thoughtworks.xstream.XStream;

public class MainFrame extends JFrame
{
	MainGraphComponent graphComponent;
	
	JMenuBar menubar;
	JMenuItem insertStMcModule, insertStMnModule, insertMidiOutModule, insertPhidgetsModule, 
				saveSession, openSession, testSession, insertMiModule;
	JMenu insertMenu, inputFolder, moduleFolder, outputFolder, fileMenu;
	Kryo kryo;
	
	public MainFrame() 
	{
		Log.set(Log.LEVEL_DEBUG);
		kryo = new Kryo();
		kryo.register(GraphModule.class, new ModuleSerializer(kryo, GraphModule.class));
		kryo.register(JComboBox.class, new JavaSerializer());
		kryo.register(ControlPanel.class, new JavaSerializer());
		kryo.register(JLabel.class, new JavaSerializer());
		kryo.register(RangeSlider.class, new JavaSerializer());
		
		((DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
		//kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		
		graphComponent = new MainGraphComponent();
		menubar = new JMenuBar();
		insertStMcModule = new JMenuItem("Sensor To Midi CC");
		insertStMnModule = new JMenuItem("Sensor To Midi Note");
		insertMidiOutModule = new JMenuItem("Midi Output");
		insertPhidgetsModule = new JMenuItem("Phidgets Input");
		insertMiModule = new JMenuItem("Midi Input");
		saveSession = new JMenuItem("Save");
		openSession = new JMenuItem("Open");
		testSession = new JMenuItem("Test");
		insertMenu = new JMenu("Insert");
		inputFolder = new JMenu("Input");
		outputFolder = new JMenu("Output");
		moduleFolder = new JMenu("Module");
		fileMenu = new JMenu("File");
		
		inputFolder.add(insertPhidgetsModule);
		inputFolder.add(insertMiModule);
		outputFolder.add(insertMidiOutModule);
		moduleFolder.add(insertStMcModule);
		moduleFolder.add(insertStMnModule);
		insertMenu.add(inputFolder);
		insertMenu.add(outputFolder);
		insertMenu.add(moduleFolder);
		fileMenu.add(openSession);
		fileMenu.add(saveSession);
		fileMenu.add(testSession);
		menubar.add(fileMenu);
		menubar.add(insertMenu);
		
		this.setLayout(new BorderLayout());
		this.setJMenuBar(menubar);
		
		this.add(graphComponent, BorderLayout.CENTER);
		this.add(graphComponent.getInspectorPanel(), BorderLayout.EAST);
		
		graphComponent.setDragEnabled(false);
		
		insertPhidgetsModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					graphComponent.addModule(new PhidgetsModule());
				} catch (PhidgetException e) {
					e.printStackTrace();
				}
			}
		});
		
		insertStMcModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphComponent.addModule(new StMcModule());
			}
		});
		
		insertStMnModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphComponent.addModule(new StMnModule());
			}
		});
		
		insertMidiOutModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Enter output port name: ");
				graphComponent.addModule(new MoModule(name));
			}
		});
		
		insertMiModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Enter input port name: ");
				graphComponent.addModule(new MiModule(name));
			}
		});
		
		openSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				try 
				{
					Input input = new Input(new FileInputStream("/Users/Chris/" + JOptionPane.showInputDialog("Enter Session name: ") + ".kry"));
					SessionContainer container = kryo.readObject(input, SessionContainer.class);
					graphComponent.getGraph().addCells(container.getCells());
				} 
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				} 
			}
		});
		
		saveSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				try
				{
					Object[] cells = graphComponent.getGraph().getChildCells(graphComponent.getGraph().getDefaultParent());
					SessionContainer container = new SessionContainer();
					container.setCells(cells);
					System.out.println(Arrays.toString(cells));
					
					Output output = new Output(new FileOutputStream("/Users/Chris/" + JOptionPane.showInputDialog("Enter Session name: ") + ".kry"));
					kryo.writeObject(output, container);
					output.flush();
					output.close();
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		});
		
		testSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				graphComponent.setupTestSession();
			}
		});
		
		
	}
	
	public static void main(String[] args) throws PhidgetException
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				MainFrame frame = new MainFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 320);
				frame.setVisible(true);
			}
		});
		
	}

}
