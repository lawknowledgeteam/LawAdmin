String.prototype.trim = function () {
    return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
}

String.prototype.endWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substring(this.length - str.length) == str)
        return true;
    else
        return false;
    return true;
}


if ($.fn.pagination) {
    $.fn.pagination.defaults.beforePageText = '第';
    $.fn.pagination.defaults.afterPageText = '共{pages}页';
    $.fn.pagination.defaults.displayMsg = '显示{from}到{to},共{total}记录';
}
if ($.fn.datagrid) {
    $.fn.datagrid.defaults.loadMsg = '';
    // $.fn.datagrid.defaults.loadMsg = '正在处理，请稍待。。。';
}
if ($.fn.treegrid && $.fn.datagrid) {
    $.fn.treegrid.defaults.loadMsg = $.fn.datagrid.defaults.loadMsg;
}
if ($.messager) {
    $.messager.defaults.ok = '确定';
    $.messager.defaults.cancel = '取消';
}
if ($.fn.validatebox) {
    $.fn.validatebox.defaults.missingMessage = '该输入项为必输项';
    $.fn.validatebox.defaults.rules.email.message = '请输入有效的电子邮件地址';
    $.fn.validatebox.defaults.rules.url.message = '请输入有效的URL地址';
    $.fn.validatebox.defaults.rules.length.message = '输入内容长度必须介于{0}和{1}之间';
    $.fn.validatebox.defaults.rules.remote.message = '请修正该字段';
}
if ($.fn.textbox) {
    $.fn.textbox.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.numberbox) {
    $.fn.numberbox.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.combobox) {
    $.fn.combobox.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.combotree) {
    $.fn.combotree.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.combogrid) {
    $.fn.combogrid.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.calendar) {
    $.fn.calendar.defaults.weeks = ['日', '一', '二', '三', '四', '五', '六'];
    $.fn.calendar.defaults.months = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'];
}
if ($.fn.datebox) {
    $.fn.datebox.defaults.currentText = '今天';
    $.fn.datebox.defaults.closeText = '关闭';
    $.fn.datebox.defaults.okText = '确定';
    $.fn.datebox.defaults.missingMessage = '该输入项为必输项';
    $.fn.datebox.defaults.formatter = function (date) {
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
    };
    $.fn.datebox.defaults.parser = function (s) {
        if (!s) return new Date();
        var ss = s.indexOf('/') >= 0 ? s.split('/') : s.split('-');
        var y = parseInt(ss[0], 10);
        var m = parseInt(ss[1], 10);
        var d = parseInt(ss[2], 10);
        if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
            return new Date(y, m - 1, d);
        } else {
            return new Date();
        }
    };
}
if ($.fn.datetimebox && $.fn.datebox) {
    $.extend($.fn.datetimebox.defaults, {
        currentText: $.fn.datebox.defaults.currentText,
        closeText: $.fn.datebox.defaults.closeText,
        okText: $.fn.datebox.defaults.okText,
        missingMessage: $.fn.datebox.defaults.missingMessage
    });
}

(function ($) {
    $.extend($.fn.validatebox.methods, {
        setNoValidate: function (jq, msg) {
            return jq.each(function () {
                var state = $.data(this, 'validatebox');
                var opts = state.options;
                var box = $(this);
                box.addClass('validatebox-invalid');

                $(this).tooltip($.extend({}, opts.tipOptions, {
                    content: msg,
                    position: opts.tipPosition,
                    deltaX: opts.deltaX
                })).tooltip('show');
                this.tip = true;
            })
        },

        
        showTip: function (jq, msg) {
            return jq.each(function () {
                var state = $.data(this, 'validatebox');
                var opts = state.options;

                $(this).tooltip($.extend({}, opts.tipOptions, {
                    content: msg,
                    position: opts.tipPosition,
                    deltaX: 0,
                    onShow:function(){ $(this).tooltip("tip").css({ color: "#ffff00", borderColor: "#ddd", backgroundColor: "#33be40" });}
                }));                
                $(this).tooltip('show');
                this.tip = true;
                var target = this;
                setTimeout(function(){                
                    $(target).tooltip('hide');
                },1000);
            })
        },

        hideTip: function (jq) {
            return jq.each(function () {
                var state = $.data(this, 'validatebox');
                state.tip = false;
                $(this).tooltip('hide');
            })
        }
    });


})(jQuery);



