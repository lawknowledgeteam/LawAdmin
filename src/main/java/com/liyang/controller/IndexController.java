package com.liyang.controller;

import com.jfinal.core.Controller;

public class IndexController extends Controller {
    public void index() {
       //renderText("Hello Wifi Finder!");
       //renderText(getPara());
    	render("/web_pc/index.html");
    }
}
