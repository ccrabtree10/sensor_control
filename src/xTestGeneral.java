
public class xTestGeneral {

	public static void main(String[] args) {
		
		TeVirtualMIDI port = new TeVirtualMIDI("test");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

}