$.extend($.fn.validatebox.defaults.rules, {
    NoWu: {// 不能填写无 
        validator: function (value) {
            return value.trim()!='';
        },
        message: '请务必准确填写。'
    },
    idcard: {// 验证身份证 
        validator: function (value) {
            return /^\d{15}(\d{2}[A-Za-z0-9])?$/i.test(value);
        },
        message: '身份证号码格式不正确'
    },
    minLength: {
        validator: function (value, param) {
            return value.length >= param[0];
        },
        message: '请输入至少（{0}）个字符.'
    },
    code: {// 编号
        validator: function (value) {
            return /^\d{3}$/i.test(value);
        },
        message: '三个数字'
    },
    fullcode: {// 编号
        validator: function (value) {
            return /^(\d{3})+$/i.test(value);
        },
        message: '长度为3的整数倍的数字组合'
    },
    guid: {
        validator: function (value) {
            var aa = value.split("-");

            if (aa.length == 5) {

                if (aa[0].length == 8 && aa[1].length == 4 && aa[2].length == 4 && aa[3].length == 4 && aa[4].length == 12) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        },
        message: '格式错误.'
    },
    length: { validator: function (value, param) {
        var len = $.trim(value).length;
        return len >= param[0] && len <= param[1];
    },
        message: "输入内容长度必须介于{0}和{1}之间."
    },
    lengthV: { validator: function (value, param) {
        var len = $.trim(value).length;
        return len === param[0];
    },
        message: "请输入{0}个字符."
    },
    phone: {// 验证电话号码 
        validator: function (value) {
            return /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/i.test(value);
        },
        message: '格式不正确,请使用下面格式:020-88888888'
    },
    mobile: {// 验证手机号码 
        validator: function (value) {
            return /^(13|15|18)\d{9}$/i.test(value);
        },
        message: '手机号码格式不正确'
    },
    intOrFloat: {// 验证整数或小数 
        validator: function (value) {
            return /^\d+(\.\d+)?$/i.test(value);
        },
        message: '请输入数字，并确保格式正确'
    },
    currency: {// 验证货币 
        validator: function (value) {
            return /^\d+(\.\d+)?$/i.test(value);
        },
        message: '货币格式不正确'
    },
    qq: {// 验证QQ,从10000开始 
        validator: function (value) {
            return /^[1-9]\d{4,9}$/i.test(value);
        },
        message: 'QQ号码格式不正确'
    },
    int: {// 验证整数 
        validator: function (value) {
            if (value == 0) {
                return true;
            }
            return /^[+|-]?[1-9]+\d*$/i.test(value);
        },
        message: '请输入整数'
    },
    integer: {// 验证整数 
        validator: function (value) {
            if (value == 0) {
                return true;
            }
            return /^[+|-]?[1-9]+\d*$/i.test(value);
        },
        message: '请输入整数'
    },
    uint: {// 验证整数 
        validator: function (value) {
            return /^[+]?[1-9]+\d*$/i.test(value);
        },
        message: '请输入非负整数'
    },
    age: {// 验证年龄
        validator: function (value) {
            return /^(?:[1-9][0-9]?|1[01][0-9]|120)$/i.test(value);
        },
        message: '年龄必须是0到120之间的整数'
    },

    chinese: {// 验证中文 
        validator: function (value) {
            return /^[\Α-\￥]+$/i.test(value);
        },
        message: '请输入中文'
    },
    english: {// 验证英语 
        validator: function (value) {
            return /^[A-Za-z]+$/i.test(value);
        },
        message: '请输入英文'
    },
    unnormal: {// 验证是否包含空格和非法字符 
        validator: function (value) {
            return /.+/i.test(value);
        },
        message: '输入值不能为空和包含其他非法字符'
    },
    password: {// 验证密码 
        validator: function (value) {
            return /^[a-zA-Z][a-zA-Z0-9_]{5,15}$/i.test(value);
        },
        message: '不合法（6-16字符，字母开头，允许字母、数字、下划线，区分大小写）'
    },
    faxno: {// 验证传真 
        validator: function (value) {
            //            return /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/i.test(value); 
            return /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/i.test(value);
        },
        message: '传真号码不正确'
    },
    zip: {// 验证邮政编码 
        validator: function (value) {
            return /^[1-9]\d{5}$/i.test(value);
        },
        message: '邮政编码格式不正确'
    },
    ip: {// 验证IP地址 
        validator: function (value) {
            return /d+.d+.d+.d+/i.test(value);
        },
        message: 'IP地址格式不正确'
    },
    name: {// 验证姓名，可以是中文或英文 
        validator: function (value) {
            return /^[\Α-\￥]+$/i.test(value) | /^\w+[\w\s]+\w+$/i.test(value);
        },
        message: '请输入姓名'
    },
    date: {// 验证姓名，可以是中文或英文 
        validator: function (value) {
            //格式yyyy-MM-dd或yyyy-M-d
            return /^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value);
        },
        message: '清输入合适的日期格式'
    },
    dateSmall: {
        validator: function (dateString) {
            dateString += '.01';
            // First check for the pattern
            if (!/^\d{4}\.\d{2}\.\d{2}$/.test(dateString))  //YYYY/MM/DD
                return false;

            // Parse the date parts to integers
            var parts = dateString.split(".");
            var day = parseInt(parts[2], 10);
            var month = parseInt(parts[1], 10);
            var year = parseInt(parts[0], 10);

            // Check the ranges of month and year
            if (year < 1000 || year > 3000 || month == 0 || month > 12)
                return false;

            var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

            // Adjust for leap years
            if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
                monthLength[1] = 29;

            // Check the range of the day
            return day > 0 && day <= monthLength[month - 1];
            //return new Date(input) instanceof Date
        },
        message: '请输入正确的日期，例如1998.03'
    },
    dateyyyy: {
        validator: function (dateString) {
            dateString += '0101';
            // First check for the pattern
            if (!/^\d{4}\d{2}\d{2}$/.test(dateString))  //YYYY/MM/DD
                return false;

            // Parse the date parts to integers
            var day = dateString.substring(6);
            var month = dateString.substring(4, 6);
            var year = dateString.substring(0, 4);

            // Check the ranges of month and year
            if (year < 1000 || year > 3000 || month == 0 || month > 12)
                return false;

            var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

            // Adjust for leap years
            if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
                monthLength[1] = 29;

            // Check the range of the day
            return day > 0 && day <= monthLength[month - 1];
            //return new Date(input) instanceof Date
        },
        message: '请输入正确的年度，例如2016'
    },
    dateyyyymm: {
        validator: function (dateString) {
            dateString += '01';
            // First check for the pattern
            if (!/^\d{4}\d{2}\d{2}$/.test(dateString))  //YYYY/MM/DD
                return false;

            // Parse the date parts to integers
            var day = dateString.substring(6);
            var month = dateString.substring(4, 6);
            var year = dateString.substring(0, 4);

            // Check the ranges of month and year
            if (year < 1000 || year > 3000 || month == 0 || month > 12)
                return false;

            var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

            // Adjust for leap years
            if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
                monthLength[1] = 29;

            // Check the range of the day
            return day > 0 && day <= monthLength[month - 1];
            //return new Date(input) instanceof Date
        },
        message: '请输入正确的日期，例如199803'
    },
    dateCuryyyymm: {
        validator: function (dateString2) {
            var dateString = dateString2 + '01';
            // First check for the pattern
            if (!/^\d{4}\d{2}\d{2}$/.test(dateString))  //YYYY/MM/DD
                return false;

            // Parse the date parts to integers
            var day = dateString.substring(6);
            var month = dateString.substring(4, 6);
            var year = dateString.substring(0, 4);

            // Check the ranges of month and year
            if (year < 1000 || year > 3000 || month == 0 || month > 12)
                return false;

            var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

            // Adjust for leap years
            if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
                monthLength[1] = 29;

            // Check the range of the day
            var rr = day > 0 && day <= monthLength[month - 1];
            if (rr) {
                return parseInt(dateString2.substring(0, 4)) == (new Date().getFullYear() - 1);
            }
            else {
                return false;
            }
            //return new Date(input) instanceof Date
        },
        message: '请输入正确的日期，例如' + (new Date().getFullYear() - 1) + '03'
    },
    datemm: {
        validator: function (dateString) {
            dateString = (new Date().getFullYear()-1) + dateString;
            dateString += '01';
            // First check for the pattern
            if (!/^\d{4}\d{2}\d{2}$/.test(dateString))  //YYYY/MM/DD
                return false;

            // Parse the date parts to integers
            var day = dateString.substring(6);
            var month = dateString.substring(4, 6);
            var year = dateString.substring(0, 4);

            // Check the ranges of month and year
            if (year < 1000 || year > 3000 || month == 0 || month > 12)
                return false;

            var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

            // Adjust for leap years
            if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
                monthLength[1] = 29;

            // Check the range of the day
            return day > 0 && day <= monthLength[month - 1];
            //return new Date(input) instanceof Date
        },
        message: '请输入正确的月份，例如03'
    },
    msn: {
        validator: function (value) {
            return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value);
        },
        message: '请输入有效的msn账号(例：abc@hotnail(msn/live).com)'
    },
    timeCompare: {
        validator: function (value, param) {
        try{
            var startTime = $(param[0]).datetimebox('getValue'),
            endTime = $(param[1]).datetimebox('getValue');
            if (startTime && endTime) {
                return endTime > startTime;
            }
            return true;
          }
          catch(eff){}

          return true;
        },
        message: '结束时间必须大于开始时间！'
    },
    equals: {
        validator: function (value, param) {
            return value == $(param[0]).val();
        },
        message: '两次密码不一致！'
    },
    telphone: {  //手机号码和电话号码
        validator: function (value) {
            //判断是不是电话号码 /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/i.test(value)
            //再判断是不是手机号码 /^(13|15|18)\d{9}$/i.test(value)
            var tel = /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/i.test(value);
            var mobile = /^(13|15|18)\d{9}$/i.test(value);
            return (tel || mobile);
        },
        message: '请输入正确格式的电话号码'
    }
});




