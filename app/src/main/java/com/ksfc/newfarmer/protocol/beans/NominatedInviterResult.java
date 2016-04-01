package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * Created by HePeng on 2016/2/25.
 */
public class NominatedInviterResult extends ResponseResult {

    public Datas nominated_inviter;

    public static class Datas {
        public String phone;    //推荐新农代表的手机号
        public String name;     //推荐新农代表的姓名

    }
}
