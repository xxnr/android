package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;

import java.util.List;

/**
 * Created by HePeng on 2016/3/22.
 */
public class OfflinePayWayResult extends ResponseResult {

    /**
     * type : 3
     * name : 现金
     */

    public List<OfflinePayTypeEntity> offlinePayType;


    public static class OfflinePayTypeEntity {
        public int type;
        public String name;
    }
}
