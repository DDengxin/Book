package com.sinolife.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


public class Base64ImageUtil {
    /**
     * Base64ToBufferedImage
     * @param base64String
     * @return
     */
    public  static BufferedImage Base64ToBufferedImage(String base64String){
        BufferedImage bufferedImage = null;
        InputStream stream = null;
        try {
            stream=Base64ToInputStream(base64String);
            bufferedImage = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (stream!=null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bufferedImage;
    }
    /**
     * base64转输入流
     * @param base64string
     * @return
     */
    public static InputStream Base64ToInputStream (String base64string){
        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(base64string);
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public  static  String BufferedImageToBase(BufferedImage bufferedImage){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
        try {
            ImageIO.write(bufferedImage, "jpg", baos);//写入流中
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();//转换成字节
        BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
        base64= base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        return base64;
    }

    public  static  boolean generateImage(String imgStr,String filePath){
        if (imgStr==null) {
            return  false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        OutputStream out=null;
        try {
            byte[] bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i]<0) {
                    bytes[i]+=256;
                }
            }
            out = new FileOutputStream(filePath);
            out.write(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (out!=null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
