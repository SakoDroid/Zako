package Server.API;

public class Factory {

    public static API getAPI(String api){
        switch (api){
            case "/" -> {
                return new index();
            }
            case "/getcp" -> {
                return new CapthaSender();
            }
            case "/chkcp" -> {
                return new CapthaChecker();
            }
            default -> {
                return new Def();
            }
        }
    }
}
