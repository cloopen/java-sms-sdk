package com.cloopen.rest.sdk.utils;

import java.security.MessageDigest;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 加密类
 */
public class Encrypt {
	private static final String UTF8 = "utf-8";
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
	

	/** 
     * BASE64编码
     * @param src 
     * @return 
     * @throws Exception 
     */  
    public static String base64Encoder(String src) throws Exception {  
        BASE64Encoder encoder = new BASE64Encoder();  
        return encoder.encode(src.getBytes(UTF8));
    }  
      
    /** 
     * BASE64解码
     * @param dest 
     * @return 
     * @throws Exception 
     */  
    public static String base64Decoder(String dest) throws Exception {  
        BASE64Decoder decoder = new BASE64Decoder();  
        return new String(decoder.decodeBuffer(dest), UTF8);
    }  

}
