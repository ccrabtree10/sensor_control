
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.phidgets.PhidgetException;


public class DesignerPanel extends mxGraphComponent implements Serializable
{
	mxGraph graph;
	InspectorPanel inspectorPanel;
	
	public DesignerPanel() {
		super(new ApplicationGraph());		
		inspectorPanel = new InspectorPanel();
		inspectorPanel.setPreferredSize(new Dimension(250, 0));
		graph = this.getGraph();	
		
		// Make a connection between modules.
		this.getConnectionHandler().addListener(mxEvent.CONNECT, new mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				mxCell wire = (mxCell) evt.getProperty("cell");
				wire.setConnectable(false);
				Port port = (Port) wire.getSource();
				if (port.isOutput()) {
					try {
						IMessageSender messageSender = (IMessageSender) port.getValue();
						Object messageListener = wire.getTarget().getValue();
						messageSender.addMessageListener(messageListener);
					} catch (ClassCastException cce) {
						JOptionPane.showMessageDialog(null, "Ports Incompatible.");
						graph.removeCells(new Object[]{wire});
					} catch (NullPointerException npe) {
						graph.removeCells(new Object[]{wire});
					}
				} else {
					JOptionPane.showMessageDialog(null, 
						"Cannot connect to an output port.\nCan only connect from an output port to an input port.");
					graph.removeCells(new Object[]{wire});
				}
			}
		});
		
		// Delete a module or a wire.
		this.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode() == KeyEvent.VK_DELETE) {
					Object cell = graph.getSelectionCell();
					// If the cell is a module.
					if (cell instanceof GraphModule) {
						GraphModule graphModule = (GraphModule) cell;
						mxCell[] listenerCells = graphModule.getMessageListenerCells();
						// For each listener cell.
						for(int listener=0; listener<listenerCells.length; listener++) {
							int edgeCount = listenerCells[listener].getEdgeCount();
							// For each edge connected to cell.
							for(int edge=0; edge<edgeCount; edge++) {
								mxCell edgeCell = (mxCell) listenerCells[listener].getEdgeAt(edge);
								// Get sender connected to edge.
								IMessageSender sourceSender = (IMessageSender) edgeCell.getSource().getValue();
								// Remove listener from senders listener list.
								sourceSender.removeMessageListener(listenerCells[listener].getValue());
							}
						}
						// Call delete do module can do any clean-up code it may have.
						graphModule.delete();
					// If the cell is a wire.
					} else if (cell instanceof mxCell) {
						mxCell mCell = (mxCell) cell;
						IMessageSender sender = (IMessageSender) mCell.getSource().getValue();
						sender.removeMessageListener(mCell.getTarget().getValue());
					}
					// Remove the selected cells from the graph.
					graph.removeCells();
					DesignerPanel.this.validate();
					DesignerPanel.this.repaint();
				}
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
		});
		
		// Show control panel when module selected.
		graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				inspectorPanel.removeAll();
				try {
					GraphModule graphModule = (GraphModule) graph.getSelectionCell();
					inspectorPanel.viewControlPanel(graphModule.getControlPanel());
				} catch (ClassCastException cce) {
					
				} catch (NullPointerException npe) {
					
				} finally {
					inspectorPanel.validate();
					inspectorPanel.repaint();
				}
			}
		});
	}
	
	public InspectorPanel getInspectorPanel() {
		return inspectorPanel;
	}
	
	public void addModule(IModule module) {
		graph.getModel().beginUpdate();
		try {
			graph.addCell(new GraphModule(module));
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "An unknown error occurred: " + e.getMessage());
		} finally {
			graph.getModel().endUpdate();
		}
	}
	
	public void setupTestSession() {
		graph.getModel().beginUpdate();
		
		try {
			GraphModule phiGraphMod = new GraphModule(new PhidgetsModule());
			GraphModule stmGraphMod = new GraphModule(new StMnModule());
			GraphModule midiDemoGraphMod = new GraphModule(new MidiDemoModule());
			
			phiGraphMod.setGeometry(new mxGeometry(10, 10, 100, 150));
			stmGraphMod.setGeometry(new mxGeometry(175, 10, 100, 50));
			midiDemoGraphMod.setGeometry(new mxGeometry(350, 10, 100, 150));

			IMessageSender phiSender1 = (IMessageSender) phiGraphMod.getMessageSenderCells()[0].getValue();
			IMessageSender phiSender2 = (IMessageSender) phiGraphMod.getMessageSenderCells()[1].getValue();
			IMessageSender phiSender3 = (IMessageSender) phiGraphMod.getMessageSenderCells()[2].getValue();
			IMessageSender stmSender1 = (IMessageSender) stmGraphMod.getMessageSenderCells()[0].getValue();
			
			Object stmListener1 = stmGraphMod.getMessageListenerCells()[0].getValue();
			Object stmListener2 = stmGraphMod.getMessageListenerCells()[1].getValue();
			Object stmListener3 = stmGraphMod.getMessageListenerCells()[2].getValue();
			Object midiDemoListener = midiDemoGraphMod.getMessageListenerCells()[0].getValue();
			
			phiSender1.addMessageListener(stmListener1);
			phiSender2.addMessageListener(stmListener2);
			phiSender3.addMessageListener(stmListener3);

			stmSender1.addMessageListener(midiDemoListener);
			
			graph.addCells(new Object[]{phiGraphMod, stmGraphMod, midiDemoGraphMod});
			graph.insertEdge(null, null, null, phiGraphMod.getMessageSenderCells()[0], stmGraphMod.getMessageListenerCells()[0]);
			graph.insertEdge(null, null, null, phiGraphMod.getMessageSenderCells()[1], stmGraphMod.getMessageListenerCells()[1]);
			graph.insertEdge(null, null, null, phiGraphMod.getMessageSenderCells()[2], stmGraphMod.getMessageListenerCells()[2]);
			graph.insertEdge(null, null, null, stmGraphMod.getMessageSenderCells()[0], midiDemoGraphMod.getMessageListenerCells()[0]);
		} 
		catch (PhidgetException pe) {
			pe.printStackTrace();
		} catch (MidiUnavailableException mue) {
			mue.printStackTrace();
		}
		finally {
			graph.getModel().endUpdate();
		}
	}
	
}
