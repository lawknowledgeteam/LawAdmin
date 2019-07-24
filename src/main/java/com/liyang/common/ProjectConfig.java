package com.liyang.common;



import com.jfinal.config.*;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;

import com.jfinal.render.ViewType;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.liyang.controller.*;
import com.liyang.model.Admin;
import com.liyang.model.AdminLog;
import com.liyang.model.CaseInfo;
import com.liyang.model.Log;
import com.liyang.model.Role;



public class ProjectConfig extends JFinalConfig {
 
    public static void main(String[] args) {
        UndertowServer.start(ProjectConfig.class, 8082, true);
        
    }
    @Override
    public void configConstant(Constants me) {
       me.setDevMode(true);
       
       /**
		 * 支持 Controller、Interceptor、Validator 之中使用 @Inject 注入业务层，并且自动实现 AOP
		 * 注入动作支持任意深度并自动处理循环注入
		 */
	
		
       me.setError404View("/404/index.html");
       me.setError500View("/404/index.html");
    	
    	//提高上传文件大小上限
       me.setMaxPostSize(100*1024*1024);
       me.setViewType(ViewType.FREE_MARKER);
    }
    @Override
    public void configRoute(Routes me) {
        me.add("/lawmap", LawMapController.class);
        me.add("/lawcase", LawCaseController.class);
       me.add("/lawlist", LawListController.class);
       me.add("/", IndexController.class);
       me.add("/web_admin",WebAdminController.class); 
       me.add("/admin",AdminController.class); 
		me.add("/log",LogController.class); 
		me.add("/analyse",AnalyseController.class); 
		me.add("/role",RoleController.class);
		me.add("/m",com.liyang.controller.MobileController.class);		
    }
    @Override
    public void configEngine(Engine me) {
    	
    }
    @Override
    public void configPlugin(Plugins me) {
    	DruidPlugin druidPlugin = new DruidPlugin(GlobalVar.DBServerIP, GlobalVar.DBServerUser, GlobalVar.DBServerPassword);
		
		me.add(druidPlugin);
		
		// 映射数据模型
		ActiveRecordPlugin arp = new ActiveRecordPlugin("mysql",druidPlugin);
		arp.setContainerFactory(new CaseInsensitiveContainerFactory());
		arp.setDialect(new AnsiSqlDialect());   
		
		arp.addMapping("tb_case", "CaseID",CaseInfo.class);
		
		arp.addMapping("tb_admin", "AdminID", Admin.class);
		arp.addMapping("sys_role", "RoleID", Role.class);
		arp.addMapping("tb_admin_log", "LogID",AdminLog.class);
		arp.addMapping("tb_log", "LogID", Log.class);
		//arp.addMapping("tb_user", "UserID", User.class);
		
		me.add(arp);
		
		
		//me.add(new EhCachePlugin());
		
		// 用于缓存news模块的redis服务
	    //RedisPlugin msgRedis = new RedisPlugin("msg", "localhost");
	    //me.add(msgRedis);
		
		//配置定时任务
		//  QuartzPlugin quartz = new QuartzPlugin();
		//  quartz.setJobs("quarter.properties");
		//  me.add(quartz);
    }
    @Override
    public void configInterceptor(Interceptors me) {
    	
    	 //ImportMacPrefixToDB("src/main/resources/MACVendor.txt");
    }

    @Override
    public void configHandler(Handlers me) {
    	RoleAuthSettings.init();		
    }
    
    
   
}
