package com.liyang.common;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;


public class Util {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static String currentDay="2015-11-11";
	public static String getDate()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		return sdf.format(new Date());
	}
	
	public static String getYear()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
		return sdf.format(new Date());
	}
	public static String getMonth()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
		return sdf.format(new Date());
	}
	public static int getDay()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd");
		return Integer.parseInt(sdf.format(new Date()));
	}
	public static String getTime()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
		return sdf.format(new Date());
	}
	public static String getDateTime()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return sdf.format(new Date());
	}
	public static String getWeekday()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("E");
		return sdf.format(new Date());
	}
	public static String getTimeStamp()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
	public static String getEndTimeStamp()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date(System.currentTimeMillis()+1000*60*6));
	}
	public static String getJsonText(String text)
	{
		return "{\"msg\":\""+text+"\"}";
	}
	public static String getSuccessResult()
	{
		return getResult("0000","SUCCESS");
	}
	public static String getResult(String code,String msg)
	{
		return "{\"code\":\""+code+"\",\"msg\":\""+msg+"\"}";
	}
	public static String getWebServiceReturn()
	{
		return "{\"result\":\"00\",\"error\":\"\"}";
	}
	public static String getWebServiceReturn(String code,String msg)
	{
		return "{\"result\":\""+code+"\",\"error\":\""+msg+"\"}";
	}
	private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

	private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

	public static String Md5(String str){
        //确定计算方法
        MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
	      //  BASE64Encoder base64en = new BASE64Encoder();
	        //加密后的字符�?
	        String newstr=byteArrayToHexString(md5.digest(str.getBytes("utf-8"))).toUpperCase();   
	        return newstr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;    
    }
	public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换�? 十六进制 �?
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
	
	/** 
     * 使用 HMAC-SHA1 签名方法对encryptText进行签名 
     *  
     * @param encryptText  被签名的字符串 
     * @param encryptKey   密钥      
     * @return 加密后的字符串 
     */  
    public static String HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception     
    {           
        byte[] data=encryptKey.getBytes("UTF-8");  
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称  
        SecretKey secretKey = new SecretKeySpec(data,  "HmacSHA1");   
        //生成一个指定 Mac 算法 的 Mac 对象  
        Mac mac = Mac.getInstance( "HmacSHA1");   
        //用给定密钥初始化 Mac 对象  
        mac.init(secretKey);    
          
        byte[] text = encryptText.getBytes("UTF-8");    
        //完成 Mac 操作   
        byte[] rawHmac =mac.doFinal(text);
        
        StringBuffer hexString = new StringBuffer();
        // 字节数组转换 十六进制
        for (int i = 0; i < rawHmac.length; i++) {
            String shaHex = Integer.toHexString(rawHmac[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }   
    
    
  
	public static String getRandomString(int length) { //length表示生成字符串的长度  
	    String base = "0123456789"; 
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	 } 
	public static String getRandomString() { //length表示生成字符串的长度  
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789"; 
	    int length=16;
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	 } 
	public static String getEncodeText(String text)
	{
		String str="";
		try {
			str=URLEncoder.encode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	public static String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //去掉�?-”符�? 
        return s.replaceAll("-", ""); 
    }
	
	
	/**
	 * 从文件名中获得文件后缀名
	 * @param request
	 * @return String
	 */
	public static String getFileSuffix(String s){
		int start=s.indexOf("/");
		int end=s.indexOf(";base64");
		String sType="";
		if((start>0)&&(end>start))
			sType=s.substring(start+1,end);
        return sType;
    }
	
	/**
	 * 从文件名中获得文件类型
	 * @param request
	 * @return String
	 */
	public static String getFileType(String s){
		int start=s.indexOf(":");
		int end=s.indexOf("/");
		String sType="";
		if((start>0)&&(end>start))
			sType=s.substring(start+1,end);
        return sType;
    }
	

	
	public static String getParasToString(Object [] paras){
		StringBuilder sb=new StringBuilder("");
		for (Object object : paras) {
			sb.append(object.toString()+";");
		}
		return sb.toString();
	} 
	

	
	 public static String ReplaceEnterWithBr(String myString){  
	        String newString=myString.replaceAll("\\r\\n", "<br/>");//将内容区域的回车换行去除
	        return newString;  
	    }
	 
	 public static String getIpAddress(HttpServletRequest request) {  
		          String ip = request.getHeader("x-forwarded-for");  
		          if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		              ip = request.getHeader("Proxy-Client-IP");  
		          }  
		          if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		              ip = request.getHeader("WL-Proxy-Client-IP");  
		          }  
		          if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		              ip = request.getHeader("HTTP_CLIENT_IP");  
		          }  
		          if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		              ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
		          }  
		          if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		              ip = request.getRemoteAddr();  
		          }  
		          return ip;  
    }  
	 
	    //sMac format: 98:9C:57:D8:EB:F9
	    public static long MacToLong(String sMac)
		{
	    	String temp=sMac.replace(":","");
	    	long lRet= Long.parseLong(temp,16);
			return lRet;
		}
}