(function ($) {

    $.loading = function (options) {
        var defaults = $.loading.defaults;
        options = options || {};
        var opts = $.extend({}, defaults, options);

        var jq = opts.topMost ? top.$ : $,
	        locale = jq(opts.locale),
	        array = locale.children().map(function () {
	            var zindex = $(this).css("z-index");
	            return $.isNumeric(zindex) ? parseInt(zindex) : 0;
	        }),
	        zindex = _.max(array);

        var opacity = opts.opacity;
        var opacityCss = { 'opacity': opacity, 'filter': 'alpha(opacity=' + (parseFloat(opacity) * 100) + ')' };

        if (opacity == '1') {
            opacityCss["background"] = "#fff";
        }

        locale.addClass("mask-container");
        locale.find('.datagrid-mask').remove();
        var mask = jq("<div></div>").addClass("datagrid-mask loading-mask")
        							.css({ display: "block", "z-index": ++zindex })
                                    .css(opacityCss)
        							.css({ height: document.body.scrollHeight })
        							.appendTo(locale);

        if (opts.showLoadingMsg) {
            locale.find('.datagrid-mask-msg ').remove();
            var msg = jq("<div></div>").addClass("datagrid-mask-msg loading-msg")
    				        			.css({ "border-radius": "5px 5px 5px 5px", display: "block", left: "50%", "z-index": ++zindex })
        							    .css({ top: ($(document).scrollTop() + document.body.clientHeight / 2) })
    				        			.html(opts.loadingMsg)
                                        .appendTo(locale);

            msg.css("marginLeft", -msg.outerWidth() / 2);

            if (!opts.showLoadingImg) {
                msg.css(opts.msgcss);
            }

            return mask.add(msg);
        } else {
            return mask;
        }
    };


    $.loaded = function (locale, topMost) {
        var opts = { locale: "body", topMost: false };
        if (arguments.length == 1) {
            if ($.isPlainObject(arguments[0])) {
                $.extend(opts, arguments[0]);
            } else if ($.type(arguments[0]) == 'boolean') {
                opts.topMost = arguments[0];
            } else {
                opts.locale = arguments[0];
            }
        }
        if (arguments.length == 2) {
            if ($.type(arguments[0]) == 'boolean') {
                $.extend(opts, { locale: arguments[1], topMost: arguments[0] });
            } else {
                $.extend(opts, { locale: arguments[0], topMost: arguments[1] });
            }
        }
        var jq = opts.topMost ? top.$ : $, locale = jq(opts.locale);
        locale.removeClass("mask-container");
        locale.find("div.datagrid-mask-msg,div.datagrid-mask").remove();
    };

    $.loading.defaults = {
        opacity: '0.5',
        loadingMsg: '正在加载，请稍等...',
        locale: 'body',
        topMost: false,
        showLoadingImg: true,
        msgcss: {}, // 在不显示loading 图片时启作用
        showLoadingMsg: true // 显示加载动画图片及文字
    };

})(jQuery);




