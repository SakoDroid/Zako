package Server.Captcha;

import Server.Utils.Configs;
import Server.Utils.Logger;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;

class Core {

    private final static HashMap<String,String> ipans = new HashMap<>();

    private final static char[] chars = {'a','A','b','B','0','c','1','C','d','D','e','E','2','f','F','g','G','h','3','H','i','I','4','j','J','k','K','l','L','5','m','M','n','N','o','O','p','6','P','q','Q',
    '7','r','R','s','S','t','8','T','u','U','v','9','V','w','W','x','X','y','Y','z','Z','0','1','2','3','4','5','6','7','8','9'};

    private final static int[] numbers = {-3,3,-1,5,0,2,7};

    private final static Random rnd = new Random();

    private static String getRandomString(int length){
        String out = "";
        for (int i = 0 ; i < length ; i++){
            out += chars[rnd.nextInt(chars.length)];
        }
        return out;
    }

    private static byte[] getPicture(String cap){
        int hr = Configs.captchaHardness;
        int startind = 20;
        BufferedImage img = new BufferedImage(140,80,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        int stat = rnd.nextInt(10);
        if (stat%2 == 0) g.setColor(Color.white);
        else g.setColor(Color.black);
        g.fillRect(0,0,140,80);
        for (int i = 0 ; i < cap.length() ; i++){
            //g.setPaint(new GradientPaint((float)0,(float)0,new Color(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255)),(float)20,(float)20,new Color(0,0,0)));
            g.setColor(new Color(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255)));
            g.setFont(new Font("Serif",Font.PLAIN,30));
            g.drawString(new String(new char[]{cap.charAt(i)}),startind + i*140/(cap.length()+2),35 + i*numbers[rnd.nextInt(numbers.length)]);
        }
        for (int i = 0 ; i < hr*3 ; i++){
            g.setColor(new Color(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255)));
            g.drawLine(rnd.nextInt(140),rnd.nextInt(80),rnd.nextInt(140),rnd.nextInt(80));
        }
        for (int i = 0 ; i < hr*15 ; i++){
            g.setColor(new Color(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255)));
            g.fillOval(rnd.nextInt(140),rnd.nextInt(80),hr-1,hr-1);
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

    public static byte[] getCaptcha(String ip){
        String str = getRandomString(Configs.captchaLength);
        ipans.put(ip,str.toLowerCase());
        return getPicture(str);
    }

    public static boolean checkAns(String ip,String ans){
        if (ipans.get(ip).equals(ans)){
            ipans.remove(ip);
            return true;
        }else return false;
    }

}