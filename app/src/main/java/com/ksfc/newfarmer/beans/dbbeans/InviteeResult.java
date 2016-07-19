package com.ksfc.newfarmer.beans.dbbeans;

import com.ksfc.newfarmer.protocol.ResponseResult;

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


}
