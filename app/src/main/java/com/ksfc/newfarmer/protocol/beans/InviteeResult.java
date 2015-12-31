package com.ksfc.newfarmer.protocol.beans;

import java.io.Serializable;
import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

@SuppressWarnings("serial")
public class InviteeResult extends ResponseResult {

    public List<Invitee> invitee;

    // account:邀请人的手机号
    // nickname：邀请人的昵称
    // name: 邀请人名字
    public static class Invitee implements Serializable {
        public String account;
        public String nickname;
        public String name;
        public int newOrdersNumber;
        public String photo;
        public String userId;

    }
}
