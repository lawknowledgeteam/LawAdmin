var ES = {};
ES.detailDialog = 'dia_detail';
ES.loadUrl = "JList";
ES.addUrl = "Detail";
ES.editUrl = "Detail";

ES.createGuid = function () {
    var S4 = function() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    };
    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
};

ES.join = function(arr,fieldName){
    var result = '';
    $(arr).each(function (index, row) {
        if(row[fieldName]){
            result += row[fieldName] + ',';
        }
    })
    if (result.length) {
        result = result.substr(0, result.length - 1);
    }
    return result;
}

ES.blockConfirm = function(title,message){
    var defer = $.Deferred(),
        t = title?title:'确认',
        m = message?message:'确定删除吗？';

    //解决confirm框右上角关闭按钮的点击事件，捕获到是defer的结果
    $.fn.window.defaults.closable = false;

    $.messager.confirm(t,m,function(r) {
        if (r) {
            defer.resolve();
        }
        else{
            defer.reject();
        }
    });

    $.fn.window.defaults.closable = true;

    return defer;
};
ES.addAttr = function(target,attrName,attrValue){
    $(target).attr(attrName,attrValue);
}

ES.toArray = function ($target) {
    var arr = new Array();
    for (var i = 0; i < $target.length; i++) {
        arr[i] = $target[i];
    }
    return arr;
}


ES.getSelectValue = function ($dg) {
    var idField = $dg.datagrid('options').idField,
        selectNode = $dg.datagrid('getSelected');
    if (selectNode && selectNode[idField]!==null) {
        return selectNode[idField];
    }
    $.notify.warning('请选中行。')
    return '';
}

ES.Download = function (formid, submitUrl) {
    $.messager.confirm('确认', '确定导出当前查询条件下的所有数据吗？', function (r) {
        if (r) {
            var $form = $('#' + formid);
            $form.attr('target', "_blank");
            $form.attr('action', submitUrl);

            $form.submit(); //表单提交

            $form.attr('target', "");
            $form.attr('action', "");
        } 
    });
}


ES.DownloadFile = function (formid, submitUrl) {
            var $form = $('#' + formid);
            $form.attr('target', "_blank");
            $form.attr('action', submitUrl);

            $form.submit(); //表单提交

            $form.attr('target', "");
            $form.attr('action', "");
}

ES.addFavorite = function (url, title) {
    try {
        url = url || window.location.href;

        title = title || document.title;

        window.external.addFavorite(url, title);
    } catch (e) {
        try {
            window.sidebar.addPanel(title, url, "");
        } catch (e) {
            return false;
        }
    }

    return true;
}
ES.SetHome = function (obj, vrl) {
    try {
        obj.style.behavior = 'url(#default#homepage)'; obj.setHomePage(vrl);
    }
    catch (e) {
        if (window.netscape) {
            try {
                netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
            } catch (e) {
                alert("抱歉！您的浏览器不支持直接设为首页。请在浏览器地址栏输入“about:config”并回车然后将[signed.applets.codebase_principal_support]设置为“true”，点击“设为首页”后忽略安全提示，即可设置成功。");
            }
            var prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefBranch);
            prefs.setCharPref('browser.startup.homepage', vrl);
        } else {
            alert('抱歉，您的浏览器不支持自动设置首页, 请使用浏览器菜单手动设置!');
        }
    }
}


ES.DateFormat = function (strDate) {
    var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
            function (a) { return parseInt(a, 10) - 1; }).match(/\d+/g) + ')');
    return date;
}

ES.Bind = function (data, dataName, elementID) {
    var viewMode = {};
    viewMode[dataName] = ko.observableArray(data);
    ko.applyBindings(viewMode, $('#' + elementID)[0]);
}

ES.StateFormater = function (value, row, index) {
    var state = row["Order_State"],
        finalState = row["Order_FinalState"];
    if (finalState == 0) {
        switch (state) {
            case 0: return '待受理'; break;
            case 1: return '已受理'; break;
            case -1: return '未受理'; break;
            case 2: return '待测试'; break;
            case 3: return '已测试'; break;
            case 4: return '已结算'; break;
            case 5: return '已结束'; break;
        }
    }
    else {
        switch (finalState) {
            case 1: return '已结束'; break;
            case -1: return '已撤销'; break;
        }
    }
    return '';
}

