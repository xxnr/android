package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class InviteeResult extends ResponseResult implements Serializable {


    /**
     * invitee : [{"userId":"7d1853749c","account":"18732199883","name":"As ","dateinvited":"2016-04-07T05:14:50.853Z","sex":true,"newOrdersNumber":0,"namePinyin":"as ","nameInitial":"A"}]
     * total : 1
     */

    public int total;
    /**
     * userId : 7d1853749c
     * account : 18732199883
     * name : As
     * dateinvited : 2016-04-07T05:14:50.853Z
     * sex : true
     * newOrdersNumber : 0
     * namePinyin : as
     * nameInitial : A
     */

    public List<InviteeEntity> invitee;

    @Table(name = "InviteeEntity")
    public static class InviteeEntity implements Serializable {
        @Id
        public String userId;
        @Column(column = "account")
        public String account;
        @Column(column = "name")
        public String name;
        @Column(column = "dateinvited")
        public String dateinvited;
        @Column(column = "sex")
        public boolean sex;
        @Column(column = "newOrdersNumber")
        public int newOrdersNumber;
        @Column(column = "namePinyin")
        public String namePinyin;
        @Column(column = "nameInitial")
        public String nameInitial;


    }
}
