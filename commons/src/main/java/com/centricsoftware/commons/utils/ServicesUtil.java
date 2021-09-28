/**
* @author GHUANG
* @version 2016年3月28日 下午5:48:18
*
*/
package com.centricsoftware.commons.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;

/**
 * 发送http请求工具类
 * 同类方法{@link cn.hutool.http.HttpUtil}
 * @author zheng.gong
 * @date 2020/4/27
 */
public class ServicesUtil {

    public static String HtmLog = "ConnectHtmLog";

    static HashMap<String, String> initmap = null;
    static {
        initmap = new HashMap();
        initmap.put("0", "");
        initmap.put("1004", "Exception:服务器网络异常，请稍后重试");
        initmap.put("1001", "Exception:请求参数错误");
        initmap.put("1002", "Exception:该用户不存在");
        initmap.put("1003", "Exception:该sign签名不合法");
        initmap.put("1005", "Exception:SCM错误!");
        initmap.put("1006", "Exception:SCM错误!");
        initmap.put("1007", "Exception:网络连接错误!");
        initmap.put("1008", "Exception:SCM错误!");
        initmap.put("1009", "Exception:SCM错误!");
    }

    /**
     * REST
     *
     * @param targetURL
     * @param url
     * @param typeId
     * @return
     * @author GHUANG
     * @version 2019年4月20日 上午7:26:42
     */
    public static String getData(String targetURL, String url, String typeId) {
        //
        String output = "";
        try {

            URL restServiceURL = new URL(targetURL);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConnection.setRequestProperty("Charset", "UTF-8");
            httpConnection.setRequestProperty("Accept", "application/json");

            if (httpConnection.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                        + httpConnection.getResponseCode());
            }

            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            String line;
            while ((line = responseBuffer.readLine()) != null) {
                output = output + line;
            }
            httpConnection.disconnect();
            System.out.println("get Server Success:\n" + output);
            if (output.indexOf("{") > 0) {
                output = output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1);
            }
        } catch (Exception e) {
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int i = 0; i < stackArray.length; i++) {
                StackTraceElement element = stackArray[i];
            }

            e.printStackTrace();
            output = "Exception  when PLM try to connect K3 ";

        }
        return output;
    }

