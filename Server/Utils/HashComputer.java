package Server.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class HashComputer {

    private final File fl;

    public HashComputer(File fl){
        this.fl = fl;
    }

    public String computeHash(){
        String hashed = "";
        try {
            FileInputStream fis = new FileInputStream(fl);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(fis.readAllBytes());
            BigInteger bigInt = new BigInteger(1,md.digest());
            hashed = bigInt.toString(16);
        }catch (Exception ex){
            Logger.logException(ex);
        }
        return hashed;
    }
}
