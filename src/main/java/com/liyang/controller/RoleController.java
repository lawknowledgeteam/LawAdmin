package com.liyang.controller;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liyang.common.GlobalVar;
import com.liyang.common.RoleAuthSettings;
import com.liyang.common.Util;
import com.liyang.model.Admin;
import com.liyang.model.Role;

public class RoleController extends Controller {

	
	
	public void web_showRoleList() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		
		setAttr("authAdd", RoleAuthSettings.getValue(admin.getInt("RoleID")+"sysRoleAdd"));
		setAttr("authEdit", RoleAuthSettings.getValue(admin.getInt("RoleID")+"sysRoleEdit"));
		setAttr("authState", RoleAuthSettings.getValue(admin.getInt("RoleID")+"sysRoleState"));
		
		render("/web_admin/webRoleList.html");
	}
	

	/**
	 * 角色数量
	 */

	//@Before(AdminInterceptor.class)
	public void web_getCount() {
		String sql = "SELECT Count(RoleID) FROM sys_role";
		if (!getPara("keyWord").equals("")) {
			String keyWord = getPara("keyWord");
			sql += " where ((RoleName LIKE '%" + keyWord + "%') or (RoleDescription LIKE '%" + keyWord + "%'))";
		}
		long RecordCount = Db.queryLong(sql);
		setAttr("RecordCount", RecordCount);
		renderJson();
	}
	
	/**
	 * 分页查询角色信息
	 */
	//@Before(AdminInterceptor.class)
	public void web_getListPaging()
	{
		int page = 1;
		int pageSize = 10;
		if (getParaToInt("pageIndex") != null) {
			page = getParaToInt("pageIndex");
			pageSize = getParaToInt("pageSize");
		}

		String sqlFromWhere = "FROM sys_role ";

		if (!getPara("keyWord").equals("")) {
			String keyWord = getPara("keyWord");
			sqlFromWhere += " where ((RoleName LIKE '%" + keyWord + "%') or (RoleDescription LIKE '%" + keyWord + "%'))";
		}

		List<Record> lists = Db.paginate(page, pageSize, "select * ", sqlFromWhere).getList();
		setAttr("recs", lists);
		renderJson(); 
	}
	
	//@Before(AdminInterceptor.class)*/
	public void web_add(){

		Role temp = new Role();
		setAttr("IsNew", 1);
		setAttr("rec", temp);
		render("/web_admin/webRoleInfo.html");
	}
	
	public void web_change(){

		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
		int id = getParaToInt("ID");
			
		Role temp = Role.dao.findById(id);
		setAttr("IsNew", 0);
		setAttr("rec", temp);
		render("/web_admin/webRoleInfo.html");
	}
	

		public void web_saveInfo(){
			Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
			if ( admin== null) {
				renderHtml(Util.getResult("0001", "请先登录本系统!"));	
				return;
			}
			int id=0;
			boolean ret=false;
			
			Role recRole=getModel(Role.class,"info");
			if(getParaToInt("IsNew")==0)//修改信息
			{
				id=getParaToInt("ID");
				Role temp=Role.dao.findById(id);
				if(temp!=null)
				{
					temp.set("RoleName", recRole.getStr("RoleName"));
					temp.set("RoleDescription",recRole.getStr("RoleDescription"));
					ret=temp.update();
					LogController.addLog(8, admin.getInt("AdminID"), "修改角色信息,名称="+temp.getStr("RoleName")+",描述="+temp.getStr("RoleDescription"));
					
				}
				else
				{
					renderHtml(Util.getResult("0001", "调用接口参数错误，保存失败！"));
					return;
				}
			}
			else//新增信息
			{
				//判断该角色名称是否已经存在
				Record temp = Db.findFirst("select * from sys_role where RoleName=?",recRole.getStr("RoleName"));
				if(temp==null)
				{
					recRole.set("State",0);
					ret=recRole.save();
					LogController.addLog(6, admin.getInt("AdminID"), "添加角色信息,名称="+recRole.getStr("RoleName")+",描述="+recRole.getStr("RoleDescription"));
					
				}
				else
				{
					renderHtml(Util.getResult("0002", "该角色名称已经存在，请更换名称！"));
					return;
				}
			}
			
			if(ret){
				renderHtml(Util.getResult("0000", "保存成功！"));
			}
			else
			{
					renderHtml(Util.getResult("0001", "保存失败！"));
			}
		}


    public void web_changeState()
   {
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
			int id = getParaToInt("ID");
			int state=getParaToInt("newState");
			boolean ret=false;
			
			// 判断ID是否有效
			if(id > 0)
			{	
				if(state==1)//停用
				{
					Role rec=Role.dao.findById(id);
					rec.set("State",1);
					ret=rec.update();
					if(ret)
					{
						LogController.addLog(8, admin.getInt("AdminID"), "停用角色,名称="+rec.getStr("RoleName"));
						
						renderHtml(Util.getResult("0000", "停用成功！"));
					}
					else
					{
						renderHtml(Util.getResult("0001", "停用失败"));
					}
				}
				else//启用
				{
					Role rec=Role.dao.findById(id);
					rec.set("State",0);
					ret=rec.update();
					if(ret)
					{
						LogController.addLog(8, admin.getInt("AdminID"), "启用角色,名称="+rec.getStr("RoleName"));
						
						renderHtml(Util.getResult("0000", "启用成功！"));
						return;
					}
					else
					{
						renderHtml(Util.getResult("0001", "启用失败"));
						return;
					}
				}
			}
			else
				renderHtml(Util.getResult("0001", "传入ID值错误！"));
				
		}
	public void web_showRoleRight() {
		String sql = "select RoleID,RoleName  FROM sys_role ";
		List<Record> lists = Db.find(sql);
		setAttr("roles", lists);
		
		sql = "select MenuID,MenuType, MenuName  FROM sys_menu ";
		List<Record> lists2 = Db.find(sql);
		setAttr("menus", lists2);
		
		sql = "select MenuAuthID, Description, MenuID, MenuName  FROM uv_all_menu_auth ";
		List<Record> lists3 = Db.find(sql);
		setAttr("menuAuths", lists3);
		
		
		render("/web_admin/webRoleRight.html");
	}
	
	public void getRoleAuths() {
		int roleID = getParaToInt("roleID");
		List<Record> lists = Db.find("select MenuAuthID from sys_role_menu where RoleID="+roleID);
		setAttr("recs", lists);
		renderJson();
	}
	public void saveRoleAuths() {
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
		
		int roleID = getParaToInt("roleID");
		String[] roleAuthIDs= getPara("auths").split(",");
		String sql="";
		int ret=0;
		if(roleAuthIDs==null)
		{
			renderHtml(Util.getResult("0001", "保存失败！"));
			return;
		}
		
		LogController.addLog(8, admin.getInt("AdminID"), "修改角色权限,角色ID="+roleID);
		
		Db.update("delete from sys_role_menu where RoleID="+roleID);
		for(int i=0;i<roleAuthIDs.length;i++)
		{
			   sql="insert into sys_role_menu (RoleID,MenuAuthID) values("+roleID+","+roleAuthIDs[i]+")";
			   ret=Db.update(sql);
		}
		
		RoleAuthSettings.init();
		if(ret>0)
		    renderHtml(Util.getResult("0000", "保存成功！"));
		else
			renderHtml(Util.getResult("0001", "保存失败！"));
	}
	
}


