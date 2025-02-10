package the.david.util;

public class NumberUtils{
	public static Integer parseInt(String string){
		int parsedInt = 0;
		try{
			parsedInt = Integer.parseInt(string);
		}catch(NumberFormatException e){
			return null;
		}
		return parsedInt;
	}
}
