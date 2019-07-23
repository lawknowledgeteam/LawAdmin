package com.liyang.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.captcha.CaptchaRender;
import com.liyang.common.GlobalVar;
import com.liyang.common.Util;
import com.liyang.model.Admin;
import com.liyang.model.AdminLog;
import com.liyang.security.AdminInterceptor;
import com.liyang.security.AdminLoginSafe;
import com.liyang.security.loginSafe;

import java.util.List;

/**
 * 后台管理控制器类
 * 
 * @author Lixin
 * 
 */
@Before(AdminInterceptor.class)
public class WebAdminController extends Controller {
	//public static Logger logger = Util.getLogger();

	private static final String RANDOM_CODE_KEY = "1";// 生成验证码会用到

	// @Before(NoUrlPara.class)
	public void index() {
		render("/web_admin/index.html");
	}

	/**
	 * 生成验证码
	 */
	@Clear(AdminInterceptor.class)
	public void img() {
		CaptchaRender img = new CaptchaRender();
		img.setView(RANDOM_CODE_KEY);
		render(img);
	}

	
	
	

	@Clear(AdminInterceptor.class)
	public void login() // login.html
	{
		if (getSessionAttr(GlobalVar.WEBADMIN) != null) {
			setAttr("errorMsg", "");
			redirect("/web_admin");
			return;
		}
		render("/web_admin/login.html");
	}
	
	
	@Clear(AdminInterceptor.class)
	public void loginCheck() {
		if (getSessionAttr(GlobalVar.WEBADMIN) != null) {
			// 已登录就跳转
			redirect("/web_admin");
			return;
		}

		String sLoginName = getPara("loginName");
		if((getPara("loginName")!=null)&&(sLoginName.length()>20))
		{
			setAttr("errorMsg", "账号位数过长，请输入正确的账号！");
			render("/web_admin/login.html");
			return;
		}
		// 防暴力检测
		if (AdminLoginSafe.isExist("" + sLoginName)) {
			redirect("/web_admin/error?Msg=" + Util.getEncodeText("密码输入错误次数过多，请十分钟后再试！"));
			return;
		} else 
		{
			
			//验证码验证
			boolean result=validateCaptcha("validateCode");
			if(!result){
				LogController.addLog(2, 1000, "登录验证码错误!");
				
				setAttr("errorMsg", "验证码输入错误！");
				render("/web_admin/login.html");
				return;
			}
			
			Admin manager=Admin.dao.findFirst("SELECT * FROM tb_admin WHERE State=0 AND LoginName=?",sLoginName);
			if(manager==null)
			{
				LogController.addLog(2, 1000, "账号或密码错误!");
				setAttr("errorMsg", "账号或密码错误！");
				render("/web_admin/login.html");
				return;
			}
			else{
				checkPsw(manager,sLoginName);
			}
		}
	}
	
	@Clear(AdminInterceptor.class)
	public void checkPsw(Admin manager,String sLoginName){
		if (manager == null) {
			setAttr("errorMsg", "账号或密码错误！");
			render("/web_admin/login.html");
			return;
		}
		
		String pwdMD5=getPara("pwdMD5");
		if (pwdMD5.toLowerCase().equals( manager.getStr("PasswordMD5").toLowerCase())) {
			setSessionAttr(GlobalVar.WEBADMIN, manager);
			
			String sIP=Util.getIpAddress(getRequest());
			if (StrKit.isBlank(sIP) || sIP.equals("0.0.0.0") ||sIP.equals("0:0:0:0:0:0:0:1")) {
				sIP = "127.0.0.1";
			}
			
			String curDateTime=Util.getDateTime();
			manager.set("LastIPAddress", sIP);
			manager.set("LastLoginTime",curDateTime );
			manager.update();
			
			if(manager.getStr("LoginName").equals("admin"))
				LogController.addLog(100, 1000, "超级管理员登录!");
			else
				LogController.addLog(2, manager.getInt("AdminID"), "超级管理员登录!");//后台管理员登录
			//logger.info(sLoginName + "---登录后台");
			//renderHtml(Util.getResult("0000", "/web_admin/index.html"));
			redirect("/web_admin");
			return;
		} 
		else {
			if (getSessionAttr(sLoginName) != null) {
				int left = getSessionAttr(sLoginName);
				left--;
				if (left == 0) {
					loginSafe ls = new loginSafe(sLoginName, this);
					Thread t = new Thread(ls);
					t.start();
				} else
					setSessionAttr(sLoginName, left);
			} else
				setSessionAttr(sLoginName, 5);
			setAttr("errorMsg", "账号或密码错误！");
			//keepModel(userPSW.class);
			// render("login.html");
			render("/web_admin/login.html");
		}
		
	}
	
	
	

	/**
	 * 退出登录
	 */
	public void quit() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		
		String sIP=Util.getIpAddress(getRequest());
		if (StrKit.isBlank(sIP) || sIP.equals("0.0.0.0") ||sIP.equals("0:0:0:0:0:0:0:1")) {
			sIP = "127.0.0.1";
		}
		
		AdminLog newLog=new AdminLog();
		newLog.set("AdminID", admin.getInt("AdminID"));
		if(admin.getStr("LoginName").equals("admin"))
			newLog.set("LogType",101);//超级管理员退出
		else
		   newLog.set("LogType",4);//后台管理员退出
		newLog.set("Detail","退出登录IP:"+sIP);
		newLog.set("CreateTime",Util.getDateTime());
		newLog.set("State",0);
		newLog.save();
		
		removeSessionAttr(GlobalVar.WEBADMIN);
		redirect("/web_admin/login");
	}

	
	public void getMenuList() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		
		String sql = "SELECT * FROM uv_admin_menu WHERE AdminID="+admin.getInt("AdminID")+" order by MenuSort";
		List<Record> list = Db.find(sql);
		setAttr("recs", list);
		renderJson();
	}
		
	
	   
    /////////////////////////////////////////////////
	
	
}