//    static void callWebService(String param, String url, String user, String psd) throws AxisFault {
//        System.out.println("call begin.....");
//        RPCServiceClient serviceClient = new RPCServiceClient();
//        Options options = serviceClient.getOptions();
//        EndpointReference targetEPR = new EndpointReference(url);
//        options.setTo(targetEPR);
//        options.setManageSession(true);
//        options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
//        options.setTimeOutInMilliSeconds(600000L);
//        options.setUserName("connuser");
//        options.setPassword("a87654321");
//        // String response = "application/json ";
//        Object[] opAddEntryArgs = new Object[] { param };
//        Class[] classes = new Class[] { String.class };
//        QName opAddEntry = new QName("http://ws.apache.org/axis2", "receive");
//        System.out.println("result=" + serviceClient.invokeBlocking(opAddEntry, opAddEntryArgs, classes)[0]);
//        System.out.println("call end...");
//    }

    /**
     * REST支持
     *
     * @param targetURL
     * @param jsonarray
     * @param typeId
     * @return
     * @author GHUANG
     * @version 2019年4月20日 上午7:26:30
     */

    public static String postData(String targetURL, String jsonarray, String user, String password) {
        String remsg = "";
        HttpURLConnection httpConnection = null;
        try {
            System.out.println(targetURL);
            URL targetUrl = new URL(targetURL);
            System.out.println(user + "----" + password);
            httpConnection = (HttpURLConnection) targetUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setUseCaches(false);
            // 1.4的问题，必须有SOAPAction
            httpConnection.setRequestProperty("SOAPAction", "application/soap+xml; charset=utf-8");
            httpConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConnection.setRequestProperty("Charset", "UTF-8");
            byte[] requestStringBytes = jsonarray.getBytes("UTF-8");
            httpConnection.setRequestProperty("Content-length", "" + requestStringBytes.length);
            String users = user + ":" + password;
            httpConnection.addRequestProperty("Authorization",
                    "Basic " + new String(Base64.encodeBase64(users.getBytes())));
            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(requestStringBytes);
            outputStream.flush();
            outputStream.close();
            if (httpConnection.getResponseCode() != 200) {
                InputStream err = httpConnection.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(err, "UTF-8"));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpConnection.getResponseCode() + ",exception info" + sb.toString());
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));
//
//            String line;
//            System.out.println("Output from Server:\n");
//            while ((line = responseBuffer.readLine()) != null) {
//                System.out.println(line);
//            }
            responseBuffer.close();
            httpConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            remsg = e.getMessage();
            if (remsg.contains("refused")) {
                remsg = "对方接口拒绝连接请求!";
            }
            remsg = "Exception:" + remsg;

        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return remsg;
    }

    /**
     * REST支持SOAP，xml格式param
     *
     * @param targetURL
     * @param param
     * @param user
     * @param password
     * @return
     * @author GHUANG
     * @version 2019年4月20日 上午7:26:30
     */
    public static String putData(String targetURL, String param, String user, String password)
            throws Exception {
        String result = "";
        HttpURLConnection con = null;
        try {
            long t1 = System.currentTimeMillis(); // 执行开始时间记录
            URL url = new URL(targetURL);
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setConnectTimeout(30 * 60 * 1000);
            con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            // con.setRequestProperty("Content-Type", "multipart/form-data; charset=UTF-8; ");
            con.setRequestProperty("accept", "*/*");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setDoInput(true);
            con.setDoOutput(true);
            // con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // con.setRequestProperty("Charset", "UTF-8");
            // con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            con.setRequestMethod("POST");
            con.setUseCaches(false);
            String users = user + ":" + password;
            con.addRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(users.getBytes())));
            con.connect();
            // param = URLEncoder.encode(param, "utf-8");
            OutputStream os = con.getOutputStream();
            OutputStreamWriter out = new OutputStreamWriter(os, "utf-8");
            // String utf8String = new String(param.getBytes(), "utf-8");
            // NodeUtil.outInfo("-----" + con.getContentEncoding() + "", HtmLog);
            out.write(param);
            out.flush();
            out.close();
            System.out.println(con.getResponseCode());
            if (con.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));

                br.close();
            } else {
                InputStream err = con.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(err, "utf-8"));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                throw new RuntimeException("Failed : HTTP error code : "
                        + con.getResponseCode() + ",exception info" + sb.toString());
            }
            con.disconnect();

        } catch (Exception e) {
            result = e.getMessage();

        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return result;
    }

    /**
     * 生成sign
     *
     * @param appkey
     * @param secret
     * @param sign_method
     * @return
     */
    public static String getSign(String epid, String appkey, String secret, String timestamp, String jsonStr) {
        if (jsonStr.length() == 0) {
            return "";
        }
        String paramString = epid + appkey + secret + timestamp + jsonStr;
        String sign = "";
        sign = byte2hex(encryptMD5(paramString, "UTF-8"));
        return sign;
    }

    /**
     * MD5加密
     *
     * @param data
     *            字符串
     *
     * @param format
     *            数据编码方式，如：UTF-8、GBK
     * @return byte[]字节数组
     * @throws IOException
     */
    public static byte[] encryptMD5(String data, String format) {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes(format));
            bytes = md.digest();
        } catch (GeneralSecurityException gse) {
            gse.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();

        for (int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString().toLowerCase();
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        // freezeMaterial();
        System.out.println("---" + getData("http://192.168.37.133/plmservice/services/ESHub/pushChangeData?url=111" +
                "&attrname=222&attrvalue=22211&flag=y", "", ""));
    }

}
