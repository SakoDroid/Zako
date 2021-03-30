package Server.Reqandres.Responses;

public class Factory {

    public Response getResponse(int code){
        return switch (code){
            case 101 -> new ProtoSwitch();
            case 200 -> new Handle();
            case 401 -> new Authenticate();
            default -> new ErrorSender();
        };
    }
}
