package com.centricsoftware.commons.utils;

import cn.hutool.extra.ftp.Ftp;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.SocketException;
/**
 * Ftp上传下载工具类
 * 同类方法 {@link cn.hutool.extra.ftp.Ftp}
 * @author zheng.gong
 * @date 2020/4/27
 */
public class FtpUtil {

	public static void main(String[] args) throws IOException {
		//匿名登录（无需帐号密码的FTP服务器）
		Ftp ftp = new Ftp("172.0.0.1");
		//进入远程目录
		ftp.cd("/opt/upload");
		//上传本地文件
		ftp.upload("/opt/upload", cn.hutool.core.io.FileUtil.file("e:/test.jpg"));
		//下载远程文件
		ftp.download("/opt/upload", "test.jpg", cn.hutool.core.io.FileUtil.file("e:/test2.jpg"));

		//关闭连接
		ftp.close();
	}

	/**
	 * 获取FTPClient对象
	 *
	 * @param ftpHost
	 *            FTP主机服务器
	 * @param ftpPassword
	 *            FTP 登录密码
	 * @param ftpUserName
	 *            FTP登录用户名
	 * @param ftpPort
	 *            FTP端口 默认为21
	 * @return
	 */
	public static FTPClient getFTPClient(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort) {
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
			ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				System.out.println("未连接到FTP，用户名或密码错误。");
				ftpClient.disconnect();
			} else {
				System.out.println("FTP连接成功。");
			}
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("FTP的IP地址可能错误，请正确配置。");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("FTP的端口错误,请正确配置。");
		}
		return ftpClient;
	}

	/*
	 * 从FTP服务器下载文件
	 *
	 * @param ftpHost FTP IP地址
	 * 
	 * @param ftpUserName FTP 用户名
	 * 
	 * @param ftpPassword FTP用户名密码
	 * 
	 * @param ftpPort FTP端口
	 * 
	 * @param ftpPath FTP服务器中文件所在路径 格式： ftptest/aa
	 * 
	 * @param localPath 下载到本地的位置 格式：H:/download
	 * 
	 * @param fileName 文件名称
	 */
	public static String downloadFtpFile(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort,
			String ftpPath, String localPath, String fileName) {

		FTPClient ftpClient = null;

		try {
			ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
			ftpClient.setControlEncoding("UTF-8"); // 中文支持
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.changeWorkingDirectory(ftpPath);

			File localFile = new File(localPath + File.separatorChar + fileName);
			OutputStream os = new FileOutputStream(localFile);
			ftpClient.retrieveFile(fileName, os);
			os.close();
			ftpClient.logout();

		} catch (FileNotFoundException e) {
			System.out.println("没有找到" + ftpPath + "文件");
			e.printStackTrace();
		} catch (SocketException e) {
			System.out.println("连接FTP失败.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("文件读取错误。");
			e.printStackTrace();
		}

		return localPath + File.separatorChar + fileName;
	}

	/**
	 * Description: 向FTP服务器上传文件
	 * 
	 * @param ftpHost
	 *            FTP服务器hostname
	 * @param ftpUserName
	 *            账号
	 * @param ftpPassword
	 *            密码
	 * @param ftpPort
	 *            端口
	 * @param ftpPath
	 *            FTP服务器中文件所在路径 格式： ftptest/aa
	 * @param fileName
	 *            ftp文件名称
	 * @param input
	 *            文件流
	 * @return 成功返回true，否则返回false
	 */
	public static boolean uploadFile(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort,
			String ftpPath, String fileName, InputStream input) {
		boolean success = false;
		FTPClient ftpClient = null;
		try {
			int reply;
			ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
			reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				return success;
			}
			ftpClient.setControlEncoding("UTF-8"); // 中文支持
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.changeWorkingDirectory(ftpPath);

			ftpClient.storeFile(fileName, input);

			input.close();
			ftpClient.logout();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	}

	/**
	 * 根据文件前缀找出ftp服务器上的完整文件名
	 **/
	public static String getFileName(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort, String ftpPath, String prefix) {
		FTPClient ftpClient = null;
		String filename = "";
		try {
			ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
			ftpClient.setControlEncoding("UTF-8"); // 中文支持
			System.out.println("Path is:" + ftpPath);
			FTPFile[] files = ftpClient.listFiles(ftpPath);
			
            for (FTPFile file : files) {
                if (file.isFile()) {
                    if (file.getName().startsWith(prefix)) {
                        filename = file.getName();
                        break;
                    }
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return filename;
	}

	
}
