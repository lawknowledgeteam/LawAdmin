package com.liyang.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liyang.common.GlobalVar;
import com.liyang.common.RoleAuthSettings;
import com.liyang.common.Util;
import com.liyang.model.Admin;
import com.liyang.model.Case;
import com.liyang.security.AdminInterceptor;

public class AnalyseController extends Controller {
	
	public void web_showResidenceTimeList(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
		render("/web_admin/analyse.html");
	}

	public void web_showHistory(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));
			return;
		}
		render("/web_admin/analyseUser.html");
	}

	public void web_showUserBycharts(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));
			return;
		}
		render("/web_admin/analyseUserBycharts.html");
	}


	public void getHomepageData(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
		
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			//String dateToday =Util. getDate(); 
			String dateToday = "2018-08-07";
			String date7Ago = "2018-08-01";
			List<Record> rec7Days= Db.find("select * from tb_report where ReportType=1 and ReportDate>='"+date7Ago+"' and ReportDate<='"+dateToday+"' order by ReportDate");
			setAttr("rec7Days", rec7Days); 
			
			Record recAll = Db.findFirst("select * from tb_report where ReportType=0");
			setAttr("recAll", recAll); 
			this.renderJson();

	}


    public void web_getListPaging()
    {
        int page = 1;
        int pageSize = 10;
        if (getParaToInt("pageIndex") != null) {
            page = getParaToInt("pageIndex");
            pageSize = getParaToInt("pageSize");
        }

        String sqlFromWhere = "FROM tb_case ";

        if (!getPara("keyWord").equals("")) {
            String keyWord = getPara("keyWord");
            sqlFromWhere += " where ((CaseName LIKE '%" + keyWord + "%') )";
        }

        List<Record> lists = Db.paginate(page, pageSize,true,"select CaseKind ,count(*) as num", "from tb_case group by CaseKind").getList();
        setAttr("recs", lists);
        renderJson();
    }
    public void web_add() {

		Case temp = new Case();
		setAttr("IsNew", 1);
		setAttr("rec", temp);
		render("/web_admin/addanalyse.html");
	}


    public void web_getCount() {
        String sql = "select  count(distinct CaseKind) \n" +
                "from tb_case ";
        if (!getPara("keyWord").equals("")) {
            String keyWord = getPara("keyWord");
            sql += " where (CaseName LIKE '%" + keyWord + "%')";
        }
        long RecordCount = Db.queryLong(sql);
        setAttr("RecordCount", RecordCount);
        renderJson();
    }

	public void web_showCaseKind(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
		List<Record> recs=Db.find("select * from uv_org_device_info where OrganizationID="+admin.getInt("OrganizationID"));
		set("DeviceLists",recs);
		render("/web_admin/analyse.html");
	}

	public void web_getUserTraceCount() {
		String sql = "select  count(*) \n" +
				"FROM tb_trace LEFT OUTER JOIN  tb_case on tb_case.CaseID = tb_trace.CaseID ";
		if (!getPara("keyWord").equals("")) {
			String keyWord = getPara("keyWord");
			sql += " ,tb_trace.CaseKind LIKE '%" + keyWord + "%'";
		}
		long RecordCount = Db.queryLong(sql);
		setAttr("RecordCount", RecordCount);
		renderJson();
	}
	public void getUserTraceList() {
		int page = 1;
		int pageSize = 10;
		if (getParaToInt("pageIndex") != null) {
			page = getParaToInt("pageIndex");
			pageSize = getParaToInt("pageSize");
		}

		String sqlFromWhere = "FROM tb_trace LEFT OUTER JOIN  tb_case on tb_case.CaseID = tb_trace.CaseID ORDER BY tb_trace.RecordID";

		if (!getPara("keyWord").equals("")) {
			String keyWord = getPara("keyWord");
			sqlFromWhere += ",tb_trace.CaseKind LIKE '%" + keyWord + "%'";
		}

		List<Record> lists = Db.paginate(page, pageSize, "SELECT tb_trace.*,tb_case.`CaseName`,tb_trace.`EndTime`-tb_trace.`LastViewTime` AS BrowseLength", sqlFromWhere).getList();
		setAttr("recs", lists);
		renderJson();
	}

	public void getBrandsCount(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));
			return;
		}
		
		String startDate = get("startDate");
		String endDate = get("endDate");
		
		String DeviceIDs=get("DeviceIDs");
		
		String strSql="select VendorName, Count(ObjectID) as BrandCount from uv_object_vendor  ";
		if(DeviceIDs.length()>0)
			 strSql=strSql+" where VendorName is not null AND LastDeviceID in ("+DeviceIDs+") AND LastUpdateTime>='"+startDate+"' AND LastUpdateTime<='"+endDate+" 23:59:59'";
		else
			strSql=strSql+" where VendorName is not null AND  LastUpdateTime>='"+startDate+"' AND LastUpdateTime<='"+endDate+" 23:59:59'";
		List<Record> recs=Db.find( strSql +" Group by VendorName Order by Count(ObjectID) Desc limit 0,10" );
		set("data",recs);
        renderJson(); 
	}

	public void web_getUserCharts(){
		String startDate = get("startDate");
		String endDate = get("endDate");

		int page = 1;
		int pageSize = 10;
		String sqlFromWhere = " FROM (SELECT CaseKind AS name,COUNT(*) AS max  FROM tb_trace  GROUP BY CaseKind  DESC  LIMIT 6) AS temp  ORDER BY max DESC";
		if(!startDate.equals("") && !endDate.equals("")){
			sqlFromWhere = "FROM  (SELECT CaseKind AS name,COUNT(*) AS max  FROM tb_trace	WHERE  LastViewTime between  '"
					+startDate+"' AND  '" +endDate+ "' GROUP BY CaseKind  DESC  LIMIT 6) AS temp  ORDER BY max DESC";
		}
		System.out.println(sqlFromWhere);
		List<Record> lists = Db.paginate(page, pageSize, "SELECT *   ", sqlFromWhere).getList();
		renderJson(lists);
	}
	