(function ($) {
    var icons = { "error": "notify-icon-error",
        "info": "notify-icon-info",
        "success": "notify-icon-success",
        "warning": "notify-icon-warning"
    },

        _show = $.messager.show;


    //  重写 $.messager.show 方法，使其支持图标以及默认的单个字符串参数的重载；该方法定义如下参数：
    //      options:    表示需要弹出消息的内容、图标和方式等信息，该参数类型可以为如下：
    //          JSON Object: 兼容 $.messager.show 官方默认 API 的所有属性，并在此基础上增加如下参数：
    //              icon: 表示弹出消息的图标，为一个 String 类型值，该值可选的内容与 $.messager.alert 方法的第三个参数可选内容相同；
    //                  包括："error", "info", "success", "warning"；
    //                  具体内容参见 $.messager.alert 该方法的官方默认 API 中第三个参数可选内容。
    //              position: 表示弹出消息的位置，为一个 String 类型值，该值可选的内容定义如下：
    //                  topLeft: 屏幕左上角, topCenter: 屏幕上方中间，topRight: 屏幕右上角
    //                  centerLeft: 屏幕左侧中间，center: 屏幕正中间，centerRight: 屏幕右侧中间
    //                  bottomLeft: 屏幕左下角，bottomCenter: 屏幕下方中间，bottomRight: 屏幕右下角
    //          String: 以 icon: "info"、title: "操作提醒"、msg: options 为默认的方式调用上一重载。
    //  返回值：返回弹出的消息框 easyui-window 对象
    $.notify = {
        defaults: {
            title: "",
            confirm: "您确认要进行该操作？",
            prompt: "请输入相应内容：",
            icon: "info",
            loading: "正在加载，请稍等...",
            timeout: 5000,
            showType: "slide",
            modal:false,
            shadow:false,
zIndex:9999999
        },
        show: function (options) {

            var isString = $.type(options) == 'string' || $.type(options) == 'boolean' || $.type(options) == 'number';
            if (isString) {
                return arguments.length == 1 ? $.messager.show({ msg: String(options) }) : $.messager.show({ title: options, msg: arguments[1], icon: arguments[2], position: arguments[3] });
            }
            var defaults = $.extend({}, $.messager.defaults, $.notify.defaults, options);

            var h = 0;
            $('body').find('[class *="window notify-"]').each(function () {
                //h += $(this).outerHeight() + 5;
                h += $(this).outerHeight() + 5;
            });
            var _body = document.getElementsByTagName("body")[0];
            var position = {
                topLeft: { right: "", left: 0, top: _body.scrollTop + document.documentElement.scrollTop, bottom: "" },
                topCenter: { right: "", top: _body.scrollTop + document.documentElement.scrollTop, bottom: "" },
                topRight: { left: "", right: 0, top: _body.scrollTop + document.documentElement.scrollTop, bottom: "" },
                centerLeft: { left: 0, right: "", bottom: "" },
                center: { right: "", bottom: "" },
                centerRight: { left: "", right: 0, bottom: "" },
                bottomLeft: { left: 0, right: "", top: "", bottom: -_body.scrollTop - document.documentElement.scrollTop },
                bottomCenter: { right: "", top: "", bottom: -_body.scrollTop - document.documentElement.scrollTop },
                bottomRight: { left: "", right: 0, top: "", bottom: -_body.scrollTop - document.documentElement.scrollTop + h }
            };

            var opts = $.extend({}, defaults, options);
            opts.style = position[options.position] ? position[options.position] : position.topCenter;
            var iconCls = icons[opts.icon] ? icons[opts.icon] : icons.info;
            opts.msg = "<div class='messager-icon " + iconCls + "'></div>" + "<div class='notify-icon-text'>" + opts.msg + "</div>";
            if(opts.onClose){
                return _show(opts,opts.onClose);
            }
            
            return _show(opts);
        }
    }

    var showMessage = function (icon, title, msg,fn) {
        var opts = {
            width:410,
            height: 60,

            position: 'bottomRight'
        };
        if ($.type(icon) === "object") {
            opts = $.extend({}, opts, icon);

        } else {
            opts.title = title;
            opts.icon = icon;
            opts.msg = msg;
        }
        //关闭后事件
        if(fn){
            opts.onClose=fn;
        }

        var msgr = $.notify.show(opts);

        // msgr.window('window').css({'background-color':'yellow','borderColor':'yellow'});
        // msgr.css({'border-color': 'yellow','background-color': 'yellow','overflow':'hidden'});

        msgr.window('window').addClass('notify-' + opts.icon);
        msgr.addClass('notify notify-' + opts.icon);


        var txtHeight = $(msgr).find('[class *="messager-icon notify-icon-"]').next().outerHeight();
        if (txtHeight > (opts.height - 30)) {
            $(msgr).height(txtHeight);
        }

        msgr.on('click', function () {
            msgr.window('close');
        });
        return msgr;
    }

    $.notify.info = function (title, msg,fn) {
        if (arguments.length == 3) {
            showMessage('info', title, msg,fn);
        } else {
            if ($.type(title) == "object") {
                var opts = title;
                opts.icon = 'info';
                opts.onClose = fn;
                showMessage(opts);
            } else {
                showMessage('info', '', title,fn);
            }
        }
    }

    $.notify.success = function (title, msg,fn) {
        if (arguments.length > 1) {
            showMessage('success', title, msg,fn);
        } else {
            if ($.type(title) == "object") {
                var opts = title;
                opts.icon = 'success';
                opts.onClose = fn;
                showMessage(opts);
            } else {
                showMessage('success', '', title,fn);
            }
        }
    }

    $.notify.error = function (title, msg,fn) {
        if (arguments.length > 1) {
            showMessage('error', title, msg,fn);
        } else {
            if ($.type(title) == "object") {
                var opts = title;
                opts.icon = 'error';
                opts.onClose = fn;
                showMessage(title);
            } else {
                showMessage('error', '', title,fn);
            }
        }
    }

    $.notify.warning = function (title, msg,fn) {
        if (arguments.length > 1) {
            showMessage('warning', title, msg,fn);
        } else {
            if ($.type(title) == "object") {
                var opts = title;
                opts.icon = 'warning';
                opts.onClose = fn;
                showMessage(title);
            } else {
                showMessage('warning', '', title,fn);
            }
        }
    }

})(jQuery);


