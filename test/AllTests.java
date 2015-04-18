import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ PhidgetsMessageSenderTest.class, PhidgetsModuleTest.class,
		StMcConverterTest.class, StMcModuleTest.class, StMnConverterTest.class,
		StMnModuleTest.class })
public class AllTests {

}
