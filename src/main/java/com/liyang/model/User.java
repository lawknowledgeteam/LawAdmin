package com.liyang.model;

import com.jfinal.plugin.activerecord.Model;

public class User extends Model<User>
{
  private static final long serialVersionUID = -2506359332148505637L;
  public static final User dao = new User();
  
  public java.util.Map<String, Object> getAttrs() {
		return super._getAttrs();
	}
}