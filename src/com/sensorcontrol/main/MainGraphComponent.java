package com.sensorcontrol.main;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

import javax.swing.JFrame;

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
import com.sensorcontrol.modules.GraphModule;
import com.sensorcontrol.modules.IMessageSender;
import com.sensorcontrol.modules.IModule;
import com.sensorcontrol.modules.midiout.MoModule;
import com.sensorcontrol.modules.midinote.StMnModule;
import com.sensorcontrol.modules.phidgets.PhidgetsModule;


public class MainGraphComponent extends mxGraphComponent implements Serializable
{
	mxGraph graph;
	InspectorPanel inspectorPanel;
	
	public MainGraphComponent() {
		super(new mxGraph());
		//this.setDragEnabled(false);
		graph = this.getGraph();
		inspectorPanel = new InspectorPanel();
		inspectorPanel.setPreferredSize(new Dimension(250, 0));
		
		mxOrganicLayout layout = new mxOrganicLayout(graph);
		Object parent = graph.getDefaultParent();

		this.getConnectionHandler().addListener(mxEvent.CONNECT, new mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				mxCell cell = (mxCell) evt.getProperty("cell");
				try
				{
					IMessageSender messageSender = (IMessageSender) cell.getSource().getValue();
					Object messageListener = cell.getTarget().getValue();
					messageSender.addMessageListener(messageListener);
				}
				catch (ClassCastException cce)
				{
					System.out.println("PORTS INCOMPATIBLE");
					graph.removeCells(new Object[]{cell});
				}
			}
		});
		
		this.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode() == KeyEvent.VK_DELETE)
				{
					GraphModule graphModule = (GraphModule) graph.getSelectionCell();
					mxCell[] listenerCells = graphModule.getMessageListenerCells();
					for(int listener=0; listener<listenerCells.length; listener++)
					{
						int edgeCount = listenerCells[listener].getEdgeCount();
						for(int edge=0; edge<edgeCount; edge++)
						{
							mxCell edgeCell = (mxCell) listenerCells[listener].getEdgeAt(edge);
							IMessageSender sourceSender = (IMessageSender) edgeCell.getSource().getValue();
							sourceSender.removeMessageListener(listenerCells[listener].getValue());
						}
					}
					graphModule.delete();
					graph.removeCells();
					MainGraphComponent.this.validate();
					MainGraphComponent.this.repaint();
				}
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
		});
		
		graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				inspectorPanel.removeAll();
				try
				{
					GraphModule graphModule = (GraphModule) graph.getSelectionCell();
					inspectorPanel.viewControlPanel(graphModule.getControlPanel());
				}
				catch (ClassCastException cce) {}
				catch (NullPointerException npe) {}
				finally
				{
					inspectorPanel.validate();
					inspectorPanel.repaint();
				}
			}
		});
	}
	
	public InspectorPanel getInspectorPanel()
	{
		return inspectorPanel;
	}
	
	public void addModule(IModule module)
	{
		graph.getModel().beginUpdate();
		
		try
		{
			graph.addCell(new GraphModule(module));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
	public void setupTestSession()
	{
		graph.getModel().beginUpdate();
		
		try 
		{
			GraphModule phiGraphMod = new GraphModule(new PhidgetsModule());
			GraphModule stmGraphMod = new GraphModule(new StMnModule());
			GraphModule moGraphMod = new GraphModule(new MoModule("test"));
			phiGraphMod.setGeometry(new mxGeometry(10, 10, 100, 150));
			stmGraphMod.setGeometry(new mxGeometry(175, 10, 100, 150));
			moGraphMod.setGeometry(new mxGeometry(350, 10, 100, 150));

			IMessageSender phiSender1 = (IMessageSender) phiGraphMod.getMessageSenderCells()[0].getValue();
			IMessageSender phiSender2 = (IMessageSender) phiGraphMod.getMessageSenderCells()[1].getValue();
			IMessageSender phiSender3 = (IMessageSender) phiGraphMod.getMessageSenderCells()[2].getValue();
			IMessageSender stmSender = (IMessageSender) stmGraphMod.getMessageSenderCells()[0].getValue();
			Object stmListener1 = stmGraphMod.getMessageListenerCells()[0].getValue();
			Object stmListener2 = stmGraphMod.getMessageListenerCells()[1].getValue();
			Object stmListener3 = stmGraphMod.getMessageListenerCells()[2].getValue();
			Object moListener = moGraphMod.getMessageListenerCells()[0].getValue();
			
			phiSender1.addMessageListener(stmListener1);
			phiSender2.addMessageListener(stmListener2);
			phiSender3.addMessageListener(stmListener3);
			stmSender.addMessageListener(moListener);
			
			graph.addCells(new Object[]{phiGraphMod, stmGraphMod, moGraphMod});
			graph.insertEdge(null, null, null, phiGraphMod.getMessageSenderCells()[0], stmGraphMod.getMessageListenerCells()[0]);
			graph.insertEdge(null, null, null, phiGraphMod.getMessageSenderCells()[1], stmGraphMod.getMessageListenerCells()[1]);
			graph.insertEdge(null, null, null, phiGraphMod.getMessageSenderCells()[2], stmGraphMod.getMessageListenerCells()[2]);
			graph.insertEdge(null, null, null, stmGraphMod.getMessageSenderCells()[0], moGraphMod.getMessageListenerCells()[0]);
		} 
		catch (PhidgetException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
}