ES.Innovation_CachedFormater = function (value, row, index) {
    if (value === 0) {
        return '未结算';
    }
    else if (value === 1) { return '已结算'; }

    return '';

}

ES.DateFormater = function (value, row, index) {
    if (value) {
        var unixTimestamp = eval('new ' + value.replace('/', '').replace('/', ''));
        return unixTimestamp.format('yyyy-MM-dd hh:mm:ss');
    }
    return '';
};


ES.getUrlParam = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null && r[2] && r[2]!='null') return decodeURI(r[2]); return ''; //返回参数值
}

ES.bindEnter = function (func) {
    document.onkeydown = function (e) {
        var ev = document.all ? window.event : e;
        if (ev.keyCode == 13) {
            if (func) {
                func();
            }
        }
    }
}


ES.SubmitForm = function (formid, submitUrl) {
    if ($("#" + formid).length == 0) {
        return;
    }
    var defer = $.Deferred();
    $("#" + formid).ajaxSubmit({
        url: submitUrl,
        beforeSubmit: function () {
            return true;
        },
        success: function (data) {
            try {

                //result为请求处理后的返回值
                var result = JSON.parse(data);
                if (result.flag == -8) {
                    eval(result.msg);
                }
                else {
                    return defer.resolve(result);
                }
            }
            catch (eff) {
                ES.Notify("发生错误，请刷新。");
                return defer.resolve({flag:1,rows:[],total:0});
            }
        }
    });

    return defer;
}


ES.SubmitFormWithValidate = function (formid, submitUrl) {
    var defer = $.Deferred();
    if (!ES.ValidateByVisible($("#" + formid)) || $("#" + formid).length == 0) {
        return defer.reject();
    }

    $("#" + formid).ajaxSubmit({
        url: submitUrl,
        beforeSubmit: function () {
            return true;
        },
        success: function (data) {
            //result为请求处理后的返回值
            var result = JSON.parse(data);
            if (result.flag == -8) {
                eval(result.msg);
            }
            else {
                return defer.resolve(result);
            }
        }
    });
    return defer;
}


ES.Validate = function (objOrStr) {
    var $obj = (typeof objOrStr == 'string') ? $('#' + objOrStr) : $(objOrStr);
    $obj.form('validate');
    $obj.find('.validatebox-text:not(:disabled)').validatebox('validate');
    var invalidbox = $obj.find('.validatebox-invalid');
    invalidbox.filter(':not(:disabled):first').focus();

    if (!(invalidbox.length == 0)) {
        return false;
    }
    return true;
}

ES.ValidateByVisible = function (objOrStr) {
    var $obj = (typeof objOrStr == 'string') ? $('#' + objOrStr) : $(objOrStr);
    $obj.form('validate');
    $obj.find('.validatebox-text:not(:disabled)').validatebox('validate');
    var invalidbox = $obj.find('.validatebox-invalid').filter(':visible');
    invalidbox.filter(':not(:disabled):first').focus();

    if (!(invalidbox.length == 0)) {
        $.notify.info('验证未通过，不能提交。');
        return false;
    }
    return true;
}

ES.AddModelNew = function (formid, title, addurl, jumpUrl) {
    var lm = "";
    $("#" + formid).find("[webtype='fulleditor']").each(function () {
        $("#" + this.name).val(editors[this.name].html());
    });
    $("#" + formid).find("[webtype='simpleeditor']").each(function () {


        $("#" + this.name).val(editors[this.name].html());

    });

    if (!ES.ValidateByVisible($("#" + formid))) {
        return;
    }
    var dz = "JAdd";
    if (typeof (addurl) != "undefined") {
        dz = addurl;
    }

    return ES.SubmitForm(formid, dz)
    .then(function (result) {
        if (result.flag == 1) {
            ES.Notify(result.msg);
            ES.dialogCloseAndFresh(result.SelectedDataID);
            if (jumpUrl) {
                window.location = jumpUrl;
            }
        } else {
            $.notify.error(result.msg);
        }
        $("#" + formid).find(":file").removeAttr("disabled");
    })

}

ES.Notify = function (msg, title, fn) {
    $.notify.info('',msg,  fn);
}

ES.DialogClose = function () {
    var $dialog = $('#' + ES.detailDialog);
    $dialog.dialog('close',true);
    $dialog.dialog('destroy', true);
   // $('[id="dia_detail"]').remove();
}

