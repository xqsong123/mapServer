package com.map.utils;


import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

//mongodb 连接数据库工具类
public class MongoDBConnection {

    private static final Log log = LogFactory.getLog(MongoDBConnection.class);

    private static MongoClient mongoClient;
    //数据库名
    private static String dataBaseName = "ybss";

//    //不通过认证获取连接数据库对象
//    public static MongoDatabase getConnect1() {
//        //连接到 mongodb 服务
//        MongoClient mongoClient = new MongoClient("56.3.124.137", 27017);
//
//        //连接到数据库
//        MongoDatabase mongoDatabase = mongoClient.getDatabase(dataBaseName);
//
//        //返回连接数据库对象
//        return mongoDatabase;
//    }

    static {
        try {
            MongoClientOptions mongoClientOptions = MongoClientOptions.builder()
                    .threadsAllowedToBlockForConnectionMultiplier(10)
                    .cursorFinalizerEnabled(true)
                    .build();
            mongoClient = new MongoClient(new ServerAddress("localhost", 27017),mongoClientOptions);
        }catch (RuntimeException e){
            log.error("无法连接到mongodb数据库");
        }
    }

    public static MongoDatabase getConnect2() {

        try {
            MongoDatabase database = mongoClient.getDatabase("ybss");
            return database;
        } catch (RuntimeException e1) {
            log.error("无法获取数据库 : ybss");
        }
        return null;

//        MongoDatabase database = mongoClient.getDatabase("ybss");
        //返回连接数据库对象
    }

    public static MongoDatabase getajjqConnect() {

        try {
            MongoDatabase database = mongoClient.getDatabase("demo");
            return database;
        } catch (RuntimeException e1) {
            log.error("无法获取数据库 : ajjq");
        }
        return null;

//        MongoDatabase database = mongoClient.getDatabase("ybss");
        //返回连接数据库对象
    }

//    //需要密码认证方式连接
//    public static MongoDatabase getConnect3() {
//        List<ServerAddress> adds = new ArrayList<>();
//        //ServerAddress()两个参数分别为 服务器地址 和 端口
//        ServerAddress serverAddress = new ServerAddress("localhost", 27017);
//        adds.add(serverAddress);
//
//        List<MongoCredential> credentials = new ArrayList<>();
//        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
//        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential("username", "databaseName", "password".toCharArray());
//        credentials.add(mongoCredential);
//
//        //通过连接认证获取MongoDB连接
//        MongoClient mongoClient = new MongoClient(adds, credentials);
//
//        //连接到数据库
//        MongoDatabase mongoDatabase = mongoClient.getDatabase("demo");
//
//        //返回连接数据库对象
//        return mongoDatabase;
//    }

}
