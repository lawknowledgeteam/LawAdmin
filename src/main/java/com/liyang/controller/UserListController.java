package com.liyang.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liyang.common.GlobalVar;
import com.liyang.common.Util;
import com.liyang.model.Admin;

import java.util.List;

/**
 * @Author: Pan
 * @Date: 2019/10/25 19:26
 * @Description:
 **/
public class UserListController extends Controller {
    public void web_UserList(){
        Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
        if ( admin== null) {
            renderHtml(Util.getResult("0001", "请先登录本系统!"));
            return;
        }
        render("/web_admin/userList.html");
    }

    public void web_UserListBycharts(){
        Admin admin=getSessionAttr(GlobalVar.WEBADMIN);
        if ( admin== null) {
            renderHtml(Util.getResult("0001", "请先登录本系统!"));
            return;
        }
        render("/web_admin/userListBycharts.html");
    }

    public void getCount(){
        long lists = Db.queryLong("select count(*) FROM  `lawknowledge`.`tb_user` ");
        renderJson(lists);
    }
    public void web_getUserList(){
        int page = 1;
        int pageSize = 10;
        String sqlFromWhere = " FROM  `lawknowledge`.`tb_user` ";

        System.out.println(sqlFromWhere);
        List<Record> lists = Db.paginate(page, pageSize, "SELECT *   ", sqlFromWhere).getList();
        renderJson(lists);
    }

    public void web_getUserCharts(){
        String startDate = get("startDate");
        String endDate = get("endDate");
        String userId = get("userId");
        int page = 1;
        int pageSize = 10;
        String sqlFromWhere = " FROM (SELECT CaseKind AS name,COUNT(*) AS max  FROM tb_trace WHERE userid="+userId+"  GROUP BY CaseKind  DESC  LIMIT 2) AS temp  ORDER BY max DESC" ;

        if(!startDate.equals("") && !endDate.equals("")){
            sqlFromWhere = "FROM  (SELECT CaseKind AS name,COUNT(*) AS max  FROM tb_trace	WHERE userid="+userId+" AND LastViewTime between  '"
                    +startDate+"' AND  '" +endDate+ "'  GROUP BY CaseKind  DESC  LIMIT 2) AS temp  ORDER BY max DESC";
        }
        System.out.println(sqlFromWhere);
        List<Record> listsBrow = Db.paginate(page, pageSize, "SELECT *   ", sqlFromWhere).getList();
        setAttr("listsBrow", listsBrow);

        String sqlUser = "SELECT *\n" +
                "FROM\n" +
                "  `lawknowledge`.`tb_user` \n" +
                "WHERE UserID = " +userId;
        List<Record> listUser = Db.find(sqlUser);
        setAttr("user", listUser);

        String sqlSearchFromWhere = " FROM (SELECT KeyWord AS name,COUNT(*) AS max  FROM tb_searchtrace  GROUP BY KeyWord  DESC  LIMIT 2) AS temp  ORDER BY max DESC";
        if(!startDate.equals("") && !endDate.equals("")){
            sqlFromWhere = "FROM  (SELECT KeyWord AS name,COUNT(*) AS max  FROM tb_searchtrace	WHERE  LastViewTime between  '"
                    +startDate+"' AND  '" +endDate+ "' GROUP BY KeyWord  DESC  LIMIT 6) AS temp  ORDER BY max DESC";
        }
        List<Record> listSearch = Db.paginate(page, pageSize, "SELECT *   ", sqlSearchFromWhere).getList();
        setAttr("search", listSearch);

        renderJson();
    }

    public void web_getUserSearchCharts(){
        String startDate = get("startDate");
        String endDate = get("endDate");
        String userId = get("userId");
        int page = 1;
        int pageSize = 10;
        String sqlFromWhere = " " ;

        if(!startDate.equals("") && !endDate.equals("")){
            sqlFromWhere = "";
        }
        System.out.println(sqlFromWhere);
        List<Record> lists = Db.paginate(page, pageSize, "SELECT *   ", sqlFromWhere).getList();
        renderJson(lists);
    }


}
