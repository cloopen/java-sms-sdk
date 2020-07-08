/*
 *  Copyright (c) 2014 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.cloopen.rest.sdk;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.cloopen.rest.sdk.utils.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CCPRestSmsSDK {
	private final Log log = LogFactory.getLog(CCPRestSmsSDK.class);
	private static final String TemplateSMS = "SMS/TemplateSMS";
	private static final String AcountType = "Accounts";

	private String SERVER_IP;
	private String SERVER_PORT;
	private String ACCOUNT_SID;
	private String ACCOUNT_TOKEN;
	private String App_ID;
	private BodyType BODY_TYPE = BodyType.Type_JSON;
	private Boolean USE_SSL = true;
	public void setBodyType(BodyType bodyType){
		BODY_TYPE = bodyType;
	}
	/**
	 * 初始化服务地址和端口
	 * 
	 * @param serverIP
	 *            必选参数 服务器地址
	 * @param serverPort
	 *            必选参数 服务器端口
	 */
	public void init(String serverIP, String serverPort) {
		init(serverIP,serverPort,true);
	}
	public void init(String serverIP, String serverPort, Boolean useSSL) {
		if (StringUtils.isEmpty(serverIP) || StringUtils.isEmpty(serverPort)) {
			log.error("初始化异常:serverIP或serverPort为空");
			throw new IllegalArgumentException("必选参数:" + (StringUtils.isEmpty(serverIP) ? " 服务器地址 " : "") + (StringUtils.isEmpty(serverPort) ? " 服务器端口 " : "") + "为空");
		}
		SERVER_IP = serverIP;
		SERVER_PORT = serverPort;
		USE_SSL = useSSL;
	}
	/**
	 * 初始化主帐号信息
	 * 
	 * @param accountSid
	 *            必选参数 主帐号名称
	 * @param accountToken
	 *            必选参数 主帐号令牌
	 */
	public void setAccount(String accountSid, String accountToken) {
		if (StringUtils.isEmpty(accountSid) || StringUtils.isEmpty(accountToken)) {
			log.error("初始化异常:accountSid或accountToken为空");
			throw new IllegalArgumentException("必选参数:" + (StringUtils.isEmpty(accountSid) ? " 主帐号名称" : "") + (StringUtils.isEmpty(accountToken) ? " 主帐号令牌 " : "") + "为空");
		}
		ACCOUNT_SID = accountSid;
		ACCOUNT_TOKEN = accountToken;
	}


	/**
	 * 初始化应用Id
	 * 
	 * @param appId
	 *            必选参数 应用Id
	 */
	public void setAppId(String appId) {
		if (StringUtils.isEmpty(appId)) {
			log.error("初始化异常:appId为空");
			throw new IllegalArgumentException("必选参数: 应用Id 为空");
		}
		App_ID = appId;
	}

	/**
	 * 发送短信模板请求
	 * 
	 * @param to
	 *            必选参数 短信接收端手机号码集合，用英文逗号分开，每批发送的手机号数量不得超过100个
	 * @param templateId
	 *            必选参数 模板Id
	 * @param datas
	 *            可选参数 内容数据，用于替换模板中{序号}
	 * @return
	 */
	public HashMap<String, Object> sendTemplateSMS(String to, String templateId, String[] datas) {
		return send(to,templateId,datas,null,null);
	}
	/**
	 * 发送短信模板请求
	 *
	 * @param to
	 *            必选参数 短信接收端手机号码集合，用英文逗号分开，每批发送的手机号数量不得超过100个
	 * @param templateId
	 *            必选参数 模板Id
	 * @param datas
	 *            可选参数 内容数据，用于替换模板中{序号}
	 * @param subAppend 可选参数	扩展码，四位数字 0~9999
	 * @param reqId 可选参数	自定义消息id，最大支持32位，同账号下同一自然天内不允许重复
	 *
	 * @return
	 */
	public HashMap<String, Object> sendTemplateSMS(String to, String templateId, String[] datas,String subAppend,String reqId) {
		return send(to,templateId,datas,subAppend,reqId);
	}

	private HashMap<String, Object> send(String to, String templateId, String[] datas,String subAppend,String reqId){
		HashMap<String, Object> validate = accountValidate();
		if(validate!=null)
			return validate;
		if ((StringUtils.isEmpty(to)) || (StringUtils.isEmpty(App_ID)) || (StringUtils.isEmpty(templateId)))
			throw new IllegalArgumentException("必填参数:" + (StringUtils.isEmpty(to) ? " 手机号码 " : "") + (StringUtils.isEmpty(templateId) ? " 模板Id " : "") + "为空");
		String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
		String sig = ParmUtils.generateSig(ACCOUNT_SID, ACCOUNT_TOKEN, timestamp);
		String authorization="";
		try {
			authorization = ParmUtils.generateAuthorization(ACCOUNT_SID,timestamp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("生成authorization异常" + e.getMessage());
		}
		String url = getBaseUrl().append("/" + AcountType + "/").append(ACCOUNT_SID).append("/" + TemplateSMS + "?sig=").append(sig).toString();
		String requsetbody = "";
		if (BODY_TYPE == BodyType.Type_JSON) {
			requsetbody = generateJson(to,templateId,datas,subAppend,reqId);
		}else {
			requsetbody = generateXml(to,templateId,datas,subAppend,reqId);
		}
		log.info("sendTemplateSMS Request url:" + url);
		log.info("sendTemplateSMS Request body:" + requsetbody);
		String result = HttpClientUtil.post(url,authorization,requsetbody,BODY_TYPE, Constant.UTF8);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "返回包体错误");
		}
	}
	private HashMap<String, Object> jsonToMap(String result) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		JsonObject asJsonObject = JsonParser.parseString(result).getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = asJsonObject.entrySet();
		HashMap<String, Object> hashMap2 = new HashMap<String, Object>();

		for (Map.Entry<String, JsonElement> m : entrySet) {
			if ("statusCode".equals(m.getKey()) || "statusMsg".equals(m.getKey()))
				hashMap.put(m.getKey(), m.getValue().getAsString());
			else {
				if ("SubAccount".equals(m.getKey()) || "totalCount".equals(m.getKey())
						||"token".equals(m.getKey())||"downUrl".equals(m.getKey())) {
					if (!"SubAccount".equals(m.getKey()))
						hashMap2.put(m.getKey(), m.getValue().getAsString());
					else {
						try {
							if((m.getValue().toString().trim().length()<=2)&&!m.getValue().toString().contains("[")){
								hashMap2.put(m.getKey(), m.getValue().getAsString());
								hashMap.put("data", hashMap2);
								break;
							}
							if(m.getValue().toString().contains("[]")){
								hashMap2.put(m.getKey(), new JsonArray());
								hashMap.put("data", hashMap2);
								continue;
							}
							JsonArray asJsonArray = JsonParser.parseString(m.getValue().toString()).getAsJsonArray();
							ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
							for (JsonElement j : asJsonArray) {
								Set<Entry<String, JsonElement>> entrySet2 = j.getAsJsonObject().entrySet();
								HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
								for (Map.Entry<String, JsonElement> m2 : entrySet2) {
									hashMap3.put(m2.getKey(), m2.getValue().getAsString());
								}
								arrayList.add(hashMap3);
							}
							hashMap2.put("SubAccount", arrayList);
						} catch (Exception e) {
							JsonObject asJsonObject2 = JsonParser.parseString(m.getValue().toString()).getAsJsonObject();
							Set<Entry<String, JsonElement>> entrySet2 = asJsonObject2.entrySet();
							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Map.Entry<String, JsonElement> m2 : entrySet2) {
								hashMap3.put(m2.getKey(), m2.getValue().getAsString());
							}
							hashMap2.put(m.getKey(), hashMap3);
							hashMap.put("data", hashMap2);
						}

					}
					hashMap.put("data", hashMap2);
				} else {

					JsonObject asJsonObject2 = JsonParser.parseString(m.getValue().toString()).getAsJsonObject();
					Set<Entry<String, JsonElement>> entrySet2 = asJsonObject2.entrySet();
					HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
					for (Map.Entry<String, JsonElement> m2 : entrySet2) {
						hashMap3.put(m2.getKey(), m2.getValue().getAsString());
					}
					if (hashMap3.size() != 0) {
						hashMap2.put(m.getKey(), hashMap3);
					} else {
						hashMap2.put(m.getKey(), m.getValue().getAsString());
					}
					hashMap.put("data", hashMap2);
				}
			}
		}
		return hashMap;
	}

	/**
	 * @description 将xml字符串转换成map
	 * @param xml
	 * @return Map
	 */
	private HashMap<String, Object> xmlToMap(String xml) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
			for (Iterator i = rootElt.elementIterator(); i.hasNext();) {
				Element e = (Element) i.next();
				if ("statusCode".equals(e.getName()) || "statusMsg".equals(e.getName()))
					map.put(e.getName(), e.getText());
				else {
					if ("SubAccount".equals(e.getName()) || "totalCount".equals(e.getName())
							||"token".equals(e.getName())||"downUrl".equals(e.getName())) {
						if (!"SubAccount".equals(e.getName())) {
							hashMap2.put(e.getName(), e.getText());
						} else {
							ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Iterator i2 = e.elementIterator(); i2.hasNext();) {
								Element e2 = (Element) i2.next();
								hashMap3.put(e2.getName(), e2.getText());
								arrayList.add(hashMap3);
							}
							hashMap2.put("SubAccount", arrayList);
						}
						map.put("data", hashMap2);
					} else {

						HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
						for (Iterator i2 = e.elementIterator(); i2.hasNext();) {
							Element e2 = (Element) i2.next();
							// hashMap2.put(e2.getName(),e2.getText());
							hashMap3.put(e2.getName(), e2.getText());
						}
						if (hashMap3.size() != 0) {
							hashMap2.put(e.getName(), hashMap3);
						} else {
							hashMap2.put(e.getName(), e.getText());
						}
						map.put("data", hashMap2);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	private String generateJson(String to,String templateId, String[] datas,String subAppend,String reqId){
		JsonObject json = new JsonObject();
		json.addProperty("appId", App_ID);
		json.addProperty("to", to);
		json.addProperty("templateId", templateId);
		if (datas != null) {
			StringBuilder sb = new StringBuilder("[");
			for (String s : datas) {
				sb.append("\"");
				sb.append(s);
				sb.append("\"");
				sb.append(",");
			}
			sb.replace(sb.length() - 1, sb.length(), "]");
			JsonArray jarray = JsonParser.parseString(sb.toString()).getAsJsonArray();
			json.add("datas", jarray);
		}
		if(!StringUtils.isBlank(subAppend)&&ParmUtils.checkSubAppend(subAppend)){
			json.addProperty("subAppend", subAppend);
		}
		if(!StringUtils.isBlank(reqId)&&ParmUtils.checkReqId(reqId)){
			json.addProperty("reqId", reqId);
		}
		return json.toString();
	}
	private String generateXml(String to,String templateId, String[] datas,String subAppend,String reqId){
		StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
		sb.append("<appId>").append(App_ID).append("</appId>").append("<to>").append(to).append("</to>").append("<templateId>").append(templateId)
				.append("</templateId>");
		if (datas != null) {
			sb.append("<datas>");
			for (String s : datas) {
				sb.append("<data>").append(s).append("</data>");
			}
			sb.append("</datas>");
		}
		if(!StringUtils.isBlank(subAppend)&&ParmUtils.checkSubAppend(subAppend)){
			sb.append("<subAppend>").append(subAppend).append("</subAppend>");
		}
		if(!StringUtils.isBlank(reqId)&&ParmUtils.checkReqId(reqId)){
			sb.append("<reqId>").append(reqId).append("</reqId>");
		}
		sb.append("</TemplateSMS>").toString();
		return sb.toString();
	}



	private StringBuffer getBaseUrl() {
		StringBuffer sb = new StringBuffer();
		if(USE_SSL){
			sb.append("https://");
		}else {
			sb.append("http://");
		}
		sb.append(SERVER_IP).append(":").append(SERVER_PORT);
		sb.append("/2013-12-26");
		return sb;
	}


	private HashMap<String, Object> getMyError(String code, String msg) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("statusCode", code);
		hashMap.put("statusMsg", msg);
		return hashMap;
	}

	private HashMap<String,Object> accountValidate() {
		if ((StringUtils.isEmpty(SERVER_IP))) {
			return getMyError("172004", "IP为空");
		}
		if ((StringUtils.isEmpty(SERVER_PORT))) {
			return getMyError("172005", "端口错误");
		}
		if ((StringUtils.isEmpty(ACCOUNT_SID))) {
			return getMyError("172006", "主帐号为空");
		}
		if ((StringUtils.isEmpty(ACCOUNT_TOKEN))) {
			return getMyError("172007", "主帐号令牌为空");
		}
		if ((StringUtils.isEmpty(App_ID))) {
			return getMyError("172012", "应用ID为空");
		}
		return null;
	}
}