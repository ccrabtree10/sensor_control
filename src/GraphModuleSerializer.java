import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;


public class GraphModuleSerializer extends FieldSerializer
{
	
	public GraphModuleSerializer(Kryo kryo, Class type) {
		super(kryo, type);
	}
	
	public void write(Kryo kryo, Output output, GraphModule graphModule)
	{
		System.out.println("Writing");
	}
	
	public Object read(Kryo kryo, Input input, Class type)
	{
		GraphModule graphModule = (GraphModule) super.read(kryo, input, type);
		
		System.out.println("Done in GraphModuleSerializer");
		return graphModule;
	}

}
