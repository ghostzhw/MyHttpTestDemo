package com.ntes.interfacetest.demo;
/*
 * get请求demo
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.DocumentException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ntes.interfacetest.utils.InterfaceTest;
import com.ntes.interfacetest.utils.InterfaceUtils;

public class BrandTest extends InterfaceTest {

	@Test(enabled = true, dataProvider = "brandTest")
	public void test(String account, String device_id, String from,
			String limit, String ver, String ret) throws ParseException, IOException, DocumentException {

		// (1)读配置文件得到url
//		String url = InterfaceUtils.parseXml("constant_info.xml", "BrandTest").get("url");
		String url = InterfaceUtils.parseYaml("constant_info.yaml", "BrandTest").get("url");

		// (2)设置param
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("account", account));
		param.add(new BasicNameValuePair("divice_id", device_id));
		param.add(new BasicNameValuePair("from", from));
		param.add(new BasicNameValuePair("limit", limit));
		param.add(new BasicNameValuePair("ver", ver));

		// (3)获得result
		// 如有需要，设置代理, 配置好本机地址和端口，否则连接失败————设置代理，实际就是配置client
//		client = InterfaceUtils.getProxyClient(client, "172.31.135.82", 8887);
//		String result = getHttpResult(HttpType.GET, url, param, InterfaceUtils.parseXml("constant_info.xml", "header_data"));
		String result = getHttpResult(HttpType.GET, url, param, InterfaceUtils.parseYaml("constant_info.yaml", "header_data"));

		// (4)逻辑验证
		// 视具体情形将结果转为JSONObject或JSONArray，进行后续逻辑验证
		JSONObject jsonObject = JSONObject.fromObject(result);
		Assert.assertEquals(jsonObject.getString("ret"), ret);

	}

	@DataProvider(name = "brandTest")
	public static Object[][] brandInterface() {
		return new Object[][] {
				// 正常用例
				{ "evansjun@gmail.com", "", "ios", "5", "", "OK" },
				{ "wyydhxy@163.com", "", "android", "10", "", "OK" },
				{ "178d870ae1c59a2f384bff8f65121726@tencent.163.com", "", "pc",	"10", "", "OK" },
				{ "", "", "", "", "", "OK" },

//				// 用户名空/非法/不存在
//				{ "$^*", "", "", "", "", "OK" },
//				{ "11111@2222.com", "", "", "", "", "OK" },
//				// deviceid空/非法，from空/非法/不存在
//				{ "", "$^*", "", "", "", "OK" },
//				{ "", "", "$^*", "", "", "OK" },
//				{ "", "", "11111", "", "", "OK" },
//				// limit为空/过大过小/非法
//				{ "", "", "", "-1", "", "OK" },
//				{ "", "", "", "9999999999", "", "OK" },
//				{ "", "", "", "$^*", "", "OK" },
//				// ver空/非法
//				{ "", "", "", "", "$^*", "OK" }, 
		};
	}
	
}
