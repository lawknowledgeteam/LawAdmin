package com.liyang.model;

import com.jfinal.plugin.activerecord.Model;

public class Role extends Model<Role>
{
  private static final long serialVersionUID = 23933234378L;
  public static final Role dao = new Role();
  
  public java.util.Map<String, Object> getAttrs() {
		return super._getAttrs();
	}
}