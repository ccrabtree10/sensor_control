
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;

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

import java.util.logging.Logger;

/**
 * A specialised JFrame which is used to display the application.
 * @author Christopher Crabtree
 *
 */
public class MainFrame extends JFrame {
	DesignerPanel designerPanel;
	JMenuBar menubar;
	JMenuItem insertStMcModule, insertStMnModule, insertMidiOutModule, insertPhidgetsModule, 
		insertMidiDemoModule, insertSensorMonitor, insertMidiMonitor, saveSession, 
		openSession, closeSession, testSession;
	JMenu insertMenu, inputFolder, converterFolder, outputFolder, fileMenu, monitorFolder;
	Kryo kryo;
	
	public MainFrame() {
		// Initialise log.
		SimpleLog.init();
		
		// Setup Kryo serialization.
		kryo = new Kryo();
		DefaultInstantiatorStrategy defStrategy = new DefaultInstantiatorStrategy();
		StdInstantiatorStrategy stdStrategy = new StdInstantiatorStrategy();
		defStrategy.setFallbackInstantiatorStrategy(stdStrategy);
		kryo.setInstantiatorStrategy(stdStrategy);
		kryo.getRegistration(ArrayList.class).setInstantiator(defStrategy.newInstantiatorOf(ArrayList.class));		
		
		// Setup GUI menus.
		designerPanel = new DesignerPanel();
		menubar = new JMenuBar();
		insertStMcModule = new JMenuItem("Sensor To Midi CC");
		insertStMnModule = new JMenuItem("Sensor To Midi Note");
		insertMidiOutModule = new JMenuItem("Midi Output");
		insertPhidgetsModule = new JMenuItem("Phidgets Input");
		insertMidiDemoModule = new JMenuItem("Midi Demo Module");
		insertSensorMonitor = new JMenuItem("Sensor Monitor");
		insertMidiMonitor = new JMenuItem("Midi Monitor");
		saveSession = new JMenuItem("Save Session...");
		openSession = new JMenuItem("Open Session...");
		closeSession = new JMenuItem("Close Session");
		testSession = new JMenuItem("Test");
		insertMenu = new JMenu("Insert");
		inputFolder = new JMenu("Input");
		outputFolder = new JMenu("Output");
		converterFolder = new JMenu("Converter");
		monitorFolder = new JMenu("Monitor");
		
		fileMenu = new JMenu("File");
		
		inputFolder.add(insertPhidgetsModule);
		outputFolder.add(insertMidiOutModule);
		outputFolder.add(insertMidiDemoModule);
		converterFolder.add(insertStMcModule);
		converterFolder.add(insertStMnModule);
		monitorFolder.add(insertSensorMonitor);
		monitorFolder.add(insertMidiMonitor);
		insertMenu.add(inputFolder);
		insertMenu.add(outputFolder);
		insertMenu.add(converterFolder);
		insertMenu.add(monitorFolder);
		fileMenu.add(openSession);
		fileMenu.add(saveSession);
		fileMenu.add(closeSession);
		//fileMenu.add(testSession);
		menubar.add(fileMenu);
		menubar.add(insertMenu);
		
		this.setLayout(new BorderLayout());
		this.setJMenuBar(menubar);
		
		this.add(designerPanel, BorderLayout.CENTER);
		this.add(designerPanel.getInspectorPanel(), BorderLayout.EAST);
		
		designerPanel.setDragEnabled(false);
				
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
		
		insertMidiDemoModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					designerPanel.addModule(new MidiDemoModule());
				} catch (MidiUnavailableException mue) {
					JOptionPane.showMessageDialog(MainFrame.this, 
						"Error occurred during module creation: " + mue.getMessage());
				}
			}
		});
		
		insertSensorMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				designerPanel.addModule(new SensorMonitorModule());
			}
		});
		
		insertMidiMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				designerPanel.addModule(new MidiMonitorModule());
			}
		});
		
		closeSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.closeSession(
						"Are you sure you want to close this session and start a new session?\nUnsaved data will be lost!");
			}
		});
		
		openSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// Check with the user that the definitely want to open a session.
				boolean userConfirm = MainFrame.this.closeSession(
						"Are you sure you want to close this session and open another?\nUnsaved data will be lost!");
				if (userConfirm == true) {
					JFileChooser chooser = new JFileChooser();
				    FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "Session files", "session");
				    chooser.setFileFilter(filter);
				    int returnVal = chooser.showOpenDialog(MainFrame.this);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				    	String filename = chooser.getSelectedFile().getAbsolutePath();
				    	String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
				    	if (extension.equals("session")) {
					    	try  {
								Input input = new Input(new FileInputStream(filename));
								SessionContainer container = kryo.readObject(input, SessionContainer.class);
								designerPanel.getGraph().addCells(container.getCells());
								designerPanel.validate();
								JOptionPane.showMessageDialog(MainFrame.this, "Session successfully loaded");
							} catch (FileNotFoundException fnfe) {
								JOptionPane.showMessageDialog(
									MainFrame.this, "File could not be found: " + filename);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(
								MainFrame.this, "An unknown error occurred: " + e.getMessage());
							}
				    	} else {
				    		JOptionPane.showMessageDialog(MainFrame.this, 
				    				"Cannot open this type of file: must be a .session file.");
				    	}
				    }
				}
			}
		});
		
		saveSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				JFileChooser chooser = new JFileChooser();
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Session files", "session");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showSaveDialog(MainFrame.this);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	String filename = chooser.getSelectedFile().getAbsolutePath() + ".session";
			    	try {
						Object[] cells = designerPanel.getGraph().getChildCells(designerPanel.getGraph().getDefaultParent());
						SessionContainer container = new SessionContainer();
						container.setCells(cells);					
						Output output = new Output(new FileOutputStream(filename));
						kryo.writeObject(output, container);
						output.flush();
						output.close();
						JOptionPane.showMessageDialog(MainFrame.this, "Session succesfully saved.");
			    	} catch (IOException ioe) {
			    		JOptionPane.showMessageDialog(MainFrame.this, 
								"A disk error occurred during saving the session: " + ioe.getMessage());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(MainFrame.this, 
								"An unknown error occurred during saving the session: " + e.getMessage());
					}
			    }
			}
		});
		
		testSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				designerPanel.setupTestSession();
			}
		});	
	}
	
	private boolean closeSession(String message) {
		int reply = JOptionPane.showConfirmDialog(MainFrame.this, 
				message,
				"Warning",
				JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			Object[] cells = designerPanel.getGraph().getChildCells(designerPanel.getGraph().getDefaultParent());
			for (Object cell : cells) {
				if (cell instanceof GraphModule) {
					((GraphModule) cell).delete();
				}
			}
			designerPanel.getGraph().removeCells(cells);
			designerPanel.validate();
			return true;
		} else {
			return false;
		}
	}
	
	public static void main(String[] args) throws PhidgetException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame frame = new MainFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 500);
				frame.setVisible(true);
			}
		});
	}

}
