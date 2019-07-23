package com.liyang.controller;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liyang.common.GlobalVar;
import com.liyang.common.Util;
import com.liyang.model.Admin;
import com.liyang.security.AdminInterceptor;

public class AdminController extends Controller {

	
	
	public void web_showList() {
		render("/web_admin/webAdminList.html");
	}
	
	/**
	 * 添加管理员
	 */

	public void web_add() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		
		if(admin.getInt("RoleID")!=1) //普通管理员
		{
			renderHtml(Util.getResult("0001", "您无权添加管理员。"));
			return;
		}
		else
		{
			//绑定角色下拉框
			List<Record> lists=Db.find("select * from sys_role");
			setAttr("roleLists",lists);
			
			setAttr("RoleID", 0);
			
			
			render("/web_admin/webAdminInfo.html");
		}
		    
	}
	
	/**
	 * 修改管理员信息
	 */
	public void web_changeAdmin() {
		int sid = 1;
		if (getParaToInt("ID") != null) {
			sid = getParaToInt("ID");
		}
		//绑定角色下拉框
		List<Record> lists=Db.find("select * from sys_role");
		setAttr("roleLists",lists);
		
		Record rec = Db.findFirst("SELECT * FROM tb_admin WHERE AdminID=" + sid);
		setAttr("rec", rec);
		
		setAttr("RoleID", rec.getInt("RoleID"));	
		
		render("/web_admin/webAdminInfo.html");
	}
	
	
	/**
	 * 删除管理员信息
	 */

	
	/**
	 * 得到管理员的总记录数
	 */
	public void web_getCount() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		
		String sql = "SELECT Count(AdminID) FROM tb_admin,sys_role WHERE sys_role.RoleID=tb_admin.RoleID ";
		if(admin.getInt("RoleID")!=1) //普通管理员
			sql+=" AND AdminID="+admin.getInt("AdminID");
		if (!getPara("keyWord").equals("")) {
			String keyWord = getPara("keyWord");
			sql += " AND ((LoginName LIKE '%" + keyWord + "%')"+" OR (RoleName LIKE '%" + keyWord + "%'))";
		}
		long RecordCount = Db.queryLong(sql);
		setAttr("RecordCount", RecordCount);
		renderJson();
	}
	

	/**
	 * 分页查询管理员的记录
	 */

	public void web_getListPaging() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		
		int page = 1;
		int pageSize = 10;
		if (getParaToInt("pageIndex") != null) {
			page = getParaToInt("pageIndex");
			pageSize = getParaToInt("pageSize");
		}

		String sqlFromWhere = "FROM tb_admin,sys_role WHERE sys_role.RoleID=tb_admin.RoleID";
		if(admin.getInt("RoleID")!=1) //普通管理员
			sqlFromWhere+=" and AdminID="+admin.getInt("AdminID");
		if (!getPara("keyWord").equals("")) {
			String keyWord = getPara("keyWord");
			sqlFromWhere += " AND ((LoginName LIKE '%" + keyWord + "%')"+" OR (RoleName LIKE '%" + keyWord + "%'))";
		}

		List<Record> lists = Db.paginate(page, pageSize, "select tb_admin.*,sys_role.RoleName, '0' as CompanyNames", sqlFromWhere).getList();
		for(int i=0;i<lists.size();i++)
		{
			String sCom=lists.get(i).getStr("CompanyIDs");
			if(sCom.equals("0"))
				lists.get(i).set("CompanyNames", "全部");
			else
			{ 
				String[] sComs=sCom.split(",");
				String strComs="";
				for(int j=0;j<sComs.length;j++)
				{
					if(j==0)
					   strComs=Db.findFirst("select ShortCompanyName from tb_company where CompanyID="+sComs[j]).getStr("ShortCompanyName");
					else 
						strComs+=","+Db.findFirst("select ShortCompanyName from tb_company where CompanyID="+sComs[j]).getStr("ShortCompanyName");
					
				}
				lists.get(i).set("CompanyNames",strComs);
			}
				
				
		}
		setAttr("recs", lists);
		renderJson(); 
	}

	/**
	 * 保存管理员信息
	 */
	public void web_saveAdminInfo() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		
		int id=0;
		boolean ret=false;
		boolean flag=true;
		
		Admin recAdmin=getModel(Admin.class,"admin");
		
		if(getParaToInt("ID")!=null)//修改
		{
			id=getParaToInt("ID");
			recAdmin.set("AdminID", id);
			
			Admin temp = Admin.dao.findFirst("select AdminID from tb_admin where LoginName=?",recAdmin.getStr("LoginName"));
			if(temp!=null)
			{
				if(temp.getInt("AdminID")!=id)
				{
					renderHtml(Util.getResult("0002", "该登录账号已被注册,请更换登录账号名称！"));
					return;
				}
			}
			
			Admin test=Admin.dao.findById(id);
			String temp2=recAdmin.getStr("PasswordMD5");
			if(temp2==null)//密码不变
			{
				recAdmin.set("PasswordMD5", test.get("PasswordMD5"));
			}
			else
			{
				recAdmin.set("PasswordMD5",Util.Md5(temp2).toLowerCase());
			}
			recAdmin.set("CreateTime", test.get("CreateTime"));
			recAdmin.set("LastLoginTime", test.get("LastLoginTime"));
			recAdmin.set("LastIPAddress", test.get("LastIPAddress"));
			recAdmin.set("State", test.get("State"));
			
			ret=recAdmin.update();
			LogController.addLog(8, admin.getInt("AdminID"), "修改后台管理员信息成功，登录名="+recAdmin.getStr("LoginName")+",RoleID="+recAdmin.getInt("RoleID"));
		}
		else//新增 
		{
			
			//判断用户名是否已被注册
			Admin temp = Admin.dao.findFirst("select * from tb_admin where LoginName=?",recAdmin.getStr("LoginName"));
			if(temp==null)
			{
				
				String temp2=recAdmin.getStr("PasswordMD5");
				if(temp2==null)//未设置密码初始化为123456
				{
					recAdmin.set("PasswordMD5",Util.Md5("123456").toLowerCase());
				}
				else
				{
					recAdmin.set("PasswordMD5",Util.Md5(temp2).toLowerCase());
				}
				recAdmin.set("CreateTime", Util.getDateTime());
				
				recAdmin.set("State",0);
				ret=recAdmin.save();
				LogController.addLog(6, admin.getInt("AdminID"), "添加后台管理员信息成功，登录名="+recAdmin.getStr("LoginName")+",RoleID="+recAdmin.getInt("RoleID"));
			}else{
				flag=false;
			}
			
		}
		
		if(ret){
			renderHtml(Util.getResult("0000", "保存成功！"));
		}else{
			if(flag==false){
				renderHtml(Util.getResult("0002", "该登录账号已被注册,请更换登录账号名称!"));
			}else{
				renderHtml(Util.getResult("0001", "保存失败！"));
			}	
		}
	
	}


	public void web_changeState()
	{
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		int id = getParaToInt("ID");
		int state=getParaToInt("State");
		boolean ret=false;
		
		// 判断ID是否有效
		if (id > 0) 
		{	
			if(state==0)//停用
			{
				Admin recAdmin=Admin.dao.findById(id);
				recAdmin.set("State",1);
				ret=recAdmin.update();
				if(ret)
				{
					LogController.addLog(8, admin.getInt("AdminID"), "停用"+recAdmin.getSql("LoginName")+"管理员账号! RoleID="+recAdmin.getInt("RoleID"));
					renderHtml(Util.getResult("0000", "停用成功！"));
				}
				else
				{
					renderHtml(Util.getResult("0001", "停用失败"));
				}
			}
			else//启用
			{
				Admin recAdmin=Admin.dao.findById(id);
				recAdmin.set("State",0);
				ret=recAdmin.update();
				if(ret)
				{
					LogController.addLog(8, admin.getInt("AdminID"), "启用"+recAdmin.getSql("LoginName")+"管理员账号! RoleID="+recAdmin.getInt("RoleID"));
					renderHtml(Util.getResult("0000", "启用成功！"));
				}
				else
				{
					renderHtml(Util.getResult("0001", "启用失败"));
				}
			}
		}
	}
	
	/////////////////////////////////////////////
	/**
	 * 管理员设置，只能修改昵称密码
	 */
	public void web_showAdminChangeInfo() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}

		
		setAttr("rec", admin);
		
		render("/web_admin/webAdminChangeInfo.html");
	}
	
	//保存管理员设置结果，只能修改昵称密码
	public void saveInfo() {
		int id=0;
		boolean ret=false;
		boolean flag=true;
		
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		id=getParaToInt("AdminID");
		if(admin.getInt("AdminID")==id)//修改
		{
			String temp2=admin.getStr("PasswordMD5").toLowerCase();
			String oldMd5=Util.Md5(this.getPara("oldPass")).toLowerCase();
			if(!temp2.equals(oldMd5))
			{
				renderHtml(Util.getResult("0001", "旧密码不正确，无法修改!"));
			    return;
			}
			admin.set("NickName", getPara("NickName"));
			admin.set("PasswordMD5", Util.Md5(getPara("newPass")).toLowerCase());
			
			ret=admin.update();
			if(ret)
				renderHtml(Util.getResult("0000", "管理员信息修改成功!"));
			else
				renderHtml(Util.getResult("0000", "管理员信息修改失败!"));
		}
		else
			renderHtml(Util.getResult("0001", "传入参数错误!"));
	}
	
}