$.extend($.fn.datagrid.defaults, {
    groupHeight: 25,
    expanderWidth: 30,
    groupStyler: function (value, rows) { return '' }
});

var groupview = $.extend({}, $.fn.datagrid.defaults.view, {
    render: function (target, container, frozen) {
        var table = [];
        var groups = this.groups;
        for (var i = 0; i < groups.length; i++) {
            table.push(this.renderGroup.call(this, target, i, groups[i], frozen));
        }
        $(container).html(table.join(''));
    },

    renderGroup: function (target, groupIndex, group, frozen) {
        var state = $.data(target, 'datagrid');
        var opts = state.options;
        var fields = $(target).datagrid('getColumnFields', frozen);

        if (frozen) {
            if (!(opts.rownumbers || (opts.frozenColumns && opts.frozenColumns.length))) {
                return '';
            }
        }

        var table = [];

        var css = opts.groupStyler.call(target, group.value, group.rows);
        var cs = parseCss(css, 'datagrid-group');
        table.push('<div group-index=' + groupIndex + ' ' + cs + '>');
        if ((frozen && (opts.rownumbers || opts.frozenColumns.length)) ||
				(!frozen && !(opts.rownumbers || opts.frozenColumns.length))) {
            table.push('<span class="datagrid-group-expander">');
            table.push('<span class="datagrid-row-expander datagrid-row-collapse">&nbsp;</span>');
            table.push('</span>');
        }
        if (!frozen) {
            table.push('<span class="datagrid-group-title">');
            table.push(opts.groupFormatter.call(target, group.value, group.rows));
            table.push('</span>');
        }
        table.push('</div>');

        table.push('<table class="datagrid-btable" cellspacing="0" cellpadding="0" border="0"><tbody>');
        var index = group.startIndex;
        for (var j = 0; j < group.rows.length; j++) {
            var css = opts.rowStyler ? opts.rowStyler.call(target, index, group.rows[j]) : '';
            var classValue = '';
            var styleValue = '';
            if (typeof css == 'string') {
                styleValue = css;
            } else if (css) {
                classValue = css['class'] || '';
                styleValue = css['style'] || '';
            }

            var cls = 'class="datagrid-row ' + (index % 2 && opts.striped ? 'datagrid-row-alt ' : ' ') + classValue + '"';
            var style = styleValue ? 'style="' + styleValue + '"' : '';
            var rowId = state.rowIdPrefix + '-' + (frozen ? 1 : 2) + '-' + index;
            table.push('<tr id="' + rowId + '" datagrid-row-index="' + index + '" ' + cls + ' ' + style + '>');
            table.push(this.renderRow.call(this, target, fields, frozen, index, group.rows[j]));
            table.push('</tr>');
            index++;
        }
        table.push('</tbody></table>');
        return table.join('');

        function parseCss(css, cls) {
            var classValue = '';
            var styleValue = '';
            if (typeof css == 'string') {
                styleValue = css;
            } else if (css) {
                classValue = css['class'] || '';
                styleValue = css['style'] || '';
            }
            return 'class="' + cls + (classValue ? ' ' + classValue : '') + '" ' +
					'style="' + styleValue + '"';
        }
    },

    bindEvents: function (target) {
        var state = $.data(target, 'datagrid');
        var dc = state.dc;
        var body = dc.body1.add(dc.body2);
        var clickHandler = ($.data(body[0], 'events') || $._data(body[0], 'events')).click[0].handler;
        body.unbind('click').bind('click', function (e) {
            var tt = $(e.target);
            var expander = tt.closest('span.datagrid-row-expander');
            if (expander.length) {
                var gindex = expander.closest('div.datagrid-group').attr('group-index');
                if (expander.hasClass('datagrid-row-collapse')) {
                    $(target).datagrid('collapseGroup', gindex);
                } else {
                    $(target).datagrid('expandGroup', gindex);
                }
            } else {
                clickHandler(e);
            }
            e.stopPropagation();
        });
    },

    onBeforeRender: function (target, rows) {
        var state = $.data(target, 'datagrid');
        var opts = state.options;

        initCss();

        var groups = [];
        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var group = getGroup(row[opts.groupField]);
            if (!group) {
                group = {
                    value: row[opts.groupField],
                    rows: [row]
                };
                groups.push(group);
            } else {
                group.rows.push(row);
            }
        }

        var index = 0;
        var newRows = [];
        for (var i = 0; i < groups.length; i++) {
            var group = groups[i];
            group.startIndex = index;
            index += group.rows.length;
            newRows = newRows.concat(group.rows);
        }

        state.data.rows = newRows;
        this.groups = groups;

        var that = this;
        setTimeout(function () {
            that.bindEvents(target);
        }, 0);

        function getGroup(value) {
            for (var i = 0; i < groups.length; i++) {
                var group = groups[i];
                if (group.value == value) {
                    return group;
                }
            }
            return null;
        }
        function initCss() {
            if (!$('#datagrid-group-style').length) {
                $('head').append(
					'<style id="datagrid-group-style">' +
					'.datagrid-group{height:' + opts.groupHeight + 'px;overflow:hidden;font-weight:bold;border-bottom:1px solid #ccc;}' +
					'.datagrid-group-title,.datagrid-group-expander{display:inline-block;vertical-align:bottom;height:100%;line-height:' + opts.groupHeight + 'px;padding:0 4px;}' +
					'.datagrid-group-expander{width:' + opts.expanderWidth + 'px;text-align:center;padding:0}' +
					'.datagrid-row-expander{margin:' + Math.floor((opts.groupHeight - 16) / 2) + 'px 0;display:inline-block;width:16px;height:16px;cursor:pointer}' +
					'</style>'
				);
            }
        }
    },
    onAfterRender: function (target) {
        $.fn.datagrid.defaults.view.onAfterRender.call(this, target);

        var view = this;
        var state = $.data(target, 'datagrid');
        var opts = state.options;
        if (!state.onResizeColumn) {
            state.onResizeColumn = opts.onResizeColumn;
        }
        if (!state.onResize) {
            state.onResize = opts.onResize;
        }
        opts.onResizeColumn = function (field, width) {
            if (!opts.fitColumns) {
                view.resizeGroup(target);
            }
            state.onResizeColumn.call(target, field, width);
        }
        opts.onResize = function (width, height) {
            if (opts.fitColumns) {
                view.resizeGroup(target);
            }
            state.onResize.call($(target).datagrid('getPanel')[0], width, height);
        }
        view.resizeGroup(target);
    }
});