/*	public void web_showObjects() {
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));
			return;
		}
				
		List<Record> recs=Db.find( "select * from uv_admin_room where OrganizationID="+admin.getInt("OrganizationID") );
		set("rooms",recs);
		
		List<Record> recs2=Db.find( "select * from uv_admin_room_device where OrganizationID="+admin.getInt("OrganizationID") );
		set("devices",recs2);
		render("/web_admin/webAnalyseObjects.html");

	}

 */
/*	public void web_showObjectCount() {
		 Admin admin = getSessionAttr(GlobalVar.WEBADMIN); 
		 
		 if (admin == null) {
		     renderHtml(Util.getResult("0001", "请先登录本系统!")); 
		     return; 
		 }
		
		// 获取前端输入的时间段
		String startDate = get("startDate");
		String endDate = get("endDate");
		String deviceIDs=get("deviceIDs");
		int statType= this.getInt("statType");

		if(statType==1)
		{//按小时查询
		}
		else if(statType==2)
		{//按天查询
			List<Record> recs = Db.find(
					"select ScanDate from uv_stat_deviceday where ScanDate >='" + startDate + "' AND ScanDate <='" + endDate + "' group by ScanDate");	
			setAttr("ScanDate", recs);
			
			List<Record> recs2 = Db.find(
					"select DeviceID,Location,ScanDate,ObjectCount from uv_stat_deviceday where DeviceID in ("+deviceIDs+")  AND ScanDate >='" + startDate + "' AND ScanDate <='" + endDate + "' order by Location, ScanDate");	
			
			int DeviceID=0;
			int index=0;
			JSONArray jsonSerial=null;
			for (Record oneDate : recs2) { 
				if(DeviceID!=oneDate.getInt("DeviceID"))
				{//处理不同设备数据
					if(DeviceID!=0)
					{
						setAttr("serial"+index, jsonSerial);
					}
					DeviceID=oneDate.getInt("DeviceID");
					jsonSerial = new JSONArray();
					index++;
				}
				JSONObject jsonOne = new JSONObject();
    			jsonOne.put("Location", oneDate.getStr("Location"));
    			jsonOne.put("ScanDate", oneDate.getStr("ScanDate"));
    			jsonOne.put("ObjectCount", oneDate.getStr("ObjectCount"));
    			jsonSerial.add(jsonOne);
			}
			if(DeviceID!=0)
			{//设置最后一个设备数据
				setAttr("serial"+index, jsonSerial);
			}
			setAttr("serialCount", index);
			renderJson();

		}
		
	}

 */
}


