import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.FieldSerializer;


public class ModuleSerializer extends FieldSerializer
{
	
	public ModuleSerializer(Kryo kryo, Class type) {
		super(kryo, type);
	}
	
	
	public GraphModule read(Kryo kryo, Input input, Class type)
	{
		GraphModule graphModule = (GraphModule) super.read(kryo, input, type);
		System.out.println("Done in moduleSerializer");
		graphModule.initialize();
		return graphModule;
	}

}
