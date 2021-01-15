package Server.HttpAuth;

import Server.Reqandres.Request;
import java.util.HashMap;

public class Interface {

    private static Core core ;
    private static boolean ON = true;

    public static void load (boolean status){
        if (status)
            core = new Core();
        ON = status;
    }

    public static boolean needAuth(String path){
        if (ON)
            return core.apiContains(path);
        else return false;
    }

    public static int evaluate(HashMap headers){
        int temp;
        Object auth = headers.get("Authorization");
        if (auth != null){
            if (core.checkAuth(String.valueOf(auth)))
                temp = 200;
            else
                temp = 403;
        }else
            temp = 401;
        return temp;
    }

    public static void send401(Request req){
        core.askForAuth(req);
    }
}
