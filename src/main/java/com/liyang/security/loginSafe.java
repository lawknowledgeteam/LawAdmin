package com.liyang.security;

import com.jfinal.core.Controller;

public class loginSafe implements Runnable {
	String name;
	Controller c;

	public loginSafe(String tel, Controller c) {
		this.name = tel;
		this.c = c;
	}

	public void run() {
		//AdminLoginSafe.add(name, this);
		UserLoginSafe.add(this.name, this);
		
		try {
			Thread.sleep(600000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//AdminLoginSafe.remove(tel, this);
		UserLoginSafe.remove(this.name, this);
		this.c.removeSessionAttr(this.name);
	}
}