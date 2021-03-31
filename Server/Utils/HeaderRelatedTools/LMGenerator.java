package Server.Utils.HeaderRelatedTools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class LMGenerator {

    private final File fl;

    public LMGenerator(File fl){
        this.fl = fl;
    }

    public String generate(){
        Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cl.setTimeInMillis(fl.lastModified());
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        return df.format(cl.getTime()).concat(" GMT");
    }
}
