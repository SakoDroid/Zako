package Server.Utils.ViewCounter;

import Server.Utils.JSON.JSONBuilder;
import Server.Utils.JSON.JSONDocument;
import java.io.File;
import java.util.HashMap;

public class ViewCore {

    private long views = 0;
    private long views24 = 0;
    private long viewsWeek = 0;
    private HashMap<String,Long> ipBasedViews;

    public ViewCore(String rootAddress){
        this.load(rootAddress);
    }

    public synchronized void addView(String ip){
        views ++;
        views24 ++;
        viewsWeek ++;
        ipBasedViews.put(ip
                , (ipBasedViews.containsKey(ip) ? ipBasedViews.get(ip) + 1 : 1)
        );
    }

    private void load(String add){
        File vw = new File(add + "/views.json");
        if (vw.isFile()){
            JSONBuilder builder = JSONBuilder.newInstance();
            HashMap data = (HashMap) builder.parse(vw).toJava();
            views = (Long) data.get("All views");
            views24 = (Long) data.get("Last day views");
            viewsWeek = (Long) data.get("Last week views");
            ipBasedViews = (HashMap<String, Long>) data.get("IPs");
        }else
            ipBasedViews = new HashMap<>();
    }

    public JSONDocument toJson(){
        HashMap data = new HashMap();
        data.put("All views",views);
        data.put("Last day views",views24);
        data.put("Last week views",viewsWeek);
        HashMap<String,Long> ips = new HashMap<>();
        for (String ip : ipBasedViews.keySet())
            ips.put(ip,ipBasedViews.get(ip));
        data.put("IPs",ips);
        return new JSONDocument(data);
    }

    @Override
    public String toString(){
        return "All views : " + views +
                "\nviews24 : " + views24 +
                "\nviews week : " + viewsWeek +
                "\nips : " + ipBasedViews +
                "\n--------------------------------------";
    }

    public void reset24hViews(){
        this.views24 = 0;
    }

    public void resetWeekViews(){
        this.viewsWeek = 0;
    }
}
