package com.ksfc.newfarmer.protocol.beans;

import com.google.gson.Gson;
import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by CAI on 2016/2/1.
 */
public class PotentialListResult extends ResponseResult {

    /**
     * count : 0
     * potentialCustomers : [] 客户列表
     * countLeftToday : 15
     * totalPageNo : 0
     * currentPageNo : 1
     */

    public int count;
    public int countLeftToday;
    public int totalPageNo;
    public int currentPageNo;
    public List<PotentialCustomers> potentialCustomers;

    public static class PotentialCustomers {
        public String _id;
        public String name;
        public String phone;
        public String remarks;
        public boolean isRegistered;
        public boolean sex;
    }
}
