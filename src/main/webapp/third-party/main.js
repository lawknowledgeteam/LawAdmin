$(function(){	
	//$("#jquery-accordion-menu").jqueryAccordionMenu();
	//顶部导航切换
	$("#demo-list li").click(function(){
		$("#demo-list li.active").removeClass("active")
		$(this).addClass("active");
	});	
	
	$.expr[":"].Contains = function(a, i, m) {
		return (a.textContent || a.innerText || "").toUpperCase().indexOf(m[3].toUpperCase()) >= 0;
	};
	
	filterList($("#filterinput"), $("#demo-list"));
	
	addTabEasyUi("首页","/web_admin/home.html",false);
});

bootbox.setLocale("zh_CN");//弹窗提示

function addTabEasyUi(subtitle, url,closable) {
	if($('#tabs').tabs('exists',subtitle)){
		$('#tabs').tabs('select',subtitle)
		return;
	}
	$('#tabs').tabs('add',{
		title: subtitle,
		content: createFrame(url),
		closable: closable
	});
	if($('.tabs > li').length>5){//最多开启的tab数
		$('#tabs').tabs('close',1)
	}
}
function filterList(input, list) {

	$(input).change(function() {
		var filter = $(this).val();
		if (filter) {
			$matches = $(list).find("a:Contains(" + filter + ")").parent();
			$("li", list).not($matches).slideUp();
			$matches.slideDown();
		} else {
			$(list).find("li").slideDown();
		}
		return false;
	}).keyup(function() {
		$(this).change();
	});
}
function createFrame(url) {
    var s = '<div style="height: 99%;" ><iframe scrolling="auto" frameborder="0" src="'+url+'" style="width: 100%;height: 100%"></iframe></div>';
    return s;
}
function tabClose() {
	var tab = $('#tabs').tabs('getSelected');
	if (tab){
		var index = $('#tabs').tabs('getTabIndex', tab);
		if(index>0){
			$('#tabs').tabs('close', index);
		}
	}
}

function logout(){
	bootbox.confirm("确认退出吗？", function (result) {
        if (result) {
        	window.location="/web_admin/quit";
        }
    });
}
/*function clearCookie(){  
	var keys=document.cookie.match(/[^ =;]+(?=\=)/g);  
	if (keys) {  
		for (var i = keys.length; i--;)  
			document.cookie=keys[i]+'=0;expires=' + new Date( 0).toUTCString(); 
	}  
}*/

