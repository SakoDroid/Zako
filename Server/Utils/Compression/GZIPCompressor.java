package Server.Utils.Compression;

import Server.Utils.Configs.Configs;
import Server.Utils.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompressor implements Compressor{

    @Override
    public File compress(File fl) {
        File out = new File(Configs.getCWD() + "/Cache/Compressed/" + fl.getName() + ".gz");
        try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(out))) {
            FileInputStream fis = new FileInputStream(fl);
            fis.transferTo(gos);
            gos.finish();
        } catch (Exception ex) {
            Logger.logException(ex);
        }
        return out;
    }

    @Override
    public File compress(byte[] data, String id) {
        File out = new File(Configs.getCWD() + "/Cache/Compressed/" + id + ".gz");
        try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(out))) {
            gos.write(data);
        } catch (Exception ex) {
            Logger.logException(ex);
        }
        return out;
    }
}
