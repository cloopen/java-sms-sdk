package com.cloopen.rest.sdk.utils;

/**
 * ClassName: StringUtils
 * Function: 字符串处理类
 * Date:  2019-09-24 20:23
 * <p>
 */
public class StringUtils {
	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}
	public static boolean isBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
}
