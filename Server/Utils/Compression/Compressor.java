package Server.Utils.Compression;

import java.io.File;

public interface Compressor {

    File compress(File fl);

    File compress(byte[] data,String id);
}
