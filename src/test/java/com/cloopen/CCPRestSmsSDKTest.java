package com.cloopen;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;


public class CCPRestSmsSDKTest {
	@Test
	public void smsSdkTest(){
		//�������������ַ��app.cloopen.com
		String serverIp = "192.168.182.100";
		//����˿�
		String serverPort = "4100";
		//���˺�,��½��ͨѶ��վ��,���ڿ���̨��ҳ�������������˺�ACCOUNT SID�����˺�����AUTH TOKEN
		String accountSId = "ff8081813fc65581013fc72b94880000";
		String accountToken = "4e44a775d79e422a9ee26e2966d2cb66";
		//��ʹ�ù������̨���Ѵ���Ӧ�õ�APPID
		String appId = "ff80808157e69e9c0157f58313a40001";
		CCPRestSmsSDK sdk = new CCPRestSmsSDK();
		sdk.init(serverIp, serverPort);
		sdk.setAccount(accountSId, accountToken);
		sdk.setAppId(appId);
		sdk.setBodyType(BodyType.Type_XML);
		String to = "13520007311";
		String templateId= "2241";
		String[] datas = {"��Ϊ����ʱ����\r" +
				"����˵��","555"};
		String subAppend="1234";  //��ѡ	��չ�룬��λ���� 0~9999
		String reqId="fadfafas";  //��ѡ �������Զ�����Ϣid�����֧��32λӢ�����֣�ͬ�˺���ͬһ��Ȼ���ڲ������ظ�
//		HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
		HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);
		if("000000".equals(result.get("statusCode"))){
			//�����������data������Ϣ��map��
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
		}else{
			//�쳣�������������ʹ�����Ϣ
			System.out.println("������=" + result.get("statusCode") +" ������Ϣ= "+result.get("statusMsg"));
		}

	}
}
