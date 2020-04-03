package com.sinolife.util;

import java.math.BigDecimal;
import java.util.Random;
import java.util.regex.Pattern;


public class StringUtil {

	
	/**
	 * 匹配html标签
	 */
	private final static String REGEX_HTML="<[[/>]*[a-zA-Z]>]+[/>]*[>]";

	/**
	 * 判断String参数是否为空
	 * 
	 * @param str
	 *            String参数
	 * @return 为空返回true，否则返回false
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim()) || "null".equals(str.trim()) || "NULL".equals(str.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断String参数是否为空
	 * 
	 * @param str
	 *            String参数
	 * @return 不为空返回true，否则返回false
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 去掉String参数的空格
	 * 
	 * @param str
	 *            String参数
	 * @return 两边不带空格的字符串
	 */
	public static String trim(String str) {
		if (isEmpty(str))
			return null;
		return str.trim();
	}

	/**
	 * 去掉String参数的空格
	 * 
	 * @param str
	 *            String参数
	 * @return 两边不带空格的字符串
	 */
	public static String trimEmpty(String str) {
		if (isEmpty(str))
			return "";
		return str.trim();
	}

	/**
	 * 判断Object对象是否为空
	 * 
	 * @param obj
	 *            Object对象
	 * @return 为空返回true，否则返回false
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null)
			return true;
		if (obj instanceof String) {
			return isEmpty((String) obj);
		}
		if (obj instanceof StringBuilder) {
			return isEmpty(obj.toString());
		}
		if (obj instanceof StringBuffer) {
			return isEmpty(obj.toString());
		}
		return false;
	}

	/**
	 * 判断Object对象是否为空
	 * 
	 * @param obj
	 *            Object对象
	 * @return 不为空返回true，否则返回false
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

	/**
	 * 判断某个字符串是否为正数
	 * 
	 * @param str
	 *            字符串
	 * @return 为正反返回true，否则返回false
	 */
	public static boolean isPlusInt(String str) {
		int temp = 0;
		try {
			temp = Integer.parseInt(str);
		} catch (Exception e) {
			temp = -1;
		}
		return temp > 0 ? true : false;
	}

	/**
	 * 判断是否带1-2位小数的正数或负数，非空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAmountByPattern(String str) {
		if (isEmpty(str))
			return false; 
//		Pattern pattern = Pattern.compile("^[+-][0-9]{1,13}+(.[0-9]{1,2})?$");
		Pattern pattern = Pattern.compile("^(\\-)?[0-9]{1,13}+(\\.\\d{0,2})?$");
		return pattern.matcher(str).matches();
	}
	/**
	 * 判断是否数字，非空，包括整数，小数；不包括负数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumByPattern(String str) {
		if (isEmpty(str))
			return false;
		Pattern pattern = Pattern.compile("(0|[1-9]\\d*)\\.*\\d*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 判断是否数字，非空，包括整数，小数；不包括负数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumByPattern(Double d) {
		if (isEmpty(d))
			return false;
		return isNumByPattern(d.toString());
	}

	/**
	 * 产生指定位数的随机数
	 * 
	 * @param length
	 * @return
	 */
	public static String generateString(int length) {
		String str = "0123456789";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(str.charAt(random.nextInt(str.length())));
		}
		return sb.toString();
	}

	/**
	 * 截取字符串右边的空格. <br>
	 * <br>
	 * <b>示例 </b> <br>
	 * StringUtils.rightTrim(&quot; ab cd e &quot;) 返回 &quot; ab cd e&quot;
	 * StringUtils.rightTrim(null) 返回 &quot;&quot;
	 * 
	 * @param str
	 *            原字符串
	 * @return 返回截掉右边空格后的字符串
	 * @author lujicong.wb
	 */
	public static String rightTrim(String str) {
		if (str == null) {
			return "";
		}
		int length = str.length();
		for (int i = length - 1; i >= 0; i--) {
			if (str.charAt(i) != 0x20) {
				break;
			}
			length--;
		}
		return str.substring(0, length);
	}

	/**
	 * 多个字符串与比较值进行比较，如果存在一个值相等的，返回true
	 * 
	 * @param compare
	 *            比较值
	 * @param strs
	 * @return
	 */
	public static boolean equalOne(String compare, String... strs) {
		for (String str : strs) {
			if (compare.equals(str)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 多个字符串与比较值进行比较，如果存在一个值相等的，返回false
	 * 
	 * @param compare
	 *            比较值
	 * @param strs
	 * @return
	 */
	public static boolean notEqualAll(String compare, String... strs) {
		if (equalOne(compare, strs)) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * 获取字符串长度，字符长度为1，中文字符长度为2
	 * @param s
	 * @return
	 */
	public static int getLengthb(String s) {
		int length = 0;
		for (int i = 0; i < s.length(); i++) {
			int ascii = Character.codePointAt(s, i);
			if (ascii >= 0 && ascii <= 255)
				length++;
			else
				length += 2;
		}
		return length;
	}

	/**
	 * 截取字符串(oracle)
	 * 从0开始截取长度为lengthb的字符串，字符长度为1，中文字符长度为2
	 *
	 * @param s 目标字符串
	 * @param lengthb 指定长度
	 * @return
	 */
	public static String subStringb(String s, int lengthb) {
		if (s == null || lengthb <= 0) {
			return "";
		}
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			int ascii = Character.codePointAt(s, i);
			if (ascii >= 0 && ascii <= 255) {
				count++;
			} else {
				count += 2;
			}
			if (count > lengthb) {
				return s.substring(0, i);
			}
		}
		return s;
	}

	/**
	 * 逗号分隔的字符串，转换为 inSql格式
	 * 
	 * @param sourceStr
	 * @return
	 */
//	public static String getInSqlStr(String sourceStr) {
//		String reStr = "";
//		String[] arr = sourceStr.split(",");
//		for (String s : arr) {
//			if (ToolUtils.isEmpty(reStr)) {
//				reStr = "'" + s + "'";
//			} else {
//				reStr = reStr + ",'" + s + "'";
//			}
//		}
//		return reStr;
//	}
	
	/**
	 * 按正则去除Html标签
	 * @param htmlStr
	 * @param regex 不传则使用默认正则
	 * @return
	 * zhonglihai.wb
	 * 2019-10-31
	 */
	public static String delHtmlTag(String htmlStr,String regex){
		if(regex==null){
			return htmlStr.replaceAll(REGEX_HTML, "");
		}else{
			return htmlStr.replaceAll(regex, "");
		}
		
	}
	/**
	 * 比较两个字符串。返回字符串相似度
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static  double compareStr(String str1, String  str2){
	    //如果有空字符串，则返回0
		int count = 0;//统计相同字符的个数
		if(isEmpty(str1)||isEmpty(str2)){
	    	return 0.00 ;
	    }else{
	    	int len1 = str1.length();
	    	int len2 = str2.length();
	    	for(int i =0;i<len1;i++){
	    		if(len2< i + 1){
	    			break;
	    		}else{
	    			//System.out.println("i="+i+";str1="+str1.substring(i,i+1)+",str2="+str2.substring(i,i+1));
	    			if(str1.substring(i, i+1).equals(str2.substring(i, i+1))){
	    				count ++;
	    			}
	    		}
	    	}
	    	//System.out.println(count);
	    	return  new BigDecimal((float) count/( len1>len2?len1:len2)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	    }
	}
	 public static void main(String[] args){
		 String str1 = "12345";
		 String str2  = "23e";
		 System.out.println(compareStr(str1,str2));
	 }

}
