package Server.Utils;

import java.net.Socket;
import java.util.HashMap;
import java.util.TimerTask;

public class SocketsData {

    private final HashMap<Socket,Integer> maxReqsPerSock = new HashMap<>();
    private final HashMap<Socket,int[]> requestPerSocket = new HashMap<>();
    private static final SocketsData sd = new SocketsData();

    public static SocketsData getInstance(){
        return sd;
    }

    public void addRequest(Socket s){
        int[] temp = this.requestPerSocket.get(s);
        if (temp != null)
            ++temp[0];
        else
            this.requestPerSocket.put(s,new int[]{1});
    }

    public void removeEntry(Socket s){
        this.requestPerSocket.remove(s);
        this.maxReqsPerSock.remove(s);
    }

    public boolean maxReached(Socket s){
        if (this.requestPerSocket.containsKey(s))
            return this.requestPerSocket.get(s)[0] >= maxReqsPerSock.get(s);
        return true;
    }

    public void setMaxReqsPerSock(Socket s , int max){
        this.maxReqsPerSock.put(s,max);
    }
}
