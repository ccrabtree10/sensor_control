
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

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

import java.util.logging.Logger;


public class MainFrame extends JFrame
{
	DesignerPanel designerPanel;
	
	JMenuBar menubar;
	JMenuItem insertStMcModule, insertStMnModule, insertMidiOutModule, insertPhidgetsModule, 
				saveSession, openSession, testSession, insertMiModule;
	JMenu insertMenu, inputFolder, moduleFolder, outputFolder, fileMenu;
	Kryo kryo;
	
	public MainFrame() 
	{	
		// !!! debug.
		su.init();
		Log.set(Log.LEVEL_DEBUG);
		
		kryo = new Kryo();
		kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		DefaultInstantiatorStrategy strategy = new DefaultInstantiatorStrategy();
		StdInstantiatorStrategy stdStrategy = new StdInstantiatorStrategy();
		strategy.setFallbackInstantiatorStrategy(stdStrategy);
		kryo.getRegistration(ArrayList.class).setInstantiator(strategy.newInstantiatorOf(ArrayList.class));
		kryo.getRegistration(HashMap.class).setInstantiator(strategy.newInstantiatorOf(HashMap.class));
		kryo.getRegistration(Vector.class).setInstantiator(strategy.newInstantiatorOf(Vector.class));
		kryo.getRegistration(Hashtable.class).setInstantiator(strategy.newInstantiatorOf(Hashtable.class));
		
		designerPanel = new DesignerPanel();
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
		
		this.add(designerPanel, BorderLayout.CENTER);
		this.add(designerPanel.getInspectorPanel(), BorderLayout.EAST);
		
		designerPanel.setDragEnabled(false);
		
		// !!! debug - auto setup test session.
		//designerPanel.setupTestSession();
		
		insertPhidgetsModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					designerPanel.addModule(new PhidgetsModule());
				} catch (PhidgetException e) {
					e.printStackTrace();
				}
			}
		});
		
		insertStMcModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				designerPanel.addModule(new StMcModule());
			}
		});
		
		insertStMnModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				designerPanel.addModule(new StMnModule());
			}
		});
		
		insertMidiOutModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Enter output port name: ");
				designerPanel.addModule(new MoModule(name));
			}
		});
		
		insertMiModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Enter input port name: ");
				designerPanel.addModule(new MiModule(name));
			}
		});
		
		openSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try  {
					Input input = new Input(new FileInputStream("C:\\save_load\\" + JOptionPane.showInputDialog("Enter Session name: ") + ".kry"));
					SessionContainer container = kryo.readObject(input, SessionContainer.class);
					designerPanel.getGraph().addCells(container.getCells());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		});
		
		saveSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				try {
					Object[] cells = designerPanel.getGraph().getChildCells(designerPanel.getGraph().getDefaultParent());
					SessionContainer container = new SessionContainer();
					container.setCells(cells);					
					Output output = new Output(new FileOutputStream("C:\\save_load\\" + JOptionPane.showInputDialog("Enter Session name: ") + ".kry"));
					kryo.writeObject(output, container);
					output.flush();
					output.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		testSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				designerPanel.setupTestSession();
			}
		});		
	}
	
	public static void main(String[] args) throws PhidgetException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame frame = new MainFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 320);
				frame.setVisible(true);
				
			}
		});
	}

}