$.extend($.fn.datagrid.methods, {
    groups: function (jq) {
        return jq.datagrid('options').view.groups;
    },
    expandGroup: function (jq, groupIndex) {
        return jq.each(function () {
            var view = $.data(this, 'datagrid').dc.view;
            var group = view.find(groupIndex != undefined ? 'div.datagrid-group[group-index="' + groupIndex + '"]' : 'div.datagrid-group');
            var expander = group.find('span.datagrid-row-expander');
            if (expander.hasClass('datagrid-row-expand')) {
                expander.removeClass('datagrid-row-expand').addClass('datagrid-row-collapse');
                group.next('table').show();
            }
            $(this).datagrid('fixRowHeight');
        });
    },
    collapseGroup: function (jq, groupIndex) {
        return jq.each(function () {
            var view = $.data(this, 'datagrid').dc.view;
            var group = view.find(groupIndex != undefined ? 'div.datagrid-group[group-index="' + groupIndex + '"]' : 'div.datagrid-group');
            var expander = group.find('span.datagrid-row-expander');
            if (expander.hasClass('datagrid-row-collapse')) {
                expander.removeClass('datagrid-row-collapse').addClass('datagrid-row-expand');
                group.next('table').hide();
            }
            $(this).datagrid('fixRowHeight');
        });
    },
    scrollToGroup: function (jq, groupIndex) {
        return jq.each(function () {
            var state = $.data(this, 'datagrid');
            var dc = state.dc;
            var grow = dc.body2.children('div.datagrid-group[group-index="' + groupIndex + '"]');
            if (grow.length) {
                var groupHeight = grow.outerHeight();
                var headerHeight = dc.view2.children('div.datagrid-header')._outerHeight();
                var frozenHeight = dc.body2.outerHeight(true) - dc.body2.outerHeight();
                var top = grow.position().top - headerHeight - frozenHeight;
                if (top < 0) {
                    dc.body2.scrollTop(dc.body2.scrollTop() + top);
                } else if (top + groupHeight > dc.body2.height() - 18) {
                    dc.body2.scrollTop(dc.body2.scrollTop() + top + groupHeight - dc.body2.height() + 18);
                }
            }
        });
    }
});

