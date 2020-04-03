package com.sinolife.util;

import java.math.BigDecimal;
import java.util.Random;
import java.util.regex.Pattern;


public class StringUtil {

	
	/**
	 * ƥ��html��ǩ
	 */
	private final static String REGEX_HTML="<[[/>]*[a-zA-Z]>]+[/>]*[>]";

	/**
	 * �ж�String�����Ƿ�Ϊ��
	 * 
	 * @param str
	 *            String����
	 * @return Ϊ�շ���true�����򷵻�false
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim()) || "null".equals(str.trim()) || "NULL".equals(str.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * �ж�String�����Ƿ�Ϊ��
	 * 
	 * @param str
	 *            String����
	 * @return ��Ϊ�շ���true�����򷵻�false
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * ȥ��String�����Ŀո�
	 * 
	 * @param str
	 *            String����
	 * @return ���߲����ո���ַ���
	 */
	public static String trim(String str) {
		if (isEmpty(str))
			return null;
		return str.trim();
	}

	/**
	 * ȥ��String�����Ŀո�
	 * 
	 * @param str
	 *            String����
	 * @return ���߲����ո���ַ���
	 */
	public static String trimEmpty(String str) {
		if (isEmpty(str))
			return "";
		return str.trim();
	}

	/**
	 * �ж�Object�����Ƿ�Ϊ��
	 * 
	 * @param obj
	 *            Object����
	 * @return Ϊ�շ���true�����򷵻�false
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
	 * �ж�Object�����Ƿ�Ϊ��
	 * 
	 * @param obj
	 *            Object����
	 * @return ��Ϊ�շ���true�����򷵻�false
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

	/**
	 * �ж�ĳ���ַ����Ƿ�Ϊ����
	 * 
	 * @param str
	 *            �ַ���
	 * @return Ϊ��������true�����򷵻�false
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
	 * �ж��Ƿ��1-2λС���������������ǿ�
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
	 * �ж��Ƿ����֣��ǿգ�����������С��������������
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
	 * �ж��Ƿ����֣��ǿգ�����������С��������������
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
	 * ����ָ��λ���������
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
	 * ��ȡ�ַ����ұߵĿո�. <br>
	 * <br>
	 * <b>ʾ�� </b> <br>
	 * StringUtils.rightTrim(&quot; ab cd e &quot;) ���� &quot; ab cd e&quot;
	 * StringUtils.rightTrim(null) ���� &quot;&quot;
	 * 
	 * @param str
	 *            ԭ�ַ���
	 * @return ���ؽص��ұ߿ո����ַ���
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
	 * ����ַ�����Ƚ�ֵ���бȽϣ��������һ��ֵ��ȵģ�����true
	 * 
	 * @param compare
	 *            �Ƚ�ֵ
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
	 * ����ַ�����Ƚ�ֵ���бȽϣ��������һ��ֵ��ȵģ�����false
	 * 
	 * @param compare
	 *            �Ƚ�ֵ
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
	 * ��ȡ�ַ������ȣ��ַ�����Ϊ1�������ַ�����Ϊ2
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
	 * ��ȡ�ַ���(oracle)
	 * ��0��ʼ��ȡ����Ϊlengthb���ַ������ַ�����Ϊ1�������ַ�����Ϊ2
	 *
	 * @param s Ŀ���ַ���
	 * @param lengthb ָ������
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
	 * ���ŷָ����ַ�����ת��Ϊ inSql��ʽ
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
	 * ������ȥ��Html��ǩ
	 * @param htmlStr
	 * @param regex ������ʹ��Ĭ������
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
	 * �Ƚ������ַ����������ַ������ƶ�
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static  double compareStr(String str1, String  str2){
	    //����п��ַ������򷵻�0
		int count = 0;//ͳ����ͬ�ַ��ĸ���
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
