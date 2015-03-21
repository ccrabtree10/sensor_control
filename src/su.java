import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class su {

	public static Logger log;
	public static Level f = Level.FINEST;
	
	public static void init() {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		handler.setFormatter(new ChrisFormat());
		log = Logger.getLogger("general_log");
		log.addHandler(handler);
		log.setLevel(Level.FINEST);
	}	
	
	private static class ChrisFormat extends SimpleFormatter {
		public String format(LogRecord rec) {
			StringBuffer b = new StringBuffer();
			b.append(rec.getSourceClassName());
			b.append(": " + rec.getMessage());
			b.append("\n");
			return b.toString();
		}
	}
}
