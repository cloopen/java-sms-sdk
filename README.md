# Yuntongxun SMS SDK for Java

#### [容联云通讯](https://www.yuntongxun.com/) SDK

### 快速开始  [maven](https://search.maven.org/search?q=java-sms-sdk)获取

```
    <dependency>
        <groupId>com.cloopen</groupId>
        <artifactId>java-sms-sdk</artifactId>
        <version>1.0.3</version>
    </dependency>
```
### 方法介绍
1. init 
   方法原型：
   ```java
    public void init(String serverIP, String serverPort)
    public void init(String serverIP, String serverPort, Boolean useSSL)
   ```
   
2. sendTemplateSMS
   方法原型：
   ```java
    public HashMap<String, Object> sendTemplateSMS(String to, String templateId, String[] datas)
    public HashMap<String, Object> sendTemplateSMS(String to, String templateId, String[] datas,String subAppend,String reqId)
   ```
   
### 使用举例

```
    //生产环境请求地址：app.cloopen.com
    String serverIp = "https://app.cloopen.com";
    //请求端口
    String serverPort = "8883";
    //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
    String accountSId = "accountSId";
    String accountToken = "accountToken";
    //请使用管理控制台中已创建应用的APPID
    String appId = "appId";
    CCPRestSmsSDK sdk = new CCPRestSmsSDK();
    sdk.init(serverIp, serverPort);
    sdk.setAccount(accountSId, accountToken);
    sdk.setAppId(appId);
    sdk.setBodyType(BodyType.Type_JSON);
    String to = "1352*******";
    String templateId= "templateId";
    String[] datas = {"变量1","变量2","变量3"};
    String subAppend="1234";  //可选	扩展码，四位数字 0~9999
    String reqId="fadfafas";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
  //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
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
```








