package Engines.FCGI.Response;

class FCGIResponseHeader {

    public final int version;
    public int type;
    public int requestID;
    public final int contentLength;
    public final int paddingLength;
    public final int reserved;

    public FCGIResponseHeader(byte[] header){
        if (header.length < 8) throw new IllegalArgumentException("Header length should be 8!");
        version = header[0];
        type = header[1];
        int requestIdB1 = (header[2] << 8) & 0xFF00;
        int requestIdB0 = header[3] & 0xFF;
        requestID = requestIdB1 + requestIdB0;
        int contentLengthB1 = (header[4] << 8) & 0x00FF00;
        int contentLengthB0 = header[5] & 0xFF;
        contentLength = contentLengthB0 + contentLengthB1;
        paddingLength = header[6];
        reserved = header[7];
    }

}
