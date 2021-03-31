package Server.Utils.Compression;

import Server.Utils.Configs.Configs;
import Server.Utils.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Deflater;

public class DeflateCompressor implements Compressor{

    @Override
    public File compress(File fl) {
        File out = new File(Configs.getCWD() + "/Cache/Compressed/" + fl.getName() + ".df");
        try(FileOutputStream fos = new FileOutputStream(out)) {
            final Deflater df = new Deflater();
            final byte[] buff = new byte[1024];
            FileInputStream fis = new FileInputStream(fl);
            df.setInput(fis.readAllBytes());
            fis.close();
            df.finish();
            while (!df.finished()) {
                int count = df.deflate(buff);
                fos.write(buff, 0, count);
            }
            df.end();
            fos.flush();
        }catch (Exception ex){
            Logger.logException(ex);
        }
        return out;
    }

    @Override
    public File compress(byte[] data, String id) {
        File out = new File(Configs.getCWD() + "/Cache/Compressed/" + id + ".df");
        try(FileOutputStream fos = new FileOutputStream(out)) {
            final Deflater df = new Deflater();
            final byte[] buff = new byte[1024];
            df.setInput(data);
            df.finish();
            while (!df.finished()) {
                int count = df.deflate(buff);
                fos.write(buff, 0, count);
            }
            df.end();
            fos.flush();
        }catch (Exception ex){
            Logger.logException(ex);
        }
        return out;
    }
}
