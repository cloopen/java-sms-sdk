package com.cloopen.rest.sdk.utils;

import java.security.MessageDigest;


/**
 * 加密类
 */
public class Encrypt {
	public static String encryptMD5(String strInput) {
	    StringBuffer buf = null;
	    try {
	      MessageDigest md = MessageDigest.getInstance("MD5");
	      md.update(strInput.getBytes());
	      byte[] b = md.digest();
	      buf = new StringBuffer(b.length * 2);
	      for (int i = 0; i < b.length; ++i) {
	        if ((b[i] & 0xFF) < 16)
	          buf.append("0");

	        buf.append(Long.toHexString(b[i] & 0xFF));
	      }
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
	    return buf.toString();
	}
	

}
