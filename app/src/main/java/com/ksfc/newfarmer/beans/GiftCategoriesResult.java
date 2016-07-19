package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by CAI on 2016/6/17.
 */
public class GiftCategoriesResult extends ResponseResult {

    /**
     * _id : 5757cdc18e87934c07918c53
     * name : 精美礼品
     * deliveries : [{"deliveryType":1,"deliveryName":"网点自提"}]
     * datecreated : 2016-06-08T07:48:17.238Z
     */

    public List<CategoriesBean> categories;

    public static class CategoriesBean {
        public String _id;
        public String name;
        public String datecreated;
        /**
         * deliveryType : 1
         * deliveryName : 网点自提
         */

        public List<DeliveriesBean> deliveries;

        public static class DeliveriesBean {
            public int deliveryType;
            public String deliveryName;
        }
    }
}
