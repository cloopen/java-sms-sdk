package com.cloopen.rest.sdk.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

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

	public static boolean checkSubAppend(String subAppend){
		if(subAppend.length()>4){
			return false;
		}
		if(isInteger(subAppend)) {
			return true;
		}else {
			return false;
		}
	}
	public static boolean checkReqId(String reqId){
		if(reqId.length()>32) {
			return false;
		}
		if(isLetterDigit(reqId)){
			return true;
		}else {
			return false;
		}
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	public static boolean isLetterDigit(String str) {
		String regex = "^[a-z0-9A-Z]+$";
		return str.matches(regex);
	}
}
