package Engines.FCGI.Client.Utils;

public class RequestIDMatchException extends Exception{

    private final int reqID1;
    private final int reqID2;

    public RequestIDMatchException(int id1, int id2){
        this.reqID1 = id1;
        this.reqID2 = id2;
    }

    @Override
    public String toString(){
        return reqID2 + " received from FCGI server which does not match " + reqID1 + " !";
    }
}
