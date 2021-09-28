package com.centricsoftware.commons.utils;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.centricsoftware.pi.tools.http.ConnectionInfo;
import com.centricsoftware.pi.tools.http.SunsCookieHandler;
import com.centricsoftware.pi.tools.http.WriterToUTF8;
import com.centricsoftware.pi.tools.util.C8ResponseXML;
import com.centricsoftware.pi.tools.xml.Document;
import com.centricsoftware.pi.tools.xml.Element;
import com.centricsoftware.pi.tools.xml.XML;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

public class C8Communication {
  static {
    CookieHandler.setDefault((CookieHandler)new SunsCookieHandler());
  }

    public static class Param {
        boolean file;
        String name;
        String value;

        public Param(boolean f, String n, String v) {
            this.file = f;
            this.name = n;
            this.value = v;
        }

        public String toString() {
            return this.name + "=" + this.value;
        }
    }
  
  public static void simpleLogin(ConnectionInfo info) throws Exception {
    LinkedList<Param> parameters = new LinkedList<>();
    parameters.add(new Param(false, "Module", "DataSource"));
    parameters.add(new Param(false, "Operation", "SimpleLogin"));
    parameters.add(new Param(false, "LoginID", info.getLoginId()));
    parameters.add(new Param(false, "Password", info.getPassword()));
    post(info, parameters, true);
  }
  
  public static void simpleLogout(ConnectionInfo info) throws Exception {
    LinkedList<Param> parameters = new LinkedList<>();
    parameters.add(new Param(false, "Module", "DataSource"));
    parameters.add(new Param(false, "Operation", "SimpleLogout"));
    post(info, parameters, false);
  }
  
  public static List<String> getCNLByType(ConnectionInfo info, String type, int count) throws Exception {
    LinkedList<Param> parameters = new LinkedList<>();
    parameters.add(new Param(false, "Module", "Search"));
    parameters.add(new Param(false, "Operation", "SimpleAndQuery"));
    parameters.add(new Param(false, "Qry.Limit.End", "" + count));
    parameters.add(new Param(false, "Fmt.Node.Info", "Min"));
    parameters.add(new Param(false, "Fmt.Attr.Inc", "No"));
    parameters.add(new Param(false, "Qry.Node.Type", "EQ" + type));
    Document response = post(info, parameters, true);
    return C8ResponseXML.resultNodeCNLs(response);
  }
  
  public static List<String> getCNLByType(ConnectionInfo info, String type, String name, int count) throws Exception {
    LinkedList<Param> parameters = new LinkedList<>();
    parameters.add(new Param(false, "Module", "Search"));
    parameters.add(new Param(false, "Operation", "SimpleAndQuery"));
    parameters.add(new Param(false, "Qry.Limit.End", "" + count));
    parameters.add(new Param(false, "Fmt.Node.Info", "Min"));
    parameters.add(new Param(false, "Fmt.Attr.Inc", "No"));
    parameters.add(new Param(false, "Qry.Node.Type", "EQ" + type));
    parameters.add(new Param(false, "Qry.Node.Name", "EQ" + name));
    Document response = post(info, parameters, true);
    return C8ResponseXML.resultNodeCNLs(response);
  }
  
  public static Document queryNode(ConnectionInfo info, String cnl) throws Exception {
    LinkedList<Param> parameters = new LinkedList<>();
    parameters.add(new Param(false, "Module", "Search"));
    parameters.add(new Param(false, "Operation", "QueryByURL"));
    parameters.add(new Param(false, "Qry.URL", cnl));
    return post(info, parameters, true);
  }
  
  public static JSONObject JSqueryNode(ConnectionInfo info, String cnl) throws Exception {
	    LinkedList<Param> parameters = new LinkedList<>();
	    parameters.add(new Param(false, "Module", "Search"));
	    parameters.add(new Param(false, "Operation", "QueryByURL"));
	    parameters.add(new Param(false, "Qry.URL", cnl));
	    parameters.add(new Param(false, "OutputJSON", "2"));
	    return JSpost(info, parameters, true);
	  }
  
  public static void processScript(ConnectionInfo info, Document doc) throws Exception {
    for (Element request : doc.getRootElement().elements("Request"))
      processRequest(info, request); 
  }
  
