package com.ksfc.newfarmer.beans.dbbeans;

import com.ksfc.newfarmer.protocol.ResponseResult;


import java.util.List;

/**
 * Created by HePeng on 2016/2/1.
 */
public class PotentialListResult extends ResponseResult {

    /**
     * count : 5
     * potentialCustomers : [{"_id":"56b07a7b25228d9a3e2c4a3c","name":"何鹏","phone":"13271788770","nameInitial":"H","namePinyin":"hepeng","isRegistered":false,"sex":false},{"_id":"56e24077944958272af5106f","name":"何鹏","phone":"13271788773","nameInitial":"H","namePinyin":"hepeng","isRegistered":false,"sex":true},{"_id":"56b07ac7b5e1eed73e386dc9","name":"美佳","phone":"15201532356","nameInitial":"M","namePinyin":"meijia","isRegistered":false,"sex":false},{"_id":"56cfc80d295199af731c56bb","name":"美佳","phone":"18732199883","nameInitial":"M","namePinyin":"meijia","isRegistered":true,"sex":false},{"_id":"56b07f9025228d9a3e2c4a3d","name":"秀秀","phone":"13246464645","nameInitial":"X","namePinyin":"xiuxiu","isRegistered":false,"sex":true}]
     */

    public int count;
    public String userId;//数据库动态建表

    /**
     * _id : 56b07a7b25228d9a3e2c4a3c
     * name : 何鹏
     * phone : 13271788770
     * nameInitial : H
     * namePinyin : hepeng
     * isRegistered : false
     * sex : false
     */

    public List<PotentialCustomersEntity> potentialCustomers;


}
