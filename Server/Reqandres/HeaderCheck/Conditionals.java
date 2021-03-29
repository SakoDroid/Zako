package Server.Reqandres.HeaderCheck;

import Server.Utils.Enums.Methods;
import Server.Utils.Headers.HashComputer;
import Server.Utils.Headers.LMGenerator;
import java.io.File;
import java.util.HashMap;

class Conditionals {

    private int status = 200;
    private File fl;
    private HashMap<String,String> headers;
    private Methods method;

    public void decide(HashMap<String,String> headers, File fl, Methods mth){
        this.fl = fl;
        this.headers = headers;
        this.method = mth;
        if (fl.exists()){
            this.checkETagRelated();
            this.checkLMRelated();
        }
    }

    private void checkETagRelated(){
        if (headers.containsKey("If-None-Match")){
            String currentHash = new HashComputer(fl).computeHash();
            String ifHeader = headers.get("If-None-Match");
            if (!ifHeader.trim().equals("*")){
                for (String etag : ifHeader.split(",")) {
                    if (etag.replace("\"", "").trim().equals(currentHash)) {
                        this.status = 304;
                        break;
                    }
                }
            }else{
                if (fl.exists())
                    this.status = 304;
            }
        }
        else if (headers.containsKey("If-Match")){
            String currentHash = new HashComputer(fl).computeHash();
            String ifHeader = headers.get("If-Match");
            if (!ifHeader.trim().equals("*")){
                for (String etag : ifHeader.split(",")) {
                    if (!etag.replace("\"", "").trim().equals(currentHash)) {
                        this.status = 412;
                        break;
                    }
                }
            }else{
                if (!fl.exists())
                    this.status = 304;
            }
        }
    }

    private void checkLMRelated(){
        if (headers.containsKey("If-Modified-Since")){
            if (method == Methods.GET || method == Methods.HEAD){
                String lm = new LMGenerator(this.fl).generate();
                if (headers.get(("If-Modified-Since")).trim().equals(lm))
                    this.status = 304;
            }else
                this.status = 400;
        }
        else if (headers.containsKey("If-Unmodified-Since")){
            String lm = new LMGenerator(this.fl).generate();
            if (!headers.get(("If-Modified-Since")).trim().equals(lm))
                this.status = 412;

        }
    }

    public int getStatus(){
        return this.status;
    }
}
