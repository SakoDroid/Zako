package Server.Method;

import Server.Utils.Enums.Methods;

public class Factory {

    public Method getMt(Methods mtd){
        switch (mtd){
            case CONNECT -> {
                return new CONNECT();
            }
            case PUT -> {
                return new PUT();
            }
            case DELETE -> {
                return new DELETE();
            }
            case OPTIONS -> {
                return new OPTIONS();
            }
            case TRACE -> {
                return new TRACE();
            }
            default -> {
                return new Def();
            }
        }
    }
}
