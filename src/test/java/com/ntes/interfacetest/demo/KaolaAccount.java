package com.ntes.interfacetest.demo;
/*
 * post请求demo
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ntes.interfacetest.utils.InterfaceTest;
import com.ntes.interfacetest.utils.InterfaceUtils;

/*
 * @project: 考拉支付信息接收接口开发
 * @description:支付信息抄送接口,接收考拉支付信息数据，更新到KaolaCharge表，改动的字段为pay_type、pay_uid、update_time。       
 * @author: hzyezi
 * @Date: 2015-5-6
 */

/*
 * 调用参数说明 orderId String Y 订单id payType int Y 支付类型，1：网易宝，2：支付宝 uid String Y
 * 支付帐号uid
 * 
 */

public class KaolaAccount extends InterfaceTest {
	
	@Test(enabled = true, dataProvider = "kaola_accountData", dataProviderClass = KaolaAccountData.class)
	public void accountActivity(String desc, String accountId, String numberId, String createTime, String fingerprint, 
			String deviceId, String clientType, String ip, int error, String exceptDeviceId) throws ParseException, IOException {

		/*从yaml配置文件中解析得到url*/
		String url = InterfaceUtils.parseYaml("constant_info.yaml", "kaolaAccount").get("url");
		
		/*设置param*/
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("accountId", accountId));
		param.add(new BasicNameValuePair("numberId", numberId));
		param.add(new BasicNameValuePair("createTime", createTime));
		param.add(new BasicNameValuePair("fingerprint", fingerprint));
		param.add(new BasicNameValuePair("deviceId", deviceId));
		param.add(new BasicNameValuePair("clientType", clientType));
		param.add(new BasicNameValuePair("ip", ip));
		
		/* 获得结果 */
		String result = getHttpResult(HttpType.POST, url, param, null);

		/* 结果处理 */
		JSONObject jsonObject = JSONObject.fromObject(result);
		logger.info("用例描述：“" + desc + "” 返回结果：" + jsonObject);
		Assert.assertEquals(jsonObject.getInt("error"), error);
		Assert.assertEquals(jsonObject.getString("deviceId"), exceptDeviceId);
		
	}

}
