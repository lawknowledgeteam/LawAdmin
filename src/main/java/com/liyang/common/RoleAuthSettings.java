package com.liyang.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class RoleAuthSettings {
  public static Map<String,Integer> maps=new HashMap<String,Integer>();
  
  public static void init()
  {
	  maps.clear();
	  List<Record> settings=Db.find("select RoleID,AuthName from uv_role_menu_auth");
	  for (Record s : settings) {
		maps.put(s.getInt("RoleID")+s.getStr("AuthName"), 1);
	}
  } 

  public static Integer getValue(String key)
  {
	  if(maps.get(key)==null)
		  return 0;
	  else
	      return maps.get(key);
  }
}
