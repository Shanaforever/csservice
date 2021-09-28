package com.centricsoftware.commons.utils;

import com.centricsoftware.config.entity.CsProperties;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import java.io.*;

/**
 * 向共享盘上传递图片信息
 * 同类方法 {@link cn.hutool.extra.ftp.Ftp}
 * @author Harry
 *
 */
public class SMBFileUtil {
	
	public String fName = "";
	public String fPassword = "";
	public String fHost = "";
	public String fFolder = "";
	
	public SMBFileUtil(String userName, String password, String host,String folder){
		this.fName = userName;
		this.fPassword = password;
		this.fHost = host;
		this.fFolder = folder;
	}
	
	public SMBFileUtil(){
		CsProperties properties = NodeUtil.getProperties();
		String userName = properties.getValue("cs.imageserver.username");
		String password  = properties.getValue("cs.imageserver.password");
		String host = properties.getValue("cs.imageserver.host");
		String folder = properties.getValue("cs.imageserver.folder");
		
		this.fName = userName;
		this.fPassword = password;
		this.fHost = host;
		this.fFolder = folder;
	}
	
	public String uploadFile(File localFile){
		InputStream in = null;  
		OutputStream out = null; 
		String filepath = "";
		try {  
		    //获取图片  
		   
		    String remotePhotoUrl = "smb://"+this.fHost+"/"+this.fFolder; //存放图片的共享目录  
		    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",this.fName,this.fPassword);
		    //SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS_");  
		    SmbFile remoteFile = new SmbFile(remotePhotoUrl + "/" +  localFile.getName(),auth);  
		    remoteFile.connect(); //尝试连接  
		    
			//如果远程可以连接，那就判断先父节点目录是否存在，不存在就创建
			SmbFile parentDir = new SmbFile(remoteFile.getParent(),auth);
			if (!parentDir.exists())
			{
				parentDir.mkdirs();
			}
			
			filepath = "http://"+this.fHost+"/"+this.fFolder.replace("eeka", "")+"/"+localFile.getName();
		    in = new BufferedInputStream(new FileInputStream(localFile));  
		    out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));  
		  
		    byte[] buffer = new byte[4096];  
		    int len = 0; //读取长度  
		    while ((len = in.read(buffer, 0, buffer.length)) != -1) {  
		        out.write(buffer, 0, len);  
		    }  
		    out.flush(); //刷新缓冲的输出流  
		}  
		catch (Exception e) {  
		    String msg = "发生错误：" + e.getLocalizedMessage();  
		    System.out.println(msg);  
		}  
		finally {  
		    try {  
		        if(out != null) {  
		            out.close();  
		        }  
		        if(in != null) {  
		            in.close();  
		        }  
		    }  
		    catch (Exception e) {}  
		} 
		return filepath;
	}
	
	public static void uploadFile(File localFile,String userName, String password, String host,String folder){
		InputStream in = null;  
		OutputStream out = null; 
		try {  
		    //获取图片  
		   
		    String remotePhotoUrl = "smb://"+host+"/"+folder; //存放图片的共享目录  
		    //SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS_");  
		    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",userName,password);
		    SmbFile remoteFile = new SmbFile(remotePhotoUrl + "/" + localFile.getName(),auth);  
		    remoteFile.connect(); //尝试连接  
			
			//如果远程可以连接，那就判断先父节点目录是否存在，不存在就创建
			SmbFile parentDir = new SmbFile(remoteFile.getParent(),auth);
			if (!parentDir.exists())
			{
				parentDir.mkdirs();
			}

		    System.out.println(remotePhotoUrl+"=connected");
		    in = new BufferedInputStream(new FileInputStream(localFile));  
		    out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));  
		  
		    byte[] buffer = new byte[4096];  
		    int len = 0; //读取长度  
		    while ((len = in.read(buffer, 0, buffer.length)) != -1) {  
		        out.write(buffer, 0, len);  
		    }  
		    out.flush(); //刷新缓冲的输出流  
		}  
		catch (Exception e) {  
			e.printStackTrace();
		    String msg = "发生错误：" + e.getLocalizedMessage();  
		    System.out.println(msg);  
		}  
		finally {  
		    try {  
		        if(out != null) {  
		            out.close();  
		        }  
		        if(in != null) {  
		            in.close();  
		        }  
		    }  
		    catch (Exception e) {}  
		} 
	}
	
	/**
	 * 下载文件
	 * @return
	 */
	public byte[] downloadFile(String fileName){
		InputStream in = null ;  
		ByteArrayOutputStream out = null ;  
		try {  
		    //创建远程文件对象  
			String remotePhotoUrl = "smb://"+this.fHost+"/"+this.fFolder+"/"+fileName;  
			 NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",this.fName,this.fPassword);
		    SmbFile remoteFile = new SmbFile(remotePhotoUrl,auth);  
		    remoteFile.connect(); //尝试连接  
			
			//如果远程可以连接，那就判断先父节点目录是否存在，不存在就创建
			SmbFile parentDir = new SmbFile(remoteFile.getParent(),auth);
			if (!parentDir.exists())
			{
				parentDir.mkdirs();
			}
			
			
		    //创建文件流  
		    in = new BufferedInputStream(new SmbFileInputStream(remoteFile));  
		    out = new ByteArrayOutputStream((int)remoteFile.length());  
		    //读取文件内容  
		    byte[] buffer = new byte[4096];  
		    int len = 0; //读取长度  
		    while ((len = in.read(buffer, 0, buffer.length)) != - 1) {  
		        out.write(buffer, 0, len);  
		    }  
		  
		    out.flush(); //刷新缓冲的输出流  
		    return out.toByteArray();  
		}  
		catch (Exception e) {  
		    String msg = "下载远程文件出错：" + e.getLocalizedMessage();  
		    System.out.println(msg);  
		}  
		finally {  
		    try {  
		        if(out != null) {  
		            out.close();  
		        }  
		        if(in != null) {  
		            in.close();  
		        }  
		    }  
		    catch (Exception e) {}  
		}  
		return null; 
	}
	
	public static void main(String[] args) {
		uploadFile(new File("D:\\C8\\C8Test.model.xml"), "eeka", "Gst#2017", "10.7.121.10", "eeka/centric/");
	}
}
