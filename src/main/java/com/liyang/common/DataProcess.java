package com.liyang.common;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class DataProcess  implements Runnable{
	private static  BlockingQueue<String> myQueue= new LinkedBlockingQueue<>(10);
	DataProcess() 
	{ 
		//queue = q; 
	}
	
	public void run() {
	    try {
	        while(true) 
	        { 
	        	process(myQueue.take()); 
	        }    //当队列空时，消费者阻塞等待
	    } 
	    catch (InterruptedException ex) 
	    { 
	    	System.out.println("catch Exception："+ex.getMessage());
	    }
    }
	
	void process(String x) { 
		System.out.println("process");
	}
}