  public static void processRequest(ConnectionInfo info, Element params) throws Exception {
    LinkedList<Param> parameters = new LinkedList<>();
    parameters.add(new Param(false, "Module", params.attributeValue("Module")));
    parameters.add(new Param(false, "Operation", params.attributeValue("Operation")));
    boolean parse = params.attributeBooleanValue("Parse", true);
    String id = params.attributeValue("Parse");
    String origTime = params.attributeValue("OrigTime");
    for (Element param : params.elements("Param")) {
      boolean file = params.attributeBooleanValue("File", false);
      parameters.add(new Param(file, param.attributeValue("Name"), param.attributeValue("Value")));
    } 
    if (id == null)
      System.out.println("Processing request: " + parameters); 
    long time = System.nanoTime();
    post(info, parameters, parse);
    time = (System.nanoTime() - time) / 1000000L;
    if (id != null)
      if (origTime == null) {
        System.out.println("Request Id: " + id + " Now: " + time + " ms.");
      } else {
        System.out.println("Request Id: " + id + " Was: " + origTime + " ms. Now: " + time + " ms.");
      }  
  }
  
  public static Document post(ConnectionInfo info, List<Param> parameters, boolean parse) throws Exception {
    InputStream in = null;
    try {
      in = post(info, parameters);
      if (!parse) {
        while (in.read() != -1);
        return null;
      } 
      Document doc = XML.fromStream(in);
      Element response = doc.getRootElement();
      Element status = response.element("Status");
      if (status == null)
        throw new RuntimeException("Wrong Format for the XML Responce. Missing (<Status>)"); 
      String sStatus = status.getText();
      if (sStatus.equals("Successful"))
        return doc; 
      Element error = response.element("Error");
      if (error == null)
        throw new RuntimeException("Wrong Format for the XML Responce. Missing (<Error>)"); 
      String sError = error.getText();
      Element adminError = response.element("ErrorAdmin");
      if (adminError == null)
        throw new RuntimeException("Wrong Format for the XML Responce. Missing (<ErrorAdmin>)"); 
      String sAdminError = adminError.getText();
      Element exception = response.element("Exception");
      if (exception == null)
        throw new RuntimeException("Wrong Format for the XML Responce. Missing (<Exception>)"); 
      String sException = exception.getText();
      throw new Exception("Server side Error: " + sError + ".\nServer side Admin Error: " + sAdminError + ".\nServer side Exception: " + sException);
    } finally {
      if (in != null)
        in.close(); 
    } 
  }
  
  /*
   * 新增一个JSPost方法，输入的参数不变，但是返回结果是JSONObject Ricardo.lu 20200222
  */
  public static JSONObject JSpost(ConnectionInfo info, List<Param> parameters, boolean parse) throws Exception {
	    InputStream in = null ;
	    JSONObject js = new JSONObject();
	    try {
	      in = post(info, parameters);
	      if (!parse) {
	        while (in.read() != -1);
	        return null;
	      } 
	      BufferedReader StreamReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
	      String Var1 = new String();
	      String InputStr;
	      while((InputStr = StreamReader.readLine()) != null) {
	    	  Var1 += InputStr;
	      };
	      //判断是否有返回值，如果不为空，则保存成JSONobject
	      if(!Var1.equalsIgnoreCase("")) {
	    	  //对返回值进行Unicode解码，使/uade 类似的中文字符能正常显示
	    	  String b =  ascii2Native(Var1);
              js = JSONUtil.parseObj(b);
//	 	      js = JSON.parseObject(b);
	      }
	      Boolean Status = js.containsKey("Status");
	      if (!Status) {
	    	  throw new RuntimeException("Wrong Format for the XML Responce. Missing (<Status>)");
	    	  } else {
//	    		  String sStatus = js.getString("Status");
	    		  String sStatus = js.getStr("Status");
	    		  if (sStatus.equals("Successful")) {
	    			  return js;
	    			}else {
	    				Boolean error = js.containsKey("Error");
	    				if(!error) {
	    					throw new RuntimeException("Wrong Format for the XML Responce. Missing (<Error>)");
	    	  		}else {
//	    	  			String sError = js.getString("Error");
	    	  			String sError = js.getStr("Error");
	    	  			Boolean adminError = js.containsKey("ErrorAdmin");
	    	  			if(!adminError) {
	    	  				throw new RuntimeException("Wrong Format for the XML Responce. Missing (<ErrorAdmin>)");
	    	  			}else {
//	    	  				String sAdminError = js.getString("ErrorAdmin");
	    	  				String sAdminError = js.getStr("ErrorAdmin");
	    	  				Boolean exception = js.containsKey("Exception");
	    	  				if(!exception) {
	    	  					throw new RuntimeException("Wrong Format for the XML Responce. Missing (<Exception>)");
	    	  				}else {
//	    	  					String sException = js.getString("Exception");
	    	  					String sException = js.getStr("Exception");
	    	  					throw new Exception("Server side Error: " + sError + ".\nServer side Admin Error: " + sAdminError + ".\nServer side Exception: " + sException);
	    	  				}
	    	  			}
	    	  		}
	    	  	}
	    	  }
	    }finally {
			if(in != null) {
				in.close();
			}
		}
	}
  
