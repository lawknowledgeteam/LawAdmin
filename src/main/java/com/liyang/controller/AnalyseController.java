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
import com.liyang.security.AdminInterceptor;

public class AnalyseController extends Controller {
	
	public void web_showResidenceTimeList(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
		render("/web_admin/webResidenceTimeList.html");
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
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public void web_showOldNewCustomerDist()
	{
		render("/web_admin/webOldNewCustomer.html");
	}
	public void web_showHotmap()
	{
		render("/web_admin/webHotmap.html");
	}
	
	/**
	 * @description: 停留时长统计数据条数
	 * @author: wxy
	 * @date: 2019-05-11 17:12
	 * @param :
	 * @return:
	 */
	public void web_getResidenceTimeCount(){
		Admin admin = getSessionAttr(GlobalVar.WEBADMIN);
		if (admin == null) {
			redirect("/web_admin/login.html");
			return;
		}
		//查询的开始时间和结束时间
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		String sql = "select count(ResidenceTimeID) from tb_residence_time where CreateTime >'"+startTime+"' and "+"CreateTime < '"+endTime+"'";
		if (!getPara("keyWord").equals("")) {
			String keyWord = getPara("keyWord");
			List<Long> objectMacs = Db.query("select objectMac from tb_object where MacAddress like '%" + keyWord + "%'");
			String macs = "(";
			for (int i=0;i<objectMacs.size();i++){
				if(i==objectMacs.size()-1){
					macs = macs+objectMacs.get(i)+")";
				}else {
					macs = macs + objectMacs.get(i) + ",";
				}
			}
			sql +=" and objectMac in "+macs;
		}
		Integer recordCount = Db.queryInt(sql);
		setAttr("recordCount",recordCount);
		renderJson();
	}

	/**
	 * @description: 分页查询停留时长统计信息
	 * @author: wxy
	 * @date: 2019-05-11 17:12
	 * @param :
	 * @return:
	 */
	public void web_getResidenceTimeList(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
		int page = 1;
		int pageSize = 10;
		if (getParaToInt("pageIndex") != null) {
			page = getParaToInt("pageIndex");
			pageSize = getParaToInt("pageSize");
		}
		//查询的开始时间和结束时间
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		String sql = "from uv_object_stay_time where CreateTime > '"+startTime+"' and "+"CreateTime < '"+endTime+"'";
		if (!getPara("keyWord").equals("")) {
			String keyWord = getPara("keyWord");
			List<Long> objectMacs = Db.query("select objectMac from tb_object where MacAddress like '%" + keyWord + "%'");
			String macs = "(";
			for (int i=0;i<objectMacs.size();i++){
				if(i==objectMacs.size()-1){
					macs = macs+objectMacs.get(i)+")";
				}else {
					macs = macs + objectMacs.get(i) + ",";
				}
			}
			sql +=" and ObjectMac in "+macs;
		}
		//计算过后存储的数据放在list<Record>中
		List<Record> residenceTimeRecords = Db.paginate(page,pageSize,"select *",sql+"order by CreateTime DESC,TotalResidenceTime DESC").getList();
		for(Record rec:residenceTimeRecords){
			String objectMac = rec.getStr("ObjectMac");
			Record recf = Db.findFirst("select MacAddress,Description from tb_object where ObjectMac = " + objectMac);
			String mac = recf.getStr("MacAddress");
			String description = recf.getStr("Description");
			rec.set("MacAddress",mac);
			rec.set("ObjectName",description);
		}
		setAttr("residenceTimeRecords",residenceTimeRecords);
		//将数据传递到前端
		renderJson();
	}
	/**
	 * @description:
	 * @author: wxy
	 * @date: 2019-05-11 17:11
	 * @param : 物体客流统计
	 * @return:
	 */
	public void web_getCustomer(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));
			return;
		}
		String startTime = get("startTime");
		String endTime = get("endTime");
		//旧客户信息
		List<Record> oldCustomers = Db.find("select * from tb_object where CreateTime < '" + startTime+"'");
		//新客户信息
		List<Record> newCustomers = Db.find("select * from tb_object where CreateTime > '" + startTime+"' and CreateTime < '"+endTime+"'");
        set("oldCustomersCount",oldCustomers.size()).set("newCustomersCount",newCustomers.size());
        renderJson();
	}
	
	public void getHotmapResult(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));
			return;
		}
		HashMap resultMap = new HashMap();
		//获取相应格式的时间
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String now = sdf.format(date);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		//前端传来的参数，统计分类
		int flag = getInt("flag");
		//相差天数
		int days = 0;
		Date startDate = null;
		Date endDate = null;
		if(flag == 0){
			//获取任意两个时间间隔的报表
			//获取前台传过来的时间
			String startTime = get("startTime");
			String endTime = get("endTime");
			try {
				startDate = sdf.parse(startTime);
				endDate = sdf.parse(endTime);
			}catch (Exception e){
				System.out.println(e);
			}
			//时间差
			days = (int)((endDate.getTime()-startDate.getTime())/(24*60*60*1000));
		}else if(flag == 1){
			days = 7;
			//过去七天的报表
		}else{
			//过去30天报表
			days = 30;
		}
		//查询到设备信息
		//根据查询到的设备以及品牌信息来进行分类
		for(int i =0;i<days;i++){
			Calendar calendar1 = new GregorianCalendar();
			calendar1.setTime(date);
			calendar1.add(Calendar.DATE,(-days+i));
			String firstPastDay = sdf.format(calendar1.getTime());
			calendar1.setTime(date);
			calendar1.add(Calendar.DATE,(-days+1+i));
			String secondPastDay = sdf.format(calendar1.getTime());
			System.out.println(firstPastDay+"*******"+secondPastDay);
			List<Record> objectList = Db.find("select * from uv_object_vendor where LastUpdateTime > '"+firstPastDay+"' and LastUpdateTime < '"+secondPastDay+"'");
			HashMap<String, HashMap<String,Integer>> roomMap = new HashMap<>();
			for(Record record:objectList){
				//得到该设备的品牌信息
				String brand = record.getStr("VendorName");
				if(brand !=null){
					//得到采集该设备信息的房间名
					String LocationRoom = record.getStr("LastLocation");
					if(roomMap.containsKey(LocationRoom)){
						//如果房间map中已有设备
						//检验是否有该品牌
						if(roomMap.get(LocationRoom).containsKey(brand)){
							//如果包含，数量+1
							int count = roomMap.get(LocationRoom).get(brand);
							roomMap.get(LocationRoom).put(brand,++count);
						}else{
							//如果不包含，那么将这个品牌加入到map中，赋值设备数量为1
							roomMap.get(LocationRoom).put(brand,1);
						}
						int objectAmount = roomMap.get(LocationRoom).get("objectAmount");
						roomMap.get(LocationRoom).put("objectAmount",++objectAmount);
					}else{
						//如果不包含该房间，那么把该房间加入到roomMap中
						HashMap<String,Integer> brandMap = new HashMap<>();
						brandMap.put(brand,1);
						roomMap.put(LocationRoom,brandMap);
						roomMap.get(LocationRoom).put("objectAmount",1);
					}
				}
			}
			resultMap.put(firstPastDay,roomMap);
		}
        set("resultMap",resultMap);
		renderJson();
	}
	
	public void web_showBrandDist(){
		Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
		if ( admin== null) {
			renderHtml(Util.getResult("0001", "请先登录本系统!"));	
			return;
		}
		List<Record> recs=Db.find("select * from uv_org_device_info where OrganizationID="+admin.getInt("OrganizationID"));
		set("DeviceLists",recs);
		render("/web_admin/webAnalyseBrand.html");
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
	
	public void web_showObjects() {
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
	public void web_showObjectCount() {
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
}


