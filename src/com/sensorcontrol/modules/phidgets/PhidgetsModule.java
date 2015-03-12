package com.sensorcontrol.modules.phidgets;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.phidgets.*;
import com.phidgets.event.*;
import com.sensorcontrol.modules.IModule;
import com.sensorcontrol.modules.IModuleChangeListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.BorderFactory;

public class PhidgetsModule implements IModule, AttachListener, SensorChangeListener
{
	private transient InterfaceKitPhidget ikp;
	private PhidgetsMessageSender[] messageSenders;
	private transient IModuleChangeListener moduleChangeListener;
	private JPanel controlPanel, moduleControlPanel;
	private JLabel debugLabel;
	
	public PhidgetsModule() throws PhidgetException
	{
		messageSenders = new PhidgetsMessageSender[8];
		for (int x=0; x<8; x++)
		{
			messageSenders[x] = new PhidgetsMessageSender(x);
		}
		
		ikp = new InterfaceKitPhidget();
		ikp.open(117182);
		ikp.waitForAttachment();
		ikp.addAttachListener(this);
		ikp.addSensorChangeListener(this);
	}	
	
	public void attached(AttachEvent ae)
	{
		try 
		{
			System.out.println(ae.getSource().getSerialNumber());
		} 
		catch (PhidgetException pe) {}
	}
	
	public void sensorChanged(SensorChangeEvent sce) 
	{
		messageSenders[sce.getIndex()].send(sce);
	}

	public Object[] getMessageSenders()
	{
		return messageSenders;
	}
	
	public Object[] getMessageListeners()
	{
		return new Object[0];
	}

	public JComponent getControlPanel()
	{
		return controlPanel;
	}

	public void delete() 
	{
		try 
		{
			ikp.close();
		} catch (PhidgetException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void initialize()
	{
		
	}

	public void setModuleChangeListener(IModuleChangeListener listener) 
	{
		moduleChangeListener = listener;
	}
}
