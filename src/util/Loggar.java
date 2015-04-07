package util;

public class Loggar {
	private final static boolean debug = true;
	
	public static void log(String text){
		if(debug){
			System.out.print(text);
		}
	}
	
	public static void logln(String text){
		if(debug){
			System.out.println(text);
		}
	}
}
