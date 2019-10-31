package com.cloopen.rest.sdk.utils;

import com.cloopen.rest.sdk.BodyType;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;


/**
 * ClassName: HttpClientUtil
 * Function: http 公共类
 * Date:  2019-10-11 11:34
 * <p>
 */
@Slf4j
public class HttpClientUtil {
	public static final int CONNECTION_TIMEOUT = 5000;// 连接超时时间

	public static final int CONNECTION_REQUEST_TIMEOUT = 5000;// 请求超时时间

	public static final int SOCKET_TIMEOUT = 10000;// 数据读取等待超时

	public static final String HTTP = "http";// http

	public static final String HTTPS = "https";// https

	public static final int DEFAULT_HTTP_PORT = 80;// http端口

	public static final int DEFAULT_HTTPS_PORT = 443;// https端口

	public static final String DEFAULT_ENCODING = "UTF-8";// 默认编码

	/**
	 * post请求(1.处理http请求;2.处理https请求,信任所有证书)[默认编码:UTF-8]
	 *
	 * @param url
	 * @param reqMap
	 * @return
	 */
	public static String post(String url, Map<String, String> reqMap) {
		return post(url, reqMap, DEFAULT_ENCODING);
	}

	/**
	 * post请求(1.处理http请求;2.处理https请求,信任所有证书)
	 *
	 * @param url
	 * @param reqMap   入参是个map
	 * @param encoding
	 * @return
	 */
	public static String post(String url, Map<String, String> reqMap, String encoding) {
		String result = "";

		// 添加参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (reqMap != null && reqMap.keySet().size() > 0) {
			Iterator<Map.Entry<String, String>> iter = reqMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entity = iter.next();
				params.add(new BasicNameValuePair(entity.getKey(), entity.getValue()));
			}
		}

		CloseableHttpClient httpClient = null;
		if (url.startsWith(HTTPS)) {
			// 创建一个SSL信任所有证书的httpClient对象
			httpClient = HttpClientUtil.createSSLInsecureClient();
		} else {
			httpClient = HttpClients.createDefault();
		}
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(CONNECTION_TIMEOUT)   //设置连接超时时间
					.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT) // 设置请求超时时间
					.setSocketTimeout(SOCKET_TIMEOUT)
					.setRedirectsEnabled(true)//默认允许自动重定向
					.build();
			httpPost.setConfig(requestConfig);
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			httpPost.setEntity(new UrlEncodedFormEntity(params, encoding));

			// 发送请求，并接收响应
			response = httpClient.execute(httpPost);
			result = handleResponse(url, encoding, response);
		} catch (IOException e) {
			log.error("-----> url:" + url + ",post请求异常:" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeResource(httpClient, response);
		}

		return result;
	}

	/**
	 * post请求(1.处理http请求;2.处理https请求,信任所有证书)
	 *
	 * @param url
	 * @param authorization 验证信息
	 * @param strParams     入参是个字符串(json/xml)
	 * @param encoding
	 * @return
	 */
	public static String post(String url, String authorization, String strParams, BodyType BODY_TYPE, String encoding) {
		String result = "";

		CloseableHttpClient httpClient = null;
		if (url.startsWith(HTTPS)) {
			// 创建一个SSL信任所有证书的httpClient对象
			httpClient = HttpClientUtil.createSSLInsecureClient();
		} else {
			httpClient = HttpClients.createDefault();
		}
		CloseableHttpResponse response = null;

		try {
			HttpPost httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(CONNECTION_TIMEOUT)   //设置连接超时时间
					.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT) // 设置请求超时时间
					.setSocketTimeout(SOCKET_TIMEOUT)
					.setRedirectsEnabled(true)//默认允许自动重定向
					.build();
			httpPost.setConfig(requestConfig);
			httpPost.setHeader(Constant.AUTHORIZATION, authorization);
			if (BODY_TYPE == BodyType.Type_JSON) {
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-Type", "application/json;charset=" + encoding);
				httpPost.setEntity(new StringEntity(strParams, ContentType.create("application/json", encoding)));
			} else {
				httpPost.setHeader("Accept", "application/xml");
				httpPost.setHeader("Content-Type", "application/xml;charset=" + encoding);
				httpPost.setEntity(new StringEntity(strParams, ContentType.create("application/xml", encoding)));
			}
			// 发送请求，并接收响应
			response = httpClient.execute(httpPost);
			result = handleResponse(url, encoding, response);
			log.info("Response body:{}", result);
		} catch (IOException e) {
			log.error("-----> url:" + url + ",post请求异常:" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeResource(httpClient, response);
		}

		return result;
	}

	/**
	 * 创建一个SSL信任所有证书的httpClient对象
	 *
	 * @return
	 */
	public static CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				// 默认信任所有证书
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();

			SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslContext);

			return HttpClients.custom().setSSLSocketFactory(sslcsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		return HttpClients.createDefault();
	}

	/**
	 * 处理响应，获取响应报文
	 *
	 * @param url
	 * @param encoding
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private static String handleResponse(String url, String encoding, CloseableHttpResponse response) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;

		try {
			if (response != null) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					// 获取响应实体
					HttpEntity entity = response.getEntity();

					if (entity != null) {
						br = new BufferedReader(new InputStreamReader(entity.getContent(), encoding));
						String s = null;
						while ((s = br.readLine()) != null) {
							sb.append(s);
						}
					}

					// 释放entity
					EntityUtils.consume(entity);
				} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
					log.info("-----> get请求404,未找到资源. url:" + url);
				} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
					log.info("-----> get请求500,服务器端异常. url:" + url);
				}
			}
		} catch (Exception e) {
			log.error("----->url:" + url + ",处理响应，获取响应报文异常：" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 释放资源
	 *
	 * @param httpClient
	 * @param response
	 */
	private static void closeResource(CloseableHttpClient httpClient, CloseableHttpResponse response) {
		if (response != null) {
			try {
				response.close();
			} catch (IOException e) {
				log.error("-----> 释放response资源异常:" + e.getMessage());
				e.printStackTrace();
			}
		}

		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (Exception e) {
				log.error("-----> 释放httpclient资源异常:" + e.getMessage());
				e.printStackTrace();
			}
		}
	}


	/**
	 * 绕过验证
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[]{trustManager}, null);
		return sc;
	}

}
