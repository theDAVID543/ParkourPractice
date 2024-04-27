package the.david.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class NumberUtils {
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
