package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CAI on 2016/6/17.
 */
public class GiftDetailResult extends ResponseResult  implements Serializable{

    /**
     * _id : 575e40ae94ba82804f4c188f
     * id : 66477171c0
     * name : 熊本熊- 可爱萌萌哒的宠物
     * marketPrice : 60
     * points : 200
     * dateupdated : 2016-06-13T05:12:14.407Z
     * datecreated : 2016-06-13T05:12:14.407Z
     * istop : true
     * category : {"_id":"5757cdc18e87934c07918c53","name":"精美礼品","deliveries":[{"deliveryType":1,"deliveryName":"网点自提"}]}
     * pictures : [{"largeUrl":"/images/large/14657946085508o3fecdi.jpg","thumbnail":"/images/thumbnail/14657946085508o3fecdi.jpg","originalUrl":"/images/original/14657946085508o3fecdi.jpg"}]
     * online : true
     * soldout : false
     * appbody_url : http://www.xinxinnongren.com/gift/appbody/66477171c0
     * largeUrl : /images/large/14657946085508o3fecdi.jpg
     * thumbnail : /images/thumbnail/14657946085508o3fecdi.jpg
     * originalUrl : /images/original/14657946085508o3fecdi.jpg
     */

    public GiftBean gift;

    public static class GiftBean implements Serializable{
        public String _id;
        public String id;
        public String name;
        public String marketPrice;
        public int points;
        public String dateupdated;
        public String datecreated;
        public boolean istop;
        /**
         * _id : 5757cdc18e87934c07918c53
         * name : 精美礼品
         * deliveries : [{"deliveryType":1,"deliveryName":"网点自提"}]
         */

        public CategoryBean category;
        public boolean online;
        public boolean soldout;
        public String appbody_url;
        public String largeUrl;
        public String thumbnail;
        public String originalUrl;
        /**
         * largeUrl : /images/large/14657946085508o3fecdi.jpg
         * thumbnail : /images/thumbnail/14657946085508o3fecdi.jpg
         * originalUrl : /images/original/14657946085508o3fecdi.jpg
         */

        public List<PicturesBean> pictures;

        public static class CategoryBean  implements Serializable{
            public String _id;
            public String name;
            /**
             * deliveryType : 1
             * deliveryName : 网点自提
             */

            public List<DeliveriesBean> deliveries;

            public static class DeliveriesBean  implements Serializable{
                public int deliveryType;
                public String deliveryName;
            }
        }

        public static class PicturesBean  implements Serializable{
            public String largeUrl;
            public String thumbnail;
            public String originalUrl;
        }
    }
}
