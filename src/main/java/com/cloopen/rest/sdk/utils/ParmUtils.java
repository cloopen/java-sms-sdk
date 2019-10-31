package com.cloopen.rest.sdk.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * ClassName: ParamUtils
 * Function: 生成 sig 和 鉴权信息
 * Date:  2019-10-16 13:47
 * <p>
 */
public class ParmUtils {
	public static String generateSig(String accountSid, String accountToken, String timestamp) {
		StringBuffer sb = new StringBuffer(accountSid).append(accountToken).append(timestamp);
		String sig = Encrypt.encryptMD5(sb.toString()).toUpperCase();
		return sig;
	}

	public static String generateAuthorization(String accountSid, String timestamp) throws UnsupportedEncodingException {
		StringBuffer sb= new StringBuffer(accountSid).append(":").append(timestamp);
		String authorization = new String(Base64.encodeBase64(sb.toString().getBytes(Constant.UTF8)), Constant.UTF8);

		return authorization;
	}
}
