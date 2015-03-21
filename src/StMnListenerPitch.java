


public class StMnListenerPitch implements IMessageListenerSensor
{
	StMnNoteConverter converter;
	
	public void receive(MessageSensor message) 
	{
		converter.setPitch(message);
	}
	
	public void setConverter(StMnNoteConverter converter)
	{
		this.converter = converter;
	}

}
