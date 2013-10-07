package exceptions;

public class FatalHandler {
	public static void handle(Exception e) {
		System.out.println("bug!");
		e.printStackTrace();
		System.exit(1);
	}

	public static void handle(String msg, Exception e) {
		System.out.println(msg);
		e.printStackTrace();
		System.exit(1);
	}

    public static void handle(String msg) {
		System.out.println(msg);
		System.exit(1);
	}
}