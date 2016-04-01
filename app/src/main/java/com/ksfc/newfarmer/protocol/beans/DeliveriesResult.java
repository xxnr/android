package com.ksfc.newfarmer.protocol.beans;

import com.google.gson.Gson;
import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by HePeng on 2016/3/9.
 */
public class DeliveriesResult extends ResponseResult {
    /**
     * count : 1
     * items : [{"deliveryType":1,"deliveryName":"网点自提"}]
     */
    public DatasEntity datas;


    public static class DatasEntity {
        public int count;
        /**
         * deliveryType : 1
         * deliveryName : 网点自提
         */
        public List<ItemsEntity> items;

        public static class ItemsEntity {
            public int deliveryType;
            public String deliveryName;

        }
    }
}
