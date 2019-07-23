;
(function ($, window, document, undefined) {
    $.leftNav = function (where, data) {
        var where = where;
        var menu = "";
        var html = "";
        var defaultmenu = {
            name: '', //名字
            level: '0',//第几级
            haschildren: true, //是否有子节点
            url: '#', //url路径
            isopen: false, //是否展开
            children: [{ //子节点
                name: '1',
                level: '1',
                haschildren:true,
                url: '',
                isopen: true,
                children: [{
                    name: '1.1',
                    level: '2',
                    haschildren: true,
                    url: '',
                    isopen: true,
                    children: [{
                        name: '1.1.1',
                        level: '3',
                        haschildren: false,
                        url: './14-1.html',
                        isopen: true,
                        children: ''
                    }]
                }]
            }]
        };
        var here = $("body").find(where);
        if (!here.length) {
            where = "body";
        }
        if (data) {
            menu = data;
        } else {
            menu = defaultmenu;
        }
        $(where).append('<div class="nav_box"><ul class="nav"></ul></div>');
        sidebarNav(menu);

        function sidebarNav(data) {
            html += '<li class="left_nav_list">';
            html += '<div class="left_nav_name level' + data.level;
            if (data.isopen) {
                html += ' nav_open';
            }
            html += '"><em>' + data.name + '</em></div>';
            if (data.haschildren) {
                html += '<ul class="nav_ul"';
                if (data.isopen) {
                    html += ' style="display:block;"';
                }
                html += '>';
                for (var i = 0, l = data.children.length; i < l; i++) {
                    var children = data.children[i];
                    if (children.haschildren) {
                        sidebarNav(children);
                    } else {
                        html += '<li class="nav_li level' + children.level;
                        if (children.isopen) {
                            html += ' nav_li_open';
                        }
                        html += '" data-url="' + children.url + '">';
                        html += '<em>' + children.name + '</em>';
                        html += '</li>';
                    }
                }
                html += '</ul>';
            }
            html += '</li>';
        }

        $(where + " .nav").html(html);
        $(where + " .left_nav_name").on("click", function () {
            var open = $(this).hasClass("nav_open");
            if (open) {
                $(this).removeClass("nav_open");
                $(this).next(".nav_ul").slideUp();
            } else {
                $(this).addClass("nav_open");
                $(this).next(".nav_ul").slideDown();
                $(this).parents(".left_nav_list").siblings("li").find(".nav_ul").slideUp();
                $(this).parents(".left_nav_list").siblings("li").find(".left_nav_name").removeClass("nav_open");
            }
        })
        $(where + " .nav_li").on("click", function () {
            $(where + " .nav_li").removeClass("nav_li_open");
            $(this).addClass("nav_li_open");
            $(this).siblings(".left_nav_list").find(".nav_ul").slideUp();
            $(this).siblings(".left_nav_list").find(".left_nav_name").removeClass("nav_open");
            var url = $(this).attr("data-url");
            console.log(url);
        })
    }
})(jQuery, window, document);