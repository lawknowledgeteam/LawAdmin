package com.liyang.controller;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liyang.common.GlobalVar;
import com.liyang.common.RoleAuthSettings;
import com.liyang.common.Util;
import com.liyang.model.Admin;
import com.liyang.model.AdminLog;
import com.liyang.model.Log;

public class LogController extends Controller {

	
	
	public void web_showLog() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		LogController.addLog(3, admin.getInt("AdminID"), "查看日志记录");
		
		setAttr("authExport", RoleAuthSettings.getValue(admin.getInt("RoleID")+"logExport"));
		
		render("/web_admin/loglist.html");
	}


	
	/**
	 * 删除日志
	 */

	public void web_delete(){
		
		int id = getParaToInt("ID");
		// 判断ID是否有效
		if (id > 0) {
			int count = Db.update("update tb_admin_log set State = 1 where LogID=?",id);	
			if (count > 0) {
				renderHtml(Util.getSuccessResult());// 返回0000，成功
			} else
				renderHtml(Util.getResult("0001", "删除失败"));
		} else {
			renderHtml(Util.getResult("0001", "删除失败！"));
		}
	}
	
	/**
	 * 得到管理员的总记录数
	 */
	public void web_getCount() {
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		
		String StartDate = this.getPara("StartDate");
		String EndDate = this.getPara("EndDate");
		
		String sql = "SELECT Count(LogID) FROM tb_admin_log ";   //where LogType<100 and CreateTime>= '"+StartDate+" 0:0:0' AND CreateTime<='"+EndDate+" 23:59:59'
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

		String StartDate = this.getPara("StartDate");
		String EndDate = this.getPara("EndDate");
		String sqlFromWhere = "FROM tb_admin_log,list_log_type,tb_admin WHERE  tb_admin_log.AdminID=tb_admin.AdminID and tb_admin_log.LogType=list_log_type.LogType ";
		//FROM tb_admin_log,list_log_type,tb_admin WHERE tb_admin_log.LogType<100 and tb_admin_log.AdminID=tb_admin.AdminID and tb_admin_log.LogType=list_log_type.LogType AND (tb_admin_log.CreateTime>='"+StartDate+" 0:0:0') AND (tb_admin_log.CreateTime<='"+EndDate+" 23:59:59')";
		List<Record> lists = Db.paginate(page, pageSize, "select LogID,LoginName,LogTypeName, Detail, tb_admin_log.CreateTime ", sqlFromWhere+"Order by tb_admin_log.CreateTime DESC").getList();
		setAttr("recs", lists);
		renderJson(); 
	}
	

	
	public void getExportLogList(){
		String sStart=getPara("StartDate")+" 00:00:00";
		String sEnd=getPara("EndDate")+" 23:59:59";
		List<Record> list = Db.find("select LogID,LoginName,LogTypeName, Detail, tb_log.CreateTime FROM tb_log,list_log_type,tb_admin WHERE tb_log.LogType=list_log_type.LogType AND tb_admin.AdminID=tb_log.AdminID AND (tb_log.CreateTime>='"+sStart+" 0:0:0') AND (tb_log.CreateTime<='"+sEnd+" 23:59:59') Order by tb_log.CreateTime DESC");
		setAttr("recs", list);
		renderJson();
	}
	
	public static boolean addLog(int LogType, int AdminID, String Detail)
	{
		Log newLog=new Log();
		newLog.set("AdminID", AdminID);
    	newLog.set("LogType",LogType);//后台管理员登录
		newLog.set("Detail", Detail);
		newLog.set("CreateTime",Util.getDateTime());
		newLog.set("State",0);
		return newLog.save();
	}	

}


