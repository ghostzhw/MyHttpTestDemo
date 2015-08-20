package com.ntes.interfacetest.demo;

import org.testng.annotations.DataProvider;

import com.ntes.interfacetest.utils.InterfaceUtils;

/**
 * 调用参数说明
 * accountId	string	Y	用户帐号id
 * numberId		long	Y	用户数字id
 * createTime	long	Y	创建时间
 * fingerprint	string	Y	指纹信息
 * deviceId		string	N	设备id
 * clientType	int		Y	客户端类型(订单来源)10:web端;20:wap端;31:ios端;32:android端
 * ip			string	N	ip
 * 
 ************************
 * error		int		Y	接口调用结果，0:正常，其他值：调用出错，出错信息见msg字段
 * deviceId		String 	Y	设备id
 * 
 */

public class KaolaAccountData {
	@DataProvider(name = "kaola_accountData")
	public static Object[][] kaolaCheckNameData(){
		String deviceId_31_1="{\"id_ver\":\"1.1.0\",\"rk\":\"o/zX8xOuZLeUYSZHHBgOQGTh35koF2PrZ SO4EhRvgrhIxLZEwSTbJx1n8/lKxN KLuGrLXMgGDtry9lG2G94j1Zs Ky4hNLsCmBwdpRDspeY68ln2RC9bahVg1BFxDH60GXDVASEsEpx6f3WAWEao5BiCrQCDZfT9GIEbclWUw=\",\"rdata\":\"fNXM4 0F0BVmSjwl5ao8k1CMQSw2RNe7yh2MZSX8ppwWJfxC7BLuRt7Qu0Ii7BBWUKA5q6ntxGJfBgpJLoSJ6P2H/KjQbSxToDz1V6 yNaxJqNi1IkjUwtrxiQj4dW7bp9vS VC5aecNO5u5uixttS1tBBANna93cu2JAZQZr 8vtgNH52mpgc7z/oQTgbZ01qFC3Jr6trsH1aAMq3w75DalUmiB7FMYd7NcWgKPgsY=\",\"datatype\":\"aimt_datas\"}";
		
		Object[][] a = new Object[5][10];
		for(int i=0; i<5; i++){
			a[i][0]="";
			a[i][1]="oo6x529";
			a[i][2]="837830";
			a[i][3]="8861253749899";
			a[i][4]="dhuas";
			a[i][5]=deviceId_31_1;
			a[i][6]="31";
			a[i][7]=InterfaceUtils.getRandomIp();
			a[i][8]=0;
			a[i][9]="b5966f45b03ceb72c8063bf731b4c06bc0ea5d7b";
								
		}
		
		return a;
				
	}

}
