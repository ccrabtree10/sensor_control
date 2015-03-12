package com.sensorcontrol.modules;
import javax.swing.JComponent;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;


public class GraphModule extends mxCell implements IModuleChangeListener
{
	transient Object[] messageSenders;
	transient Object[] messageListeners;
	transient mxCell[] senderCells;
	transient mxCell[] listenerCells;
	final double PORT_RADIUS = 5;
	double height;
	IModule module;
	
	public GraphModule(IModule module)
	{
		super(module);
		this.module = module;
		readModule();
		module.setModuleChangeListener(this);
		this.setVertex(true);
	}
	
	private void readModule()
	{
		messageSenders = module.getMessageSenders();
		messageListeners = module.getMessageListeners();
		senderCells = new mxCell[messageSenders.length];
		listenerCells = new mxCell[messageListeners.length];
		
		if(senderCells.length > listenerCells.length)
		{
			height = senderCells.length*2*(2*PORT_RADIUS);
		}
		else
		{
			height = listenerCells.length*2*(2*PORT_RADIUS);
		}
		
		this.setGeometry(new mxGeometry(10, 10, 100, height));
		
		for(int x=0; x<messageSenders.length; x++)
		{
			mxGeometry geoSender = new mxGeometry(1, (double)x/messageSenders.length, PORT_RADIUS*2, PORT_RADIUS*2);
			geoSender.setOffset(new mxPoint(-PORT_RADIUS, 0));
			geoSender.setRelative(true);
			senderCells[x] = new mxCell(messageSenders[x], geoSender, "shape=ellipse;perimter=ellipsePerimeter");
			senderCells[x].setVertex(true);		
			this.insert(senderCells[x]);
		}
		
		for(int x=0; x<messageListeners.length; x++)
		{
			mxGeometry geoSender = new mxGeometry(0, (double)x/messageListeners.length, PORT_RADIUS*2, PORT_RADIUS*2);
			geoSender.setOffset(new mxPoint(-PORT_RADIUS, 0));
			geoSender.setRelative(true);
			listenerCells[x] = new mxCell(messageListeners[x], geoSender, "shape=ellipse;perimter=ellipsePerimeter");
			listenerCells[x].setVertex(true);		
			this.insert(listenerCells[x]);
		}
	}

	public JComponent getControlPanel() 
	{
		return module.getControlPanel();
	}
	
	public mxCell[] getMessageSenderCells()
	{
		return senderCells;
	}
	
	public mxCell[] getMessageListenerCells()
	{
		return listenerCells;
	}
	
	public void delete()
	{
		module.delete();
	}
	
	public void initialize()
	{
		module.initialize();
		readModule();
	}
	
	public void updateView()
	{
		System.out.println("Module changed.");
	}

	
}
