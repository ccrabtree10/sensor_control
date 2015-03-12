import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/* teVirtualMIDI Java interface
 *
 * Copyright 2009-2012, Tobias Erichsen
 * All rights reserved, unauthorized usage & distribution is prohibited.
 *
 *
 * File: TeVirtualMIDI.java
 *
 * This file implements the Java-class-wrapper for the teVirtualMIDI-driver.
 * This class encapsualtes the native C-type interface which is integrated
 * in the teVirtualMIDI32.dll and the teVirtualMIDI64.dll.
 */

public class TeVirtualMIDI {

	/* default size of sysex-buffer */
	private static final int TE_VM_DEFAULT_SYSEX_SIZE = 65535;

        /* TE_VM_LOGGING_MISC - log internal stuff (port enable, disable...) */
	public static final int TE_VM_LOGGING_MISC = 1;
        /* TE_VM_LOGGING_RX - log data received from the driver */
        public static final int TE_VM_LOGGING_RX = 2;
        /* TE_VM_LOGGING_TX - log data sent to the driver */
        public static final int TE_VM_LOGGING_TX = 4;

        /* TE_VM_FLAGS_PARSE_RX - parse incoming data into single, valid MIDI-commands */
	public static final int TE_VM_FLAGS_PARSE_RX = 1;
	
	private String portName;


	public TeVirtualMIDI( String portName, int maxSysexLength, int flags ) {

		isOpen = false;

		int error = nativePortCreate( portName, maxSysexLength, flags );

		if ( 0 != error ) {

			TeVirtualMIDIException.ThrowExceptionForReasonCode( error );

		}

		isOpen = true;
		this.portName = portName;

	}


	public TeVirtualMIDI( String portName, int maxSysexLength ) {

		this( portName, maxSysexLength, TE_VM_FLAGS_PARSE_RX );

	}


	public TeVirtualMIDI( String portName ) {
		
		this( portName, TE_VM_DEFAULT_SYSEX_SIZE, TE_VM_FLAGS_PARSE_RX );

	}
	
	public TeVirtualMIDI()
	{
		System.out.println("Default cons");
	}

	public void shutdown( ) {

		int error = nativePortShutdown( handle );

		if ( 0 != error ) {

			TeVirtualMIDIException.ThrowExceptionForReasonCode( error );

		}

	}


	public void sendCommand( byte[] command ) {

		if ( command.length == 0 ) {

			return;

		}

		int error = nativeSendCommand( handle, command );

		if ( 0 != error ) {

			TeVirtualMIDIException.ThrowExceptionForReasonCode( error );

		}

	}


	public byte[] getCommand( ) {

		getError = 0;

		byte[] result = nativeGetCommand( handle );

		if ( result.length == 0 ) {

			if ( 0 != getError ) {

				TeVirtualMIDIException.ThrowExceptionForReasonCode( getError );

			}

		}

		return result;

	}


	public native static int getVersionMajor();
	public native static int getVersionMinor();
	public native static int getVersionRelease();
	public native static int getVersionBuild();
	public native static String getVersionString();
	public native static int logging( int flags );


	private native int nativePortCreate( String portName, int maxSysexSize, int flags );
	private native int nativePortClose( long pointerReference );
	private native int nativePortShutdown( long pointerReference );
	private native int nativeSendCommand( long pointerReference, byte[] command );
	private native byte[] nativeGetCommand( long pointerReference );

	/*
	protected void finalize() throws Throwable {

		try {

			if ( isOpen ) {

				nativePortClose( handle );

				isOpen = false;

			}

		} finally {

			super.finalize();

		}

	}*/


	private boolean isOpen;


 	static {

    		try {

			System.loadLibrary( "teVirtualMIDI32" );

		} catch( UnsatisfiedLinkError ignored ) {

			System.loadLibrary( "teVirtualMIDI64" );

		}

	}
 	
	// Never, ever change the following part of the class, since it is
	// used from within the native-JNI-DLL to store the object pointer
	// to the native teVirtualMIDI instance!
	private long handle;
	private int getError;

}