ES.dialogCloseAndFresh = function (selectedDataID) {
    ES.DialogClose();
    if (window.Refresh) {
       return window.Refresh(selectedDataID);
    }
}

ES.Ajax = function (opts, isNotJson) {
    var defer = $.Deferred();

    $.ajax(
        $.extend(true, {
            type: "POST",
            success: function (data) {
                if (isNotJson) {
                    return defer.resolve(data);
                }
                //result为请求处理后的返回值
                var result = JSON.parse(data);
                if (result.flag == -8) {
                    eval(result.msg);
                }
                else {
                    return defer.resolve(result);
                }
            }
        }, opts)
    );

    return defer;
}

ES.loadTree = function (selectID) {
    var parentID = $treegrid.treegrid('getSelected')[idField],
    $combotree = $('#Sys_Dept_Parent_Dept_Id');

    return ES.Ajax("JTree")
    .then(function (nodelist) {
        if (nodelist.length) {
            $combotree.combotree('loadData', nodelist);
            selectID = parentID || nodelist[0]['id'];
            $combotree.combotree('setValue', selectID);
        }
    })

}

ES.loadRoleData = function () {
    return ES.Ajax({ url: "/_WorkSpace/Sys_Role/JList" });
}

ES.loadDeptData = function (opts) {
    return ES.Ajax({ url: "/Common/Data/Dept" });
}



ES.loadAreaData = function (opts) {
    return ES.Ajax($.extend(true, { url: "/Common/Data/Area?startLayer=1" }, opts));
}

ES.openDialogWithBar = function (opts,$target,_dialogID) {
    var defer = $.Deferred(),
        dialogID = _dialogID?_dialogID:ES.detailDialog,
        $dlg;
        $("[id='" + dialogID + "']").remove();

    if ($target) {
        $dlg = $('<div id="' + dialogID + '"></div>').appendTo($target);
    }
    else {
        $dlg = $('<div id="' + dialogID + '"></div>').appendTo('body');
    }
    

    var p = $(window.parent.document),
        h = p?p.find('#mainPanle').height()-100:null;
    var nOpts = $.extend(true, {
        width: 700,
        height: h? h:450,
        closed: false,
        closable: true,
        cache: false,
      //  resizable: true,
        modal: true,
        buttons: [{
            text: '确定',
            handler: function () {
                var rr = opts.save;
                if (rr) {
                    if (rr.then) {
                        return opts.save()
                        .then(function () {
                            ES.closeDialog(dialogID);
                        });
                    }
                    else {
                        opts.save();
                        ES.closeDialog(dialogID);
                        return $.Deferred().resolve();
                    }
                }
            }
        }, {
            text: '关闭',
            handler: function () { ES.closeDialog(dialogID); }
        }],
        onLoad: function () {
            ES.focusFirst();
            defer.resolve();
        }
    }, opts);
    if (nOpts.href) {
        var href = nOpts.href;
        if (href.indexOf('?') >= 0) {
            href += "&IsJson=0";
        }
        else {
            href += "?IsJson=0";
        }
        nOpts.href = href;
    }

    $dlg.dialog(nOpts);

    return defer;
}

ES.openDialog = function (opts,_dialogID) {
    var defer = $.Deferred(),
        dialogID = _dialogID?_dialogID:ES.detailDialog;
    $("[id='" + dialogID + "']").remove();
    $('<div id="' + dialogID + '"></div>').appendTo('body');
    var nOpts = $.extend(true, {
        width: 700,
        height: 450,
        closed: false,
        closable: true,
        cache: false,
        resizable: true,
        modal: true,
        onLoad: function () {
            ES.focusFirst();
            defer.resolve();
        }
    }, opts);
    if (nOpts.href) {
        var href = nOpts.href;
        if (href.indexOf('?') >= 0) {
            href += "&IsJson=0";
        }
        else {
            href += "?IsJson=0";
        }
        nOpts.href = href;
    }

    $('#' + dialogID).dialog(nOpts);
    return defer;
}

ES.closeDialog = function (id) {
    var $dia;
    if (id) {
        $dia = $('#' + id);
    }
    else {
        $dia = $('#' + ES.detailDialog);
    }
    $dia.dialog('close');
}

function createFrame(url, id) {
    var s = "<iframe name=\"" + id + "\" id=\"" + id + "\" scrolling=\"auto\" frameborder=\"0\"  src=\"" + url + "\" style=\"width:100%;height:100%;\"></iframe>";
    return s;
}

