import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class SimpleLog {

	public static Logger log;
	public static Level f = Level.FINEST;
	
	public static void init() {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(f);
		handler.setFormatter(new SimpleFormat());
		log = Logger.getLogger("SimpleLog");
		log.addHandler(handler);
		log.setLevel(f);
	}	
	
	private static class SimpleFormat extends SimpleFormatter {
		public String format(LogRecord rec) {
			StringBuffer b = new StringBuffer();
			b.append(rec.getSourceClassName());
			b.append(": " + rec.getMessage());
			b.append("\n");
			return b.toString();
		}
	}
}
