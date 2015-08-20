package com.ntes.interfacetest.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * 通用父类： 提供logger, client, 生成http请求, 获得返回结果
 */
public class InterfaceTest {

	public Logger logger = Logger.getLogger(this.getClass().getName());
	public DefaultHttpClient client = null;

	public enum HttpType {
		GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH
	};

	@BeforeClass
	public void init() {
		logger.info("------------------test begin----------------");
		client = new DefaultHttpClient();
	}

	@AfterClass
	public void clean() {
		client.getConnectionManager().shutdown();
		logger.info("------------------test end------------------");
	}

	/*
	 * HttpGet, HttpPost, HttpPut, HttpDelete, HttpHead, HttpOptions, HttpPatch
	 * GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH
	 */
	public HttpRequestBase generateHttpRequest(HttpType httpType, String url,
			List<NameValuePair> param, HashMap<String, String> headerData) throws UnsupportedEncodingException {

		HttpRequestBase request = null;
		switch (httpType) {
		case GET:
			request = new HttpGet(url + "?" + URLEncodedUtils.format(param, "UTF-8"));
			break;
		case POST:
			request = new HttpPost(url);
			((HttpPost) request).setEntity(new UrlEncodedFormEntity(param, "UTF-8"));
			break;
		case PUT:
			request = new HttpPut(url);
			break;
		case PATCH:
			request = new HttpPatch(url);
			break;
		case DELETE:
			request = new HttpDelete(url);
			break;
		case HEAD:
			request = new HttpHead(url);
			break;
		case OPTIONS:
			request = new HttpOptions(url);
			break;
		default:
			break;
		}

		if (headerData != null) {
			for (Entry<String, String> entry : headerData.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				request.setHeader(key, value);
			}
		}

		logger.info(request);
		return request;
	}

	public String getHttpResult(HttpType httpType, String url,List<NameValuePair> param, 
			HashMap<String, String> headerData) throws ParseException, IOException {
		
		String result = null;
		HttpRequestBase request = generateHttpRequest(httpType,url,param,headerData);		
		HttpResponse response = client.execute(request);
		logger.info("Response Status = " + response.getStatusLine());
		
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {// 处理301/302跳转
//			result = getHttpResponse(generateHttpRequest(HttpType.GET, response.getFirstHeader("Location").toString(), null, null));
			// 如果是重定向的请求，这里不做自动处理，直接返回url，供使用者在上层，指定http类型，重新调用generateHttpRequest, getHttpResponse
			result = response.getFirstHeader("Location").toString();
		} else {
			result = EntityUtils.toString(response.getEntity());
		}

		logger.info(result);

		request.abort();
		request.releaseConnection();
		return result;
	}	

}
