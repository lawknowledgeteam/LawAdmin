package com.liyang.controller;

import java.util.List;

import com.liyang.common.GlobalVar;
import com.liyang.common.TokenManager;
import com.liyang.common.Util;
import com.alibaba.druid.util.StringUtils;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.liyang.model.Admin;
import com.liyang.model.User;
import com.liyang.security.UserLoginSafe;
import com.liyang.security.loginSafe;


public class MobileController extends Controller {
	private static final String RANDOM_CODE_KEY = "1";// 生成验证码会用到
	
    public void index() {
       render("web_mobile/mobLogin.html");
    }
    
    /**
	 * 生成验证码
	 */
	public void img() {
		CaptchaRender img = new CaptchaRender();
		img.setView(RANDOM_CODE_KEY);
		render(img);
	}

	public void loginPost() {

		System.out.println("loginPost");
		// 防暴力检测
		
		if (getPara("LoginName") == null) {
			redirect("web_mobile/mobLogin.html");
			return;
		}//*/
		
		String strLoginName=getPara("LoginName");
		if(strLoginName.length()>6)
		{
			setAttr("Msg", "账号位数过长，请输入正确的账号！");
			render("404/error.html");
			return;
		}
		if (UserLoginSafe.isExist(strLoginName)) {
			setAttr("Msg", "密码输入错误次数过多，请十分钟后再试！");
			render("404/error.html");
			return;
		} 
		else 
		{
			
			User curUser = User.dao.findFirst("select * from tb_user where MobilePhone=?", strLoginName);
			if (curUser == null) {
				LogController.addLog(1, 1000, "用户登录账号'"+strLoginName+"'不存在!");
				
				System.out.println("账号不存在！MobilePhone:"+strLoginName);
				renderHtml(Util.getResult("0001", "账号不存在！"));	
				return;
			}
			String sPsw=curUser.getStr("PasswordMD5");
			String sPsw2=getPara("pswMD5");
			if (sPsw2.equals(sPsw)) 
			{//密码正确
				
				
				String ip = Util.getIpAddress(getRequest());
				if (StrKit.isBlank(ip) || ip.equals("0.0.0.0") ||ip.equals("0:0:0:0:0:0:0:1")) {
					ip = "127.0.0.1";
				}
				
				String curDateTime=Util.getDateTime();
				curUser.set("LastIPAddress", ip);
				curUser.set("LastLoginTime",curDateTime);
				curUser.update();
				
				String sToken=com.liyang.common.TokenManager.getMe().generateToken(curUser);
				
				System.out.println("登录成功，UserID="+curUser.getInt("UserID"));
				LogController.addLog(1, 1000, "用户账号'"+strLoginName+"'登录成功!");
				if(curUser.getInt("State")==1)
				{//停用	
					renderHtml(Util.getResult("0001", "用户账号已经过期或停用，请与管理员联系！"));
				}
				else if(curUser.getInt("State")==0)
				{//
					setAttr("sToken", sToken);
				    render("web_mobile/index_tab.html");
					//redirect(Util.getResult("0000", "/web_front/mobQuestionnaireWelcome.html"));
				}
				else
				{
					renderHtml(Util.getResult("0001", "用户账号已经过期或删除，请与管理员联系！"));
				}
					
					
				return;
			} 
			else 
			{//密码错误
				LogController.addLog(1, 1000, "用户账号'"+strLoginName+"'密码错误!");
				
				if (getSessionAttr(strLoginName) != null) {
					int left = getSessionAttr(strLoginName);
					left--;
					if (left == 0) {
						loginSafe ls = new loginSafe(strLoginName,this);
						Thread t = new Thread(ls);
						t.start();
					} else
						setSessionAttr(strLoginName, left);
				} else
			    setSessionAttr(strLoginName, 5);
				renderHtml(Util.getResult("0001", "账号或密码错误！"));	
			}
		}
	}

		

	public void quit() {	
		//User user=getSessionAttr(GlobalVar.WEBUSER);
		//判断用户是否登录
		//user.set(GlobalVar.WEBUSER, "").update();
		String sToken = getPara("sToken");
        if (StringUtils.isEmpty(sToken)) {
        	renderHtml(Util.getResult("0001", "sToken不能为空!"));
            return;
        }
        User user = TokenManager.getMe().validate(sToken);
        if(user!=null)
		     LogController.addLog(10, 1000, "UserID'"+user.getStr("UserID")+"'退出!");
		TokenManager.getMe().removeToken(sToken);
		//removeSessionAttr(GlobalVar.WEBUSER);
		render("web_front/mobLogin.html");
	}
	
}