$.extend(groupview, {
    refreshGroupTitle: function (target, groupIndex) {
        var state = $.data(target, 'datagrid');
        var opts = state.options;
        var dc = state.dc;
        var group = this.groups[groupIndex];
        var span = dc.body2.children('div.datagrid-group[group-index=' + groupIndex + ']').find('span.datagrid-group-title');
        span.html(opts.groupFormatter.call(target, group.value, group.rows));
    },
    resizeGroup: function (target, groupIndex) {
        var state = $.data(target, 'datagrid');
        var dc = state.dc;
        var ht = dc.header2.find('table');
        var fr = ht.find('tr.datagrid-filter-row').hide();
        var ww = ht.width();
        if (groupIndex == undefined) {
            var groupHeader = dc.body2.children('div.datagrid-group');
        } else {
            var groupHeader = dc.body2.children('div.datagrid-group[group-index=' + groupIndex + ']');
        }
        groupHeader._outerWidth(ww);
        fr.show();
    },

    insertRow: function (target, index, row) {
        var state = $.data(target, 'datagrid');
        var opts = state.options;
        var dc = state.dc;
        var group = null;
        var groupIndex;

        if (!state.data.rows.length) {
            $(target).datagrid('loadData', [row]);
            return;
        }

        for (var i = 0; i < this.groups.length; i++) {
            if (this.groups[i].value == row[opts.groupField]) {
                group = this.groups[i];
                groupIndex = i;
                break;
            }
        }
        if (group) {
            if (index == undefined || index == null) {
                index = state.data.rows.length;
            }
            if (index < group.startIndex) {
                index = group.startIndex;
            } else if (index > group.startIndex + group.rows.length) {
                index = group.startIndex + group.rows.length;
            }
            $.fn.datagrid.defaults.view.insertRow.call(this, target, index, row);

            if (index >= group.startIndex + group.rows.length) {
                _moveTr(index, true);
                _moveTr(index, false);
            }
            group.rows.splice(index - group.startIndex, 0, row);
        } else {
            group = {
                value: row[opts.groupField],
                rows: [row],
                startIndex: state.data.rows.length
            }
            groupIndex = this.groups.length;
            dc.body1.append(this.renderGroup.call(this, target, groupIndex, group, true));
            dc.body2.append(this.renderGroup.call(this, target, groupIndex, group, false));
            this.groups.push(group);
            state.data.rows.push(row);
        }

        this.refreshGroupTitle(target, groupIndex);
        this.resizeGroup(target);

        function _moveTr(index, frozen) {
            var serno = frozen ? 1 : 2;
            var prevTr = opts.finder.getTr(target, index - 1, 'body', serno);
            var tr = opts.finder.getTr(target, index, 'body', serno);
            tr.insertAfter(prevTr);
        }
    },

    updateRow: function (target, index, row) {
        var opts = $.data(target, 'datagrid').options;
        $.fn.datagrid.defaults.view.updateRow.call(this, target, index, row);
        var tb = opts.finder.getTr(target, index, 'body', 2).closest('table.datagrid-btable');
        var groupIndex = parseInt(tb.prev().attr('group-index'));
        this.refreshGroupTitle(target, groupIndex);
    },

    deleteRow: function (target, index) {
        var state = $.data(target, 'datagrid');
        var opts = state.options;
        var dc = state.dc;
        var body = dc.body1.add(dc.body2);

        var tb = opts.finder.getTr(target, index, 'body', 2).closest('table.datagrid-btable');
        var groupIndex = parseInt(tb.prev().attr('group-index'));

        $.fn.datagrid.defaults.view.deleteRow.call(this, target, index);

        var group = this.groups[groupIndex];
        if (group.rows.length > 1) {
            group.rows.splice(index - group.startIndex, 1);
            this.refreshGroupTitle(target, groupIndex);
        } else {
            body.children('div.datagrid-group[group-index=' + groupIndex + ']').remove();
            for (var i = groupIndex + 1; i < this.groups.length; i++) {
                body.children('div.datagrid-group[group-index=' + i + ']').attr('group-index', i - 1);
            }
            this.groups.splice(groupIndex, 1);
        }

        var index = 0;
        for (var i = 0; i < this.groups.length; i++) {
            var group = this.groups[i];
            group.startIndex = index;
            index += group.rows.length;
        }
    }
});