ES.removeData = function ($dg) {
    var ids = ES.getSelectValue($dg);
    if (ids) {
        $.messager.confirm('确认', '确认删除当前选中的记录吗？', function (r) {
            if (r) {
                $.loading();
                $.ajax({
                    type: "POST",
                    url: "JDelete",
                    data: "ids=" + ids,
                    success: function (resultStr) {
                        $.loaded();
                        var result = JSON.parse(resultStr);
                        if (result.flag) {
                            $.notify.success(result.msg);
                            Refresh();
                        }
                        else {
                            $.notify.info(result.msg);
                        }
                    }
                });
            }
        });
    }

}

ES.batchRemoveData = function ($dg) {
    var ids = ES.GetSelectionStr($dg,true);
    if (ids) {
        $.messager.confirm('确认', '确认删除当前选中的记录吗？', function (r) {
            if (r) {
                $.loading();
                $.ajax({
                    type: "POST",
                    url: "JDelete",
                    data: "ids=" + ids,
                    success: function (resultStr) {
                        $.loaded();
                        var result = JSON.parse(resultStr);
                        if (result.flag) {
                            $.notify.success(result.msg);
                            Refresh();
                        }
                        else {
                            $.notify.info(result.msg);
                        }
                    }
                });
            }
        });
    }
    else {
        ES.removeData($dg);
    }
}

ES.GetSelectionStr = function ($dg,noMsg) {
    var list = $dg.datagrid('getChecked'),
        idField = $dg.datagrid('options').idField,
        result = '';
    if (list.length) {
        $(list).each(function (index, item) {
            result += item[idField] + ',';
        })
        if (result.lastIndexOf(',') == result.length - 1) {
            result = result.substring(0, result.length - 1);
        }
        return result;
    }
    if (!noMsg) {
        $.notify.warning('请选中复选框。');
    }
    return result;
}

ES.comboTreeSelectLeaf = function (comboTreeID) {
    //选择树节点触发事件
    $('#' + comboTreeID).combotree({
        //选择树节点触发事件  
        onSelect: function (node) {
            //返回树对象  
            var tree = $(this).tree;
            //选中的节点是否为叶子节点,如果不是叶子节点,清除选中  
            var isLeaf = tree('isLeaf', node.target);
            if (!isLeaf) {
                //清除选中  
                $('#' + comboTreeID).combotree('clear');
                $.notify.info('请选择叶子节点。');
            }
        }
    });

}


// 对Date的扩展，将 Date 转化为指定格式的String 
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
// 例子： 
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1,                 //月份 
        "d+": this.getDate(),                    //日 
        "h+": this.getHours(),                   //小时 
        "m+": this.getMinutes(),                 //分 
        "s+": this.getSeconds(),                 //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds()             //毫秒 
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


//带阻塞的confirm
ES.BlockConfirm = function (title, message) {
    var defer = $.Deferred(),
                        t = title ? title : '删除确认',
                        m = message ? message : "确定删除吗？";

    //解决confirm框右上角关闭按钮的点击事件，捕获到是defer的结果
    $.fn.window.defaults.closable = false;

    $.messager.confirm(t, m, function (r) {
        if (r) {
            defer.resolve();
        }
        else {
            defer.reject();
        }
    });

    $.fn.window.defaults.closable = true;

    return defer;
};



function openW(url) {
    if (self != top) {
        top.openW(url);
        return;
    }
        window.open(url);
        return;   
}


