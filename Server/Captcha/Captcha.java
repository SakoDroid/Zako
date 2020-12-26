package Server.Captcha;

import Server.Utils.Configs;
import Server.Utils.Logger;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

public class Captcha {

    public byte[] image;

    private final char[] chars = {'a','A','b','B','0','c','1','C','d','D','e','E','2','f','F','g','G','h','3','H','i','I','4','j','J','k','K','l','L','5','m','M','n','N','o','O','p','6','P','q','Q',
            '7','r','R','s','S','t','8','T','u','U','v','9','V','w','W','x','X','y','Y','z','Z','0','1','2','3','4','5','6','7','8','9'};

    private final Random rnd = new Random();

    public Captcha(String ip,String host){
        Logger.glog("generating captcha for " + ip,host);
        String ans = getRandomString(Configs.captchaLength);
        image = getPicture(ans);
        Data.addRecord(ip,ans);
    }

    private String getRandomString(int length){
        StringBuilder out = new StringBuilder();
        for (int i = 0 ; i < length ; i++){
            out.append(chars[rnd.nextInt(chars.length)]);
        }
        return out.toString();
    }

    private byte[] getPicture(String cap){
        int mode = rnd.nextInt(3);
        int hr = Configs.captchaHardness;
        int startind = 20;
        BufferedImage img = new BufferedImage(140,80,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        int stat = rnd.nextInt(10);
        if (stat % 2 == 0) g.setColor(Color.white);
        else g.setColor(Color.black);
        g.fillRect(0, 0, 140, 80);
        if (mode == 0){
            for (int i = 0; i < cap.length(); i++) {
                g.setColor(new Color(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
                g.setFont(new Font("Serif", Font.PLAIN, 30));
                g.drawString(new String(new char[]{cap.charAt(i)}), startind + i * 140 / (cap.length() + 2), (rnd.nextInt(8) + 3) * 8);
            }
            for (int i = 0; i < hr * 3; i++) {
                g.setColor(new Color(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
                g.drawLine(rnd.nextInt(140), rnd.nextInt(80), rnd.nextInt(140), rnd.nextInt(80));
            }
            for (int i = 0; i < hr * 15; i++) {
                g.setColor(new Color(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
                g.fillOval(rnd.nextInt(140), rnd.nextInt(80), hr - 1, hr - 1);
            }
        }
        else if (mode == 1){
            if (stat % 2 == 0) g.setColor(Color.black);
            else g.setColor(Color.white);
            for (int i = 0; i < cap.length(); i++) {
                g.setFont(new Font("Serif", Font.PLAIN, 30));
                g.drawString(new String(new char[]{cap.charAt(i)}), startind + i * 140 / (cap.length() + 2), (rnd.nextInt(8) + 3) * 8);
            }
            g.setStroke(new BasicStroke(2));
            for (int j = 0 ; j < 2 ; j++){
                if (j == 0){
                    if (stat % 2 == 0) g.setColor(Color.white);
                    else g.setColor(Color.black);
                }else{
                    if (stat % 2 == 0) g.setColor(Color.black);
                    else g.setColor(Color.white);
                }
                for (int i = 0 ; i < hr * 2 ; i++){
                    int y = 80 / hr / 2 * i;
                    g.drawLine(0,y,140,y);
                }
                for (int i = 0 ; i < hr ; i++){
                    int x = 140 / hr * i;
                    g.drawLine(x,0,x,80);
                }
            }
        }
        else if (mode == 2){
            if (stat % 2 == 0) g.setColor(Color.black);
            else g.setColor(Color.white);
            for (int i = 0; i < cap.length(); i++) {
                g.setFont(new Font("Serif", Font.PLAIN, 30));
                g.drawString(new String(new char[]{cap.charAt(i)}), startind + i * 140 / (cap.length() + 2), (rnd.nextInt(8) + 3) * 8);
            }
            if (stat % 2 == 0) g.setColor(Color.white);
            else g.setColor(Color.black);
            for (int i = 0 ; i < hr * 3 ; i++){
                g.setStroke(new BasicStroke(rnd.nextInt(4)));
                g.drawLine(rnd.nextInt(140),rnd.nextInt(80),rnd.nextInt(140),rnd.nextInt(80));
            }
            if (stat % 2 == 0) g.setColor(Color.black);
            else g.setColor(Color.white);
            for (int i = 0 ; i < hr * 23 ; i++){
                if (i % 2 == 0){
                    g.drawOval(rnd.nextInt(140),rnd.nextInt(80),rnd.nextInt(5),rnd.nextInt(5));
                }else {
                    g.fillOval(rnd.nextInt(140),rnd.nextInt(80),rnd.nextInt(5),rnd.nextInt(5));
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            ImageIO.write(img,"png",out);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        return out.toByteArray();
    }
}