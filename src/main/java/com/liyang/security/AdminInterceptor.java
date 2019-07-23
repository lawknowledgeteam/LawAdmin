package com.liyang.security;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.liyang.common.GlobalVar;
import com.liyang.model.Admin;

public class AdminInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		Admin manager=arg0.getController().getSessionAttr(GlobalVar.WEBADMIN);
		if(manager==null)
			arg0.getController().redirect("/web_admin/login");
		else
		{
			arg0.getController().setAttr("RoleID", manager.getStr("RoleID"));
			arg0.getController().setAttr("AdminNickName", manager.getStr("NickName"));
			arg0.getController().setAttr("AdminLoginName", manager.getStr("LoginName"));
			
			InetAddress address=null;
			try {
				address = InetAddress.getLocalHost();//获取的是本地的IP地址 /
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String hostAddress = address.getHostAddress(); 
			arg0.getController().setAttr("ServerAddress", hostAddress);
			arg0.getController().setAttr("AdminID", manager.getInt("AdminID"));
			
			arg0.invoke();
		}
			
	}

}