package Server.API;

import Server.Utils.CaptchaConfigs;

public class Factory {

    public static API getAPI(String api, String host){
        if (api.equals("/"))
            return new index();
        else if (api.equals(CaptchaConfigs.getCGA(host)))
            return new CaptchaSender();
        else if (api.equals(CaptchaConfigs.getCPA(host)))
            return new CaptchaChecker();
        else
            return new Def();
    }
}
