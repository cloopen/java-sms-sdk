package com.cloopen;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;


public class CCPRestSmsSDKTest {
	@Test
	public void smsSdkTest(){
		//生产环境请求地址：app.cloopen.com
		String serverIp = "192.168.182.100";
		//请求端口
		String serverPort = "4100";
		//主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
		String accountSId = "ff8081813fc65581013fc72b94880000";
		String accountToken = "4e44a775d79e422a9ee26e2966d2cb66";
		//请使用管理控制台中已创建应用的APPID
		String appId = "ff80808157e69e9c0157f58313a40001";
		CCPRestSmsSDK sdk = new CCPRestSmsSDK();
		sdk.init(serverIp, serverPort);
		sdk.setAccount(accountSId, accountToken);
		sdk.setAppId(appId);
		sdk.setBodyType(BodyType.Type_XML);
		String to = "13520007311";
		String templateId= "2241";
		String[] datas = {"成为而定时发送\r" +
				"发的说法","555"};
		String subAppend="1234";  //可选	扩展码，四位数字 0~9999
		String reqId="fadfafas";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
//		HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
		HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
		}

	}
}
