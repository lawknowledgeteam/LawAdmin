/**
 * @description: 日期时间通用JS文件
 * @author: lainingrong
 * @email:
 * @time: 2013-04-19
 */
(function () {
    /***************yyyy-MM-dd 转成 毫秒数**********/
    showToDB = function (dataTime) {
        if (dataTime) {
            var arry = dataTime.split("-");
            var t = (parseInt(arry[1], 10) - 1);
            if (t < 10) {
                arry[1] = '0' + t;
            } else {
                arry[1] = '' + t;
            }
            var now = new Date(arry[0], arry[1], arry[2]);
            if (!isNaN(now.getTime())) {
                return now.getTime();
            } else {
                return "";
            }
        } else {
            return "";
        }
    };
    /**
     *转换日期对象为日期字符串
     * @param date 日期对象
     * @param isFull 是否为完整的日期数据,
     *               为true时, 格式如"2000-03-05 01:05:04"
     *               为false时, 格式如 "2000-03-05"
     * @return 符合要求的日期字符串
     */
    getSmpFormatDate = function (date, isFull) {
        var pattern = "";
        if (isFull == true || isFull == undefined) {
            pattern = "yyyy-MM-dd hh:mm:ss";
        } else {
            pattern = "yyyy-MM-dd";
        }
        return getFormatDate(date, pattern);
    };
    /**
     * 获取当前时间的前一天
     * */
    getYesterdayFormat = function (date) {
        var yesterdayMillons = date.getTime() - 1000 * 60 * 60 * 24;
        var yesterday = new Date();
        yesterday.setTime(yesterdayMillons);
        var yearStr = yesterday.getFullYear();
        var dayStr = yesterday.getDate();
        var monthStr = yesterday.getMonth() + 1;
        if (monthStr < 10) {
            monthStr = "0" + monthStr;
        }
        if (dayStr < 10) {
            dayStr = "0" + dayStr;
        }
        var resultData = yearStr + "-" + monthStr + "-" + dayStr;
        return resultData;
    };

    /**
     *转换当前日期对象为日期字符串
     * @param date 日期对象
     * @param isFull 是否为完整的日期数据,
     *               为true时, 格式如"2000-03-05 01:05:04"
     *               为false时, 格式如 "2000-03-05"
     * @return 符合要求的日期字符串
     */
    getSmpFormatNowDate = function (isFull) {
        return getSmpFormatDate(new Date(), isFull);
    };

    /**
     *转换long值为日期字符串
     * @param l long值
     * @param isFull 是否为完整的日期数据,
     *               为true时, 格式如"2000-03-05 01:05:04"
     *               为false时, 格式如 "2000-03-05"
     * @return 符合要求的日期字符串
     */
    getSmpFormatDateByLong = function (l, isFull) {
        return getSmpFormatDate(new Date(l), isFull);
    };
    /**
     *转换long值为日期字符串
     * @param l long值
     * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss
     * @return 符合要求的日期字符串
     */

    getFormatDateByLong = function (l, pattern) {
        if (l && !isNaN(l)) {
            return getFormatDate(new Date(l), pattern);
        } else {
            return "";
        }
    };
    /**
     *转换日期对象为日期字符串
     * @param l long值
     * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss
     * @return 符合要求的日期字符串
     */
    getFormatDate = function (date, pattern) {
        if (date == undefined) {
            date = new Date();
        }
        if (pattern == undefined) {
            pattern = "yyyy-MM-dd hh:mm:ss";
        }
        return date.format(pattern);
    };

//获得两个日期字符串之间的天数差
    daysBetween = function (startDate, endDate) {
        var cha = (Date.parse(startDate.replace("-", "/")) - Date.parse(endDate.replace("-", "/")));
        return Math.abs(cha) / 86400000;
    };
    compareDays = function (startDate, endDate) {
        var startdate = startDate.split('-');
        var enddate = endDate.split('-');
        startdate = new Date(startdate[0], startdate[1], startdate[2]);
        enddate = new Date(enddate[0], enddate[1], enddate[2]);
        if (startdate > enddate) {
            return true;
        } else {
            return false;
        }
    };
    getTodayPrecedeWeek = function () {
        var now = new Date();
        var ls = new Date(now.getTime() - 1000 * 60 * 60 * 24 * 7);
        var date = ls.getYear() + "-" + (ls.getMonth() + 1) + "-" + ls.getDate();
        return date;
    };
// 取月的最后一天
    getMonthLastDay = function (dat) {
        var year = dat.substring(0, 4);
        var month = dat.substring(5, 7);
        var new_year = Number(year);
        var new_month = Number(month);
        new_month++;
        if (new_month > 12) {
            new_month -= 12;
            new_year++;
        }
        var new_date = new Date(new_year, month, 1, 0, 0, 0);
        dat = dat + "-" + (new Date(new_date.getTime() - 1000 * 60)).getDate();
        return dat;
    };
    /*
     * *****************************************************日期扩展
     */
    Date.prototype.toCommonCase = function () {
        var xYear = this.getYear();
        xYear = xYear + 1900;
        var xMonth = this.getMonth() + 1;
        if (xMonth < 10) {
            xMonth = "0" + xMonth;
        }
        var xDay = this.getDate();
        if (xDay < 10) {
            xDay = "0" + xDay;
        }
        var xHours = this.getHours();
        if (xHours < 10) {
            xHours = "0" + xHours;
        }
        var xMinutes = this.getMinutes();
        if (xMinutes < 10) {
            xMinutes = "0" + xMinutes;
        }
        var xSeconds = this.getSeconds();
        if (xSeconds < 10) {
            xSeconds = "0" + xSeconds;
        }
        return xYear + "-" + xMonth + "-" + xDay + " " + xHours + ":" + xMinutes + ":" + xSeconds;
    };
    Date.prototype.toCommon2Case = function () {
        var xYear = this.getFullYear();
        var xMonth = this.getMonth() + 1;
        if (xMonth < 10) {
            xMonth = "0" + xMonth;
        }
        var xDay = this.getDate();
        if (xDay < 10) {
            xDay = "0" + xDay;
        }
        return xYear + "-" + xMonth + "-" + xDay;
    };
//扩展Date的format方法 
    Date.prototype.format = function (format) {
        var o = {
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "h+": this.getHours(),
            "m+": this.getMinutes(),
            "s+": this.getSeconds(),
            "q+": Math.floor((this.getMonth() + 3) / 3),
            "S": this.getMilliseconds()
        };
        if (/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (var k in o) {
            if (new RegExp("(" + k + ")").test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
    };
    Date.prototype.addDays = function (d) {
        this.setDate(this.getDate() + d);
    };
    Date.prototype.addWeeks = function (w) {
        this.addDays(w * 7);
    };
    Date.prototype.addMonths = function (m) {
        var d = this.getDate();
        this.setMonth(this.getMonth() + m);
        if (this.getDate() < d)
            this.setDate(0);
    };
    Date.prototype.addYears = function (y) {
        var m = this.getMonth();
        this.setFullYear(this.getFullYear() + y);

        if (m < this.getMonth()) {
            this.setDate(0);
        }
    };
    getnowTime = function (d) {
        var date = new Date();
        if (d) {
            return date.format("yyyy-MM-dd hh:mm:ss");
        } else {
            return date.format("yyyy-MM-dd");
        }
    };
})();