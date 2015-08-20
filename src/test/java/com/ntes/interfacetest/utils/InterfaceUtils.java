package com.ntes.interfacetest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.ho.yaml.Yaml;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 收集了一些通用的方法类
 *
 */
public class InterfaceUtils {
	
	public static Logger logger = Logger.getLogger(InterfaceUtils.class.getName());

	/* 解析xml文件，根据keyName找到对应的数据，并存入HashMap中 */
	public static HashMap<String, String> parseXml(String fileName, String keyName) {
		HashMap<String, String> hm = new HashMap<String, String>();
		// 创建文件对象
		File inputXml = new File(fileName);
		// 创建一个读取XML文件的对象
		SAXReader saxReader = new SAXReader();
		
		try{
			// 创建一个文档对象
			Document document = saxReader.read(inputXml);
			// 获取文件的根节点
			Element users = document.getRootElement();
			for (Iterator<?> i = users.elementIterator(); i.hasNext();) {
				Element user = (Element) i.next();
				if (keyName.equals(user.attributeValue("key_name"))) {
					for (Iterator<?> j = user.elementIterator(); j.hasNext();) {
						Element node = (Element) j.next();
						hm.put(node.getName(), node.getText());
					}
					break;
				}
			}
		}catch(DocumentException e){
			e.printStackTrace();
		}
		
		return hm;
	}
	
	/*设置代理*/
	public static DefaultHttpClient getProxyClient(DefaultHttpClient client, String ip, int port) {
		HttpHost proxy = new HttpHost(ip, port);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		return httpclient;
	}

	
	/*
	 * 校验返回结果中是否包含所有指定的字段，方法针对String/JSONArray/JSONObject，需要检查的字段放在params[]中传入
	 * 给定的jsonArray中是否每个jsonObject都包含params中的所有key，仅针对一层array套object结构，多层结构需定位到最内一层array传入
	 * 
	 * JSONObject --> JSONArray：
	 * JSONArray jsonArray = JSONArray.fromObject("[" + jsonObject.toString() + "]");
	 * String --> JSONObject：
	 * JSONObject jsonObject = JSONObject.fromObject(resultString);
	 */
	public static Boolean hasKey(JSONArray ja, String[] params) {

		Boolean isAllTagExist = true;

		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo = ja.getJSONObject(i);
                                                      
			Boolean judgeExist = true;
			for (int j = 0; j < params.length; j++) {
				judgeExist &= jo.has(params[j]);
			}

			// System.out.println(judgeExist);
			if (judgeExist == false)
				logger.error("异常数据，字段有缺失：" + jo);

			isAllTagExist &= judgeExist;
		}

		return isAllTagExist;
	}

	/*
	 * 校验返回结果中指定字段是否都不为空值，方法针对String/JSONArray/JSONObject，需要检查的字段放在params[]中传入
	 * 给定的jsonArray中是否每个jsonObject中，params所有的字段，都是非null，仅针对一层array套object结构，多层结构需定位到最内一层array传入
	 */
	public static Boolean isKeyNotNull(JSONArray ja, String[] params) {

		Boolean isAllTagNotNull = true;

		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo = ja.getJSONObject(i);

			Boolean judgeNull = true;
			for (int j = 0; j < params.length; j++) {
				judgeNull &= (!jo.getString(params[j]).isEmpty());
			}

			// System.out.println(judgeNull);
			if (judgeNull == false)
				logger.error("异常数据，字段有空：" + jo);

			isAllTagNotNull &= judgeNull;
		}

		return isAllTagNotNull;
	}
	
	public static int subStringCalc(String str, String subStr){
		int count = 0;
		int start = 0;
		while (str.indexOf(subStr, start) >=0 && start < str.length()){
			count++;
			start = str.indexOf(subStr, start) + subStr.length();
		}
		return count;
	}
	
	/**
	 * 获取重定向之后的网址信息
	 * 
	 * @see HttpClient缺省会自动处理客户端重定向
	 * @see 即访问网页A后,假设被重定向到了B网页,那么HttpClient将自动返回B网页的内容
	 * @see 若想取得B网页的地址,就需要借助HttpContext对象,HttpContext实际上是客户端用来在多次请求响应的交互中,保持状态信息的
	 * @see 我们自己也可以利用HttpContext来存放一些我们需要的信息,以便下次请求的时候能够取出这些信息来使用
	 * 
	 * 给定跳转前带参且编码好的url，返回跳转后的实际url
	 */
	public static String getRedirectUrl(String url) {	
		String result = null;
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(url);//确认该url已经自带并编码了参数
		try {
			// 将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			// 获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
			HttpHost targetHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			// 获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
			HttpUriRequest realRequest = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);		
			
			result = targetHost.toString()+realRequest.getURI().toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	/*获取随机ip地址*/
	public static String getRandomIp(){        
        //ip范围
        int[][] range = {{607649792,608174079},		//36.56.0.0-36.63.255.255
                         {1038614528,1039007743},	//61.232.0.0-61.237.255.255
                         {1783627776,1784676351},	//106.80.0.0-106.95.255.255
                         {2035023872,2035154943},	//121.76.0.0-121.77.255.255
                         {2078801920,2079064063},	//123.232.0.0-123.235.255.255
                         {-1950089216,-1948778497},	//139.196.0.0-139.215.255.255
                         {-1425539072,-1425014785},	//171.8.0.0-171.15.255.255
                         {-1236271104,-1235419137},	//182.80.0.0-182.92.255.255
                         {-770113536,-768606209},	//210.25.0.0-210.47.255.255
                         {-569376768,-564133889}, 	//222.16.0.0-222.95.255.255
        };
         
        Random rdint = new Random();
        int index = rdint.nextInt(10);
        String ip = num2ip(range[index][0]+new Random().nextInt(range[index][1]-range[index][0]));
        return ip;
    }
 
    /*将十进制转换成ip地址*/     
    public static String num2ip(int ip) {
        int [] b=new int[4] ;
        String x = "";
         
        b[0] = (int)((ip >> 24) & 0xff);
        b[1] = (int)((ip >> 16) & 0xff);
        b[2] = (int)((ip >> 8) & 0xff);
        b[3] = (int)(ip & 0xff);
        x=Integer.toString(b[0])+"."+Integer.toString(b[1])+"."+Integer.toString(b[2])+"."+Integer.toString(b[3]); 
         
        return x; 
     }
    
    /* 解析yaml文件，根据fileName、keyName找到对应的数据，并存入HashMap中 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap<String, String> parseYaml(String fileName, String keyName) throws FileNotFoundException{
    	File yamlFile = new File(fileName);        
		HashMap cash_hm = Yaml.loadType(new FileInputStream(yamlFile.getAbsolutePath()), HashMap.class);
		HashMap<String, String> res_hm = (HashMap) cash_hm.get(keyName);
        return res_hm;                
    }
	
	
}
