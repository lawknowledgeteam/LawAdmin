package com.liyang.security;

import java.util.HashMap;

public class UserLoginSafe
{
  private static HashMap<String, loginSafe> map = new HashMap<String, loginSafe>();

  public static void add(String tel, loginSafe ls)
  {
    map.put(tel, ls);
  }

  public static void remove(String tel, loginSafe ls)
  {
    map.remove(tel);
  }

  public static boolean isExist(String tel) {
    return map.containsKey(tel);
  }
}