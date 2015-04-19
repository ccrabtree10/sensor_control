


/**
 * Loads a system library teVirtualMidi.dll and constructs a virtual MIDI port with the name
 * given to the constructor.
 * @author Tobias Erichsen
 *
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

	/**
	 * Construct a virtual MIDI port with the given values.
	 * @param portName The name of the MIDI port.
	 * @param maxSysexLength The max length of a sysex message.
	 * @param flags
	 */
	public TeVirtualMIDI( String portName, int maxSysexLength, int flags ) {

		isOpen = false;

		int error = nativePortCreate( portName, maxSysexLength, flags );

		if ( 0 != error ) {

			TeVirtualMIDIException.ThrowExceptionForReasonCode( error );

		}

		isOpen = true;

	}

	/**
	 * Construct a virtual MIDI port with the given values.
	 * @param portName The name of the MIDI port.
	 * @param maxSysexLength The max length of a sysex message.
	 */
	public TeVirtualMIDI( String portName, int maxSysexLength ) {

		this( portName, maxSysexLength, TE_VM_FLAGS_PARSE_RX );

	}

	/**
	 * Construct a virtual MIDI port with the given values.
	 * @param portName The name of the MIDI port.
	 */
	public TeVirtualMIDI( String portName ) {

		this( portName, TE_VM_DEFAULT_SYSEX_SIZE, TE_VM_FLAGS_PARSE_RX );

	}

	/**
	 * Close this port and release its system resources.
	 */
	public void shutdown( ) {

		int error = nativePortShutdown( handle );

		if ( 0 != error ) {

			TeVirtualMIDIException.ThrowExceptionForReasonCode( error );

		}

	}

	/**
	 * Send this message to the virtual MIDI port.
	 * @param command The array of bytes which make up the MIDI message.
	 */
	public void sendCommand( byte[] command ) {

		if ( command.length == 0 ) {

			return;

		}

		int error = nativeSendCommand( handle, command );

		if ( 0 != error ) {

			TeVirtualMIDIException.ThrowExceptionForReasonCode( error );

		}

	}

	/**
	 * Receive a MIDI message from this virtual MIDI port.
	 * @return The byte array which makes up the received MIDI message.
	 */
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


	protected void finalize() throws Throwable {

		try {

			if ( isOpen ) {

				nativePortClose( handle );

				isOpen = false;

			}

		} finally {

			super.finalize();

		}

	}


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