function addTab(subtitle, url, idstr) {
    if (self != top) {
        top.addTab(subtitle, url, idstr);
        return;
    }
    //    else {
    //        window.open(url, subtitle);
    //        return;
    //    }
    if (typeof (_tabs_) == "undefined") {
        window.open(url, subtitle);
        return;
    }
    subtitle = subtitle.trim();
    var ps = subtitle.indexOf("|");
    if (ps > -1) {
        subtitle = subtitle.substr(ps, subtitle.length - ps);
    }
    subtitle = subtitle.trim().replace("|---", "");
    var tabss = _tabs_.tabs('tabs');
    var you = false;
    var youtab = null;
    for (var i = 0; i < tabss.length; i++) {

        if (tabss[i][0].id == idstr) {
            you = true;
            youtab = tabss[i];
            break;
        }
    }

    if (!you) {
        _tabs_.tabs('add', {
            id: idstr,
            title: subtitle,
            content: createFrame(url, idstr),
            closable: true,
            width: $('#mainPanle').width() - 10,
            height: $('#mainPanle').height() - 26
        });
    } else {

        _tabs_.tabs('select', _tabs_.tabs('getTabIndex', youtab));
        $("iframe[name='" + idstr + "']").each(function () { this.src = url; });
    }
}
function gettabindex(idstr) {
    var youtab = null;
    var tabss = _tabs_.tabs('tabs');
    for (var i = 0; i < tabss.length; i++) {

        if (tabss[i][0].id == idstr) {

            you = true;
            youtab = tabss[i];
            break;
        }
    }
    if (youtab != null) {
        return _tabs_.tabs('getTabIndex', youtab);
    }
    else {
        return "ddddddddddddddddddd";
    }
}
function closetab(idstr, refreshIdStr, msg) {   
    if (self != top) {
        top.closetab(idstr, refreshIdStr, msg);      
        return;
    }
    if (typeof (_tabs_) != "undefined") {
        _tabs_.tabs('close', gettabindex(idstr));
        if(refreshIdStr){
            reflashtab(refreshIdStr,msg);
        }
    }
}

function reflashtab(idstr,msg) {
    if (self != top) {
        top.reflashtab(idstr,msg);
        return;
    }
    $("iframe[name='" + idstr + "']").each(function () { reflashcontent(this,msg); });
}
function reflashcontent(obj, msg) {   
    if(obj.contentWindow){
        if(obj.contentWindow.Search){
            obj.contentWindow.Search();
        }
        if (obj.contentWindow.KH_Term_Search) {
            obj.contentWindow.KH_Term_Search();
        }
        if (obj.contentWindow.KH_Daily_Search) {
            obj.contentWindow.KH_Daily_Search();
        } 
        if(msg){
           obj.contentWindow.showInfo(msg);
        }
        $("#tabs").tabs("resize");
    }
}

function showInfo(msg){
    $.notify.success(msg);
}
//focus第一个输入框
ES.focusFirst = function () {
    $(".SubmitForm").find(":input:visible").filter(":not([readonly='readonly']):first").focus();
}



ES.Import = function () {
                $.loading();
                $.ajax({
                    type: "POST",
                    url: "JImport",
                    success: function (resultStr) {
                        $.loaded();
                        var result = JSON.parse(resultStr);
                        if (result.flag) {
                            ES.Notify(result.msg);
                            Search();
                        }
                    }
                });

            };




//批量设置封存
function BatchSetSeal() {
    var selections = $KH_Year_Grid.datagrid('getChecked');
    if (selections && selections.length) {
        if ($.messager) {
            $.messager.defaults.ok = '封存';
            $.messager.defaults.cancel = '解除封存';
        }
        $.messager.confirm('封存', '封存还是解除封存？', function (r) {
            var v = ES.GetSelectionStr($KH_Year_Grid);
            if (r) {
                SetSeal(v, 1);
            }
            else {
                SetSeal(v, 0);
            }
        });
        if ($.messager) {
            $.messager.defaults.ok = '确定';
            $.messager.defaults.cancel = '取消';
        }
        return;
    }
    else {
        var selectNode = $KH_Year_Grid.datagrid('getSelected');
        if (selectNode) {
            if ($.messager) {
                $.messager.defaults.ok = '封存';
                $.messager.defaults.cancel = '解除封存';
            }
            $.messager.confirm('封存', '封存还是解除封存？', function (r) {
                var v = selectNode[KH_Year_IDField];
                if (r) {
                    SetSeal(v, 1);
                }
                else {
                    SetSeal(v, 0);
                }
            });
            if ($.messager) {
                $.messager.defaults.ok = '确定';
                $.messager.defaults.cancel = '取消';
            }
            return;
        }
    }

    $.notify.warning('请选中节点。');
}

function SetSeal(ids, v,url) {
    return ES.Ajax({ url:url, data: { Type: 1, IDs: ids, Value: v } })
        .then(function (result) {
            if (result.flag) {
                $.notify.success(result.msg);
                KH_Year_Refresh();
            }
            else {
                $.notify.error(result.msg);
            }
        })
}

//四舍五入
function fomatFloat(src, pos) {
    return Math.round(src * Math.pow(10, pos)) / Math.pow(10, pos);

}



