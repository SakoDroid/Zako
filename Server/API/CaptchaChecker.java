package Server.API;

import Engines.Captcha.Data;
import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.basicUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaptchaChecker implements API{
    @Override
    public void init(ServerRequest req, RequestProcessor reqp) {
        String returnAns = "NA";
        String ans = new String(basicUtils.toByteArray(reqp.Body));
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("text/plain");
        fs.setKeepAlive(false);
        Pattern ptn = Pattern.compile("Target=[^&]+");
        Matcher mc = ptn.matcher(ans);
        if (mc.find()){
            returnAns = Data.checkAnswer(req.getIP(),new String(basicUtils.toByteArray(reqp.Body)),req.getHost());
            new TargetChecker(mc.group(), returnAns, req.getIP());
        }
        fs.send(returnAns,req.out,req.getIP(),req.getID(),req.getHost());
    }

    private static class TargetChecker extends Thread{
        private final String target;
        private final String ans;
        private final String ip;

        public TargetChecker(String Target, String answer,String ip){
            target = Target;
            ans = answer;
            this.ip = ip;
            this.start();
        }

        @Override
        public void run(){
            if (target.contains("DDOS")){
                if (ans.equals("OK"))
                    Engines.DDOS.Interface.clearRecords(ip);
            }
        }
    }
}
