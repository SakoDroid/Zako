package Engines.FCGI.Utils;

import java.util.*;

public class Utils {

    private static final Random rnd = new Random();
    private static final List<Integer> ids = new ArrayList<>();

    public static int getID(){
        int tempId;
        do {
            tempId = rnd.nextInt(65535);
        }while (ids.contains(tempId));
        ids.add(tempId);
        return tempId;
    }

    public static void delId(int id){
        ids.remove(id);
    }
}
