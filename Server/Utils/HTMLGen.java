package Server.Utils;

public class HTMLGen {

    private HTMLGen(){}

    public static String genIPBan(String ip){
        return """
            HTTP/1.1 403 Forbidden
            Server: """ + basicUtils.Zako + """
            Content-Type: text/html
                                    
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width initial-scale=1.0"/>
                <title>Forbidden!</title>
            </head>
            <body>
            <div style="margin-top: 20%;font-family: 'Courier'">
                <h1 style="margin: auto;width: fit-content;font-size: 100px;">403</h1>
                <h3 style="margin: auto;width: fit-content;font-size: 30px;">Your ip (""" + ip + """
            )has been banned!</h3><br/><br/>
            </div>
            </body>
            </html>""";
    }

    public static String genTooManyRequests(String ip){
        return """
                HTTP/1.1 429 Too Many Requests
                Server: """ + basicUtils.Zako + """
                Content-Type: text/html
                Retry-After: 5
                                        
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8"/>
                    <meta name="viewport" content="width=device-width initial-scale=1.0"/>
                    <title>Too Many Requests!</title>
                </head>
                <body>
                <div style="margin-top: 20%;font-family: 'Courier'">
                    <h1 style="margin: auto;width: fit-content;font-size: 100px;">429</h1>
                    <h3 style="margin: auto;width: fit-content;font-size: 30px;">Server is receiving too many requests from your ip! ( """ + ip + """
            )</h3><br/><br/>
                <h5 style="margin: auto;width: fit-content;font-size: 25px;">(Zako server DDOS protection)</h5><br/><br/>
            </div>
            </body>
            </html>""";
    }

    public static String genOverLoad(){
        return """
            HTTP/1.1 503 Service Unavailable
            Server: """ + basicUtils.Zako + """
            Content-Type: text/html
            Retry-After: 5
                                    
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width initial-scale=1.0"/>
                <title>Service Unavailable!</title>
            </head>
            <body>
            <div style="margin-top: 20%;font-family: 'Courier'">
                <h1 style="margin: auto;width: fit-content;font-size: 100px;">503</h1>
                <h3 style="margin: auto;width: fit-content;font-size: 30px;">Server is receiving too many requests!</h3><br/><br/>
                <h5 style="margin: auto;width: fit-content;font-size: 25px;">Thank you for being patient</h5><br/><br/>
            </div>
            </body>
            </html>""";
    }

    public static String gen413(){
        return """
            HTTP/1.1 413 Payload Too Large
            Server: """ + basicUtils.Zako + """
            Content-Type: text/html
                                    
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width initial-scale=1.0"/>
                <title>Payload Too Large!</title>
            </head>
            <body>
            <div style="margin-top: 20%;font-family: 'Courier'">
                <h1 style="margin: auto;width: fit-content;font-size: 100px;">413</h1>
                <h3 style="margin: auto;width: fit-content;font-size: 30px;">Payload Too Large!</h3><br/><br/>
            </div>
            </body>
            </html>""";
    }
}
