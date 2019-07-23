package com.liyang.controller;

import com.liyang.common.GlobalVar;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenerateResidenceData  implements Job, Serializable {
	public static long ConstStayTime = 1000*60;  //分析停留时长为一分钟
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //进行数据存储脚本
        SparkSession sparkSession = SparkSession.builder()
                .master("local[*]")
                .appName("stay time")
                .getOrCreate();
        Dataset<Row> results = sparkSession.read()
                .format("jdbc")
                .option("url", "jdbc:mysql://211.87.227.204:8306/wifinder?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC")
                .option("dbtable", "tb_scan_result")
                .option("user", "sa")
                .option("password", "212-!2018E")
                .load();
        //时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取前一天时间
        String proxTime = null;
        //获取当前时间
        String nowTime = null;
        Date date = new Date();
        nowTime = simpleDateFormat.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        proxTime = simpleDateFormat.format(calendar.getTime());
        //查询的开始时间，结束时间
        String startTime = proxTime;
        String endTime = nowTime;
        //算停留时间的时间间隔
        long intervalTime = 1000*60*5;
        //上一条记录的objectMac
        long previousObject = -1;
        //上一条记录的创建时间
        long previousCreateTime = -1;
        //返回到数据库的统计数据
        List<String> analysisData = new ArrayList<>();
        //统计同一个设备在不同采集设备上面的统计数据
        HashMap<Long, ArrayList> diMap = new HashMap<>();
        //判断条件搜索具备相应条件的数据并对其进行相应排序
        Dataset<Row> sortResults = results.where("CreateTime>='"+startTime+"' and CreateTime<= '"+endTime+"'")
                .sort("ObjectMac","CreateTime");
        sortResults.show(100);
        //将dataset转化为list，进行数据分析
        List<Row> sortResultList = sortResults.collectAsList();
        int index=1;
        for (Row r:sortResultList){
            //若是第一条，则把第一条的数据赋值给他们
            if(previousObject==-1) {
                previousObject = r.getLong(2);
                previousCreateTime = r.getTimestamp(6).getTime();
            }
            //判断当前数据是否还是该设备和是否还是这一天
            if(previousObject == r.getLong(2) && simpleDateFormat.format(previousCreateTime).equals(simpleDateFormat.format(r.getTimestamp(6)))){
                //仍旧是该设备
                //是同一天的数据
                //判断map是否已有已知的deviceid
                if(diMap.containsKey(r.getLong(1))){
                    //如果存在，那么判断是否为连续数据
                    //判断创建时间是否为连续的(1000毫秒等于1秒)
                    if(r.getTimestamp(6).getTime()-((Long)diMap.get(r.getLong(1)).get(2))<=intervalTime){
                        //为连续数据
                        diMap.get(r.getLong(1)).set(0,((Long)diMap.get(r.getLong(1)).get(0)) + r.getTimestamp(6).getTime()-((Long)diMap.get(r.getLong(1)).get(2)));
                    }else{
                        //是一次新的停留
                        diMap.get(r.getLong(1)).set(1,((int)diMap.get(r.getLong(1)).get(1))+1);
                        diMap.get(r.getLong(1)).set(0,((Long)diMap.get(r.getLong(1)).get(0)) + (1000L*60L));
                    }
                    diMap.get(r.getLong(1)).set(2,r.getTimestamp(6).getTime());
                }else{
                    //如果不存在，那么将这个deviceId作为key放入到map中去
                    // diMap.put(r.getLong(1),);
                    ArrayList<Object> diList = new ArrayList<>();
                    diList.add(0,0L);//总停留时间
                    diList.add(1,1);//总停留次数
                    diList.add(2,r.getTimestamp(6).getTime());//最近计算的时间点
                    diMap.put(r.getLong(1),diList);
                }
            }else{
                //不是同一个设备或者不是同一天或者不是同一个采集设备
                //将数据保存起来,首先判断总停留时长在一分钟以上的才存储到数据库中
                for(Long deviceId:diMap.keySet()){
                    //遍历map，将数据放入到字符串中
                    ArrayList arrayList = diMap.get(deviceId);
                    if(((Long)arrayList.get(0))>=ConstStayTime) {
                        analysisData.add(deviceId + " " + previousObject + " " + simpleDateFormat.format(arrayList.get(2)) + " " + arrayList.get(1) + " " + ((Long) arrayList.get(0)) / 1000 / 60);
                    }else if(((Long)arrayList.get(0))==0){
                        //如果仅有一条该记录，那么设置时间为1分钟
                        analysisData.add(deviceId + " " + previousObject + " " + simpleDateFormat.format(arrayList.get(2)) + " " + arrayList.get(1) + " " + 1L);
                    }
                }
                //初始化统计属性
                previousObject = r.getLong(2);
                diMap.clear();
            }
            previousCreateTime = r.getTimestamp(6).getTime();
            //判断是否是最后一条
            if(sortResultList.size() == index){
                //证明是最后一条
                //将最后一条信息加入
                //将数据保存起来
                for(Long deviceId:diMap.keySet()){
                    //遍历map，将数据放入到字符串中
                    ArrayList arrayList = diMap.get(deviceId);
                    if(((Long)arrayList.get(0))>=ConstStayTime) {
                        analysisData.add(deviceId + " " + previousObject + " " + simpleDateFormat.format(arrayList.get(2)) + " " + arrayList.get(1) + " " + ((Long) arrayList.get(0)) / 1000 / 60);
                    }else if(((Long)arrayList.get(0))==0){
                        //如果仅有一条该记录，那么设置时间为1分钟
                        analysisData.add(deviceId + " " + previousObject + " " + simpleDateFormat.format(arrayList.get(2)) + " " + arrayList.get(1) + " " + 1L);
                    }
                }
            }
            index++;
        }
        System.out.println(analysisData.size());
        //只能有一个sparkContext在运行
        sparkSession.sparkContext().stop();
        //将数据传送到数据库存储起来
        JavaSparkContext sparkContext = new JavaSparkContext("local[*]" ,"upload Date");
        SQLContext sqlContext = new SQLContext(sparkContext);
        JavaRDD<String> parallelizeData = sparkContext.parallelize(analysisData);
        JavaRDD<Row> stayTimeRDD = parallelizeData.map(new Function<String,Row>() {
            // private static final long serialVersionUID = 2743346561101954630L;
            public Row call(String line) throws Exception {
                String[] split = line.split(" ");
                return RowFactory.create(Long.valueOf(split[0]),Long.valueOf(split[1]),java.sql.Date.valueOf(split[2]),Integer.valueOf(split[3]), Long.valueOf(split[4]));
            }
        });
        //动态构造DataFrame的元数据
        List<StructField> structFields = new ArrayList();
        structFields.add(DataTypes.createStructField("DeviceID",DataTypes.LongType,true));
        structFields.add(DataTypes.createStructField("ObjectMac",DataTypes.LongType,true));
        structFields.add(DataTypes.createStructField("CreateTime",DataTypes.DateType,true));
        structFields.add(DataTypes.createStructField("ResidenceNumber",DataTypes.IntegerType,true));
        structFields.add(DataTypes.createStructField("TotalResidenceTime",DataTypes.LongType,true));
        //构建StructType，用于最后DataFrame元数据的描述
        StructType structType = DataTypes.createStructType(structFields);
        //构造DataFrame（Dataset）
        Dataset<Row> dataFrame = sqlContext.createDataFrame(stayTimeRDD, structType);
        //构造写入的数据库信息
        String url = "jdbc:mysql://211.87.227.204:8306/wifinder?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";
        Properties connectionProperties = new Properties();
        connectionProperties.put("user","sa");
        connectionProperties.put("password","212-!2018E");
        connectionProperties.put("driver","com.mysql.jdbc.Driver");
        //将数据写入数据库表中
        dataFrame.write().mode("append").jdbc(url,"tb_residence_time",connectionProperties);
        sparkContext.stop();
        System.out.println(nowTime+"执行完"+proxTime+"的数据分析上传");
    }
}
