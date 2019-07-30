package com.liyang.model;

import com.jfinal.plugin.activerecord.Model;

public class Case extends Model<Case>
{
  private static final long serialVersionUID = 23933234378L;
  public static final Case dao = new Case();
  
  public java.util.Map<String, Object> getAttrs() {
		return super._getAttrs();
	}
}