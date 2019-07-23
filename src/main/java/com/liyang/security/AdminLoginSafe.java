package com.liyang.security;

import java.util.HashMap;

public class AdminLoginSafe {
	private static HashMap<String, loginSafe> map=new HashMap<String,loginSafe>();
	
	public static void add(String name,loginSafe ls)
	{
		map.put(name, ls);
	}
	
	public static void remove(String name,loginSafe ls)
	{
		map.remove(name);
	}
	public static boolean isExist(String name)
	{
		return map.containsKey(name);
	}

}

