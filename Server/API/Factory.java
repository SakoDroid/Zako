package Server.API;

import Server.Utils.CaptchaConfigs;

public class Factory {

    public static API getAPI(String api){
        if (api.equals("/"))
            return new index();
        else if (api.equals(CaptchaConfigs.CGA))
            return new CaptchaSender();
        else if (api.equals(CaptchaConfigs.CPA))
            return new CaptchaChecker();
        else
            return new Def();
    }
}