  public static InputStream get(ConnectionInfo info) throws Exception {
    HttpURLConnection conn = (HttpURLConnection)info.getRequestURL().openConnection();
    conn.setRequestMethod("GET");
    return conn.getInputStream();
  }
  
  public static InputStream post(ConnectionInfo info, List<Param> parameters) throws Exception {
    WriterToUTF8 writerToUTF8 = null;
    Writer out = null;
    try {
      String BOUNDARY = "C6EE5092158B4206939DC028F7CEC00F";
      HttpURLConnection conn = (HttpURLConnection)info.getRequestURL().openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.addRequestProperty("Content-Type", "multipart/form-data; charset=UTF-8; boundary=C6EE5092158B4206939DC028F7CEC00F");
      OutputStream outStream = conn.getOutputStream();
      writerToUTF8 = new WriterToUTF8(outStream);
      for (Param p : parameters) {
        writerToUTF8.write("--C6EE5092158B4206939DC028F7CEC00F\r\n");
        if (p.file) {
          writerToUTF8.write("Content-disposition: form-data; name=\"" + p.name + "\"; filename=\"" + p.value + "\"\r\n");
          writerToUTF8.write("Content-Transfer-Encoding: binary\r\nContent-Type: application/octet-stream\r\n\r\n");
          File f = new File(p.value);
          if (!f.exists() || !f.isFile() || !f.canRead())
            throw new Exception("Failed to find the file to upload: " + p.value); 
          if (!f.canRead())
            throw new Exception("The request file: " + p.value + " is locked."); 
          InputStream inFile = new FileInputStream(f);
          byte[] buffer = new byte[2048];
          while (true) {
            int n = inFile.read(buffer);
            if (n > 0) {
              outStream.write(buffer, 0, n);
              continue;
            } 
            if (n < 0)
              break; 
          } 
          inFile.close();
        } else {
          writerToUTF8.write("Content-disposition: form-data; name=\"" + p.name + "\"\r\n\r\n");
          writerToUTF8.write(p.value);
        } 
        writerToUTF8.write("\r\n");
      } 
      writerToUTF8.write("--C6EE5092158B4206939DC028F7CEC00F--");
      writerToUTF8.flush();
      InputStream in = conn.getInputStream();
      CookieHandler.getDefault().put(info.getRequestURL().toURI(), conn.getHeaderFields());
      return in;
    } finally {
      if (writerToUTF8 != null)
        writerToUTF8.close(); 
    } 
  }
  

  		//当返回JSON的值时，unicode转为本地
		public static String ascii2Native(String str) {
		StringBuilder sb = new StringBuilder();
		int begin = 0;
		int index = str.indexOf("\\u");
		while (index != -1) {
			sb.append(str.substring(begin, index));
			sb.append(ascii2Char(str.substring(index, index + 6)));
			begin = index + 6;
			index = str.indexOf("\\u", begin);
		}
		sb.append(str.substring(begin));
		return sb.toString();
	}

		private static char ascii2Char(String str) {
		if (str.length() != 6) {
			throw new IllegalArgumentException(
					"Ascii string of a native character must be 6 character.");
		}
		if (!"\\u".equals(str.substring(0, 2))) {
			throw new IllegalArgumentException(
					"Ascii string of a native character must start with \"\\u\".");
		}
		String tmp = str.substring(2, 4);
		int code = Integer.parseInt(tmp, 16) << 8;
		tmp = str.substring(4, 6);
		code += Integer.parseInt(tmp, 16);
		return (char) code;
	}

}
