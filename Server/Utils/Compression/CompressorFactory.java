package Server.Utils.Compression;

public class CompressorFactory {

    public Compressor getCompressor(Algorithm alg){
        return switch (alg){
            case gzip -> new GZIPCompressor();
        };
    }
}
