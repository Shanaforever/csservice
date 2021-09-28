package com.centricsoftware.commons.utils;

import jcifs.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** 
 * 从网络获取图片到本地
 * 同类方法{@link cn.hutool.http.HttpUtil}
 * @author Harry
 * @version 1.0 
 * @since 
 */  
public class FileUtil {  
    /** 
     * 测试 
     * @param args 
     */  
    public static void main(String[] args) {  
        String url = "http://www.baidu.com/img/baidu_sylogo1.gif";
        String dest = "D:\\test\\image";
//       String fileName = downloadImage(url,"D:\\test\\image");
//       System.out.println(fileName);
//        HttpUtil.downloadFile(url,dest);
    }  
    
    public static String downloadImage(String url,String filePath){
    	if(!"".equals(url) && url != null){
    		byte[] btImg = getImageFromNetByUrl(url);
	   		 if(null != btImg && btImg.length > 0){
	   			String fileName = url.substring(url.lastIndexOf("/")+1);
	   		    writeImageToDisk(btImg, filePath,fileName); 
	   		    return filePath+fileName;
	   		 }
    	}		
		return null;    	
    }
    /** 
     * 将图片写入到磁盘 
     * @param img 图片数据流 
     * @param fileName 文件保存时的名称 
     */  
    public static void writeImageToDisk(byte[] img, String filePath, String fileName){  
        try {  
        	File folder = new File(filePath);
        	folder.mkdirs();
            File file = new File(filePath+fileName);             
            FileOutputStream fops = new FileOutputStream(file);  
            fops.write(img);  
            fops.flush();  
            fops.close();  
            System.out.println("图片已经写入");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    
    /** 
     * 将图片写入到磁盘 
     * @param img 图片数据流 
     * @param filePath 文件保存时的路径
     */  
    public static void writeImageToDisk(byte[] img, String filePath){  
        try {  
            File file = new File(filePath);             
            FileOutputStream fops = new FileOutputStream(file);  
            fops.write(img);  
            fops.flush();  
            fops.close();  
            System.out.println("图片已经写入");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    /** 
     * 根据地址获得数据的字节流 
     * @param strUrl 网络连接地址 
     * @return 
     */  
    public static byte[] getImageFromNetByUrl(String strUrl){  
        try {  
            URL url = new URL(strUrl);  
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
            conn.setRequestMethod("GET");  
            conn.setConnectTimeout(5 * 1000);  
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据  
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据  
            return btImg;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
    /** 
     * 从输入流中获取数据 
     * @param inStream 输入流 
     * @return 
     * @throws Exception 
     */  
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    } 
    
    public static void write2File(String filename,String str){
    	try {
            String strEncode = Base64.encode(str.getBytes());
    		byte[] bytes = strEncode.getBytes();
            File file = new File(filename);             
            FileOutputStream fops = new FileOutputStream(file);  
            fops.write(bytes);  
            fops.flush();  
            fops.close();   
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
    public static String byte2hex(byte[] b) // 二进制转字符串  
    {  
       StringBuffer sb = new StringBuffer();  
       String stmp = "";  
       for (int n = 0; n < b.length; n++) {  
        stmp = Integer.toHexString(b[n] & 0XFF);  
        if (stmp.length() == 1){  
            sb.append("0" + stmp);  
        }else{  
            sb.append(stmp);  
        }  
          
       }  
       return sb.toString();  
    }  
      
    public static byte[] hex2byte(String str) { // 字符串转二进制  
        if (str == null)  
         return null;  
        str = str.trim();  
        int len = str.length();  
        if (len == 0 || len % 2 == 1)  
         return null;  
        byte[] b = new byte[len / 2];  
        try {  
         for (int i = 0; i < str.length(); i += 2) {  
          b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();  
         }  
         return b;  
        } catch (Exception e) {  
        	e.printStackTrace();
         return null;  
        }  
     } 
} 
